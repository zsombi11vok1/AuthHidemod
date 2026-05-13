package com.zsombi.authhide.mixin;

import com.zsombi.authhide.AuthHideMod;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addToMessageHistory", at = @At("HEAD"), cancellable = true)
    private void onAddToMessageHistory(String message, CallbackInfo ci) {
        if (AuthHideMod.isHiddenEnabled) {
            if (isPasswordCommand(message)) {
                ci.cancel();
            }
        }
    }

    private boolean isPasswordCommand(String message) {
        return message.startsWith("/login ") ||
                message.startsWith("/l ") ||
                message.startsWith("/register ") ||
                message.startsWith("/reg ") ||
                message.startsWith("/unregister ") ||
                message.startsWith("/unreg ") ||
                message.startsWith("/changepassword ") ||
                message.startsWith("/changepass ") ||
                message.startsWith("/cp ") ||
                message.startsWith("/email ") ||
                message.startsWith("/captcha ") ||
                message.startsWith("/2fa ") ||
                message.startsWith("/totp ") ||
                message.startsWith("/recovery ") ||
                message.startsWith("/setemail ") ||
                message.startsWith("/setpassword ") ||
                message.startsWith("/setpass ");
    }
}