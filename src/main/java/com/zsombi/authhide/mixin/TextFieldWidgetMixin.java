package com.zsombi.authhide.mixin;

import com.zsombi.authhide.AuthHideMod;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin {

    @Shadow private String text;

    private String savedText = null;

    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!AuthHideMod.isHiddenEnabled || this.text == null) return;

        if (isPasswordCommand(this.text)) {
            int spaceIndex = this.text.indexOf(" ");
            if (spaceIndex != -1) {
                String command = this.text.substring(0, spaceIndex + 1);
                String password = this.text.substring(spaceIndex + 1);
                savedText = this.text;
                this.text = command + "*".repeat(password.length());
            }
        }
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void afterRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (savedText != null) {
            this.text = savedText;
            savedText = null;
        }
    }

    private boolean isPasswordCommand(String text) {
        return text.startsWith("/login ") ||
                text.startsWith("/l ") ||
                text.startsWith("/register ") ||
                text.startsWith("/reg ") ||
                text.startsWith("/unregister ") ||
                text.startsWith("/unreg ") ||
                text.startsWith("/changepassword ") ||
                text.startsWith("/changepass ") ||
                text.startsWith("/cp ") ||
                text.startsWith("/email ") ||
                text.startsWith("/captcha ") ||
                text.startsWith("/2fa ") ||
                text.startsWith("/totp ") ||
                text.startsWith("/recovery ") ||
                text.startsWith("/setemail ") ||
                text.startsWith("/setpassword ") ||
                text.startsWith("/setpass ");
    }
}