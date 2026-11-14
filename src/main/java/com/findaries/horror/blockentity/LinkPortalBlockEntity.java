package com.findaries.horror.blockentity;

import com.findaries.horror.block.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LinkPortalBlockEntity extends BlockEntity {
    private ResourceKey<Level> destDim = Level.OVERWORLD;
    private double destX;
    private double destY;
    private double destZ;

    public LinkPortalBlockEntity(final BlockPos pos, final BlockState state) {
        super(ModBlocks.LINK_PORTAL_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public void setDestination(final ResourceKey<Level> dim, final double x, final double y, final double z) {
        this.destDim = dim;
        this.destX = x;
        this.destY = y;
        this.destZ = z;
        setChanged();
    }

    public void teleport(final ServerPlayer player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        ModBlocks.teleportPlayer(player, destDim, destX, destY, destZ);
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("DestDim", destDim.location().toString());
        tag.putDouble("DestX", destX);
        tag.putDouble("DestY", destY);
        tag.putDouble("DestZ", destZ);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        this.destDim = ResourceKey.create(Registry.DIMENSION_REGISTRY,
                new ResourceLocation(tag.getString("DestDim")));
        this.destX = tag.getDouble("DestX");
        this.destY = tag.getDouble("DestY");
        this.destZ = tag.getDouble("DestZ");
    }
}
