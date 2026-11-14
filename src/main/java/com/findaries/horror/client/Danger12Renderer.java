package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.entity.Danger12Entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobEntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidModel;
import net.minecraft.resources.ResourceLocation;

public class Danger12Renderer extends MobEntityRenderer<Danger12Entity, HumanoidModel<Danger12Entity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FindariesMod.MODID, "textures/entity/danger12.png");

    public Danger12Renderer(final EntityRenderDispatcher dispatcher) {
        super(dispatcher, new HumanoidModel<>(1.0F, false, 64, 64), 0.5F);
        this.shadowRadius = 0.0F;
        this.shadowScale = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(final Danger12Entity pEntity) {
        return TEXTURE;
    }
}
