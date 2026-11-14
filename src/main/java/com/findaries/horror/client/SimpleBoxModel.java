package com.findaries.horror.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.ModelPartBuilder;
import net.minecraft.client.model.geom.builders.PartPose;
import net.minecraft.world.entity.Entity;

import java.util.Collections;

/** Простейшая коробочная модель для негуманоидных аномалий. */
public class SimpleBoxModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart root;

    public SimpleBoxModel(final ModelPart root) {
        this.root = root;
    }

    public static ModelPart box(final float minX, final float minY, final float minZ,
                                final float sizeX, final float sizeY, final float sizeZ) {
        final MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("box",
                ModelPartBuilder.create().uv(0, 0).addBox(minX, minY, minZ, sizeX, sizeY, sizeZ, 0.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 0.0F).bakeToRoot();
    }

    @Override
    public void setupAnim(final T pEntity, final float pLimbSwing, final float pLimbSwingAmount,
                          final float pAgeInTicks, final float pNetHeadYaw, final float pPitch) {
    }

    @Override
    public void renderToBuffer(final PoseStack pStack, final VertexConsumer pBuffer,
                               final int pPackedLight, final int pPackedOverlay,
                               final float pRed, final float pGreen, final float pBlue, final float pAlpha) {
        root.render(pStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return Collections.singletonList(root);
    }
}
