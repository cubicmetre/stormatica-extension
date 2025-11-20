package fi.dy.masa.litematica.world;

import com.google.common.collect.Iterables;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityLookup;

public class SchematicEntityLookup<T extends EntityLike> implements EntityLookup<T>, AutoCloseable
{
    private final ConcurrentHashMap<Integer, T> entityMap;
    private final ConcurrentHashMap<UUID, Integer> uuidMap;

    protected SchematicEntityLookup()
    {
        this.entityMap = new ConcurrentHashMap<>();
        this.uuidMap = new ConcurrentHashMap<>();
    }

    protected void put(T entity)
    {
        T tmp = this.get(entity.getUuid());

        if (tmp != null)
        {
            this.remove(entity.getUuid());
        }

        synchronized (this.uuidMap)
        {
            this.uuidMap.put(entity.getUuid(), entity.getId());
        }

        synchronized (this.entityMap)
        {
            this.entityMap.put(entity.getId(), entity);
        }
    }

    protected int size()
    {
        return this.entityMap.size();
    }

    protected void remove(UUID uuid)
    {
        Integer key = this.uuidMap.get(uuid);

        if (key != null)
        {
            synchronized (this.entityMap)
            {
                this.entityMap.remove(key);
            }

            synchronized (this.uuidMap)
            {
                this.uuidMap.remove(uuid);
            }
        }
        else
        {
            synchronized (this.entityMap)
            {
                for (Integer id : this.entityMap.keySet())
                {
                    T e = this.entityMap.get(id);

                    if (e.getUuid().equals(uuid))
                    {
                        this.entityMap.remove(id);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public @Nullable T get(int id)
    {
        if (this.entityMap.containsKey(id))
        {
            T e = this.entityMap.get(id);

            if (!this.uuidMap.containsKey(e.getUuid()))
            {
                synchronized (this.uuidMap)
                {
                    this.uuidMap.put(e.getUuid(), id);
                }
            }

            return e;
        }

        return null;
    }

    @Override
    public @Nullable T get(@Nonnull UUID uuid)
    {
        if (this.uuidMap.containsKey(uuid))
        {
            int key = this.uuidMap.get(uuid);

            if (this.entityMap.containsKey(key))
            {
                return this.entityMap.get(key);
            }

            synchronized (this.uuidMap)
            {
                this.uuidMap.remove(uuid);
            }

            return null;
        }

        for (Integer id : this.entityMap.keySet())
        {
            T e = this.entityMap.get(id);

            if (e.getUuid().equals(uuid))
            {
                if (!this.uuidMap.containsKey(uuid))
                {
                    synchronized (this.uuidMap)
                    {
                        this.uuidMap.put(uuid, id);
                    }
                }

                return e;
            }
        }

        return null;
    }

    @Override
    public @Nonnull Iterable<T> iterate()
    {
        return Iterables.unmodifiableIterable(this.entityMap.values());
    }

    @Override
    public void forEachIntersects(@Nonnull Box box, @Nonnull Consumer<T> action)
    {
        this.entityMap.forEach(
                (id, e) ->
                {
                    if (box.intersects(e.getBoundingBox()))
                    {
                        LazyIterationConsumer<T> consumer = LazyIterationConsumer.forConsumer(action);

                        if (consumer.accept(e).shouldAbort())
                        {
                            return;
                        }
                    }
                });
    }

    @Override
    public <U extends T> void forEachIntersects(@Nonnull TypeFilter<T, U> filter, @Nonnull Box box, @Nonnull LazyIterationConsumer<U> consumer)
    {
        this.entityMap.forEach(
                (id, e) ->
                {
                    U filtered = filter.downcast(e);

                    if (filtered != null && box.intersects(filtered.getBoundingBox()))
                    {
                        if (consumer.accept(filtered).shouldAbort())
                        {
                            return;
                        }
                    }
                });
    }

    @Override
    public <U extends T> void forEach(@Nonnull TypeFilter<T, U> filter, @Nonnull LazyIterationConsumer<U> consumer)
    {
        this.entityMap.forEach(
                (id, e) ->
                {
                    U filtered = filter.downcast(e);

                    if (filtered != null)
                    {
                        if (consumer.accept(filtered).shouldAbort())
                        {
                            return;
                        }
                    }
                });
    }

    @Override
    public void close() throws Exception
    {
        synchronized (this.entityMap)
        {
            this.entityMap.clear();
        }

        synchronized (this.uuidMap)
        {
            this.uuidMap.clear();
        }
    }
}
