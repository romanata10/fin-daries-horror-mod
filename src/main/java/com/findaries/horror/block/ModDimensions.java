package com.findaries.horror.block;

import com.findaries.horror.FindariesMod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * Ключи измерений мода. Сами измерения регистрируются через данные:
 * data/findaries/dimension_type/*.json  и  data/findaries/worldgen/level_stem/*.json
 */
public class ModDimensions {
    public static final ResourceKey<Level> WONDERLAND =
            ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(FindariesMod.MODID, "wonderland"));
    public static final ResourceKey<Level> WONDERFLOWERS =
            ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(FindariesMod.MODID, "wonderflowers"));
    public static final ResourceKey<Level> CAGE_WONDERLAND =
            ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(FindariesMod.MODID, "cage_wonderland"));
}
