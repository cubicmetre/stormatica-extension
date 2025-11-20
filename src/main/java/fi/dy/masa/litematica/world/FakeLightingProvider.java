package fi.dy.masa.litematica.world;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LightStorage;
import net.minecraft.world.chunk.light.LightingProvider;
import fi.dy.masa.litematica.config.Configs;

public class FakeLightingProvider extends LightingProvider
{
    private final FakeLightingView lightingView;
    private static final ChunkNibbleArray chunkNibbleArray = new ChunkNibbleArray(15);

    public FakeLightingProvider(ChunkProvider chunkProvider)
    {
        super(chunkProvider, false, false);

        this.lightingView = new FakeLightingView();
    }

    @Override
    public @Nonnull ChunkLightingView get(@Nonnull LightType type)
    {
        return this.lightingView;
    }

    @Override
    public int getLight(@Nonnull BlockPos pos, int ambientDarkness)
    {
        //return 15;
        return Configs.Visuals.RENDER_FAKE_LIGHTING_LEVEL.getIntegerValue();
    }

    public static ChunkNibbleArray getChunkNibbleArray() { return chunkNibbleArray; }

    @Override
    public boolean isLightingEnabled(long sectionPos)
    {
        return true;
    }

    @Override
    public @Nonnull LightStorage.Status getStatus(@Nonnull LightType lightType, @Nonnull ChunkSectionPos pos)
    {
        return LightStorage.Status.LIGHT_ONLY;
    }

    @Override
    public @Nonnull String displaySectionLevel(@Nonnull LightType lightType, @Nonnull ChunkSectionPos pos)
    {
        return Integer.toString(1);
    }

    public static class FakeLightingView implements ChunkLightingView
    {
        @Nullable
        @Override
        public ChunkNibbleArray getLightSection(@Nonnull ChunkSectionPos pos)
        {
            return FakeLightingProvider.chunkNibbleArray;
        }

        @Override
        public int getLightLevel(@Nonnull BlockPos pos)
        {
            //return 15;
            return Configs.Visuals.RENDER_FAKE_LIGHTING_LEVEL.getIntegerValue();
        }

        @Override
        public void checkBlock(@Nonnull BlockPos pos)
        {
            // Checked
        }

        @Override
        public void propagateLight(@Nonnull ChunkPos chunkPos)
        {
            // Done
        }

        @Override
        public boolean hasUpdates()
        {
            return false;
        }

        @Override
        public int doLightUpdates()
        {
            return 0;
        }

        @Override
        public void setSectionStatus(@Nonnull ChunkSectionPos pos, boolean notReady)
        {
            // NO-OP
        }

        @Override
        public void setColumnEnabled(@Nonnull ChunkPos chunkPos, boolean bl)
        {
            // NO-OP
        }
    }
}
