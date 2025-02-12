package foxo.flanty.proxyApi.handlers;

import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.REST.requests.Auth;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.message.Style;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static foxo.flanty.proxyApi.settings.Language.*;

public class LoginHandler implements LimboSessionHandler {

    private final ProxyApi plugin;
    private LimboPlayer player;
    private final Logger logger;
    boolean authStage;

    public LoginHandler(ProxyApi plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer player) {
        this.player = player;
        player.disableFalling();
        authStage = Config.passwords.containsKey((player.getProxyPlayer().getUsername()));
        if (!authStage) login();
        else register();


        //ScheduledFuture<?> task1 = player.getScheduledExecutor().schedule(()->{
        //    logger.info("test");
        //    //player.getProxyPlayer().sendMessage(Component.text("da"));
        //},20,TimeUnit.SECONDS);
        //scheduledTasks.add(task1);

    }
    private void register() {
        AtomicInteger step = new AtomicInteger(0);
        player.getScheduledExecutor().scheduleAtFixedRate(() -> {
            switch (step.getAndIncrement() % 4) {
                case 0 -> player.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle1), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
                case 1 -> player.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle2), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
                case 2 -> player.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle3), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
                case 3 -> player.getProxyPlayer().showTitle(Title.title(Component.text(registrationTitle4), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)));
            }

        }, 0, 500, TimeUnit.MILLISECONDS);
        Auth.register(player.getProxyPlayer().getUsername());
    }
    private void login() {
        player.getProxyPlayer().sendMessage(Style.SCHALKER_1.style("Добро пожаловать на ").append(Style.GOLD.style(serverName)));
        player.getProxyPlayer().sendMessage(Style.DARK_AQUA.style("Авторизуйтесь с ").append(Style.WHITE.style("/login <пароль>")));
    }

    @Override
    public void onChat(String chat) {
        if (chat.equals("go back")) {
            player.disconnect();
        }
    }
    @Override
    public void onDisconnect() {
    }

    private void schedulerStop() {
        ScheduledExecutorService executor = player.getScheduledExecutor();
        executor.shutdown();
    }
}
