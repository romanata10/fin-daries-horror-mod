# Fin Daries Horror — Forge 1.20.1

A first playable version of a Fin Daries-inspired horror mod for Minecraft Java 1.20.1 / Forge 47.2.x.

## Build

Use JDK 17 and Gradle 8.x (ForgeGradle 6 downloads the Forge toolchain/dependencies):

```sh
gradle build
```

The distributable JAR is written to `build/libs/`. Install Forge 1.20.1, then put the JAR in the instance's `mods` folder. This repository does not include a Gradle wrapper.

## In-game content

- Unpredictable ambient events in the Overworld: a huge moving black triangle and other sky silhouette, a colossal saucer, a fleeting 50-block cobblestone column, a cave-sound mushroom circle with temporary giant fungi, uncanny forest sightings, and rare portal houses/gates.
- `kill000145236` is rendered with the supplied faceless Steve skin as a roughly five-block-tall figure. Approaching it too closely disconnects the player; the encounter expires afterward. The dark `danger_12` figure pursues players in Wonderflowers. Wonderland also has a watcher that vanishes into a dandelion when approached. The blue walker vanishes into a water source when approached.
- Wonderland, Wonderflowers, and Cage Wonderland are implemented as isolated, themed regions in the Overworld, linked by gates. This avoids adding custom dimension registries during save loading. The cage region has an exit gate and grants the **Alive** advancement; Wonderland shows its altered version string and contains giant flowers, a block-built house, and the requested chest note.

The scary events are intentionally built from Minecraft particles, vanilla sounds, blocks, and the provided skin so no external asset pack is needed. They are atmospheric sightings rather than video-perfect animated models. Back up worlds before testing: the mushroom event removes only its own temporary mushrooms, while the 50-block cobblestone sighting is deliberately left behind.
