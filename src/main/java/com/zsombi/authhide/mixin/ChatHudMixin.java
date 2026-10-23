package com.zsombi.authhide.mixin;

import com.zsombi.authhide.AuthHideMod;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    private static final String[] DEFAULT_PASSWORD_COMMANDS = {
            "/login", "/l",
            "/register", "/reg",
            "/unregister", "/unreg",
            "/changepassword", "/changepass", "/cp",
            "/email", "/captcha",
            "/2fa", "/totp",
            "/recovery",
            "/setemail", "/setpassword", "/setpass"
    };

    @Inject(method = "addToMessageHistory", at = @At("HEAD"), cancellable = true)
    private void onAddToMessageHistory(String message, CallbackInfo ci) {
        if (!AuthHideMod.isHiddenEnabled || message == null) {
            return;
        }
        if (isPasswordCommand(message)) {
            ci.cancel();
        }
    }

    private boolean isPasswordCommand(String message) {
        for (String cmd : DEFAULT_PASSWORD_COMMANDS) {
            if (message.startsWith(cmd + " ")) {
                return true;
            }
        }
        if (AuthHideMod.config != null && AuthHideMod.config.customCommands != null) {
            for (String cmd : AuthHideMod.config.customCommands) {
                if (cmd == null || cmd.isEmpty()) continue;
                String normalized = cmd.startsWith("/") ? cmd : "/" + cmd;
                if (message.startsWith(normalized + " ")) {
                    return true;
                }
            }
        }
        return false;
    }
}
