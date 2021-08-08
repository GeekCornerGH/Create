package com.simibubi.create.content.contraptions.components.structureMovement.render;

import static com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher.CONTRAPTION;
import static com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher.buildStructureBuffer;

import net.minecraft.world.level.LevelAccessor;

import org.apache.commons.lang3.tuple.Pair;

import com.jozufozu.flywheel.event.RenderLayerEvent;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;

import net.minecraft.client.renderer.RenderType;

public class SBBContraptionManager extends ContraptionRenderManager<ContraptionRenderInfo> {
	public SBBContraptionManager(LevelAccessor world) {
		super(world);
	}

	@Override
	public void renderLayer(RenderLayerEvent event) {
		super.renderLayer(event);
		visible.forEach(info -> renderContraptionLayerSBB(event, info));
	}

	@Override
	protected ContraptionRenderInfo create(Contraption c) {
		PlacementSimulationWorld renderWorld = ContraptionRenderDispatcher.setupRenderWorld(world, c);
		return new ContraptionRenderInfo(c, renderWorld);
	}

	private void renderContraptionLayerSBB(RenderLayerEvent event, ContraptionRenderInfo renderInfo) {
		RenderType layer = event.getType();

		if (!renderInfo.isVisible()) return;

		SuperByteBuffer contraptionBuffer = CreateClient.BUFFER_CACHE.get(CONTRAPTION, Pair.of(renderInfo.contraption, layer), () -> buildStructureBuffer(renderInfo.renderWorld, renderInfo.contraption, layer));

		if (!contraptionBuffer.isEmpty()) {
			ContraptionMatrices matrices = renderInfo.getMatrices();

			contraptionBuffer.transform(matrices.getModel())
					.light(matrices.getWorld())
					.hybridLight()
					.renderInto(matrices.getViewProjection(), event.buffers.bufferSource()
							.getBuffer(layer));
		}

	}
}
