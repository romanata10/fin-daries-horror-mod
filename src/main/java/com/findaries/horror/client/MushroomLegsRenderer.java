package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.MushroomLegsEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.overlay.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MushroomLegsRenderer extends EntityRenderer<MushroomLegsEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FindariesMod.MODID, "textures/entity/mushroom_legs.png");
    private final SimpleBoxModel<MushroomLegsEntity> model;

    public MushroomLegsRenderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.model = new SimpleBoxModel<>(SimpleBoxModel.box(-10, -16, -10, 20, 32, 20));
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final MushroomLegsEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(final MushroomLegsEntity pEntity, final float pYaw, final float pPartialTicks,
                       final PoseStack pStack, final MultiBufferSource pBuffer, final int pPackedLight) {
        pStack.pushPose();
        model.renderToBuffer(pStack, pBuffer.getBuffer(RenderType.entitySolid(TEXTURE)),
                pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pStack.popPose();
    }
}
