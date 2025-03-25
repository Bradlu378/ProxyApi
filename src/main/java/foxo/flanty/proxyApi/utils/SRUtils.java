package foxo.flanty.proxyApi.utils;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.SkinApplier;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class SRUtils {
    private static final SkinsRestorer api;
    private static final SkinApplier<Player> applier;
    static  {
        api = SkinsRestorerProvider.get();
        applier = api.getSkinApplier(Player.class);
    }


    /**
     * @param skinUrl skin png url
     * @param playerName player nickname
     * @param variant skin type SLIM/CLASSIC
     * @return 0: ok, 1: no player, 2: skin set error.
     */
    public static byte setSkin(String skinUrl, String playerName, SkinVariant variant) {
        Optional<SkinProperty> skin;
        Optional<Player> player = Config.proxyServer.getPlayer(playerName);
        if (player.isEmpty()) return 1;
        try {
            skin = Optional.of(api.getMineSkinAPI().genSkin(skinUrl, variant).getProperty());
        } catch (MineSkinException | DataRequestException e) {
            return 2;
        }
        applier.applySkin(player.get(),skin.get());
        return 0;
    }

    /**
     * @param property skin base64
     * @param playerName player nickname
     * @return 0: ok, 1: player offline
     */
    public static byte setSkin(SkinProperty property, String playerName) {
        Optional<Player> player = Config.proxyServer.getPlayer(playerName);
        if (player.isEmpty()) return 1;
        applier.applySkin(player.get(),property);
        return 0;
    }

    public static Optional<SkinProperty> getSkin(Player player) {
        return api.getPlayerStorage().getSkinOfPlayer(player.getUniqueId());
    }

    /**
     * Возвращает ссылку на скин и тип скина
     * @param base64
     * @return String[2]{Url, SkinVariant}
     */
    public static String[] textureDecode(String base64) {
        String json = new String(java.util.Base64.getDecoder().decode(base64));
        JSONObject jsonObject = new JSONObject(json);
        String skinUrl = jsonObject.getJSONObject("textures").getJSONObject("SKIN").getString("url");
        String variant;
        try {//todo костыль тк metadata если скин не slim тупо нету
            variant = jsonObject.getJSONObject("textures").getJSONObject("SKIN").getJSONObject("metadata").getString("model");
        } catch (JSONException e) {
            variant = "CLASSIC";
        }
        return new String[]{skinUrl, variant};
    }
}
