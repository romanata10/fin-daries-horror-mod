package com.findaries.horror.event;

import com.findaries.horror.block.ModBlocks;
import com.findaries.horror.block.ModDimensions;
import com.findaries.horror.block.ModStructures;
import com.findaries.horror.entity.*;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

/**
 * Менеджер случайных ужасных событий Fin Diaries:
 * полёты объектов в небе, аномалии у игрока, порталы, спавн сущностей.
 */
public class HorrorEventManager {
    @SubscribeEvent
    public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        if (event.side != TickEvent.Side.SERVER) {
            return;
        }
        final Player player = event.player;
        final Level level = player.level();
        if (level.isClientSide) {
            return;
        }
        final long time = level.getGameTime();
        final CompoundTag data = player.getPersistentData();

        // Первичная застройка при прибытии в измерение.
        if (level.dimension() == ModDimensions.WONDERLAND && !data.getBoolean("findaries:arrived_wonderland")) {
            data.putBoolean("findaries:arrived_wonderland", true);
            ModStructures.placeWonderlandHouse((ServerLevel) level, player.blockPosition());
        } else if (level.dimension() == ModDimensions.WONDERFLOWERS && !data.getBoolean("findaries:arrived_wonderflowers")) {
            data.putBoolean("findaries:arrived_wonderflowers", true);
            ModStructures.placeWonderflowersArrival((ServerLevel) level, player.blockPosition());
        } else if (level.dimension() == ModDimensions.CAGE_WONDERLAND && !data.getBoolean("findaries:arrived_cage")) {
            data.putBoolean("findaries:arrived_cage", true);
            ModStructures.placeCageWonderlandSpawn((ServerLevel) level, player.blockPosition());
        }

        // Безликий Стив: касание = возврат в главное меню.
        final AABB steveCheck = player.getBoundingBox().inflate(3.0);
        for (FacelessSteveEntity steve : level.getEntitiesOfClass(FacelessSteveEntity.class, steveCheck)) {
            if (steve.isPlayerTooClose()) {
                ((ServerPlayer) player).connection.disconnect(Component.literal("Безликий Стив поймал тебя..."));
                steve.remove(RemovalReason.KILLED);
                data.putLong("findaries:steve_cooldown", time + 24000);
            }
        }

        rollEvents(player, (ServerLevel) level, time, data);
    }

    private void rollEvents(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (level.random.nextInt(18000) != 0) {
            return; // примерно раз в 15 минут на игрока в среднем
        }
        switch (level.random.nextInt(10)) {
            case 0 -> spawnGiantTriangle(player, level, time, data);
            case 1 -> spawnFacelessSteveForest(player, level, time, data);
            case 2 -> spawnMushroomLegs(player, level, time, data);
            case 3 -> spawnRisingThing(player, level, time, data);
            case 4 -> spawnBlueStranger(player, level, time, data);
            case 5 -> spawnUfo(player, level, time, data);
            case 6 -> spawnSkyThing(player, level, time, data);
            case 7 -> spawnWonderlandPortalFrame(player, level, time, data);
            case 8 -> spawnDanger12(player, level, time, data);
            default -> spawnWatcher(player, level, time, data);
        }
    }

    /* ---------- отдельные события ---------- */

    private static boolean onCooldown(final CompoundTag data, final String key, final long time) {
        return data.getLong(key) > time;
    }

    private static void setCooldown(final CompoundTag data, final String key, final long time, final int ticks) {
        data.putLong(key, time + ticks);
    }

    private static int surfaceY(final Level level, final double x, final double z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z) + 1;
    }

    private void spawnGiantTriangle(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_triangle", time)) return;
        setCooldown(data, "findaries:ev_triangle", time, 36000);
        final int ox = (int) player.getX() + level.random.nextInt(61) - 30;
        final int oz = (int) player.getZ() + level.random.nextInt(61) - 30;
        final int y = surfaceY(level, ox, oz) + 45 + level.random.nextInt(40);
        final GiantTriangleEntity tri = new GiantTriangleEntity(ModEntities.GIANT_TRIANGLE.get(), level);
        tri.moveTo(ox, y, oz, level.random.nextFloat() * 360.0F, 0.0F);
        final double dx = (level.random.nextBoolean() ? 0.35 : -0.35);
        final double dz = (level.random.nextBoolean() ? 0.35 : -0.35);
        tri.launch(new Vec3(dx, 0, dz));
        level.addFreshEntity(tri);
        level.playSound(null, ox, y, oz, SoundEvents.BLOCK_FIRE_AMBIENT, SoundSource.HOSTILE, 2.5F, 1.0F);
    }

    private void spawnFacelessSteveForest(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_steve", time)) return;
        if (level.getBiome(player.blockPosition()).value().biomeCategory() != BiomeCategory.FOREST) {
            return;
        }
        setCooldown(data, "findaries:ev_steve", time, 40000);
        final double ay = Math.toRadians(player.getYRot());
        final double bx = player.getX() - Math.sin(ay) * 14.0;
        final double bz = player.getZ() + Math.cos(ay) * 14.0;
        final int by = surfaceY(level, bx, bz);
        final FacelessSteveEntity steve = new FacelessSteveEntity(ModEntities.FACELESS_STEVE.get(), level);
        steve.moveTo(bx, by, bz, player.getYRot() + 180.0F, 0.0F);
        level.addFreshEntity(steve);
        level.playSound(null, bx, by, bz, SoundEvents.AMBIENT_CAVE, SoundSource.HOSTILE, 1.5F, 1.0F);
    }

    private void spawnWatcher(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_watcher", time)) return;
        setCooldown(data, "findaries:ev_watcher", time, 40000);
        final double bx = player.getX() + (level.random.nextBoolean() ? 12 : -12);
        final double bz = player.getZ() + (level.random.nextBoolean() ? 12 : -12);
        final int by = surfaceY(level, bx, bz);
        final WatcherEntity watcher = new WatcherEntity(ModEntities.WATCHER.get(), level);
        watcher.moveTo(bx, by, bz, level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(watcher);
    }

    private void spawnMushroomLegs(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_mushrooms", time)) return;
        setCooldown(data, "findaries:ev_mushrooms", time, 24000);
        final MushroomLegsEntity mushroom = new MushroomLegsEntity(ModEntities.MUSHROOM_LEGS.get(), level);
        mushroom.moveTo(player.getX(), player.getY() + 2, player.getZ(), level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(mushroom);
    }

    private void spawnRisingThing(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_rising", time)) return;
        setCooldown(data, "findaries:ev_rising", time, 24000);
        final RisingThingEntity thing = new RisingThingEntity(ModEntities.RISING_THING.get(), level);
        thing.moveTo(player.getX(), player.getY() + 6, player.getZ(), 0.0F, 0.0F);
        level.addFreshEntity(thing);
    }

    private void spawnBlueStranger(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_blue", time)) return;
        setCooldown(data, "findaries:ev_blue", time, 24000);
        final double bx = player.getX() + (level.random.nextInt(9) + 3) * (level.random.nextBoolean() ? 1 : -1);
        final double bz = player.getZ() + (level.random.nextInt(9) + 3) * (level.random.nextBoolean() ? 1 : -1);
        final int by = surfaceY(level, bx, bz);
        final BlueStrangerEntity stranger = new BlueStrangerEntity(ModEntities.BLUE_STRANGER.get(), level);
        stranger.moveTo(bx, by, bz, level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(stranger);
    }

    private void spawnUfo(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_ufo", time)) return;
        setCooldown(data, "findaries:ev_ufo", time, 50000);
        final int ox = (int) player.getX() + level.random.nextInt(121) - 60;
        final int oz = (int) player.getZ() + level.random.nextInt(121) - 60;
        final int y = surfaceY(level, ox, oz) + 35 + level.random.nextInt(20);
        final UfoEntity ufo = new UfoEntity(ModEntities.UFO.get(), level);
        ufo.moveTo(ox, y, oz, level.random.nextFloat() * 360.0F, 0.0F);
        ufo.launch(new Vec3((level.random.nextBoolean() ? 1.0 : -1.0) * 0.8, 0, 0));
        level.addFreshEntity(ufo);
    }

    private void spawnSkyThing(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_skything", time)) return;
        setCooldown(data, "findaries:ev_skything", time, 36000);
        final int ox = (int) player.getX() + level.random.nextInt(61) - 30;
        final int oz = (int) player.getZ() + level.random.nextInt(61) - 30;
        final int y = surfaceY(level, ox, oz) + 25 + level.random.nextInt(15);
        final SkyThingEntity thing = new SkyThingEntity(ModEntities.SKY_THING.get(), level);
        thing.moveTo(ox, y, oz, level.random.nextFloat() * 360.0F, 0.0F);
        thing.launch(new Vec3((level.random.nextBoolean() ? 0.2 : -0.2), 0, 0));
        level.addFreshEntity(thing);
    }

    private void spawnWonderlandPortalFrame(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_wonder_portal", time)) return;
        setCooldown(data, "findaries:ev_wonder_portal", time, 50000);
        final int ox = (int) player.getX() + level.random.nextInt(13) - 6;
        final int oz = (int) player.getZ() + level.random.nextInt(13) - 6;
        final int y = surfaceY(level, ox, oz);
        //рамка портала 2x3
        for (int dy = 0; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                level.setBlockAndUpdate(new BlockPos(ox + dx, y + dy, oz),
                        ModBlocks.WONDER_PORTAL_BLOCK.get().defaultBlockState());
            }
        }
    }

    private void spawnDanger12(final Player player, final ServerLevel level, final long time, final CompoundTag data) {
        if (onCooldown(data, "findaries:ev_danger12", time)) return;
        if (level.dimension() != ModDimensions.WONDERFLOWERS) {
            return;
        }
        setCooldown(data, "findaries:ev_danger12", time, 24000);
        final double bx = player.getX() + (level.random.nextInt(13) + 8) * (level.random.nextBoolean() ? 1 : -1);
        final double bz = player.getZ() + (level.random.nextInt(13) + 8) * (level.random.nextBoolean() ? 1 : -1);
        final int by = surfaceY(level, bx, bz);
        final Danger12Entity danger = new Danger12Entity(ModEntities.DANGER_12.get(), level);
        danger.moveTo(bx, by, bz, level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(danger);
        level.playSound(null, bx, by, bz, SoundEvents.AMBIENT_CAVE, SoundSource.HOSTILE, 1.5F, 1.0F);
    }
}
