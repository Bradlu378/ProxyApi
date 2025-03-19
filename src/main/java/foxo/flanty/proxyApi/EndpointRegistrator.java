package foxo.flanty.proxyApi;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.modules.skins.Endpoints;
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
        app = Javalin.create().start(7000);
        app.post("/proxy/set-skin", Endpoints::setSkin);//эндпонит смены скина на стороне api
        app.put("/proxy/auth/password", foxo.flanty.proxyApi.modules.auth.Endpoints::setPass);//смена пароля/регистрация, 2in1.
    }
    public void disable() {
        if (app != null) {
                app.jettyServer().stop();
                app.stop();
                app = null;
        }
    }
}
