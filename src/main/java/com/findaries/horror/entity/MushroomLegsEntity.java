package com.findaries.horror.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

/**
 * Гриб на ногах (image-4). Появляется резко, стоит недолго, вокруг игрока возникает
 * кольцо из грибов со звуком пещеры, затем гриб исчезает.
 */
public class MushroomLegsEntity extends Entity {
    private int lifeTicks;

    public MushroomLegsEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        this.lifeTicks++;
        if (this.lifeTicks == 60) {
            this.spawnMushroomRing();
            this.remove(RemovalReason.KILLED);
        } else if (this.lifeTicks > 400) {
            this.remove(RemovalReason.KILLED);
        }
    }

    private void spawnMushroomRing() {
        final Optional<Player> player = this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 64.0D);
        if (player.isEmpty()) {
            return;
        }
        final Player p = player.get();
        final int centerX = (int) p.getX();
        final int centerZ = (int) p.getZ();
        final int y = (int) p.getY();
        final int radius = 6;
        for (int i = 0; i < 16; i++) {
            double angle = 2.0 * Math.PI * i / 16.0;
            int bx = centerX + (int) (radius * Math.cos(angle));
            int bz = centerZ + (int) (radius * Math.sin(angle));
            BlockPos pos = new BlockPos(bx, y, bz);
            net.minecraft.world.level.block.state.BlockState state = this.level.getBlockState(pos);
            if (state.isAir() || state.canBeReplaced()) {
                this.level.setBlockAndUpdate(pos, (this.random.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM).defaultBlockState());
            }
        }
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.AMBIENT_CAVE, SoundSource.HOSTILE, 1.5F, 1.0F);
    }

    @Override
    public boolean canSpawn(final ServerLevelAccessor level, final MobSpawnType spawnType) {
        return true;
    }

    @Override
    public void saveAdditional(final CompoundTag tag) { super.saveAdditional(tag); }
    @Override
    public void load(final CompoundTag tag) { super.load(tag); }
}
