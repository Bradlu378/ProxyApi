package foxo.flanty.proxyApi.modules.skins;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.SRUtils;
import net.skinsrestorer.api.event.SkinApplyEvent;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;

import java.util.*;

import static foxo.flanty.proxyApi.modules.skins.Requests.updateSkin;

public class ProxyEventListener {
    private final static Set<String> skinCooldown = new HashSet<>();
    private final static Map<String,Long> skinCooldownMap = new HashMap<>();

    @Subscribe
    public void onSkinApply(SkinApplyEvent event) {
        Player player = event.getPlayer(Player.class);
        if (skinCooldownMap.containsKey(player.getUsername()) && skinCooldownMap.get(player.getUsername()) < 1L) {
            skinCooldownMap.put(player.getUsername(), skinCooldownMap.get(player.getUsername())+1);
            System.out.println("###");
            event.setCancelled(true);
            return;
        } else skinCooldownMap.remove(player.getUsername());
        System.out.println("end         ");
        skinCooldownMap.put(player.getUsername(), 0L);
        updateSkin(event.getProperty(), player).thenAccept(success -> {
            System.out.println(success);
            if (success) Config.logger.info(player.getUsername() + "updated skin");
            else {
                try {
                    SRUtils.setSkin("Notch", "_Flanty_", false);
                } catch (DataRequestException e) {
                    throw new RuntimeException(e);
                }
            }
            //else if (SRUtils.lastSkin(player.getUsername()).isPresent()) {
            //    try {
            //        Thread.sleep(1000);
            //    } catch (InterruptedException e) {
            //        throw new RuntimeException(e);
            //    }
            //    SRUtils.setSkin(SRUtils.lastSkin(player.getUsername()).get(), player.getUsername(), true);
            //    skinCooldown.add(player.getUsername());
            //}
        });
    }

}
