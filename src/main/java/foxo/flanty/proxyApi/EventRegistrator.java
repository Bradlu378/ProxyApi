package foxo.flanty.proxyApi;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.modules.skins.ProxyEventListener;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import org.slf4j.Logger;

import java.util.List;

public class EventRegistrator {
    Logger logger;
    ProxyApi proxyApi;
    ProxyServer server;
    EventManager eventManager;

    public EventRegistrator(Logger logger, ProxyApi proxyApi, ProxyServer server) {
        this.logger = logger;
        this.proxyApi = proxyApi;
        this.server = server;
    }

    public void register() {
        eventManager = server.getEventManager();
        eventManager.register(proxyApi, new foxo.flanty.proxyApi.modules.player.ProxyEventListener(proxyApi,logger));
        SkinsRestorerProvider.get().getEventBus().subscribe(proxyApi, SkinApplyEvent.class, new ProxyEventListener()::onSkinApply);

    }
}
