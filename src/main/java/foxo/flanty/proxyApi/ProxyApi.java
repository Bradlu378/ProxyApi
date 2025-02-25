package foxo.flanty.proxyApi;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.REST.endpoints.EndpointRegistrator;
import foxo.flanty.proxyApi.REST.requests.Auth;
import foxo.flanty.proxyApi.commands.Reload;
import foxo.flanty.proxyApi.listeners.EventRegistrator;
import foxo.flanty.proxyApi.settings.Config;
import net.elytrium.limboapi.api.LimboFactory;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "proxyapi", name = "ProxyApi", version = BuildConstants.VERSION, authors = {"Flanty"}, dependencies = {@Dependency(id = "limboapi"), @Dependency(id = "skinsrestorer")})
public class ProxyApi {
    private final Logger logger;
    private final ProxyServer server;
    private final LimboFactory limboFactory;

    @Inject
    public ProxyApi(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.server = server;

        this.limboFactory = (LimboFactory) this.server.getPluginManager()
                .getPlugin("limboapi")
                .flatMap(PluginContainer::getInstance)
                .orElseThrow(() -> new IllegalStateException("LimboAPI не найден!"));
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reload();
    }

    public void reload() {
        Config.logger = logger;
        Config.proxyServer = server;
        Config.proxy = this;

        server.getEventManager().unregisterListeners(this);
        new EventRegistrator(logger,this,server,limboFactory).register();


        EndpointRegistrator endpoints = new EndpointRegistrator(logger,this,server);
        endpoints.disable();
        endpoints.enable();

        Config.passwords.clear();
        Auth.getPasswords().thenAccept(passwords->Config.passwords = passwords);

        Config.authPlayers.clear();

        CommandManager commandManager = server.getCommandManager();
        commandManager.unregister("reload");
        commandManager.register("reload", new Reload());
    }
}
