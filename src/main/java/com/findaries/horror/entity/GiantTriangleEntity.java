package com.findaries.horror.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GiantTriangleEntity extends Entity {
    private int lifeTicks;

    public GiantTriangleEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    public void launch(final Vec3 movement) {
        this.setDeltaMovement(movement);
        this.setYRot((float) (Math.atan2(movement.z, movement.x) * 180.0 / Math.PI) - 90.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 1.0, 0.98));
        this.lifeTicks++;
        if (this.lifeTicks > 900) {
            this.remove(RemovalReason.KILLED);
        }
        if (!this.level.isClientSide && this.random.nextInt(20) == 0) {
            ((net.minecraft.server.level.ServerLevel) this.level).sendParticles(
                    new DustParticleOptions(1.0F, 1.0F, 1.0F, 1.0F), true,
                    this.getX(), this.getY() + 1.0, this.getZ(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean shouldRenderAtSqr(final double distance) {
        return distance < 4096.0;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
