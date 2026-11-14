package com.findaries.horror.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Наблюдатель (image-1). Живет в wonderland, стоит и смотрит на игрока.
 * При приближении исчезает, оставляя одуванчик.
 */
public class WatcherEntity extends Monster {
    public WatcherEntity(final EntityType<? extends Monster> type, final Level level) {
        super(type, level);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) {
            return;
        }
        final Optional<Player> player = this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 3.0D);
        if (player.isPresent()) {
            // Исчезает, оставляя одуванчик.
            final BlockPos here = this.blockPosition();
            this.level.setBlockAndUpdate(here, Blocks.DANDELION.defaultBlockState());
            this.level.playSound(null, here.getX(), here.getY(), here.getZ(),
                    SoundEvents.AMBIENT_CAVE, SoundSource.HOSTILE, 1.0F, 0.8F);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean canSpawn(final ServerLevelAccessor level, final net.minecraft.world.entity.MobSpawnType spawnType) {
        return true;
    }

    @Override
    public void saveAdditional(final CompoundTag tag) { super.saveAdditional(tag); }
    @Override
    public void load(final CompoundTag tag) { super.load(tag); }
}
