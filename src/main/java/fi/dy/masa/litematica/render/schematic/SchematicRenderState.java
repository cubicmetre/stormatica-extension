package fi.dy.masa.litematica.render.schematic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;

public class SchematicRenderState
{
	protected CameraRenderState cameraState;
	protected final List<EntityRenderState> entityStates;
	protected final List<BlockEntityRenderState> tileEntityStates;

	protected SchematicRenderState()
	{
		this.cameraState = new CameraRenderState();
		this.entityStates = new ArrayList<>();
		this.tileEntityStates = new ArrayList<>();
	}

	protected void clear()
	{
		this.entityStates.clear();
		this.tileEntityStates.clear();
	}
}
