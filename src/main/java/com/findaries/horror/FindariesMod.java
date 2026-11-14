package com.findaries.horror;

import com.findaries.horror.client.ModClient;
import com.findaries.horror.event.HorrorEventManager;
import com.findaries.horror.registration.Registration;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Fin Diaries Horror — 공포/хоррор мод для Minecraft 1.20.1 (Forge)
 * по мотивам канала Fin Diaries (@findaries8589).
 *
 * Особенности:
 *  - гигантский черный треугольник в небе со звуком треска/визга,
 *  - безликий Стив (длинноногий, 5 блоков высотой) в лесах,
 *  - порталы в измерение wonderland.avi, wonderflowers и cage_wonderland,
 *  - случайные аномалии: грибы на ногах, поднимающийся столб, синий незнакомец,
 *    НЛО, странные объекты в небе,
 *  - сущность-наблюдатель в wonderland, сущность danger_12 в wonderflowers.
 */
@Mod(FindariesMod.MODID)
public class FindariesMod {
    public static final String MODID = "findaries";
    public static final String NAME = "Fin Diaries Horror";
    public static final String VERSION = "@version@";

    public FindariesMod() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Регистрация объектов (блоки, предметы, сущности, звуки, блочные сущности)
        Registration.register(modBus);

        // Общая инициализация
        modBus.addListener(this::commonSetup);

        // Игровые события (сервер + общие)
        MinecraftForge.EVENT_BUS.register(new HorrorEventManager());

        // Клиентская часть (рендеры сущностей, оверлеи, отключение игрока)
        DistExecutor.safeRunForDist(Dist.CLIENT, () -> () -> ModClient.init(modBus, MinecraftForge.EVENT_BUS));

        FindariesMod.LOGGER.info("Fin Diaries Horror mod initializing...");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Измерения регистрируются через данные (data/findaries/dimension_type, worldgen/level_stem),
            // дополнительная работа на стороне сервера не требуется.
        });
    }

    private static final org.apache.logging.log4j.Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger();
}
