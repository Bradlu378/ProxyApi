package foxo.flanty.proxyApi;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.modules.skins.ProxyEventListener;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.command.LimboCommandMeta;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import org.slf4j.Logger;

import java.util.List;

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
        VirtualWorld world = factory.createVirtualWorld(Dimension.THE_END, 14, 100, 88, 90F, 0F);//пасхалко
        limbo = factory.createLimbo(world).registerCommand(new LimboCommandMeta(List.of("login", "log"))).setName("Auth");

        eventManager.register(proxyApi, new foxo.flanty.proxyApi.modules.auth.ProxyEventListener(proxyApi,limbo,logger));
        eventManager.register(proxyApi, new foxo.flanty.proxyApi.modules.player.ProxyEventListener(proxyApi,logger));
        SkinsRestorerProvider.get().getEventBus().subscribe(proxyApi, SkinApplyEvent.class, new ProxyEventListener()::onSkinApply);

    }
}
