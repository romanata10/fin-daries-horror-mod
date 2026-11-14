package com.findaries.horror.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RisingThingEntity extends Entity {
    private int lifeTicks;

    public RisingThingEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        this.lifeTicks++;

        // Поднимается вверх.
        this.move(MoverType.SELF, 0, 0.3D, 0);
        this.setDeltaMovement(0, 0.3D, 0);

        // Желтые точки вокруг.
        if (!this.level.isClientSide && this.random.nextInt(4) == 0) {
            ((net.minecraft.server.level.ServerLevel) this.level).sendParticles(
                    new DustParticleOptions(1.0F, 1.0F, 0.0F, 0.8F), true,
                    this.getX(), this.getY() + 1.0, this.getZ(), 2, 0.1, 0.1, 0.1, 0.05);
        }

        if (this.lifeTicks > 260) {
            this.leavePillar();
            this.remove(RemovalReason.KILLED);
        }
    }

    private void leavePillar() {
        if (this.level.isClientSide) {
            return;
        }
        int bx = (int) this.getX();
        int by = (int) this.getY();
        int bz = (int) this.getZ();
        int maxY = this.level.getBuildHeight() - 3;
        int height = 50;
        if (by + height > maxY) {
            height = maxY - by;
        }
        if (height < 1) {
            return;
        }
        BlockState pillar = Blocks.COBBLESTONE.defaultBlockState();
        for (int i = 1; i <= height; i++) {
            this.level.setBlockAndUpdate(new BlockPos(bx, by + i, bz), pillar);
        }
    }

    @Override
    public boolean shouldRenderAtSqr(final double distance) {
        return distance < 4096.0;
    }

    @Override
    public void saveAdditional(final CompoundTag tag) { super.saveAdditional(tag); }
    @Override
    public void load(final CompoundTag tag) { super.load(tag); }
}
