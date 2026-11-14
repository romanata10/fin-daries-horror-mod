package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.FacelessSteveEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobEntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidModel;
import net.minecraft.resources.ResourceLocation;

public class FacelessSteveRenderer extends MobEntityRenderer<FacelessSteveEntity, HumanoidModel<FacelessSteveEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FindariesMod.MODID, "textures/entity/steve_no_face.png");

    public FacelessSteveRenderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher, new HumanoidModel<>(1.0F, false, 64, 64), 0.5F);
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final FacelessSteveEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(final FacelessSteveEntity pEntity, final float pYaw, final float pPartialTicks,
                       final PoseStack pStack, final MultiBufferSource pBuffer, final int pPackedLight) {
        // Вытягиваем до 5 блоков в высоту.
        pStack.pushPose();
        pStack.scale(1.0F, 2.6F, 1.0F);
        super.render(pEntity, pYaw, pPartialTicks, pStack, pBuffer, pPackedLight);
        pStack.popPose();
    }
}
