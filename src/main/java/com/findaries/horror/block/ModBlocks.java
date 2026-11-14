package com.findaries.horror.block;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.blockentity.ElevatorBlockEntity;
import com.findaries.horror.blockentity.LinkPortalBlockEntity;
import com.findaries.horror.registration.Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockBehaviour;
import net.minecraft.world.level.block.BlockEntityType;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

/**
 * Все блоки мода Fin Diaries Horror.
 */
public class ModBlocks {
    public static final Supplier<Block> GIANT_DIAMOND_BLOCK =
            Registration.BLOCKS.register("giant_diamond_block",
                    () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND)
                            .strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Supplier<Block> GIANT_COBBLESTONE_BLOCK =
            Registration.BLOCKS.register("giant_cobblestone_block",
                    () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                            .strength(3.0F, 4.0F).requiresCorrectToolForDrops()));
    public static final Supplier<Block> GIANT_CRAFTING_TABLE_BLOCK =
            Registration.BLOCKS.register("giant_crafting_table_block",
                    () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                            .strength(2.5F, 3.0F).noOcclusion()));
    public static final Supplier<Block> GIANT_POPPY_BLOCK =
            Registration.BLOCKS.register("giant_poppy",
                    () -> new FlowerBlock(BlockBehaviour.Properties.of().noCollission()
                            .offsetType(BlockBehaviour.OffsetType.XZ).randomTicks()));
    public static final Supplier<Block> GIANT_DANDELION_BLOCK =
            Registration.BLOCKS.register("giant_dandelion",
                    () -> new FlowerBlock(BlockBehaviour.Properties.of().noCollission()
                            .offsetType(BlockBehaviour.OffsetType.XZ).randomTicks()));

    // Порталы между измерениями
    public static final Supplier<Block> WONDER_PORTAL_BLOCK =
            Registration.BLOCKS.register("wonder_portal",
                    () -> new WonderPortalBlock(BlockBehaviour.Properties.of().noCollission().lightLevel(12),
                            ModDimensions.WONDERLAND, "findaries:portal_wonder_cooldown"));
    public static final Supplier<Block> COLORFUL_DOTS_PORTAL_BLOCK =
            Registration.BLOCKS.register("colorful_dots_portal",
                    () -> new ColorfulDotsPortalBlock(BlockBehaviour.Properties.of().noCollission().lightLevel(10),
                            ModDimensions.CAGE_WONDERLAND, "findaries:portal_cage_cooldown"));
    public static final Supplier<Block> WHITE_PORTAL_BLOCK =
            Registration.BLOCKS.register("white_portal",
                    () -> new WhitePortalBlock(BlockBehaviour.Properties.of().noCollission().lightLevel(15),
                            Level.OVERWORLD, "findaries:portal_white_cooldown"));

    // Связывающий портал (домик <-> коридор в одном измерении)
    public static final Supplier<Block> LINK_PORTAL_BLOCK =
            Registration.BLOCKS.register("link_portal",
                    () -> new LinkPortalBlock(BlockBehaviour.Properties.of().noCollission().lightLevel(11)));

    // Лифт в подземелье wonderflowers
    public static final Supplier<Block> ELEVATOR_BLOCK =
            Registration.BLOCKS.register("elevator",
                    () -> new ElevatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .strength(4.0F, 5.0F)));

    // Блочные сущности
    public static final Supplier<BlockEntityType<?>> ELEVATOR_BLOCK_ENTITY =
            Registration.BLOCK_ENTITIES.register("elevator",
                    () -> BlockEntityType.Builder.of(ElevatorBlockEntity::new, ELEVATOR_BLOCK.get()).build(null));
    public static final Supplier<BlockEntityType<?>> LINK_PORTAL_BLOCK_ENTITY =
            Registration.BLOCK_ENTITIES.register("link_portal",
                    () -> BlockEntityType.Builder.of(LinkPortalBlockEntity::new, LINK_PORTAL_BLOCK.get()).build(null));

    /* ---------- вспомогательная телепортация ---------- */

    /**
     * Телепортировать игрока в измерение (серверная сторона).
     * Если уходим из верхнего мира, запоминаем точку возврата для белого портала.
     */
    public static void teleportPlayer(final ServerPlayer player, final ResourceKey<Level> target,
                                      final double x, final double y, final double z) {
        final Level origin = player.level();
        if (!(origin instanceof ServerLevel serverOrigin)) {
            return;
        }
        if (origin.dimension() == Level.OVERWORLD) {
            final CompoundTag data = player.getPersistentData();
            data.putDouble("findaries:return_x", player.getX());
            data.putDouble("findaries:return_y", player.getY());
            data.putDouble("findaries:return_z", player.getZ());
        }
        serverOrigin.getServer().getPlayerList()
                .broadcastAll(Component.literal(player.getName().getString() + " исчез в аномалии..."));
        player.changeDimension(target);
        player.moveTo(x, y, z, player.getYRot(), player.getXRot());
    }

    /**
     * Вернуть игрока в последнюю сохраненную точку верхнего мира (или на спавн).
     * Выдача достижения "alive" настраивается через триггер изменения измерения
     * (data/findaries/advancements/alive.json).
     */
    public static void returnToOverworld(final ServerPlayer player) {
        final CompoundTag data = player.getPersistentData();
        double x = data.getDouble("findaries:return_x");
        double y = data.getDouble("findaries:return_y");
        double z = data.getDouble("findaries:return_z");
        final ServerLevel overworld = player.serverLevel().getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }
        if (x == 0 && y == 0 && z == 0) {
            final BlockPos spawn = overworld.getSharedSpawnPos();
            x = spawn.getX() + 0.5;
            y = spawn.getY() + 1;
            z = spawn.getZ() + 0.5;
        } else {
            int yy = overworld.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
            y = yy + 1;
        }
        player.changeDimension(overworld);
        player.moveTo(x, y, z, player.getYRot(), player.getXRot());
        data.remove("findaries:return_x");
        data.remove("findaries:return_y");
        data.remove("findaries:return_z");
    }

    /* ---------- порталы ---------- */

    public static class WonderPortalBlock extends Block {
        private final ResourceKey<Level> target;
        private final String cooldownKey;

        protected WonderPortalBlock(final BlockBehaviour.Properties props,
                                    final ResourceKey<Level> target, final String cooldownKey) {
            super(props);
            this.target = target;
            this.cooldownKey = cooldownKey;
        }

        @Override
        public void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (level.isClientSide || !(entity instanceof Player player)) {
                return;
            }
            final long now = level.getGameTime();
            final long last = player.getPersistentData().getLong(cooldownKey);
            if (now - last < 20) {
                return;
            }
            player.getPersistentData().putLong(cooldownKey, now);
            int yy = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
            teleportPlayer((ServerPlayer) player, target, pos.getX() + 0.5, yy + 2, pos.getZ() + 0.5);
        }
    }

    public static class ColorfulDotsPortalBlock extends WonderPortalBlock {
        protected ColorfulDotsPortalBlock(final BlockBehaviour.Properties props,
                                          final ResourceKey<Level> target, final String cooldownKey) {
            super(props, target, cooldownKey);
        }
    }

    public static class WhitePortalBlock extends WonderPortalBlock {
        protected WhitePortalBlock(final BlockBehaviour.Properties props,
                                   final ResourceKey<Level> target, final String cooldownKey) {
            super(props, target, cooldownKey);
        }

        @Override
        public void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (level.isClientSide || !(entity instanceof Player player)) {
                return;
            }
            returnToOverworld((ServerPlayer) player);
        }
    }

    public static class LinkPortalBlock extends Block {
        public LinkPortalBlock(final BlockBehaviour.Properties props,
                               final DeferredBlockEntityHolder holder) {
            super(props);
            holder.register(this);
        }

        public interface DeferredBlockEntityHolder {
            void register(Block block);
        }
    }
}
