package com.findaries.horror.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
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
 * Синий незнакомец (image-6). Темно-синяя фигура, появляется с водным звуком.
 * При приближении исчезает, оставляя вместо себя воду.
 */
public class BlueStrangerEntity extends Monster {
    private boolean soundPlayed;

    public BlueStrangerEntity(final EntityType<? extends Monster> type, final Level level) {
        super(type, level);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.3D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        if (!this.soundPlayed) {
            this.soundPlayed = true;
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_WATER_AMBIENT, SoundSource.HOSTILE, 1.2F, 1.0F);
        }
        final Optional<Player> player = this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 2.5D);
        if (player.isPresent()) {
            // Вместо себя ставит воду.
            this.level.setBlockAndUpdate(this.blockPosition(), Blocks.WATER.defaultBlockState());
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_WATER_AMBIENT, SoundSource.HOSTILE, 1.5F, 0.7F);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean canSpawn(final ServerLevelAccessor level, final MobSpawnType spawnType) {
        return true;
    }

    @Override
    public void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("SoundPlayed", this.soundPlayed);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        this.soundPlayed = tag.getBoolean("SoundPlayed");
    }
}
