# AuthHide

A project is build-ready with Fabric Loom.

## Build

1. Install JDK 21.
2. Open this folder in IntelliJ IDEA and let Gradle import finish.
3. Run the Gradle task `build`.

The built mod jar will appear in `build/libs/`.

## Notes

- Minecraft target: 1.21.11
- Client-only mod
- Command: `/authhide`

- Build Issues

If the mod fails to build, try the following steps:

# Clean and rebuild the project
gradlew clean build

# Refresh dependencies and rebuild
gradlew --refresh-dependencies build

Make sure you are using the correct Java version and compatible Minecraft/Fabric/Forge versions.
