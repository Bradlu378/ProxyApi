package foxo.flanty.proxyApi.listeners;

import com.velocitypowered.api.event.Subscribe;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.handlers.LoginHandler;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import org.slf4j.Logger;

public class Auth {
    private final Limbo limbo;
    private final ProxyApi proxy;
    private final Logger logger;
    public Auth(ProxyApi proxy, Limbo limbo, Logger logger) {
        this.proxy = proxy;
        this.limbo = limbo;
        this.logger = logger;
    }
    @Subscribe(priority = 32767)
    public void onLogin(LoginLimboRegisterEvent event) {
        event.addOnJoinCallback(() -> limbo.spawnPlayer(event.getPlayer(), new LoginHandler(proxy,logger)));
        //event.addCallback(() -> this.virtualServer.spawnPlayer(event.getPlayer(), new Handler()));
    }
}
