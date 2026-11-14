package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.UfoEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.overlay.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class UfoRenderer extends EntityRenderer<UfoEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FindariesMod.MODID, "textures/entity/ufo.png");
    private final SimpleBoxModel<UfoEntity> model;

    public UfoRenderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.model = new SimpleBoxModel<>(SimpleBoxModel.box(-16, -4, -16, 32, 8, 32));
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final UfoEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(final UfoEntity pEntity, final float pYaw, final float pPartialTicks,
                       final PoseStack pStack, final MultiBufferSource pBuffer, final int pPackedLight) {
        pStack.pushPose();
        pStack.scale(2.0F, 1.0F, 2.0F);
        model.renderToBuffer(pStack, pBuffer.getBuffer(RenderType.entitySolid(TEXTURE)),
                pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.9F);
        pStack.popPose();
    }
}
