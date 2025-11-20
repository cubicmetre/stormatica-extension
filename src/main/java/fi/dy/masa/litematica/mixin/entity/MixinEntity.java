package fi.dy.masa.litematica.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import fi.dy.masa.litematica.util.IEntityInvoker;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntityInvoker
{
    @Shadow protected boolean touchingWater;

    public MixinEntity()
    {
        super();
    }

    @Override
    public void litematica$toggleTouchingWater(boolean toggle)
    {
        if (toggle != this.touchingWater)
        {
            this.touchingWater = toggle;
        }
    }
}
