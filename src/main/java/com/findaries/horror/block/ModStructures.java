package com.findaries.horror.block;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.blockentity.LinkPortalBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

/**
 * Расстановка структур в мирах (выполняется кодом при первом попадании игрока
 * в измерение, либо редкими событиями в верхнем мире).
 */
public final class ModStructures {
    private static final Random RANDOM = new Random();

    private ModStructures() {
    }

    /* ---------- простые helpers ---------- */

    private static void set(final Level level, final int x, final int y, final int z, final BlockState state) {
        level.setBlockAndUpdate(new BlockPos(x, y, z), state);
    }

    private static void fill(final Level level, final int x0, final int y0, final int z0,
                             final int x1, final int y1, final int z1, final BlockState state) {
        int xx0 = Math.min(x0, x1), xx1 = Math.max(x0, x1);
        int yy0 = Math.min(y0, y1), yy1 = Math.max(y0, y1);
        int zz0 = Math.min(z0, z1), zz1 = Math.max(z0, z1);
        for (int x = xx0; x <= xx1; x++)
            for (int y = yy0; y <= yy1; y++)
                for (int z = zz0; z <= zz1; z++)
                    set(level, x, y, z, state);
    }

    private static void hollowBox(final Level level, final int x0, final int y0, final int z0,
                                  final int x1, final int y1, final int z1, final BlockState wall, final BlockState air) {
        fill(level, x0, y0, z0, x1, y1, z1, wall);
        fill(level, x0 + 1, y0 + 1, z0 + 1, x1 - 1, y1 - 1, z1 - 1, air);
    }

    private static int surfaceY(final Level level, final int x, final int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) + 1;
    }

    /* ---------- верхний мир: домик -> коридор -> дом с лифтом ---------- */

    /**
     * Построить цепочку структур в верхнем мире рядом с игроком:
     * деревянный домик с порталом -> длинный деревянный проход ->
     * деревянный дом с мебелью и лифтом в измерение wonderflowers.
     */
    public static void placeOverworldChain(final ServerLevel level, final Player player) {
        final int sx = (int) player.getX();
        final int sz = (int) player.getZ();
        final int sy = surfaceY(level, sx, sz);

        final int hx = sx;
        final int hz = sz - 6;
        placeWoodenHouse1(level, hx, sy, hz);
        // портал из первого домика ведёт в начало коридора (+40 по X)
        setLinkPortalDest(level, hx + 3, sy + 1, hz + 3, level.dimension(), hx + 42, sy + 1, hz - 3);

        final int cx = hx + 42;
        placeCorridor(level, cx, sy, hz - 3, 28);

        final int hx2 = cx + 28;
        placeWoodenHouse2(level, hx2, sy, hz - 3);
    }

    private static void placeWoodenHouse1(final Level level, final int x, final int y, final int z) {
        final BlockState planks = Blocks.SPRUCE_PLANKS.defaultBlockState();
        final BlockState log = Blocks.SPRUCE_LOG.defaultBlockState();
        final BlockState glass = Blocks.GLASS_PANE.defaultBlockState();
        final BlockState air = Blocks.AIR.defaultBlockState();
        final BlockState door = Blocks.OAK_DOOR.defaultBlockState();

        hollowBox(level, x, y, z, x + 6, y + 4, z + 6, planks, air); // стены
        fill(level, x, y + 4, z, x + 6, y + 4, z + 6, planks);       // крыша
        fill(level, x, y, z, x + 6, y, z, log);                       // уголки/цоколь
        fill(level, x, y, z + 6, x + 6, y, z + 6, log);
        fill(level, x, y, z, x, y + 3, z, log);
        fill(level, x + 6, y, z, x + 6, y + 3, z, log);

        // дверь
        set(level, x + 3, y + 1, z, door);
        set(level, x + 3, y + 2, z, door);
        // окно
        set(level, x + 2, y + 2, z + 1, glass);
        set(level, x + 4, y + 2, z + 1, glass);

        // мебель внутри
        set(level, x + 2, y + 1, z + 3, Blocks.CRAFTING_TABLE.defaultBlockState());
        set(level, x + 4, y + 1, z + 3, Blocks.CHEST.defaultBlockState());
        set(level, x + 3, y + 1, z + 5, Blocks.FURNACE.defaultBlockState());
        set(level, x + 2, y + 1, z + 5, Blocks.BOOKSHELF.defaultBlockState());

        // портал в коридор (на полу)
        set(level, x + 3, y + 1, z + 3, ModBlocks.LINK_PORTAL_BLOCK.get().defaultBlockState());
    }

    private static void placeCorridor(final Level level, final int x, final int y, final int z, final int length) {
        final BlockState planks = Blocks.SPRUCE_PLANKS.defaultBlockState();
        final BlockState glass = Blocks.GLASS_PANE.defaultBlockState();
        final BlockState torch = Blocks.TORCH.defaultBlockState();
        final BlockState air = Blocks.AIR.defaultBlockState();

        hollowBox(level, x, y, z - 1, x + length, y + 4, z + 1, planks, air);
        fill(level, x, y + 4, z - 1, x + length, y + 4, z + 1, glass); // стеклянная крыша
        for (int i = 0; i <= length; i += 4) {
            set(level, x + i, y + 3, z - 2, torch);
            set(level, x + i, y + 3, z + 2, torch);
        }
    }

    private static void placeWoodenHouse2(final Level level, final int x, final int y, final int z) {
        final BlockState planks = Blocks.OAK_PLANKS.defaultBlockState();
        final BlockState log = Blocks.OAK_LOG.defaultBlockState();
        final BlockState glass = Blocks.GLASS_PANE.defaultBlockState();
        final BlockState air = Blocks.AIR.defaultBlockState();

        hollowBox(level, x, y, z, x + 8, y + 5, z + 8, planks, air);
        fill(level, x, y + 5, z, x + 8, y + 5, z + 8, planks);
        fill(level, x, y, z, x + 8, y, z, log);
        fill(level, x, y, z + 8, x + 8, y, z + 8, log);
        fill(level, x, y, z, x, y + 4, z, log);
        fill(level, x + 8, y, z, x + 8, y + 4, z, log);

        set(level, x + 4, y + 1, z, Blocks.OAK_DOOR.defaultBlockState());
        set(level, x + 4, y + 2, z, Blocks.OAK_DOOR.defaultBlockState());
        set(level, x + 2, y + 2, z + 1, glass);
        set(level, x + 6, y + 2, z + 1, glass);

        // мебель
        set(level, x + 2, y + 1, z + 3, Blocks.CRAFTING_TABLE.defaultBlockState());
        set(level, x + 3, y + 1, z + 3, Blocks.FURNACE.defaultBlockState());
        set(level, x + 4, y + 1, z + 3, Blocks.CHEST.defaultBlockState());
        set(level, x + 5, y + 1, z + 3, Blocks.BOOKSHELF.defaultBlockState());
        set(level, x + 6, y + 1, z + 3, Blocks.BOOKSHELF.defaultBlockState());
        set(level, x + 2, y + 1, z + 6, Blocks.CHEST.defaultBlockState());
        set(level, x + 3, y + 1, z + 7, Blocks.LANTERN.defaultBlockState());

        // лифт вниз (в wonderflowers)
        set(level, x + 4, y + 1, z + 5, ModBlocks.ELEVATOR_BLOCK.get().defaultBlockState());
        set(level, x + 5, y + 1, z + 5, ModBlocks.ELEVATOR_BLOCK.get().defaultBlockState());
    }

    private static void setLinkPortalDest(final Level level, final int x, final int y, final int z,
                                          final ResourceKey<Level> destDim,
                                          final double dx, final double dy, final double dz) {
        final BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
        if (be instanceof LinkPortalBlockEntity lp) {
            lp.setDestination(destDim, dx, dy, dz);
        }
    }

    /* ---------- wonderland: дом из гигантских блоков с книгой ---------- */

    public static void placeWonderlandHouse(final ServerLevel level, final BlockPos near) {
        final int x = near.getX();
        final int y = near.getY() + 2;
        final int z = near.getZ();

        final BlockState diamond = ModBlocks.GIANT_DIAMOND_BLOCK.get().defaultBlockState();
        final BlockState cobble = ModBlocks.GIANT_COBBLESTONE_BLOCK.get().defaultBlockState();
        final BlockState table = ModBlocks.GIANT_CRAFTING_TABLE_BLOCK.get().defaultBlockState();
        final BlockState air = Blocks.AIR.defaultBlockState();

        hollowBox(level, x, y, z, x + 8, y + 6, z + 8, diamond, air);
        fill(level, x, y + 6, z, x + 8, y + 6, z + 8, table); // крыша из верстаков
        fill(level, x, y - 1, z, x + 8, y - 1, z + 8, cobble); // фундамент

        // дверной проём
        fill(level, x + 4, y, z, x + 4, y + 2, z, air);

        // сундук с запиской
        set(level, x + 4, y + 1, z + 3, Blocks.CHEST.defaultBlockState());
        final BlockEntity be = level.getBlockEntity(new BlockPos(x + 4, y + 1, z + 3));
        if (be instanceof ChestBlockEntity chest) {
            chest.setLootTable(new ResourceLocation(FindariesMod.MODID, "chests/wonderland_house"),
                    level.random.nextLong());
        }

        // искажённые гигантские деревья старого майнкрафта вокруг дома
        for (int i = 0; i < 6; i++) {
            int tx = x + level.random.nextInt(11) - 5;
            int tz = z + level.random.nextInt(11) - 5;
            placeGiantTree(level, tx, surfaceY(level, tx, tz), tz);
        }
    }

    /* ---------- wonderflowers: прибытие (дорога, река, мост, остров) ---------- */

    public static void placeWonderflowersArrival(final ServerLevel level, final BlockPos near) {
        final int x = near.getX();
        final int y = near.getY();
        final int z = near.getZ();

        // стартовая платформа
        fill(level, x - 3, y - 1, z - 3, x + 3, y - 1, z + 3, Blocks.GRASS_BLOCK.defaultBlockState());

        // прямая тропинка
        for (int i = 0; i <= 40; i++) {
            set(level, x + i, y, z, Blocks.WHITE_CONCRETE.defaultBlockState());
            set(level, x + i, y + 1, z, Blocks.WHITE_CONCRETE.defaultBlockState());
        }

        // гиганские маки и одуванчики по краям тропинки
        for (int i = 0; i <= 40; i += 3) {
            for (int side : new int[]{-3, 3}) {
                if (level.random.nextInt(3) == 0) {
                    set(level, x + i + side, y, z, ModBlocks.GIANT_POPPY_BLOCK.get().defaultBlockState());
                }
                if (level.random.nextInt(4) == 0) {
                    set(level, x + i + side, y, z, ModBlocks.GIANT_DANDELION_BLOCK.get().defaultBlockState());
                }
            }
        }

        // река через тропинку (x+14..x+19)
        for (int i = 14; i <= 19; i++) {
            for (int dz = -1; dz <= 1; dz++) {
                set(level, x + i, y - 1, z + dz, Blocks.WATER.defaultBlockState());
                set(level, x + i, y, z + dz, Blocks.WATER.defaultBlockState());
            }
        }

        // мост из цветных блоков ("дума")
        for (int i = 15; i <= 18; i++) {
            for (int dz = -1; dz <= 1; dz++) {
                set(level, x + i, y, z + dz, Blocks.WHITE_CONCRETE.defaultBlockState());
            }
        }

        // остров в конце тропинки с порталом из разноцветных точек
        final int ox = x + 42;
        fill(level, ox - 3, y - 1, z - 3, ox + 3, y - 1, z + 3, Blocks.GRASS_BLOCK.defaultBlockState());
        fill(level, ox - 2, y, z - 2, ox + 2, y, z + 2, Blocks.YELLOW_CONCRETE.defaultBlockState());
        set(level, ox, y, z, ModBlocks.COLORFUL_DOTS_PORTAL_BLOCK.get().defaultBlockState());
        for (int i = 0; i < 8; i++) {
            double a = 2 * Math.PI * i / 8;
            set(level, ox + (int) (2 * Math.cos(a)), y, z + (int) (2 * Math.sin(a)),
                switch (i) {
                    case 0, 4 -> Blocks.RED_CONCRETE.defaultBlockState();
                    case 1, 5 -> Blocks.BLUE_CONCRETE.defaultBlockState();
                    case 2, 6 -> Blocks.GREEN_CONCRETE.defaultBlockState();
                    default -> Blocks.MAGENTA_CONCRETE.defaultBlockState();
                });
        }
    }

    /* ---------- cage_wonderland: стены, деревья, вода, выход ---------- */

    public static void placeCageWonderlandSpawn(final ServerLevel level, final BlockPos near) {
        final int x = near.getX();
        final int y = near.getY();
        final int z = near.getZ();

        // стены из дерева по периметру
        final BlockState wood = Blocks.OAK_LOG.defaultBlockState();
        final BlockState air = Blocks.AIR.defaultBlockState();
        hollowBox(level, x - 15, y, z - 15, x + 15, y + 12, z + 15, wood, air);

        // гигантские деревья
        for (int i = 0; i < 12; i++) {
            int tx = x + (level.random.nextInt(26) - 13);
            int tz = z + (level.random.nextInt(26) - 13);
            int ty = surfaceY(level, tx, tz);
            placeGiantTree(level, tx, ty, tz);
        }

        // водоём
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                if (dx * dx + dz * dz <= 25) {
                    set(level, x + dx, y - 1, z + dz, Blocks.WATER.defaultBlockState());
                }
            }
        }

        // дырка в стене с белым порталом (выход домой)
        set(level, x - 15, y + 1, z, Blocks.AIR.defaultBlockState());
        set(level, x - 15, y + 2, z, Blocks.AIR.defaultBlockState());
        set(level, x - 15, y + 1, z, ModBlocks.WHITE_PORTAL_BLOCK.get().defaultBlockState());
        set(level, x - 15, y + 2, z, ModBlocks.WHITE_PORTAL_BLOCK.get().defaultBlockState());
    }

    private static void placeGiantTree(final Level level, final int x, final int y, final int z) {
        final BlockState log = Blocks.OAK_LOG.defaultBlockState();
        final BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState();
        int h = 14 + level.random.nextInt(14);
        for (int i = 0; i < h; i++) {
            set(level, x, y + i, z, log);
        }
        for (int dy = -3; dy <= 2; dy++) {
            int r = 5 - Math.abs(dy);
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (level.random.nextInt(3) != 0) {
                        set(level, x + dx, y + h - 2 + dy, z + dz, leaves);
                    }
                }
            }
        }
    }
}
