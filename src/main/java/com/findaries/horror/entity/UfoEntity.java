package com.findaries.horror.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class UfoEntity extends Entity {
    private int lifeTicks;

    public UfoEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    public void launch(final Vec3 movement) {
        this.setDeltaMovement(movement);
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.lifeTicks++;
        if (this.lifeTicks > 1200) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean shouldRenderAtSqr(final double distance) {
        return distance < 16384.0;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
