package foxo.flanty.proxyApi.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.handlers.AuthHandler;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import org.slf4j.Logger;

import java.util.Optional;


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
        event.addOnJoinCallback(() -> limbo.spawnPlayer(event.getPlayer(), new AuthHandler(proxy,logger)));
    }

    @Subscribe(order = PostOrder.LATE)
    public void onPreLoginEvent(PreLoginEvent event) {
        System.out.println(event.getUniqueId());

        foxo.flanty.proxyApi.REST.requests.Auth.isLicense(event.getUsername()).thenAccept(auth -> {
                    String UUID;
                    if (auth) UUID = event.getUsername();
                    else UUID = null;
                    if (Config.authPlayers.get(event.getUsername())==null) {
                        foxo.flanty.proxyApi.REST.requests.Auth.isLicense(event.getUsername());
                        AuthPlayer authPlayer = new AuthPlayer(
                                event.getConnection().getRemoteAddress().toString()
                                ,System.currentTimeMillis()
                                ,UUID);
                        Config.authPlayers.put(event.getUsername(), authPlayer);
        });


        }
        event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
    }
}
