# AuthHide

Client-only Fabric mod that hides password commands (`/login`, `/register`, ...) from the chat history and masks password input with `*` characters, both in chat and in login GUIs (e.g. AuthMe).

Downloads are on the [Releases](../../releases) page. Questions, bug reports and ideas: [AuthHide Discord](https://discord.gg/wUbn26bJc3).

## Commands

- `/authhide` - toggle password hiding on/off
- `/authhide spaces` - toggle whether spaces stay visible when masking (default: spaces stay visible)
- `/authhide command <add|remove|list|clear> [command]` - manage custom commands that should be hidden

The settings screen is also available through Mod Menu.

## Versions

| Mod version | Minecraft | Java | Notes |
|-------------|-----------|------|-------|
| 1.2.5 | 26.2 - 26.3 | 25 | current, Fabric Loader 0.18.4+ and Fabric API |
| 1.2.3, 1.2.4 | 26.1 - 26.3 | 25 | older |
| 1.2.3+mc1.21.11, 1.2.4+mc1.21.11 | 1.21.11 | 21 | legacy, no longer updated (`mc/1.21.11` branch) |
| 1.0.0, 1.1.0 | 1.21.1 | 21 | legacy |

## Build

1. Install JDK 25.
2. Run `./gradlew build`.

The built mod jar will appear in `build/libs/`.

If the build fails, try `./gradlew clean build` or `./gradlew --refresh-dependencies build`.
