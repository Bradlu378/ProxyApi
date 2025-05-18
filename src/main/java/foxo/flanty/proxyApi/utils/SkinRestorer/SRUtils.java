package foxo.flanty.proxyApi.utils.SkinRestorer;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.MojangSkinDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import net.skinsrestorer.api.storage.SkinStorage;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class SRUtils {
    private static final SkinsRestorer api;
    private static final Map<String, SkinProperty> skinQueue = new HashMap<>();
    private static final Map<String, SkinProperty> lastSkin = new HashMap<>();

    static {
        api = SkinsRestorerProvider.get();
    }

    /**
     * @param skinUrl  skin png url
     * @param nickname player nickname
     * @param variant  skin type SLIM/CLASSIC
     */
    public static SRCode setSkin(String skinUrl, String nickname, SkinVariant variant) {

        SkinProperty skin;
        Optional<Player> player = Config.proxyServer.getPlayer(nickname);

        try {
            skin = api.getMineSkinAPI().genSkin(skinUrl, variant).getProperty();
        } catch (MineSkinException | DataRequestException e) {
            return SRCode.SKIN_SET_ERROR;//ссылка говна/рейт лимит
        }
        if (player.isEmpty()) {
            skinQueue.put(nickname, skin);
            return SRCode.NO_PLAYER;//игрока нет
        }

        saveSkin(player.get());

        return applySkin(nickname, skin);
    }

    /**
     * @param skin     skin base64
     * @param nickname player nickname
     */
    public static SRCode setSkin(SkinProperty skin, String nickname) {
        Optional<Player> player = Config.proxyServer.getPlayer(nickname);
        if (player.isEmpty()) {
            skinQueue.put(nickname, skin);
            return SRCode.NO_PLAYER;
        }

        saveSkin(player.get());

        return applySkin(nickname, skin);
    }

    /**
     * @param skinNickname skin base64
     * @param nickname     player nickname
     */
    public static SRCode setSkin(String skinNickname, String nickname) throws DataRequestException {
        Optional<Player> player = Config.proxyServer.getPlayer(nickname);

        Optional<MojangSkinDataResult> skin = api.getMojangAPI().getSkin(skinNickname);
        if (skin.isEmpty()) return SRCode.SKIN_SET_ERROR;//ошибка при получении скина по нику, not exist/rateLimit

        if (player.isEmpty()) {
            skinQueue.put(player.get().getUsername(),skin.get().getSkinProperty());
            return SRCode.NO_PLAYER;//игрока нет
        }

        saveSkin(player.get());

        return applySkin(nickname, skin.get().getSkinProperty());
    }


    /**
     * Возвращает ссылку на скин и тип скина
     *
     * @param base64
     * @return String[2]{Url, SkinVariant, skinHash}
     * 0 - Url to skin png
     * 1 - skin type (SLIM/CLASSIC)
     * 2 - skin hash (http://textures.minecraft.net/texture/ "7fd9ba42a7c81eeea22f1524271ae85a8e045ce0af5a6ae16c6406ae917e68b5" )
     */
    public static String[] textureDecode(String base64) {
        String json = new String(java.util.Base64.getDecoder().decode(base64));
        JSONObject jsonObject = new JSONObject(json);
        String skinUrl = jsonObject.getJSONObject("textures").getJSONObject("SKIN").getString("url");
        String variant;
        if (jsonObject.getJSONObject("textures").getJSONObject("SKIN").isNull("metadata")) variant = "classic";
        else variant = "slim";

        String textureId = skinUrl.substring(skinUrl.lastIndexOf("/") + 1);
        return new String[]{skinUrl, variant, textureId};
    }

    public static SRCode rollbackSkin(String nickname) {
        return applySkin(nickname, lastSkin.get(nickname));
    }

    private static void saveSkin(Player player) {
        Optional<SkinProperty> skinProperty = api.getPlayerStorage().getSkinOfPlayer(player.getUniqueId());
        if (skinProperty.isEmpty())
            skinProperty = Optional.of(SkinProperty.of("1", "1"));//если у чела нет скина ставится 1 для базового
        lastSkin.put(player.getUsername(), skinProperty.get());
    }

    public static void chechQueue(String player) {
        if (skinQueue.containsKey(player)) {
            applySkin(player, skinQueue.get(player));
            skinQueue.remove(player);
        }
    }


    /**
     * Устанавливает скин игрока
     *
     * @param nickname ник игрока
     * @param property текстура (сигнатура+текстура)
     */
    private static SRCode applySkin(String nickname, SkinProperty property) {

        Optional<Player> player = Config.proxyServer.getPlayer(nickname);
        if (player.isEmpty()) return SRCode.NO_PLAYER;//игрока нет

        SkinStorage skinStorage = api.getSkinStorage();

        skinStorage.setCustomSkinData(player.get().getUsername(), property);

        api.getPlayerStorage().setSkinIdOfPlayer(
                player.get().getUniqueId(),
                skinStorage.findSkinData(player.get().getUsername()).get().getIdentifier()
        );

        try {
            api.getSkinApplier(Player.class).applySkin(player.get());
        } catch (DataRequestException e) {//хуй знает что тут
            Config.logger.error(e.getMessage());
            return SRCode.SKIN_SET_ERROR;
        }
        return SRCode.OK;
    }
}
