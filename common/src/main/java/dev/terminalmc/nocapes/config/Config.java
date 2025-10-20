/*
 * Copyright 2025 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.nocapes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.terminalmc.nocapes.NoCapes;
import dev.terminalmc.nocapes.platform.Services;
import dev.terminalmc.nocapes.util.Capes;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config {

    private static final Path DIR_PATH = Services.PLATFORM.getConfigDir();
    private static final String FILE_NAME = NoCapes.MOD_ID + ".json";
    private static final String BACKUP_FILE_NAME = NoCapes.MOD_ID + ".unreadable.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Options

    public final Options options = new Options();

    public static Options options() {
        return Config.get().options;
    }

    public static class Options {

        public static final boolean hideEverythingDefault = true;
        public boolean hideEverything = hideEverythingDefault;

        public Map<String, @NotNull ShowMode> capes = defaultCapes();
    }

    public enum ShowMode {
        BOTH(0, ChatFormatting.GREEN),
        CAPE(1, ChatFormatting.YELLOW),
        ELYTRA(2, ChatFormatting.GOLD),
        NEITHER(3, ChatFormatting.RED);

        public final int index;
        public final ChatFormatting format;

        ShowMode(int index, ChatFormatting format) {
            this.index = index;
            this.format = format;
        }

        public boolean showCape() {
            return index == 0 || index == 1;
        }

        public boolean showElytra() {
            return index == 0 || index == 2;
        }
    }

    public static Map<String, ShowMode> defaultCapes() {
        Map<String, ShowMode> capes = new LinkedHashMap<>();
        for (String id : Capes.CAPES)
            capes.put(id, ShowMode.BOTH);
        return capes;
    }

    // Instance management

    private static Config instance = null;

    public static Config get() {
        if (instance == null) {
            instance = Config.load();
        }
        return instance;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Config getAndSave() {
        get();
        save();
        return instance;
    }

    @SuppressWarnings("unused")
    public static Config reloadAndSave() {
        instance = Config.load();
        save();
        return instance;
    }

    @SuppressWarnings("unused")
    public static Config resetAndSave() {
        instance = new Config();
        save();
        return instance;
    }

    // Validation

    /**
     * Cleanup and validation method, called after config is loaded and before it is saved.
     */
    private void validate() {
    }

    // Load and save

    public static @NotNull Config load() {
        Path file = DIR_PATH.resolve(FILE_NAME);
        @Nullable Config config = null;
        if (Files.exists(file)) {
            config = load(file, GSON);
            if (config == null) {
                backup();
                NoCapes.LOG.warn("Resetting config");
            }
        }
        if (config == null)
            config = new Config();
        config.validate();
        return config;
    }

    @SuppressWarnings("SameParameterValue")
    private static @Nullable Config load(Path file, Gson gson) {
        try (
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(file.toFile()),
                        StandardCharsets.UTF_8
                )
        ) {
            return gson.fromJson(reader, Config.class);
        } catch (Exception e) {
            // Catch Exception as errors in deserialization may not fall under
            // IOException or JsonParseException, but should not crash the game.
            NoCapes.LOG.error("Unable to load config", e);
            return null;
        }
    }

    private static void backup() {
        try {
            NoCapes.LOG.warn("Copying {} to {}", FILE_NAME, BACKUP_FILE_NAME);
            if (!Files.isDirectory(DIR_PATH))
                Files.createDirectories(DIR_PATH);
            Path file = DIR_PATH.resolve(FILE_NAME);
            Path backupFile = file.resolveSibling(BACKUP_FILE_NAME);
            Files.move(
                    file,
                    backupFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            NoCapes.LOG.error("Unable to copy config file", e);
        }
    }

    public static void save() {
        if (instance == null)
            return;
        instance.validate();
        try {
            if (!Files.isDirectory(DIR_PATH))
                Files.createDirectories(DIR_PATH);
            Path file = DIR_PATH.resolve(FILE_NAME);
            Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");
            try (
                    OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream(tempFile.toFile()),
                            StandardCharsets.UTF_8
                    )
            ) {
                writer.write(GSON.toJson(instance));
            } catch (IOException e) {
                throw new IOException(e);
            }
            Files.move(
                    tempFile,
                    file,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
            NoCapes.onConfigSaved(instance);
        } catch (IOException e) {
            NoCapes.LOG.error("Unable to save config", e);
        }
    }
}
