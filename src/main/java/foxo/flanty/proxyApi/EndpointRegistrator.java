package foxo.flanty.proxyApi;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.modules.skins.Endpoints;
import foxo.flanty.proxyApi.settings.Config;
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
        if (app != null)
            return;
        app = Javalin.create().start(Config.httpPort);
        app.post("/proxy/set-skin", Endpoints::setSkin);//эндпонит смены скина на стороне api
    }
    public void disable() {
        if (app != null) {
                app.jettyServer().stop();
                app.stop();
                app = null;
        }
    }
}
