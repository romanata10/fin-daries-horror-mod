package com.findaries.horror.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Optional;

/**
 * Безликий Стив — длинноногий (5 блоков высотой). Появляется в лесах за деревьями.
 * Если игрок подходит слишком близко — игрок отключается с возвратом в главное меню
 * (обработка в HorrorEventManager).
 */
public class FacelessSteveEntity extends Monster {
    private int despawnTimer;

    public FacelessSteveEntity(final EntityType<? extends Monster> type, final Level level) {
        super(type, level);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) {
            return;
        }
        this.despawnTimer++;
        if (this.despawnTimer > 6000) {
            this.remove(RemovalReason.KILLED); // исчезает примерно через 5 минут
        }
    }

    @Override
    public boolean canSpawn(final ServerLevelAccessor level, final MobSpawnType spawnType) {
        return true;
    }

    @Override
    public void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("DespawnTimer", this.despawnTimer);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        this.despawnTimer = tag.getInt("DespawnTimer");
    }

    /** Ближайший игрок в радиусе 2 блоков — сигнал для отключения. */
    public boolean isPlayerTooClose() {
        final Optional<Player> victim = this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 2.0D);
        return victim.isPresent();
    }
}
