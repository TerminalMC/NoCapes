package dev.terminalmc.nocapes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import dev.terminalmc.nocapes.config.Config;
import dev.terminalmc.nocapes.util.ModLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static dev.terminalmc.nocapes.util.Localization.localized;

public class NoCapes {
    public static final String MOD_ID = "nocapes";
    public static final String MOD_NAME = "NoCapes";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);
    public static final Component PREFIX = Component.empty()
            .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
            .append(Component.literal(MOD_NAME).withStyle(ChatFormatting.GOLD))
            .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))
            .withStyle(ChatFormatting.WHITE);
    public static final String[] CAPES = {
            "all",
            "2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933",
            "cd9d82ab17fd92022dbd4a86cde4c382a7540e117fae7b9a2853658505a80625",
            "f9a76537647989f9a0b6d001e320dac591c359e9e61a31f4ce11c88f207f0ad4",
            "afd553b39358a24edfe3b8a9a939fa5fa4faa4d9a9c3d6af8eafb377fa05c2bb",
            "cb40a92e32b57fd732a00fc325e7afb00a7ca74936ad50d8e860152e482cfbde",
            "569b7f2a1d00d26f30efe3f9ab9ac817b1e6d35f4f3cfb0324ef2d328223d350",
            "56c35628fe1c4d59dd52561a3d03bfa4e1a76d397c8b9c476c2f77cb6aebb1df",
            "e7dfea16dc83c97df01a12fabbd1216359c0cd0ea42f9999b6e97c584963e980",
            "b0cc08840700447322d953a02b965f1d65a13a603bf64b17c803c21446fe1635",
            "153b1a0dfcbae953cdeb6f2c2bf6bf79943239b1372780da44bcbb29273131da",
            "a2e8d97ec79100e90a75d369d1b3ba81273c4f82bc1b737e934eed4a854be1b6",
            "953cac8b779fe41383e675ee2b86071a71658f2180f56fbce8aa315ea70e2ed6",
            "17912790ff164b93196f08ba71d0e62129304776d0f347334f8a6eae509f8a56",
            "5786fe99be377dfb6858859f926c4dbc995751e91cee373468c5fbf4865e7151",
            "9e507afc56359978a3eb3e32367042b853cddd0995d17d0da995662913fb00f7",
            "1bf91499701404e21bd46b0191d63239a4ef76ebde88d27e4d430ac211df681e",
            "ae677f7d98ac70a533713518416df4452fe5700365c09cf45d0d156ea9396551",
            "8f120319222a9f4a104e2f5cb97b2cda93199a2ee9e1585cb8d09d6f687cb761",
            "ca35c56efe71ed290385f4ab5346a1826b546a54d519e6a3ff01efa01acce81",
            "3efadf6510961830f9fcc077f19b4daf286d502b5f5aafbd807c7bbffcaca245",
            "2262fb1d24912209490586ecae98aca8500df3eff91f2a07da37ee524e7e3cb6",
            "5048ea61566353397247d2b7d946034de926b997d5e66c86483dfb1e031aee95",
            "bcfbe84c6542a4a5c213c1cacf8979b5e913dcb4ad783a8b80e3c4a7d5c8bdac",
            "70efffaf86fe5bc089608d3cb297d3e276b9eb7a8f9f2fe6659c23a2d8b18edf",
            "d8f8d13a1adf9636a16c31d47f3ecc9bb8d8533108aa5ad2a01b13b1a0c55eac",
            "23ec737f18bfe4b547c95935fc297dd767bb84ee55bfd855144d279ac9bfd9fe",
            "2e002d5e1758e79ba51d08d92a0f3a95119f2f435ae7704916507b6c565a7da8",
            "ca29f5dd9e94fb1748203b92e36b66fda80750c87ebc18d6eafdb0e28cc1d05f",
    };
    private static final Map<UUID, String> CAPE_CACHE = new HashMap<>();
    private static boolean blockAll = false;

    public static void init() {
        Config config = Config.getAndSave();
        for (String id : CAPES) {
            if (!config.options.capes.containsKey(id)) {
                config.options.capes.put(id, true);
            }
        }
    }

    public static void onConfigSaved(Config config) {
        blockAll = !config.options.capes.get("all");
    }

    private static @Nullable String getCapeId(GameProfile profile) {
        UUID uuid = profile.getId();
        @Nullable String capeId = CAPE_CACHE.get(uuid);

        if (capeId == null) {
            MinecraftProfileTexture capeTexture = Minecraft.getInstance()
                    .getMinecraftSessionService().getTextures(profile).cape();
            if (capeTexture != null) capeId = capeTexture.getUrl();
            if (capeId == null || !capeId.contains("textures.minecraft.net/texture/")) return null;
            capeId = capeId.split("/texture/")[1];
            CAPE_CACHE.put(uuid, capeId);
        }

        return capeId;
    }

    public static boolean blockCape(GameProfile profile) {
        if (blockAll) return true;

        String capeId = getCapeId(profile);
        if (capeId == null) return false;

        @Nullable Boolean render = Config.get().options.capes.get(capeId);
        if (render == null) {
            Config.get().options.capes.put(capeId, true);
            Config.save();
            sendNewCapeMessages(capeId);
            return false;
        } else {
            return !render;
        }
    }

    private static void sendNewCapeMessages(String capeId) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        MutableComponent msg = PREFIX.copy();
        msg.append(localized("message", "unknown",
                capeId.substring(capeId.length() - 5)));
        player.sendSystemMessage(msg);

        msg = PREFIX.copy();
        msg.append(localized("message", "copy")).withStyle(msg.getStyle()
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        localized("message", "copy.tooltip")))
                .withClickEvent(new ClickEvent(
                        ClickEvent.Action.COPY_TO_CLIPBOARD, capeId)));
        player.sendSystemMessage(msg);

        msg = PREFIX.copy();
        msg.append(localized("message", "contact")).withStyle(msg.getStyle()
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        localized("message", "contact.tooltip")))
                .withClickEvent(new ClickEvent(
                        ClickEvent.Action.OPEN_URL, "https://terminalmc.dev")));
        player.sendSystemMessage(msg);
    }
}
