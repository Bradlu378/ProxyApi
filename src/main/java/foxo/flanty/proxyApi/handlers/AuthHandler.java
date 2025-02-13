package foxo.flanty.proxyApi.handlers;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.REST.requests.Auth;
import foxo.flanty.proxyApi.handlers.wrapper.LimboWrapper;
import foxo.flanty.proxyApi.handlers.wrapper.WrapperMode;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.message.Style;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.mindrot.bcrypt.BCrypt;
import org.slf4j.Logger;

import java.time.Duration;
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
    //BossBar bossBar = BossBar.bossBar();
    ScheduledFuture<?> mainExecutor;
    public AuthHandler(ProxyApi plugin, Logger logger) {
        super(WrapperMode.FULL);
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void handleSpawn(Limbo server, LimboPlayer limboPlayer, Player player, Logger logger) {
        this.limboPlayer = limboPlayer;
        this.player = player;
        limboPlayer.disableFalling();
        authStage = Config.passwords.containsKey((player.getUsername()));
        if (authStage) login();
        else register();


        //ScheduledFuture<?> task1 = player.getScheduledExecutor().schedule(()->{
        //    logger.info("test");
        //    //player.getProxyPlayer().sendMessage(Component.text("da"));
        //},20,TimeUnit.SECONDS);
        //scheduledTasks.add(task1);

    }
    private void register() {
        AtomicInteger step = new AtomicInteger(0);
        mainExecutor = limboPlayer.getScheduledExecutor().scheduleAtFixedRate(() -> {
            switch (step.getAndIncrement() % 4) {
                case 0 -> limboPlayer.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle1), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
                case 1 -> limboPlayer.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle2), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
                case 2 -> limboPlayer.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle3), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
                case 3 -> limboPlayer.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle4), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
            }

        }, 0, 500, TimeUnit.MILLISECONDS);
        Auth.register(limboPlayer.getProxyPlayer().getUsername()).thenAccept(url->
            limboPlayer.getProxyPlayer().sendMessage(Component
                    .text("Зарегистрируйтесь по ссылке\n", NamedTextColor.DARK_AQUA)
                    .append(Component.text(url, NamedTextColor.WHITE, TextDecoration.ITALIC)
                            .clickEvent(ClickEvent.openUrl(url)))));

    }
    private void login() {
        limboPlayer.getProxyPlayer().sendMessage(Style.SCHALKER_1.style("Добро пожаловать на ").append(Style.GOLD.style(serverName)));
        limboPlayer.getProxyPlayer().sendMessage(Style.DARK_AQUA.style("Авторизуйтесь с ").append(Style.WHITE.style("/login <пароль>")));
    }

    @Override
    public void handleChat(String message, String[] args, boolean isCommand) {
        if (!isCommand) return;
        switch (args[0]) {
            case "login":
                if(args.length == 2) {
                    if (BCrypt.checkpw(args[1], Config.passwords.get(limboPlayer.getProxyPlayer().getUsername()))) {
                       limboPlayer.disconnect();
                    } else limboPlayer.getProxyPlayer().sendMessage(Style.RED.style("Неверный пароль"));
                }
                break;
        }
    }

    @Override
    public void handleMove(double posX, double posY, double posZ, float yaw, float pitch) {
    }

    @Override
    public void handleGeneric(Object packet) {
    }

    @Override
    public void handleDisconnect() {
        mainExecutor.cancel(true);
        //Config.proxyServer.getAllServers().stream().findFirst().ifPresent(server -> player.getProxyPlayer().createConnectionRequest(server).connect());
    }
}
