package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.RisingThingEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.overlay.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RisingThingRenderer extends EntityRenderer<RisingThingEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FindariesMod.MODID, "textures/entity/rising_thing.png");
    private final SimpleBoxModel<RisingThingEntity> model;

    public RisingThingRenderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.model = new SimpleBoxModel<>(SimpleBoxModel.box(-6, -32, -6, 12, 64, 12));
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final RisingThingEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(final RisingThingEntity pEntity, final float pYaw, final float pPartialTicks,
                       final PoseStack pStack, final MultiBufferSource pBuffer, final int pPackedLight) {
        pStack.pushPose();
        model.renderToBuffer(pStack, pBuffer.getBuffer(RenderType.entitySolid(TEXTURE)),
                pPackedLight, OverlayTexture.NO_OVERLAY, 0.1F, 0.1F, 0.1F, 1.0F);
        pStack.popPose();
    }
}
