package foxo.flanty.proxyApi.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.handlers.AuthHandler;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import org.slf4j.Logger;

import java.util.UUID;

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
        event.addOnJoinCallback(() -> limbo.spawnPlayer(event.getPlayer(), new AuthHandler(proxy, logger)));
    }

    @Subscribe(priority = 32767)
    public void onPreLoginEvent(PreLoginEvent event) {
        String uuid = event.getUniqueId().toString();
        String username = event.getUsername();
        AuthPlayer authPlayer = Config.authPlayers.get(username);
        boolean sameUUID = foxo.flanty.proxyApi.REST.requests.Auth.isLicense(uuid).join();

        if (authPlayer != null) {
            if (System.currentTimeMillis() - authPlayer.licenseTimestamp > 43200000L | authPlayer.licensedUUID == null) {//обновление uuid если старше 12ч
                    if (sameUUID) {
                        authPlayer.licenseTimestamp = System.currentTimeMillis();
                        authPlayer.licensedUUID = uuid;
                        event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
                    }
            } else if (uuid.equals(authPlayer.licensedUUID))
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
        } else {
                Config.authPlayers.put(
                        username,
                        new AuthPlayer(event.getConnection().getRemoteAddress().toString(),
                                0L,
                                sameUUID ? uuid : null,
                                System.currentTimeMillis()));
                if (sameUUID){
                    event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
                }
        }
    }

    @Subscribe(priority = 32767)
    public void changeUUID(GameProfileRequestEvent event) {
        if(!Config.authPlayers.get(event.getUsername()).online) {
            event.setGameProfile(event.getOriginalProfile().withId(UUID.nameUUIDFromBytes(event.getUsername().getBytes())));
            Config.authPlayers.get(event.getUsername()).online = true;
        }
    }
    @Subscribe(priority = 32767)
    public void changeUUID(DisconnectEvent event) {
        Config.authPlayers.get(event.getPlayer().getUsername()).online = false;
    }
}
