# Faster Block Placement (Forge 1.16.5)

Port of the 1.20.x mod to Minecraft 1.16.5 / Forge 36.2.39. Removes the 4-tick right-click delay.
Toggle in game with **G** (rebindable in Controls).

## Build
Requires JDK 8 (or 8-17 with Gradle toolchains).

    ./gradlew build        (Windows: gradlew.bat build)

The jar appears in `build/libs/fasterblockplacement-1.0.jar`.

Changes from 1.20.x: no mixin (the delay field is reset every client tick via reflection),
1.16.5 class names/APIs, Java 8, pack_format 6.

## WARNING
Using this mod on public servers with anti-cheats can get you banned.
