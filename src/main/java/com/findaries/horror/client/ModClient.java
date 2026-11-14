package com.findaries.horror.client;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.block.ModDimensions;
import com.findaries.horror.entity.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

/** Клиентская часть: рендеры сущностей, оверлей версии, отключение при приближении монстров. */
@OnlyIn(Dist.CLIENT)
public class ModClient {
    public static void init(final IEventBus modBus, final net.minecraftforge.common.MinecraftForge forgeBus) {
        forgeBus.addListener(ModClient::registerRenderers);
        forgeBus.addListener(ModClient::onRenderGameOverlay);
        forgeBus.addListener(ModClient::onClientTick);
    }

    @SubscribeEvent
    public static void registerRenderers(final RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FACELESS_STEVE.get(), FacelessSteveRenderer::new);
        event.registerEntityRenderer(ModEntities.WATCHER.get(), WatcherRenderer::new);
        event.registerEntityRenderer(ModEntities.DANGER_12.get(), Danger12Renderer::new);
        event.registerEntityRenderer(ModEntities.BLUE_STRANGER.get(), BlueStrangerRenderer::new);
        event.registerEntityRenderer(ModEntities.GIANT_TRIANGLE.get(), GiantTriangleRenderer::new);
        event.registerEntityRenderer(ModEntities.UFO.get(), UfoRenderer::new);
        event.registerEntityRenderer(ModEntities.SKY_THING.get(), SkyThingRenderer::new);
        event.registerEntityRenderer(ModEntities.RISING_THING.get(), RisingThingRenderer::new);
        event.registerEntityRenderer(ModEntities.MUSHROOM_LEGS.get(), MushroomLegsRenderer::new);
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        final Level level = player.level();

        // Безликий Стив и danger_12 — мгновенный возврат в главное меню при касании.
        for (FacelessSteveEntity steve : level.getEntitiesOfClass(FacelessSteveEntity.class,
                player.getBoundingBox().inflate(2.0))) {
            minecraft.disconnect(Component.literal("Безликий Стив поймал тебя..."));
            return;
        }
        for (Danger12Entity danger : level.getEntitiesOfClass(Danger12Entity.class,
                player.getBoundingBox().inflate(1.2))) {
            minecraft.disconnect(Component.literal("danger_12 поймал тебя..."));
            return;
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        // В wonderland версия в углу экрана меняется.
        if (player.level().dimension() == ModDimensions.WONDERLAND) {
            final GuiGraphics gui = event.getGuiGraphics();
            final int height = minecraft.getWindow().getHeight();
            gui.drawString(minecraft.font, "minecraft.1.wonderland.0", 2, height - 10, 0xFFFFFF);
        }
    }
}
