package fi.dy.masa.litematica.mixin.render;

import java.util.Map;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientMannequinEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.util.IEntityRendererInvoker;

@Mixin(EntityRenderManager.class)
public abstract class MixinEntityRenderManager implements IEntityRendererInvoker
{
	@Shadow private Map<PlayerSkinType, PlayerEntityRenderer<ClientMannequinEntity>> mannequinRenderers;
	@Shadow private Map<PlayerSkinType, PlayerEntityRenderer<AbstractClientPlayerEntity>> playerRenderers;
	@Shadow public abstract <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity);

	@Override
	public <E extends Entity> EntityRenderer<? super E, ?> litematica_getEntityRendererNullSafe(E entity)
	{
		SkinTextures skin = DefaultSkinHelper.getSkinTextures(entity.getUuid());

		if (entity instanceof ClientMannequinEntity cme)
		{
			cme.tick();
			skin = cme.getSkin() != null ? cme.getSkin() : skin;
			return this.litematica_getMannequinRendererBySkin(skin);
		}
		else if (entity instanceof ClientPlayerEntity cpe)
		{
			skin = cpe.getSkin() != null ? cpe.getSkin() : skin;
			return this.litematica_getPlayerRendererBySkin(skin);
		}

		return this.getRenderer(entity);
	}

	@Override
	public <E extends Entity> EntityRenderState litematica_getRenderStateNullSafe(E entity, float tickProgress)
	{
		EntityRenderer<? super E, ?> renderer = this.litematica_getEntityRendererNullSafe(entity);

		if (renderer == null)
		{
			// Try the Vanilla method too? *shrug*
			renderer = this.getRenderer(entity);
		}

		if (renderer != null)
		{
			try
			{
				return renderer.getAndUpdateRenderState(entity, tickProgress);
			}
			catch (Exception err)
			{
				Litematica.LOGGER.error("litematica_getRenderState: Exception getting Entity Render State; {}", err.getLocalizedMessage());
			}
		}

		return null;
	}

	@Unique
	@SuppressWarnings("unchecked")
	private <E extends Entity> EntityRenderer<? super E, ?> litematica_getPlayerRendererBySkin(SkinTextures skin)
	{
		if (this.playerRenderers.containsKey(skin.model()))
		{
			return (EntityRenderer<? super E, ?>) this.playerRenderers.get(skin.model());
		}
		else if (this.playerRenderers.containsKey(PlayerSkinType.WIDE))
		{
			return (EntityRenderer<? super E, ?>) this.playerRenderers.get(PlayerSkinType.WIDE);
		}

		return null;
	}

	@Unique
	@SuppressWarnings("unchecked")
	private <E extends Entity> EntityRenderer<? super E, ?> litematica_getMannequinRendererBySkin(SkinTextures skin)
	{
		if (this.mannequinRenderers.containsKey(skin.model()))
		{
			return (EntityRenderer<? super E, ?>) this.mannequinRenderers.get(skin.model());
		}
		else if (this.mannequinRenderers.containsKey(PlayerSkinType.WIDE))
		{
			return (EntityRenderer<? super E, ?>) this.mannequinRenderers.get(PlayerSkinType.WIDE);
		}

		return null;
	}
}
