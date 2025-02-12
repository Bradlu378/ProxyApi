package foxo.flanty.proxyApi.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.SRUtils;
import net.skinsrestorer.api.event.SkinApplyEvent;
import net.skinsrestorer.api.property.SkinProperty;

import java.util.Optional;

import static foxo.flanty.proxyApi.REST.requests.Skins.updateSkin;

public class Skins {

    @Subscribe
    public void onSkinApply(SkinApplyEvent event) {
        Player player = event.getPlayer(Player.class);
        Optional<SkinProperty> oldSkin = SRUtils.getSkin(player);
        updateSkin(event.getProperty(), player).thenAccept(success -> {
            if (success) Config.logger.info(player.getUsername() + "updated skin");
            else if (oldSkin.isPresent() && SRUtils.setSkin(oldSkin.get(), player.getUsername()) == 1)
                Config.logger.warn(player.getUsername() + ":skin change failed x2");//чел ахуел думаю
        });
    }

}
