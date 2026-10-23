package com.zsombi.authhide;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AuthHideConfigScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget commandInput;

    public AuthHideConfigScreen(Screen parent) {
        super(Text.translatable("authhide.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Be/Ki gomb
        this.addDrawableChild(ButtonWidget.builder(
                        buildToggleLabel(),
                        button -> {
                            AuthHideMod.isHiddenEnabled = !AuthHideMod.isHiddenEnabled;
                            if (AuthHideMod.config != null) {
                                AuthHideMod.config.isHiddenEnabled = AuthHideMod.isHiddenEnabled;
                                AuthHideMod.config.save();
                            }
                            button.setMessage(buildToggleLabel());
                        })
                .dimensions(this.width / 2 - 100, 40, 200, 20)
                .build());

        // Parancs hozzáadása input
        this.commandInput = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 100, this.height - 60, 160, 20,
                Text.literal(""));
        this.commandInput.setPlaceholder(Text.translatable("authhide.config.placeholder"));
        this.addDrawableChild(this.commandInput);

        // Hozzáadás gomb
        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("authhide.config.add"),
                        button -> {
                            if (AuthHideMod.config == null || this.commandInput == null) return;
                            String cmd = this.commandInput.getText();
                            if (cmd == null) return;
                            cmd = cmd.trim();
                            if (!cmd.isEmpty() && !AuthHideMod.config.customCommands.contains(cmd)) {
                                AuthHideMod.config.customCommands.add(cmd);
                                AuthHideMod.config.save();
                                this.commandInput.setText("");
                                this.reload();
                            }
                        })
                .dimensions(this.width / 2 + 65, this.height - 60, 35, 20)
                .build());

        // Vissza gomb
        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("authhide.config.back"),
                        button -> MinecraftClient.getInstance().setScreen(this.parent))
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private Text buildToggleLabel() {
        return Text.literal(AuthHideMod.isHiddenEnabled ? "✔ " : "✘ ")
                .append(Text.translatable("authhide.config.toggle"));
    }

    private void reload() {
        this.clearChildren();
        this.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        // Parancsok listája
        int y = 70;
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("authhide.config.commands"), this.width / 2 - 100, y, 0xAAAAAA);
        y += 15;

        if (AuthHideMod.config != null && AuthHideMod.config.customCommands != null) {
            for (String cmd : AuthHideMod.config.customCommands) {
                context.drawTextWithShadow(this.textRenderer,
                        Text.literal("• " + cmd), this.width / 2 - 90, y, 0xFFFFFF);
                y += 12;
            }
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }
}
