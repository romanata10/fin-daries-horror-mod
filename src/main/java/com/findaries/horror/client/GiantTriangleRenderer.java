package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.GiantTriangleEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.overlay.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GiantTriangleRenderer extends EntityRenderer<GiantTriangleEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "block/black_wool");
    private final SimpleBoxModel<GiantTriangleEntity> model;

    public GiantTriangleRenderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.model = new SimpleBoxModel<>(SimpleBoxModel.box(-16, -1, -16, 32, 2, 32));
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final GiantTriangleEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(final GiantTriangleEntity pEntity, final float pYaw, final float pPartialTicks,
                       final PoseStack pStack, final MultiBufferSource pBuffer, final int pPackedLight) {
        pStack.pushPose();
        // Гиганский плоский треугольник в небе.
        pStack.scale(14.0F, 0.5F, 14.0F);
        model.renderToBuffer(pStack, pBuffer.getBuffer(RenderType.entitySolid(TEXTURE)),
                pPackedLight, OverlayTexture.NO_OVERLAY, 0.03F, 0.03F, 0.03F, 1.0F);
        pStack.popPose();
    }
}
