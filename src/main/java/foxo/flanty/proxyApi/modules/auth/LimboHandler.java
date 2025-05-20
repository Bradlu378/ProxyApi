package foxo.flanty.proxyApi.modules.auth;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.utils.LimboWrapper;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import foxo.flanty.proxyApi.utils.message.Style;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
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
    public LimboHandler(ProxyApi plugin, Logger logger, Login login) {//логер и плагин шото здесь нахуй не сдались, ну и ладно. UPDATE АПХАХВПХАВХПАВХПХ ПОХУЙ
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
        BossBar bossBar = BossBar.bossBar(miniMessage.deserialize(bossBarName), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        if (Config.bossBar) player.showBossBar(bossBar);
        long joinTime = System.currentTimeMillis();
        tasks.add(limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - joinTime > time*1000)
                player.disconnect(Style.RED.style(loginTimeOut));
            else
                bossBar.progress(Math.max(0.0f, bossBar.progress() - (1.0f / time)));
        }, 0, 1, TimeUnit.SECONDS));
    }

    private void needLogin() {
        authTime(Config.authTime);
        AuthPlayer authPlayer = Config.authPlayers.get(player.getUsername());
         //if (authPlayer.ip.equals(player.getRemoteAddress().getAddress().toString()) && (System.currentTimeMillis() - authPlayer.timestamp) < Config.loginSessionTime*3600000L) {
         //   limboPlayer.disconnect();
         //   authPlayer.timestamp = System.currentTimeMillis();
         //   return;
         //}
         if (AuthedPlayers.contains(player.getUsername())) {
             limboPlayer.disconnect();//ваще похуй, я строю на костях старой системы
             AuthedPlayers.remove(player.getUsername());
         }

         limboPlayer.getProxyPlayer().sendMessage(miniMessage
                 .deserialize(loginWelcome)
                 .appendNewline()
                 .append(miniMessage.deserialize(loginMessage))
                 .append(Component.text("discord.com")
                         .clickEvent(ClickEvent.openUrl(login.url))
                         .hoverEvent(showText(Component.text("Перейти на сайт авторизации")))));
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