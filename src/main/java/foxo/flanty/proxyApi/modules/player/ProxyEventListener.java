package foxo.flanty.proxyApi.modules.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import foxo.flanty.proxyApi.ProxyApi;
import org.slf4j.Logger;

import static foxo.flanty.proxyApi.SkinRestorer.SRUtils.checkQueue;

public class ProxyEventListener {
    private ProxyApi proxy;
    private Logger logger;

    public ProxyEventListener(ProxyApi proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        com.velocitypowered.api.proxy.Player player = event.getPlayer();
        if (player == null) return;
        checkQueue(player.getUsername());
    }
}
