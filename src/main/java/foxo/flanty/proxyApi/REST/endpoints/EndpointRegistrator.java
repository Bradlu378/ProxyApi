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
        if (app != null)
            return;
        app = Javalin.create().start("0.0.0.0", 7000);//todo явно установил 0.0.0.0, редуцент?
        app.post("/proxy/set-skin", Skins::setSkin);//эндпонит смены скина на стороне api
        app.put("/proxy/auth/password", Auth::setPass);//смена пароля/регистрация, 2in1.
    }
    public void disable() {
        if (app != null) {
            app.stop();
            app = null;
            logger.info("Endpoints stopped");
        }
    }
}
