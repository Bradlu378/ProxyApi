package foxo.flanty.proxyApi.handlers;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.REST.requests.Auth;
import foxo.flanty.proxyApi.handlers.wrapper.LimboWrapper;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import foxo.flanty.proxyApi.utils.message.Style;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.mindrot.bcrypt.BCrypt;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static foxo.flanty.proxyApi.settings.Language.*;

public class AuthHandler extends LimboWrapper {
    private LimboPlayer limboPlayer;
    private Player player;
    byte loginAttempts = 0;//0-3
    long lastCommandTime = 0;
    MiniMessage miniMessage;
    //Stack<ScheduledFuture<?>> tasks = new Stack<>();
    long joinTime;
    public AuthHandler(ProxyApi plugin, Logger logger) {//логер и плагин шото здесь нахуй не сдались, ну и ладно
    }

    @Override
    public void handleSpawn(Limbo server, LimboPlayer limboPlayer, Player player, Logger logger) {
        this.limboPlayer = limboPlayer;
        this.player = player;
        limboPlayer.disableFalling();
        miniMessage = MiniMessage.miniMessage();
        if (Config.passwords.containsKey((player.getUsername())))
            login();
        else register();
        joinTime = System.currentTimeMillis();
    }

    private void authTime(long time) {
        BossBar bossBar = BossBar.bossBar(miniMessage.deserialize(bossBarName), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        if (Config.bossBar) player.showBossBar(bossBar);
        tasks.add(limboPlayer.getScheduledExecutor().scheduleWithFixedDelay(() -> {
            if (System.currentTimeMillis() - joinTime > time*1000)
                player.disconnect(Style.RED.style(loginTimeOut));
            else
                bossBar.progress(Math.min(1.0f, bossBar.progress()-((float) 1 /time)));
        }, 0, 1, TimeUnit.SECONDS));
    }

    private void registrationTitle() {
        AtomicInteger step = new AtomicInteger(0);
        String[] titles = {registrationTitle1, registrationTitle2, registrationTitle3, registrationTitle4};

        tasks.add(limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {
            limboPlayer.getProxyPlayer().showTitle(
                    Title.title(
                            Component.text(titles[step.getAndIncrement() % 4]),
                            Component.empty(),
                            Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO))
            );
        }, 1000, 700, TimeUnit.MILLISECONDS));
    }

    private void register() {
        authTime(Config.registerTime);//timeout + bossbar(optional)
        registrationTitle();//Title регистрации на экране
        //получение ссылки авторизации и вывод ее игроку от rest api
        Auth.register(limboPlayer.getProxyPlayer().getUsername(), String.valueOf(player.getUniqueId())).thenAccept(url->

               player.sendMessage(miniMessage
                       .deserialize(registerMessage).append(miniMessage.deserialize(urlPlaceholder)).clickEvent(ClickEvent.openUrl(url))));


        tasks.add(limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {//ждемс пока чел зарегается
            if (Config.registeredPlayers.contains(player.getUsername())) {//при прохождении регистрации
                Config.registeredPlayers.remove(player.getUsername());
                stopSchedulers();//остановка старых шедулеров которые были для регистрации, логин запустит новые
                login();
            }
        }, 1, 3, TimeUnit.SECONDS));
    }

    private void login() {
        //timeout + bossbar(optional)
        authTime(Config.authTime);
        AuthPlayer authPlayer = Config.authPlayers.get(player.getUsername());
        if (Objects.equals(authPlayer.licensedUUID, player.getUniqueId().toString())) {
            limboPlayer.disconnect();//челы с fake uuid пойдут по онлайну. Наверное... не проверял :З
            authPlayer.timestamp = System.currentTimeMillis();
            authPlayer.ip = player.getRemoteAddress().getAddress().toString();
            return;
        } else if (authPlayer.ip.equals(player.getRemoteAddress().getAddress().toString()) && (System.currentTimeMillis() - authPlayer.timestamp) < Config.loginSessionTime) {
            limboPlayer.disconnect();
            authPlayer.timestamp = System.currentTimeMillis();
            return;
        }
        limboPlayer.getProxyPlayer().sendMessage(miniMessage
                .deserialize(loginWelcome)
                .appendNewline()
                .append(miniMessage.deserialize(loginMessage)));
    }

    @Override
    public void handleChat(String message, String[] args, boolean isCommand) {
        if (!isCommand) return;
        if(loginAttempts>Config.loginAttempts) player.disconnect(miniMessage.deserialize(loginAttemptsOut));
        if ((System.currentTimeMillis()-lastCommandTime) <= 1500) {
            player.sendMessage(miniMessage.deserialize(commandDelay));
            return;
        }
        if((Objects.equals(args[0], "login") || args[0].equals("log")) && args.length == 2) {
            if (BCrypt.checkpw(args[1], Config.passwords.get(limboPlayer.getProxyPlayer().getUsername()))) {
                limboPlayer.disconnect();
                AuthPlayer authPlayer = Config.authPlayers.get(player.getUsername());
                authPlayer.ip = String.valueOf(player.getRemoteAddress().getAddress());
                authPlayer.timestamp = System.currentTimeMillis();
            } else {
                limboPlayer.getProxyPlayer().sendMessage(miniMessage.deserialize(wrongPassword));
                ++loginAttempts;
            }
        } else {
            player.sendMessage(miniMessage
                    .deserialize(wrongCommand)
                    .appendNewline()
                    .append(miniMessage.deserialize(loginCommandExample)));
        }
        lastCommandTime = System.currentTimeMillis();
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