package foxo.flanty.proxyApi.auth;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.Auth;
import foxo.flanty.proxyApi.settings.Language;
import foxo.flanty.proxyApi.utils.LimboWrapper;
import foxo.flanty.proxyApi.settings.Config;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static foxo.flanty.proxyApi.settings.Config.AuthedPlayers;
import static foxo.flanty.proxyApi.settings.Language.*;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class LimboHandler extends LimboWrapper {
    private LimboPlayer limboPlayer;
    private Player player;
    MiniMessage miniMessage;
    Login login;

    public LimboHandler(Auth plugin, Logger logger, Login login) {
        this.login = login;
    }

    @Override
    public void handleSpawn(Limbo server, LimboPlayer limboPlayer, Player player, Logger logger) {
        this.limboPlayer = limboPlayer;
        this.player = player;
        limboPlayer.disableFalling();
        miniMessage = MiniMessage.miniMessage();
        needLogin();
    }

    private void authTime(long time) {
        BossBar bossBar = BossBar.bossBar(miniMessage.deserialize(bossBarName), 1.0f, BossBar.Color.valueOf(bossBarColor), BossBar.Overlay.PROGRESS);
        if (Config.bossBar) player.showBossBar(bossBar);
        long joinTime = System.currentTimeMillis();
        tasks.add(limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - joinTime > time * 1000)
                player.disconnect(miniMessage.deserialize(loginTimeOut));
            else
                bossBar.progress(Math.max(0.0f, bossBar.progress() - (1.0f / time)));
        }, 0, 1, TimeUnit.SECONDS));
    }

    private void needLogin() {
        authTime(Config.authTime);
        if (AuthedPlayers.contains(player.getUsername())) {
            limboPlayer.disconnect();
            AuthedPlayers.remove(player.getUsername());
            return;
        }

        Component urlComponent = miniMessage.deserialize(urlPlaceholder)
                .clickEvent(ClickEvent.openUrl(login.url))
                .hoverEvent(showText(miniMessage.deserialize(urlHoverText)));

        Component component = miniMessage.deserialize(loginWelcome)
                .appendNewline()
                .append(miniMessage.deserialize(loginMessage));

        limboPlayer.getProxyPlayer().sendMessage(component.replaceText("%url%", urlComponent));
        tasks.add(limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {
            if (AuthedPlayers.contains(player.getUsername())) {
                AuthedPlayers.remove(player.getUsername());
                limboPlayer.disconnect();
            }
        }, 0, 1, TimeUnit.SECONDS));
    }

    @Override
    public void handleChat(String message, String[] args, boolean isCommand) {
    }

    @Override
    public void handleMove(double posX, double posY, double posZ, float yaw, float pitch) {
    }

    @Override
    public void handleGeneric(Object packet) {
    }

    private void stopSchedulers() {
        for (ScheduledFuture<?> future : tasks) {
            future.cancel(true);
        }
        tasks.clear();
    }

    @Override
    public void handleDisconnect() {
        stopSchedulers();
    }
}