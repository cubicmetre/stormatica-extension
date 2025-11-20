package fi.dy.masa.litematica.mixin.easyplace;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.util.EasyPlaceUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

/**
 * Post Re-Write code
 */
@Mixin(value = ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager_easyPlace
{
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir)
    {
        if (Configs.Generic.EASY_PLACE_MODE.getBooleanValue() &&
            Configs.Generic.EASY_PLACE_POST_REWRITE.getBooleanValue())
        {
            // Prevent recursion, since the Easy Place mode can call this code again
            if (EasyPlaceUtils.isHandling() == false)
            {
                if (EasyPlaceUtils.shouldDoEasyPlaceActions())
                {
                    if (EasyPlaceUtils.handleEasyPlaceWithMessage())
                    {
                        cir.setReturnValue(ActionResult.FAIL);
                    }
                }
                else
                {
                    if (Configs.Generic.PLACEMENT_RESTRICTION.getBooleanValue())
                    {
                        if (EasyPlaceUtils.handlePlacementRestriction())
                        {
                            cir.setReturnValue(ActionResult.FAIL);
                        }
                    }
                }
            }
        }
    }

	// This causes double-placements
//    @Inject(method = "interactBlockInternal",
//			at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;",
//            shift = At.Shift.BEFORE), cancellable = true)
//    private void onInteractBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir)
//    {
//        if (Configs.Generic.EASY_PLACE_MODE.getBooleanValue() &&
//            Configs.Generic.EASY_PLACE_POST_REWRITE.getBooleanValue())
//        {
//            // Prevent recursion, since the Easy Place mode can call this code again
//            if (EasyPlaceUtils.isHandling() == false)
//            {
//                if (EasyPlaceUtils.shouldDoEasyPlaceActions() &&
//                    EasyPlaceUtils.handleEasyPlaceWithMessage())
//                {
//                    cir.setReturnValue(ActionResult.FAIL);
//                }
//            }
//        }
//    }
}
