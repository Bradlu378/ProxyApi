package foxo.flanty.proxyApi.listeners;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.ProxyApi;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import org.slf4j.Logger;

public class EventRegistrator {
    Logger logger;
    ProxyApi proxyApi;
    ProxyServer server;
    LimboFactory factory;
    Limbo limbo;
    EventManager eventManager;

    public EventRegistrator(Logger logger, ProxyApi proxyApi, ProxyServer server, LimboFactory factory) {
        this.logger = logger;
        this.proxyApi = proxyApi;
        this.server = server;
        this.factory = factory;
    }

    public void register() {
        eventManager = server.getEventManager();
        VirtualWorld world = factory.createVirtualWorld(Dimension.THE_END, 14, 100, 88, 90F, 0F);
        limbo = factory.createLimbo(world);

        eventManager.register(proxyApi, new Auth(proxyApi,limbo,logger));
        SkinsRestorerProvider.get().getEventBus().subscribe(proxyApi, SkinApplyEvent.class, new Skins()::onSkinApply);

    }
}
