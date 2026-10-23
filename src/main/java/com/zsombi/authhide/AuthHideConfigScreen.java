package com.zsombi.authhide;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AuthHideConfigScreen extends Screen {

    private final Screen parent;
    private EditBox commandInput;

    public AuthHideConfigScreen(Screen parent) {
        super(Component.translatable("authhide.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Be/Ki gomb
        this.addRenderableWidget(Button.builder(
                        buildToggleLabel(),
                        button -> {
                            AuthHideMod.isHiddenEnabled = !AuthHideMod.isHiddenEnabled;
                            if (AuthHideMod.config != null) {
                                AuthHideMod.config.isHiddenEnabled = AuthHideMod.isHiddenEnabled;
                                AuthHideMod.config.save();
                            }
                            button.setMessage(buildToggleLabel());
                        })
                .bounds(this.width / 2 - 100, 40, 200, 20)
                .build());

        // Parancs hozzáadása input
        this.commandInput = new EditBox(this.font,
                this.width / 2 - 100, this.height - 60, 160, 20,
                Component.literal(""));
        this.commandInput.setHint(Component.translatable("authhide.config.placeholder"));
        this.addRenderableWidget(this.commandInput);

        // Hozzáadás gomb
        this.addRenderableWidget(Button.builder(
                        Component.translatable("authhide.config.add"),
                        button -> {
                            if (AuthHideMod.config == null || this.commandInput == null) return;
                            String cmd = this.commandInput.getValue();
                            if (cmd == null) return;
                            cmd = cmd.trim();
                            if (!cmd.isEmpty() && !AuthHideMod.config.customCommands.contains(cmd)) {
                                AuthHideMod.config.customCommands.add(cmd);
                                AuthHideMod.config.save();
                                this.commandInput.setValue("");
                                this.reload();
                            }
                        })
                .bounds(this.width / 2 + 65, this.height - 60, 35, 20)
                .build());

        // Vissza gomb
        this.addRenderableWidget(Button.builder(
                        Component.translatable("authhide.config.back"),
                        button -> Minecraft.getInstance().setScreenAndShow(this.parent))
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private Component buildToggleLabel() {
        return Component.literal(AuthHideMod.isHiddenEnabled ? "✔ " : "✘ ")
                .append(Component.translatable("authhide.config.toggle"));
    }

    private void reload() {
        this.clearWidgets();
        this.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
        guiGraphicsExtractor.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);

        // Parancsok listája
        int y = 70;
        guiGraphicsExtractor.text(this.font,
                Component.translatable("authhide.config.commands"), this.width / 2 - 100, y, 0xAAAAAA);
        y += 15;

        if (AuthHideMod.config != null && AuthHideMod.config.customCommands != null) {
            for (String cmd : AuthHideMod.config.customCommands) {
                guiGraphicsExtractor.text(this.font,
                        Component.literal("• " + cmd), this.width / 2 - 90, y, 0xFFFFFF);
                y += 12;
            }
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(this.parent);
    }
}
