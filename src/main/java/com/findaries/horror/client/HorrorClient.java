package com.findaries.horror.client;

import com.findaries.horror.FindariesHorror;
import com.findaries.horror.entity.HorrorEntities;
import com.findaries.horror.entity.LongFigureEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FindariesHorror.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HorrorClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HorrorEntities.LONG_FIGURE.get(), context -> new FigureRenderer(context, 2.55f));
        event.registerEntityRenderer(HorrorEntities.DANGER_12.get(), context -> new FigureRenderer(context, 2.05f));
        event.registerEntityRenderer(HorrorEntities.BLUE_WALKER.get(), context -> new FigureRenderer(context, 1.75f));
        event.registerEntityRenderer(HorrorEntities.WONDER_WATCHER.get(), context -> new FigureRenderer(context, 1.9f));
    }
    @SubscribeEvent public static void overlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("wonderland_version", VERSION);
    }
    private static final IGuiOverlay VERSION = (gui, pose, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.dimension().location().equals(FindariesHorror.id("wonderland"))) {
            pose.drawString(mc.font, "minecraft.1.wonderland.0", 7, 7, 0xFFF4F45A, true);
        }
    };
    private static final class FigureRenderer extends HumanoidMobRenderer<LongFigureEntity, HumanoidModel<LongFigureEntity>> {
        private final float scale;
        FigureRenderer(EntityRendererProvider.Context context, float scale) {
            super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), .35f);
            this.scale=scale;
        }
        @Override protected void scale(LongFigureEntity entity, com.mojang.blaze3d.vertex.PoseStack pose, float partialTick) {
            pose.scale(1.0f,scale,1.0f);
        }
        @Override public ResourceLocation getTextureLocation(LongFigureEntity entity) {
            if (entity.getType() == HorrorEntities.DANGER_12.get() || entity.getType() == HorrorEntities.WONDER_WATCHER.get()) return FindariesHorror.id("textures/entity/danger_12.png");
            if (entity.getType() == HorrorEntities.BLUE_WALKER.get()) return FindariesHorror.id("textures/entity/blue_walker.png");
            return FindariesHorror.id("textures/entity/long_figure.png");
        }
    }
    private HorrorClient() {}
}
