package fi.dy.masa.litematica.mixin.render;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.render.LitematicaRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
	@Shadow @Final private Camera camera;

	@Inject(method = "updateCameraState", at = @At("TAIL"))
	private void litematica_updateCameraState(float f, CallbackInfo ci)
	{
		LitematicaRenderer.getInstance().updateCameraState(this.camera, f);
	}
}
