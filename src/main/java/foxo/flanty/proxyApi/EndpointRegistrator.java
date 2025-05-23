package foxo.flanty.proxyApi;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.modules.skins.Endpoints;
import foxo.flanty.proxyApi.settings.Config;
import io.javalin.Javalin;
import org.slf4j.Logger;

import static foxo.flanty.proxyApi.settings.Endpoints.skinSet;

public class EndpointRegistrator {
    Logger logger;
    ProxyApi proxyApi;
    ProxyServer proxyServer;
    Javalin app;

    public EndpointRegistrator(Logger logger, ProxyApi proxyApi, ProxyServer proxyServer) {
        this.logger = logger;
        this.proxyApi = proxyApi;
        this.proxyServer = proxyServer;
    }

    public void enable() {
        if (app != null)
            return;
        try {
            app = Javalin.create().start(Config.httpPort);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        app.post(skinSet, Endpoints::setSkin);
    }

    public void disable() {
        if (app != null) {
            app.jettyServer().stop();
            app.stop();
            app = null;
        }
    }
}
