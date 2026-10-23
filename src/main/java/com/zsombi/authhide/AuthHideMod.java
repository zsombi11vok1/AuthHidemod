package com.zsombi.authhide;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class AuthHideMod implements ClientModInitializer {

    public static AuthHideConfig config;
    public static boolean isHiddenEnabled;

    private static final int HEX_BLUE = 0x00BAFE;
    private static final Style BLUE_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(HEX_BLUE));

    @Override
    public void onInitializeClient() {
        config = AuthHideConfig.load();
        if (config == null) {
            config = new AuthHideConfig();
        }
        isHiddenEnabled = config.isHiddenEnabled;

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            dispatcher.register(ClientCommands.literal("authhide")
                    // /authhide  →  ki/be kapcsolja a jelszó-elrejtést
                    .executes(context -> {
                        isHiddenEnabled = !isHiddenEnabled;
                        config.isHiddenEnabled = isHiddenEnabled;
                        config.save();

                        MutableComponent stateText = isHiddenEnabled
                                ? Component.translatable("authhide.state.enabled").withStyle(ChatFormatting.GREEN)
                                : Component.translatable("authhide.state.disabled").withStyle(ChatFormatting.RED);

                        context.getSource().sendFeedback(prefix()
                                .append(Component.translatable("authhide.message.toggle").setStyle(BLUE_STYLE))
                                .append(stateText));
                        return 1;
                    })
                    // /authhide spaces  →  szóközök megtartása / csillagozása
                    .then(ClientCommands.literal("spaces")
                            .executes(context -> {
                                config.keepSpaces = !config.keepSpaces;
                                config.save();

                                MutableComponent stateText = config.keepSpaces
                                        ? Component.translatable("authhide.state.enabled").withStyle(ChatFormatting.GREEN)
                                        : Component.translatable("authhide.state.disabled").withStyle(ChatFormatting.RED);

                                context.getSource().sendFeedback(prefix()
                                        .append(Component.translatable("authhide.message.spaces").setStyle(BLUE_STYLE))
                                        .append(stateText));
                                return 1;
                            }))
                    // /authhide command ...  →  egyedi parancsok kezelése
                    .then(ClientCommands.literal("command")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(prefix()
                                        .append(Component.translatable("authhide.command.usage").setStyle(BLUE_STYLE)));
                                return 1;
                            })
                            // /authhide command list
                            .then(ClientCommands.literal("list")
                                    .executes(ctx -> {
                                        if (config.customCommands == null || config.customCommands.isEmpty()) {
                                            ctx.getSource().sendFeedback(prefix()
                                                    .append(Component.translatable("authhide.command.list_empty").setStyle(BLUE_STYLE)));
                                            return 1;
                                        }
                                        ctx.getSource().sendFeedback(prefix()
                                                .append(Component.translatable("authhide.command.list_header").setStyle(BLUE_STYLE)));
                                        for (String cmd : config.customCommands) {
                                            ctx.getSource().sendFeedback(Component.literal(" • " + cmd)
                                                    .withStyle(ChatFormatting.WHITE));
                                        }
                                        return 1;
                                    }))
                            // /authhide command add <cmd>
                            .then(ClientCommands.literal("add")
                                    .then(ClientCommands.argument("cmd", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String raw = StringArgumentType.getString(ctx, "cmd").trim();
                                                if (raw.isEmpty()) {
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Component.translatable("authhide.command.invalid")
                                                                    .withStyle(ChatFormatting.RED)));
                                                    return 0;
                                                }
                                                String normalized = raw.startsWith("/") ? raw : "/" + raw;
                                                if (normalized.contains(" ")) {
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Component.translatable("authhide.command.invalid")
                                                                    .withStyle(ChatFormatting.RED)));
                                                    return 0;
                                                }
                                                if (config.customCommands.contains(normalized)) {
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Component.translatable("authhide.command.already_exists", normalized)
                                                                    .withStyle(ChatFormatting.YELLOW)));
                                                    return 0;
                                                }
                                                config.customCommands.add(normalized);
                                                config.save();
                                                ctx.getSource().sendFeedback(prefix()
                                                        .append(Component.translatable("authhide.command.added", normalized)
                                                                .withStyle(ChatFormatting.GREEN)));
                                                return 1;
                                            })))
                            // /authhide command remove <cmd>   (autocomplete)
                            .then(ClientCommands.literal("remove")
                                    .then(ClientCommands.argument("cmd", StringArgumentType.greedyString())
                                            .suggests((context, builder) -> {
                                                if (config.customCommands != null) {
                                                    return SharedSuggestionProvider.suggest(config.customCommands, builder);
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> {
                                                String raw = StringArgumentType.getString(ctx, "cmd").trim();
                                                String normalized = raw.startsWith("/") ? raw : "/" + raw;
                                                if (config.customCommands.remove(normalized)) {
                                                    config.save();
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Component.translatable("authhide.command.removed", normalized)
                                                                    .withStyle(ChatFormatting.GREEN)));
                                                    return 1;
                                                }
                                                ctx.getSource().sendFeedback(prefix()
                                                        .append(Component.translatable("authhide.command.not_found", normalized)
                                                                .withStyle(ChatFormatting.RED)));
                                                return 0;
                                            })))
                            // /authhide command clear
                            .then(ClientCommands.literal("clear")
                                    .executes(ctx -> {
                                        int count = config.customCommands == null ? 0 : config.customCommands.size();
                                        if (count == 0) {
                                            ctx.getSource().sendFeedback(prefix()
                                                    .append(Component.translatable("authhide.command.list_empty").setStyle(BLUE_STYLE)));
                                            return 1;
                                        }
                                        config.customCommands.clear();
                                        config.save();
                                        ctx.getSource().sendFeedback(prefix()
                                                .append(Component.translatable("authhide.command.cleared", count)
                                                        .withStyle(ChatFormatting.GREEN)));
                                        return 1;
                                    }))));
        });
    }

    private static java.lang.reflect.Method screenGetter;
    private static java.lang.reflect.Field screenField;
    private static boolean screenLookupDone;

    // 26.2+: Gui#screen(), 26.1: Minecraft#screen mező
    public static Screen currentScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return null;
        try {
            if (!screenLookupDone) {
                screenLookupDone = true;
                try {
                    screenGetter = mc.gui.getClass().getMethod("screen");
                } catch (NoSuchMethodException e) {
                    screenField = Minecraft.class.getField("screen");
                }
            }
            if (screenGetter != null) return (Screen) screenGetter.invoke(mc.gui);
            if (screenField != null) return (Screen) screenField.get(mc);
        } catch (ReflectiveOperationException ignored) {
        }
        return null;
    }

    public static String maskText(String text) {
        if (config != null && !config.keepSpaces) {
            return "*".repeat(text.length());
        }
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i) == ' ' ? ' ' : '*');
        }
        return sb.toString();
    }

    private static MutableComponent prefix() {
        return Component.literal("ⓘ | ").setStyle(BLUE_STYLE);
    }
}
