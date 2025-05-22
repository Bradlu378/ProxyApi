package foxo.flanty.proxyApi;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.commands.Reload;
import foxo.flanty.proxyApi.settings.Config;
import net.elytrium.limboapi.api.LimboFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

import static foxo.flanty.proxyApi.settings.YamlUtils.loadConfigs;
import static foxo.flanty.proxyApi.settings.YamlUtils.saveConfigs;

@Plugin(
        id = "auth",
        name = "Auth",
        version = BuildConstants.VERSION,
        authors = {"Flanty", "wertiko"},
        dependencies = {
                @Dependency(id = "limboapi")
        }
)
public class Auth {
    private final Logger logger;
    private final ProxyServer server;
    private final LimboFactory limboFactory;
    private final Path dataDirectory;
    EndpointRegistrator endpoints;

    @Inject
    public Auth(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.server = server;

        this.limboFactory = (LimboFactory) this.server.getPluginManager()
                .getPlugin("limboapi")
                .flatMap(PluginContainer::getInstance)
                .orElseThrow(() -> new IllegalStateException("LimboAPI не найден!"));
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException, InterruptedException {
        endpoints = new EndpointRegistrator(logger, this, server);
        endpoints.enable();
        reload();
    }

    public void reload() throws IOException, InterruptedException {
        Config.logger = logger;
        Config.proxyServer = server;
        Config.proxy = this;

        if (!saveConfigs(dataDirectory)) {
            logger.error("Failed to save configs!");
            server.shutdown();
        }
        if (!loadConfigs(dataDirectory)) {
            logger.error("Failed to load configs!");
            server.shutdown();
        }

        server.getEventManager().unregisterListeners(this);
        new EventRegistrator(logger, this, server, limboFactory).register();

        Config.authPlayers.clear();

        CommandManager commandManager = server.getCommandManager();
        commandManager.unregister("authreload");
        commandManager.register("authreload", new Reload());
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        endpoints.disable();
    }
}
