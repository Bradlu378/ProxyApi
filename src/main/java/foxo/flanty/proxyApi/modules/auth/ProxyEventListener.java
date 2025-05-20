package foxo.flanty.proxyApi.modules.auth;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import io.javalin.http.util.JsonEscapeUtil;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.UUID;

import static foxo.flanty.proxyApi.modules.auth.Requests.getLoginRequest;
import static foxo.flanty.proxyApi.settings.Config.AuthedPlayers;

public class ProxyEventListener {
    private final Limbo limbo;
    private final ProxyApi proxy;
    private final Logger logger;

    public ProxyEventListener(ProxyApi proxy, Limbo limbo, Logger logger) {
        this.proxy = proxy;
        this.limbo = limbo;
        this.logger = logger;
    }
    @Subscribe(priority = 32767)
    public void onPreLoginEvent(PreLoginEvent event) {
        Config.authPlayers.put(event.getUsername(), new AuthPlayer(event.getConnection().getRemoteAddress().toString(),
                0L,
                 false));
    }

    /**
     * комментарий для тупых
     *
     */
    @Subscribe(priority = 32767)
    public void onLogin(LoginLimboRegisterEvent event) {
        Player player = event.getPlayer();
        Login login = getLoginRequest(player.getUsername(), player.getUniqueId().toString(),player.getRemoteAddress().getAddress().toString()).join();//пошло оно все нахуй, мне поебать
        System.out.println(login.toString());
        if (!login.is_whitelisted) player.disconnect(Component.text("You are not whitelisted", NamedTextColor.RED));//опять же поебать будет база
        if (login.is_logged_in) AuthedPlayers.add(player.getUsername());
        event.addOnJoinCallback(() -> limbo.spawnPlayer(event.getPlayer(), new LimboHandler(proxy, logger, login)));
    }

    /**
     * привод uuid подключаемого игрока к единому
     * + в придачу костыль для единичного срабатывания
     * todo: генерация по ключу?! O_o
     */
    @Subscribe(priority = 32767)
    public void changeUUID(GameProfileRequestEvent event) {
        if(!Config.authPlayers.get(event.getUsername()).online) {
            event.setGameProfile(event.getOriginalProfile().withId(UUID.nameUUIDFromBytes(event.getUsername().getBytes())));
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
