package com.findaries.horror.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LongFigureEntity extends Monster {
    private int lifetime = 20 * 90;
    public LongFigureEntity(EntityType<? extends Monster> type, Level level) { super(type, level); this.setPersistenceRequired(); }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 50).add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 5).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.12, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.65));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 48));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    @Override public void tick() {
        super.tick();
        if (!level().isClientSide && --lifetime <= 0) discard();
        if (!level().isClientSide && tickCount == 1) {
            if (getType() == HorrorEntities.LONG_FIGURE.get()) setCustomName(Component.literal("kill000145236"));
            if (getType() == HorrorEntities.DANGER_12.get()) setCustomName(Component.literal("danger_12"));
        }
    }
    @Override public boolean isPushable() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
}
