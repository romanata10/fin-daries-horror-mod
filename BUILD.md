# Сборка мода (Fin Diaries Horror, Forge 1.20.1)

Этот репозиторий содержит **исходный код мода**. Скомпилировать его в текущем песочнице нельзя (нет JDK 17 и доступа к Maven-артефактам Forge). Собирайте на своей машине.

## Требования

- JDK 17
- Gradle (или ./gradlew, если добавите обёртку)
- Доступ к `https://maven.minecraftforge.net` и `repo.maven.apache.org`

## Шаги

1. Скопируйте содержимое репозитория в пустую папку мода.
2. Запустите `./gradlew setupDecompWorkspace` (или `gradle setupDecompWorkspace`) — ForgeGradle скачает Minecraft 1.20.1 и Forge 47.1.63.
3. `./gradlew build` — соберёт `build/libs/fin-daries-horror-mod-1.20.1-1.0.0.jar`.
4. `.jar` кладите в `mods/` вместе с Forge 1.20.1 на клиент и сервер.

build.gradle уже настроен на `net.minecraftforge:forge:1.20.1-47.1.63` и `mappings channel: 'official', version: '1.20.1'`.

## Помеченные моменты (проверьте при сборке)

Код писался по API Forge 1.20.1 из памяти; при первой компиляции возможно придётся подправить nomes классов/полей:

- `ForgeRegistries.BLOCKS / ITEMS / ENTITY_TYPES / SOUND_EVENTS / BLOCK_ENTITIES` (регистрация через DeferredRegister).
- `EntityType.Builder.of(factory, category).sized(w, h).build("mod:id:name")`.
- `HumanoidModel(float scale, boolean smallArms, int texW, int texH)` — конструктор гуманоидной модели.
- `LayerDefinition.create(mesh, scale).bakeToRoot()` — сборка коробочной модели.
- `ModelPartBuilder.create().uv().addBox()...` — геометрия модели.
- `MobEntityRenderer(EntityRenderDispatcher, EntityModel<T>, float shadowRadius)`.
- `RenderType.entitySolid(ResourceLocation)`, `OverlayTexture.NO_OVERLAY`.
- `ServerPlayer.connection.disconnect(Component)`, `Minecraft.disconnect(Component)`.
- `ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(...))` для ключей измерений.
- `Level.OVERWORLD` как `ResourceKey<Level>`.
- `ChestBlockEntity.setLootTable(ResourceLocation, long)`.
- Биомы/измерения заданы через данные (`data/findaries/worldgen/biome`, `dimension_type`, `worldgen/level_stem`) — формат JSON для 1.20.1.

## Дизайн мода (кратко)

Все измерения (wonderland, wonderflowers, cage_wonderland) регистрируются дата-файлами. Структуры (дома, коридор, лифт, порталы) расставляются кодом при первом попадании игрока в измерение. Случайные события (треугольник, безликий Стив, грибы на ногах, НЛО и т.д.) управляются `HorrorEventManager` с откатами на игрока в `PersistentData`.

Текстуры сущностей — из вложений запроса ( steve без лица, image-1..image-7 ).
