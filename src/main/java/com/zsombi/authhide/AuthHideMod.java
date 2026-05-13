package com.zsombi.authhide;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AuthHideMod implements ClientModInitializer {
    public static boolean isHiddenEnabled = true;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("authhide")
                    .executes(context -> {
                        isHiddenEnabled = !isHiddenEnabled;

                        int hexBlue = 0x00bafe;
                        Text stateText = isHiddenEnabled ?
                                Text.translatable("authhide.state.enabled").formatted(Formatting.GREEN) :
                                Text.translatable("authhide.state.disabled").formatted(Formatting.RED);

                        context.getSource().sendFeedback(Text.literal("ⓘ | ")
                                .setStyle(Style.EMPTY.withColor(hexBlue).withBold(false))
                                .append(Text.translatable("authhide.message.toggle")
                                        .setStyle(Style.EMPTY.withColor(hexBlue).withBold(false)))
                                .append(stateText));

                        return 1;
                    }));
        });
    }
}