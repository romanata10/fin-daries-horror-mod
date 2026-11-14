package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.WatcherEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobEntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidModel;
import net.minecraft.resources.ResourceLocation;

public class WatcherRenderer extends MobEntityRenderer<WatcherEntity, HumanoidModel<WatcherEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FindariesMod.MODID, "textures/entity/watcher.png");

    public WatcherRenderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher, new HumanoidModel<>(1.0F, false, 64, 64), 0.4F);
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final WatcherEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(final WatcherEntity pEntity, final float pYaw, final float pPartialTicks,
                       final PoseStack pStack, final MultiBufferSource pBuffer, final int pPackedLight) {
        pStack.pushPose();
        pStack.scale(0.6F, 2.8F, 0.6F);
        super.render(pEntity, pYaw, pPartialTicks, pStack, pBuffer, pPackedLight);
        pStack.popPose();
    }
}
