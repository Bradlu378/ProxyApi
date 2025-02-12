package foxo.flanty.proxyApi.REST.endpoints;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.SRUtils;
import io.javalin.http.Context;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import org.json.JSONObject;

public class Auth {
    static void setPass(Context ctx) {
        JSONObject json = new JSONObject(ctx.body());

        String nickname = json.optString("player", null);
        String hashedPassword = json.optString("password", null);
        if (nickname == null | hashedPassword == null){
            ctx.status(400).result("Invalid request");;
            return;
        }
        Config.passwords.put(nickname, hashedPassword);
    }
}
