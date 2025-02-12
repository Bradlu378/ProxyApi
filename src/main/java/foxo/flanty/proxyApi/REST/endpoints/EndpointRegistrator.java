package foxo.flanty.proxyApi.REST.endpoints;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.ProxyApi;
import io.javalin.Javalin;
import org.slf4j.Logger;

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
        if (app != null) {
            logger.warn("API is already running!");
            return;
        }
        app = Javalin.create().start(7000);
        app.post("/proxy/set-skin", Skins::setSkin);
        app.post("/proxy/auth/register", Auth::setPass);

    }
    public void disable() {
        if (app != null) {
            app.stop();
            app = null;
            logger.info("API stopped");
        } else logger.warn("API is not running!");
    }
}
