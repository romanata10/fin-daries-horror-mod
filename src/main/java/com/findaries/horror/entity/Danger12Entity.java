package com.findaries.horror.entity;

import com.findaries.horror.block.ModBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Optional;

/**
 * danger_12 (image-2). Обитает в wonderflowers, преследует игрока.
 * При касании — выкидывает игрока из мира (отключение с возвратом в меню).
 */
public class Danger12Entity extends Monster {
    public Danger12Entity(final EntityType<? extends Monster> type, final Level level) {
        super(type, level);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.42D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) {
            return;
        }
        final Optional<Player> victim = this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 1.2D);
        if (victim.isPresent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_PLAYER_BURP, SoundSource.HOSTILE, 1.0F, 1.0F);
            final net.minecraft.server.level.ServerPlayer serverPlayer =
                    (net.minecraft.server.level.ServerPlayer) victim.get();
            serverPlayer.connection.disconnect(Component.literal("danger_12 поймал тебя. Мир закрыт."));
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean canSpawn(final ServerLevelAccessor level, final net.minecraft.world.entity.MobSpawnType spawnType) {
        return true;
    }
}
