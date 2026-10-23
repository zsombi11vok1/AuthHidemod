package com.zsombi.authhide.mixin;

import com.zsombi.authhide.AuthHideConfigScreen;
import com.zsombi.authhide.AuthHideMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(EditBox.class)
public class EditBoxMixin {

    @Shadow private String value;

    private String authhide_savedText = null;

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

    // Több nyelvű kulcsszavak a login/jelszó képernyők felismeréséhez
    private static final String[] AUTH_KEYWORDS = {
            "password", "passwort", "contraseña", "contrasena", "mot de passe",
            "пароль", "jelszó", "jelszo",
            "login", "log in", "anmeld", "connexion", "iniciar", "вход", "войти", "bejelentkez",
            "register", "registr", "регистрация", "regisztr"
    };

    @Inject(method = "extractWidgetRenderState", at = @At("HEAD"))
    private void authhide_onExtractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!AuthHideMod.isHiddenEnabled || this.value == null || this.value.isEmpty()) return;

        // 1) Chat-parancs maszkolás (pl. "/login jelszo")
        String matched = findMatchingCommand(this.value);
        if (matched != null) {
            int spaceIndex = this.value.indexOf(' ');
            if (spaceIndex != -1 && spaceIndex < this.value.length() - 1) {
                String command = this.value.substring(0, spaceIndex + 1);
                String password = this.value.substring(spaceIndex + 1);
                authhide_savedText = this.value;
                this.value = command + AuthHideMod.maskText(password);
            }
            return;
        }

        // 2) GUI jelszómező maszkolás (pl. AuthMe login képernyő)
        if (AuthHideMod.config != null && AuthHideMod.config.maskGuiPasswordFields
                && authhide_isPasswordScreen()) {
            authhide_savedText = this.value;
            this.value = AuthHideMod.maskText(this.value);
        }
    }

    @Inject(method = "extractWidgetRenderState", at = @At("TAIL"))
    private void authhide_afterExtractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (authhide_savedText != null) {
            this.value = authhide_savedText;
            authhide_savedText = null;
        }
    }

    private String findMatchingCommand(String text) {
        for (String cmd : DEFAULT_PASSWORD_COMMANDS) {
            if (text.startsWith(cmd + " ")) {
                return cmd;
            }
        }
        if (AuthHideMod.config != null && AuthHideMod.config.customCommands != null) {
            for (String cmd : AuthHideMod.config.customCommands) {
                if (cmd == null || cmd.isEmpty()) continue;
                String normalized = cmd.startsWith("/") ? cmd : "/" + cmd;
                if (text.startsWith(normalized + " ")) {
                    return normalized;
                }
            }
        }
        return null;
    }

    private boolean authhide_isPasswordScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return false;

        Screen screen = mc.gui.screen();
        if (screen == null) return false;
        // A saját config képernyőnket és a chatet soha ne maszkoljuk teljesen
        if (screen instanceof ChatScreen || screen instanceof AuthHideConfigScreen) return false;

        // a) A képernyő címe alapján (pl. "Login")
        Component titleText = screen.getTitle();
        if (titleText != null && authhide_containsKeyword(titleText.getString())) {
            return true;
        }

        // b) A mező saját (narrációs) üzenete alapján (pl. "Password")
        Component msg = ((EditBox) (Object) this).getMessage();
        if (msg != null && authhide_containsKeyword(msg.getString())) {
            return true;
        }

        return false;
    }

    private static boolean authhide_containsKeyword(String s) {
        if (s == null || s.isEmpty()) return false;
        String lower = s.toLowerCase(Locale.ROOT);
        for (String keyword : AUTH_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
