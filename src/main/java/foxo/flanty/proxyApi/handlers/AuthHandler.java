package foxo.flanty.proxyApi.handlers;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.REST.requests.Auth;
import foxo.flanty.proxyApi.handlers.wrapper.LimboWrapper;
import foxo.flanty.proxyApi.handlers.wrapper.WrapperMode;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import foxo.flanty.proxyApi.utils.message.Style;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.mindrot.bcrypt.BCrypt;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static foxo.flanty.proxyApi.settings.Language.*;

public class AuthHandler extends LimboWrapper {

    private final ProxyApi plugin;
    private LimboPlayer limboPlayer;
    private Player player;
    private final Logger logger;
    boolean authStage;
    int loginAttempts = 0;
    long joinTime;
    long lastCommandTime = 0;
    BossBar bossBar = BossBar.bossBar(Style.GOLD.style(bossBarName),1.0f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
    Stack<ScheduledFuture<?>> mainExecutor = new Stack<>();
    public AuthHandler(ProxyApi plugin, Logger logger) {
        super(WrapperMode.FULL);
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void handleSpawn(Limbo server, LimboPlayer limboPlayer, Player player, Logger logger) {
        joinTime = System.currentTimeMillis();
        this.limboPlayer = limboPlayer;
        this.player = player;
        limboPlayer.disableFalling();
        authStage = Config.passwords.containsKey((player.getUsername()));
        if (authStage) login();
        else register();
    }

    private void authTime(long time) {
        if (Config.bossBar) player.showBossBar(bossBar);//bossbar
        mainExecutor.add(limboPlayer.getScheduledExecutor().scheduleWithFixedDelay(() -> {
            if (System.currentTimeMillis() - joinTime > time*1000)
                player.disconnect(Style.RED.style(loginTimeOut));
            else
                bossBar.progress(Math.min(1.0f, bossBar.progress()-((float) 1 /time)));
        }, 0, 1, TimeUnit.SECONDS));
    }
    private void registrationTitle() {
        AtomicInteger step = new AtomicInteger(0);
        String[] titles = {registrationTitle1, registrationTitle2, registrationTitle3, registrationTitle4};

        mainExecutor.add(limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {
            limboPlayer.getProxyPlayer().showTitle(
                    Title.title(
                            Component.text(titles[step.getAndIncrement() % 4]),
                            Component.empty(),
                            Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO))
            );
        }, 1000, 700, TimeUnit.MILLISECONDS));
    }

    private void register() {
        authTime(Config.registerTime);
        //Title регистрации на экране
        registrationTitle();
        //получение ссылки авторизации от rest api бота
        Auth.register(limboPlayer.getProxyPlayer().getUsername(), String.valueOf(player.getUniqueId())).thenAccept(url->
            limboPlayer.getProxyPlayer().sendMessage(Component
                    .text("Для регистрации перейдите по ссылке:\n", NamedTextColor.DARK_AQUA)
                    .append(Component
                            .text(url, NamedTextColor.WHITE, TextDecoration.ITALIC)
                            .clickEvent(ClickEvent.openUrl(url)).decorate(TextDecoration.UNDERLINED))));
        mainExecutor.add(limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {
            if (Config.registeredPlayers.contains(player.getUsername())) {
                Config.registeredPlayers.remove(player.getUsername());
                stopSchedulers();
                login();
            }
        }, 1, 3, TimeUnit.SECONDS));
    }

    private void login() {
        authTime(Config.authTime);
        AuthPlayer authPlayer = Config.authPlayers.get(player.getUsername());
        if (Objects.equals(authPlayer.licensedUUID, player.getUniqueId().toString())) {
            limboPlayer.disconnect();//есть гарантия что челы с тем uuid полюбас пойдут по онлайну. Наверное......
        } else if (authPlayer.ip.equals(player.getRemoteAddress().getAddress().toString()) && (System.currentTimeMillis() - authPlayer.timestamp) < 86400000L) {
            limboPlayer.disconnect();
            authPlayer.timestamp = System.currentTimeMillis();
        }
        limboPlayer.getProxyPlayer().sendMessage(Style.SCHALKER_1.style("Добро пожаловать на ").append(Style.GOLD.style(serverName)));
        limboPlayer.getProxyPlayer().sendMessage(Style.DARK_AQUA.style("Авторизуйтесь с ").append(Style.WHITE.style("/login <пароль>")));
    }

    @Override
    public void handleChat(String message, String[] args, boolean isCommand) {
        if (!isCommand) return;
        if(loginAttempts>Config.loginAttempts) player.disconnect(Style.RED.style(loginAttemptsOut));
        if ((System.currentTimeMillis()-lastCommandTime) <= 1500) {
            player.sendMessage(Style.RED.style(commandDelay));
            return;
        }
        if((Objects.equals(args[0], "login") || args[0].equals("log")) && args.length == 2) {
            if (BCrypt.checkpw(args[1], Config.passwords.get(limboPlayer.getProxyPlayer().getUsername()))) {
                limboPlayer.disconnect();
                AuthPlayer authPlayer = Config.authPlayers.get(player.getUsername());
                authPlayer.ip = String.valueOf(player.getRemoteAddress().getAddress());
                authPlayer.timestamp = System.currentTimeMillis();
            } else {
                limboPlayer.getProxyPlayer().sendMessage(Style.RED.style("Неверный пароль"));
                ++loginAttempts;
            }
        } else {
            player.sendMessage(Style.RED.style("Неверная команда, пример:"));
            player.sendMessage(Style.WHITE.style("/login <пароль>"));
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
        for (ScheduledFuture<?> future : mainExecutor) {
            future.cancel(true);
        }
    }

    @Override
    public void handleDisconnect() {
        stopSchedulers();
    }
}