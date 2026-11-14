package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.BlueStrangerEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobEntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidModel;
import net.minecraft.resources.ResourceLocation;

public class BlueStrangerRenderer extends MobEntityRenderer<BlueStrangerEntity, HumanoidModel<BlueStrangerEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FindariesMod.MODID, "textures/entity/blue_stranger.png");

    public BlueStrangerRenderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher, new HumanoidModel<>(1.0F, false, 64, 64), 0.4F);
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final BlueStrangerEntity pEntity) {
        return TEXTURE;
    }
}
