package foxo.flanty.proxyApi;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.auth.Endpoints;
import foxo.flanty.proxyApi.settings.Config;
import io.javalin.Javalin;
import org.slf4j.Logger;

import static foxo.flanty.proxyApi.settings.Endpoints.loginEndpoint;
import static foxo.flanty.proxyApi.settings.Endpoints.logoutEndpoint;

public class EndpointRegistrator {
    Logger logger;
    Auth proxyApi;
    ProxyServer proxyServer;
    Javalin app;

    public EndpointRegistrator(Logger logger, Auth proxyApi, ProxyServer proxyServer) {
        this.logger = logger;
        this.proxyApi = proxyApi;
        this.proxyServer = proxyServer;
    }

    public void enable() {
        if (app != null)
            return;
        try {
            app = Javalin.create().start(Config.httpPort);
            app.post(loginEndpoint, Endpoints::loginRequest);
            app.post(logoutEndpoint, Endpoints::logoutRequest);
        } catch (Exception ignored) {
            logger.warn("Failed to start auth api", ignored);
        }
    }

    public void disable() {
        if (app != null) {
            app.jettyServer().stop();
            app.stop();
            app = null;
        }
    }
}
