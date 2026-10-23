package com.zsombi.authhide;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("authhide")
                    // /authhide  →  ki/be kapcsolja a jelszó-elrejtést
                    .executes(context -> {
                        isHiddenEnabled = !isHiddenEnabled;
                        config.isHiddenEnabled = isHiddenEnabled;
                        config.save();

                        MutableText stateText = isHiddenEnabled
                                ? Text.translatable("authhide.state.enabled").formatted(Formatting.GREEN)
                                : Text.translatable("authhide.state.disabled").formatted(Formatting.RED);

                        context.getSource().sendFeedback(prefix()
                                .append(Text.translatable("authhide.message.toggle").setStyle(BLUE_STYLE))
                                .append(stateText));
                        return 1;
                    })
                    // /authhide spaces  →  szóközök megtartása / csillagozása
                    .then(ClientCommandManager.literal("spaces")
                            .executes(context -> {
                                config.keepSpaces = !config.keepSpaces;
                                config.save();

                                MutableText stateText = config.keepSpaces
                                        ? Text.translatable("authhide.state.enabled").formatted(Formatting.GREEN)
                                        : Text.translatable("authhide.state.disabled").formatted(Formatting.RED);

                                context.getSource().sendFeedback(prefix()
                                        .append(Text.translatable("authhide.message.spaces").setStyle(BLUE_STYLE))
                                        .append(stateText));
                                return 1;
                            }))
                    // /authhide command ...  →  egyedi parancsok kezelése
                    .then(ClientCommandManager.literal("command")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(prefix()
                                        .append(Text.translatable("authhide.command.usage").setStyle(BLUE_STYLE)));
                                return 1;
                            })
                            // /authhide command list
                            .then(ClientCommandManager.literal("list")
                                    .executes(ctx -> {
                                        if (config.customCommands == null || config.customCommands.isEmpty()) {
                                            ctx.getSource().sendFeedback(prefix()
                                                    .append(Text.translatable("authhide.command.list_empty").setStyle(BLUE_STYLE)));
                                            return 1;
                                        }
                                        ctx.getSource().sendFeedback(prefix()
                                                .append(Text.translatable("authhide.command.list_header").setStyle(BLUE_STYLE)));
                                        for (String cmd : config.customCommands) {
                                            ctx.getSource().sendFeedback(Text.literal(" • " + cmd)
                                                    .formatted(Formatting.WHITE));
                                        }
                                        return 1;
                                    }))
                            // /authhide command add <cmd>
                            .then(ClientCommandManager.literal("add")
                                    .then(ClientCommandManager.argument("cmd", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String raw = StringArgumentType.getString(ctx, "cmd").trim();
                                                if (raw.isEmpty()) {
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Text.translatable("authhide.command.invalid")
                                                                    .formatted(Formatting.RED)));
                                                    return 0;
                                                }
                                                String normalized = raw.startsWith("/") ? raw : "/" + raw;
                                                if (normalized.contains(" ")) {
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Text.translatable("authhide.command.invalid")
                                                                    .formatted(Formatting.RED)));
                                                    return 0;
                                                }
                                                if (config.customCommands.contains(normalized)) {
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Text.translatable("authhide.command.already_exists", normalized)
                                                                    .formatted(Formatting.YELLOW)));
                                                    return 0;
                                                }
                                                config.customCommands.add(normalized);
                                                config.save();
                                                ctx.getSource().sendFeedback(prefix()
                                                        .append(Text.translatable("authhide.command.added", normalized)
                                                                .formatted(Formatting.GREEN)));
                                                return 1;
                                            })))
                            // /authhide command remove <cmd>   (autocomplete)
                            .then(ClientCommandManager.literal("remove")
                                    .then(ClientCommandManager.argument("cmd", StringArgumentType.greedyString())
                                            .suggests((context, builder) -> {
                                                if (config.customCommands != null) {
                                                    return CommandSource.suggestMatching(config.customCommands, builder);
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> {
                                                String raw = StringArgumentType.getString(ctx, "cmd").trim();
                                                String normalized = raw.startsWith("/") ? raw : "/" + raw;
                                                if (config.customCommands.remove(normalized)) {
                                                    config.save();
                                                    ctx.getSource().sendFeedback(prefix()
                                                            .append(Text.translatable("authhide.command.removed", normalized)
                                                                    .formatted(Formatting.GREEN)));
                                                    return 1;
                                                }
                                                ctx.getSource().sendFeedback(prefix()
                                                        .append(Text.translatable("authhide.command.not_found", normalized)
                                                                .formatted(Formatting.RED)));
                                                return 0;
                                            })))
                            // /authhide command clear
                            .then(ClientCommandManager.literal("clear")
                                    .executes(ctx -> {
                                        int count = config.customCommands == null ? 0 : config.customCommands.size();
                                        if (count == 0) {
                                            ctx.getSource().sendFeedback(prefix()
                                                    .append(Text.translatable("authhide.command.list_empty").setStyle(BLUE_STYLE)));
                                            return 1;
                                        }
                                        config.customCommands.clear();
                                        config.save();
                                        ctx.getSource().sendFeedback(prefix()
                                                .append(Text.translatable("authhide.command.cleared", count)
                                                        .formatted(Formatting.GREEN)));
                                        return 1;
                                    }))));
        });
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

    private static MutableText prefix() {
        return Text.literal("ⓘ | ").setStyle(BLUE_STYLE);
    }
}
