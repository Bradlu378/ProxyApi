package foxo.flanty.proxyApi.utils;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.MojangSkinDataResult;
import net.skinsrestorer.api.property.SkinApplier;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SRUtils {
    private static final SkinsRestorer api;
    private static final SkinApplier<Player> applier;
    static  {
        api = SkinsRestorerProvider.get();
        applier = api.getSkinApplier(Player.class);
    }
    private static final Map<String,SkinProperty> lastSkin = new HashMap<>();


    /**
     * @param skinUrl skin png url
     * @param nickname player nickname
     * @param variant skin type SLIM/CLASSIC
     * @return 0: ok, 1: no player, 2: skin set error.
     */
    public static byte setSkin(String skinUrl, String nickname, SkinVariant variant, boolean rollback) {

        SkinProperty skin;
        Optional<Player> player = Config.proxyServer.getPlayer(nickname);
        if (player.isEmpty()) return 1;
        try {
            skin = api.getMineSkinAPI().genSkin(skinUrl, variant).getProperty();
        } catch (MineSkinException | DataRequestException e) {
            return 2;
        }
        Optional<SkinProperty> skinProperty = api.getPlayerStorage().getSkinOfPlayer(player.get().getUniqueId());
        if (skinProperty.isEmpty()) {
            skinProperty = Optional.of(SkinProperty.of("1", "1"));
        }
        if (!rollback) lastSkin.put(player.get().getUsername(), skinProperty.get());
        applier.applySkin(player.get(),skin);
        return 0;
    }

    /**
     * @param property skin base64
     * @param nickname player nickname
     * @return 0: ok, 1: no player.
     */
    public static byte setSkin(SkinProperty property, String nickname, boolean rollback) {
        Optional<Player> player = Config.proxyServer.getPlayer(nickname);
        if (player.isEmpty()) return 1;
        Optional<SkinProperty> skinProperty = api.getPlayerStorage().getSkinOfPlayer(player.get().getUniqueId());
        if (skinProperty.isEmpty()) {
            skinProperty = Optional.of(SkinProperty.of("1", "1"));
        }
        if (!rollback) lastSkin.put(player.get().getUsername(), skinProperty.get());
        applier.applySkin(player.get(),property);
        return 0;
    }

    /**
     * @param skinNickname skin base64
     * @param nickname player nickname
     * @return 0: ok, 1: no player, 2: skin set error.
     */
    public static byte setSkin(String skinNickname, String nickname, boolean rollback) throws DataRequestException {
       Optional<MojangSkinDataResult> skin = api.getMojangAPI().getSkin(skinNickname);
        if (skin.isEmpty()) return 2;
        Optional<Player> player = Config.proxyServer.getPlayer(nickname);
        if (player.isEmpty()) return 1;
        Optional<SkinProperty> skinProperty = api.getPlayerStorage().getSkinOfPlayer(player.get().getUniqueId());
        if (skinProperty.isEmpty()) {
            skinProperty = Optional.of(SkinProperty.of("1", "1"));
        }
        if (!rollback) lastSkin.put(player.get().getUsername(), skinProperty.get());
        applier.applySkin(player.get(),skin.get().getSkinProperty());
        return 0;
    }

    public static Optional<SkinProperty> getSkin(Player player) {
        applier.;


        return api.getPlayerStorage().getSkinOfPlayer(player.getUniqueId());
    }
    public static Optional<SkinProperty> lastSkin(String nickname) {
        return Optional.ofNullable(lastSkin.get(nickname));
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
        if(jsonObject.getJSONObject("textures").getJSONObject("SKIN").isNull("metadata")) variant = "classic";
        else variant = "slim";

        String textureId = skinUrl.substring(skinUrl.lastIndexOf("/") + 1);
        return new String[]{skinUrl, variant, textureId};
    }
}
