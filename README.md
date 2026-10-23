# AuthHide

Client-only Fabric mod that hides password commands (`/login`, `/register`, ...) from the chat history and masks password input with `*` characters, both in chat and in login GUIs (e.g. AuthMe).

Downloads are on the [Releases](../../releases) page.

## Commands

- `/authhide` - toggle password hiding on/off
- `/authhide spaces` - toggle whether spaces stay visible when masking (default: spaces stay visible)
- `/authhide command <add|remove|list|clear> [command]` - manage custom commands that should be hidden

The settings screen is also available through Mod Menu.

## Versions

| Mod version | Minecraft | Java | Notes |
|-------------|-----------|------|-------|
| 1.2.3 | 26.1 - 26.3 | 25 | one jar for all three, Fabric Loader 0.18.4+ and Fabric API |
| 1.2.3+mc1.21.11 | 1.21.11 | 21 | separate build, see the `mc/1.21.11` branch |
| 1.0.0, 1.1.0 | 1.21.1 | 21 | |

## Build

1. Install JDK 25 (JDK 21 on the `mc/1.21.11` branch).
2. Run `./gradlew build`.

The built mod jar will appear in `build/libs/`.

If the build fails, try `./gradlew clean build` or `./gradlew --refresh-dependencies build`.
