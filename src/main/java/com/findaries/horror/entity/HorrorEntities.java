package com.findaries.horror.entity;

import com.findaries.horror.FindariesHorror;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = FindariesHorror.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class HorrorEntities {
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FindariesHorror.MOD_ID);
    public static final RegistryObject<EntityType<LongFigureEntity>> LONG_FIGURE = TYPES.register("long_figure",
            () -> EntityType.Builder.<LongFigureEntity>of(LongFigureEntity::new, MobCategory.MONSTER).sized(0.8F, 5.0F).clientTrackingRange(96).updateInterval(2).build("long_figure"));
    public static final RegistryObject<EntityType<LongFigureEntity>> DANGER_12 = TYPES.register("danger_12",
            () -> EntityType.Builder.<LongFigureEntity>of(LongFigureEntity::new, MobCategory.MONSTER).sized(1.1F, 3.8F).clientTrackingRange(96).updateInterval(2).build("danger_12"));
    public static final RegistryObject<EntityType<LongFigureEntity>> BLUE_WALKER = TYPES.register("blue_walker",
            () -> EntityType.Builder.<LongFigureEntity>of(LongFigureEntity::new, MobCategory.MONSTER).sized(0.9F, 3.2F).clientTrackingRange(96).updateInterval(2).build("blue_walker"));
    public static final RegistryObject<EntityType<LongFigureEntity>> WONDER_WATCHER = TYPES.register("wonder_watcher",
            () -> EntityType.Builder.<LongFigureEntity>of(LongFigureEntity::new, MobCategory.MONSTER).sized(0.8F, 3.5F).clientTrackingRange(96).updateInterval(2).build("wonder_watcher"));

    @SubscribeEvent public static void attributes(EntityAttributeCreationEvent event) {
        event.put(LONG_FIGURE.get(), LongFigureEntity.createAttributes().build());
        event.put(DANGER_12.get(), LongFigureEntity.createAttributes().build());
        event.put(BLUE_WALKER.get(), LongFigureEntity.createAttributes().build());
        event.put(WONDER_WATCHER.get(), LongFigureEntity.createAttributes().build());
    }
    private HorrorEntities() {}
}
