package com.findaries.horror.entity;

import com.findaries.horror.FindariesMod;
import com.findaries.horror.registration.Registration;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.entity.SpawnPlacements;

import java.util.function.Supplier;

/**
 * Все сущности мода Fin Diaries Horror.
 */
public class ModEntities {
    public static final Supplier<EntityType<GiantTriangleEntity>> GIANT_TRIANGLE =
            register("giant_triangle", GiantTriangleEntity::new, MobCategory.MISC, 30.0F, 2.0F);
    public static final Supplier<EntityType<FacelessSteveEntity>> FACELESS_STEVE =
            register("faceless_steve", FacelessSteveEntity::new, MobCategory.MONSTER, 0.6F, 5.0F);
    public static final Supplier<EntityType<WatcherEntity>> WATCHER =
            register("watcher", WatcherEntity::new, MobCategory.MONSTER, 0.6F, 2.9F);
    public static final Supplier<EntityType<Danger12Entity>> DANGER_12 =
            register("danger_12", Danger12Entity::new, MobCategory.MONSTER, 0.6F, 2.9F);
    public static final Supplier<EntityType<BlueStrangerEntity>> BLUE_STRANGER =
            register("blue_stranger", BlueStrangerEntity::new, MobCategory.CREATURE, 0.6F, 1.9F);
    public static final Supplier<EntityType<MushroomLegsEntity>> MUSHROOM_LEGS =
            register("mushroom_legs", MushroomLegsEntity::new, MobCategory.CREATURE, 0.8F, 2.2F);
    public static final Supplier<EntityType<RisingThingEntity>> RISING_THING =
            register("rising_thing", RisingThingEntity::new, MobCategory.MISC, 1.5F, 4.0F);
    public static final Supplier<EntityType<UfoEntity>> UFO =
            register("ufo", UfoEntity::new, MobCategory.MISC, 6.0F, 2.0F);
    public static final Supplier<EntityType<SkyThingEntity>> SKY_THING =
            register("sky_thing", SkyThingEntity::new, MobCategory.MISC, 4.0F, 2.0F);

    private static <T extends net.minecraft.world.entity.Entity> Supplier<EntityType<T>> register(
            final String name, final EntityType.EntityFactory<T> factory, final MobCategory category,
            final float width, final float height) {
        final Supplier<EntityType<T>> type = Registration.ENTITIES.register(name,
                () -> EntityType.Builder.<T>of(factory, category).sized(width, height).build(FindariesMod.MODID + ":" + name));
        // Спавн только по земле (если кому-то понадобится естественный спавн).
        type.get(); // initialize
        return type;
    }
}
