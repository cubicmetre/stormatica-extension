package fi.dy.masa.litematica.mixin.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface IMixinWorld
{
    @Invoker("getEntityLookup")
    EntityLookup<Entity> litematica_getEntityLookup();
}
