package com.zsombi.authhide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthHideConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "authhide.json");

    public boolean isHiddenEnabled = true;
    public boolean maskGuiPasswordFields = true;
    public boolean keepSpaces = true;
    public List<String> customCommands = new ArrayList<>();

    public static AuthHideConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                AuthHideConfig loaded = GSON.fromJson(reader, AuthHideConfig.class);
                if (loaded == null) {
                    loaded = new AuthHideConfig();
                }
                // Null-safety: ha a JSON-ban nem szerepel a customCommands, vagy null
                if (loaded.customCommands == null) {
                    loaded.customCommands = new ArrayList<>();
                }
                return loaded;
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                return new AuthHideConfig();
            }
        }
        return new AuthHideConfig();
    }

    public void save() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
