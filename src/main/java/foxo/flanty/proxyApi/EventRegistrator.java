package foxo.flanty.proxyApi;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.auth.ProxyEventListener;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import org.slf4j.Logger;

public class EventRegistrator {
    Logger logger;
    Auth proxyApi;
    ProxyServer server;
    LimboFactory factory;
    Limbo limbo;
    EventManager eventManager;

    public EventRegistrator(Logger logger, Auth proxyApi, ProxyServer server, LimboFactory factory) {
        this.logger = logger;
        this.proxyApi = proxyApi;
        this.server = server;
        this.factory = factory;
    }

    public void register() {
        eventManager = server.getEventManager();
        VirtualWorld world = factory.createVirtualWorld(Dimension.THE_END, 14, 42, 88, 90F, 0F);
        limbo = factory.createLimbo(world).setName("Auth");

        eventManager.register(proxyApi, new ProxyEventListener(proxyApi, limbo, logger));
    }
}
