package foxo.flanty.proxyApi.modules.skins;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.settings.Language;
import foxo.flanty.proxyApi.SkinRestorer.SRUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skinsrestorer.api.event.SkinApplyEvent;

import java.util.*;

import static foxo.flanty.proxyApi.modules.skins.Requests.updateSkin;


public class ProxyEventListener {
    private final static Set<String> skinCooldown = new HashSet<>();

    @Subscribe
    public void onSkinApply(SkinApplyEvent event) {
        Player player = event.getPlayer(Player.class);
        if (skinCooldown.contains(player.getUsername())) {
            skinCooldown.remove(player.getUsername());
            return;
        }
        updateSkin(event.getProperty(), player).thenAccept(success -> {
            if (success)
                Config.logger.info(player.getUsername() + "updated skin");
            else {
                skinCooldown.add(player.getUsername());
                player.sendMessage(MiniMessage.miniMessage().deserialize(Language.skinChangeError));
                Config.logger.warn(player.getUsername() + "skin update failed, api error/not responding");
                SRUtils.rollbackSkin(player.getUsername());
            }
        });

    }
}
