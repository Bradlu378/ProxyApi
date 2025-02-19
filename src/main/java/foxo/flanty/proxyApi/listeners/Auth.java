package foxo.flanty.proxyApi.listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.handlers.AuthHandler;
import foxo.flanty.proxyApi.utils.AuthUtils;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static foxo.flanty.proxyApi.utils.AuthUtils.generateHash;

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
        //event.addCallback(() -> this.virtualServer.spawnPlayer(event.getPlayer(), new Handler()));
        System.out.println(isPremium(event.getPlayer().getUsername(),"84.21.173.13"));
    }

    public static void getEncodedKey(Player player) {
        Optional<GameProfile> profile = Optional.ofNullable(player.getGameProfile());
        profile.get().getProperties().forEach(prop -> {
            System.out.println(prop.getName());
            System.out.println(prop.getSignature());
            System.out.println(prop.getValue());
            System.out.println(prop);
            System.out.println("#############");
        });
    }
    public static boolean isPremium(String username, String serverIP) {
        try {
            // Генерируем serverId (можно заменить на UUID сервера)
            String serverId = generateHash(username + serverIP);

            // Запрашиваем у Mojang, есть ли сессия у игрока
            String url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username="
                    + username + "&serverId=" + serverId;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return true; // Лицензия подтверждена
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Игрок пират
    }
}
