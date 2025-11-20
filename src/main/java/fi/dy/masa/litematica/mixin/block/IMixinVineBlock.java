package fi.dy.masa.litematica.mixin.block;

import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VineBlock.class)
public interface IMixinVineBlock
{
    @Invoker("shouldHaveSide")
    boolean litematica_invokeShouldConnectUp(BlockView blockReader, BlockPos pos, Direction side);
}
