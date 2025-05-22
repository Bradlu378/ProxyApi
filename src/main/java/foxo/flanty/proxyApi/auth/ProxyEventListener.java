package foxo.flanty.proxyApi.auth;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.Auth;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.settings.Language;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.UUID;

import static foxo.flanty.proxyApi.auth.Requests.getLoginRequest;
import static foxo.flanty.proxyApi.settings.Config.AuthedPlayers;
import static foxo.flanty.proxyApi.settings.Config.uuidGenerateKey;

public class ProxyEventListener {
    private final Limbo limbo;
    private final Auth proxy;
    private final Logger logger;

    public ProxyEventListener(Auth proxy, Limbo limbo, Logger logger) {
        this.proxy = proxy;
        this.limbo = limbo;
        this.logger = logger;
    }

    @Subscribe(priority = 32767)
    public void onPreLoginEvent(PreLoginEvent event) {
        Config.authPlayers.put(
                event.getUsername(),
                new AuthPlayer(event.getConnection().getRemoteAddress().toString(), 0L, false)
        );
    }

    /**
     * комментарий для тупых
     */
    @Subscribe(priority = 32767)
    public void onLogin(LoginLimboRegisterEvent event) {
        Player player = event.getPlayer();
        Login login = getLoginRequest(player.getUsername(), player.getUniqueId().toString(), player.getRemoteAddress().getAddress().toString()).join();
        if (!login.isWhitelisted) player.disconnect(MiniMessage.miniMessage().deserialize(Language.notInWhitelist));
        if (login.isLoggedIn) AuthedPlayers.add(player.getUsername());
        event.addOnJoinCallback(() -> limbo.spawnPlayer(event.getPlayer(), new LimboHandler(proxy, logger, login)));
    }

    /**
     * привод uuid подключаемого игрока к единому
     * + в придачу костыль для единичного срабатывания
     */
    @Subscribe(priority = 32767)
    public void changeUUID(GameProfileRequestEvent event) {
        if (!Config.authPlayers.get(event.getUsername()).online) {
            UUID uuid = UUID.nameUUIDFromBytes((event.getUsername() + uuidGenerateKey).getBytes());
            event.setGameProfile(event.getOriginalProfile().withId(uuid));
            Config.authPlayers.get(event.getUsername()).online = true;
        }
    }

    /**
     * дополнение к прошлому костылю, а то их мало шото
     */
    @Subscribe(priority = 32767)
    public void changeUUID(DisconnectEvent event) {
        Config.authPlayers.get(event.getPlayer().getUsername()).online = false;
    }
}
