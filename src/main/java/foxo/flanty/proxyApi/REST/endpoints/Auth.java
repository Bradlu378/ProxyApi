package foxo.flanty.proxyApi.REST.endpoints;

import foxo.flanty.proxyApi.settings.Config;
import io.javalin.http.Context;
import org.json.JSONObject;

public class Auth {
    static void setPass(Context ctx) {
        JSONObject json = new JSONObject(ctx.body());

        String nickname = json.optString("player", null);
        String hashedPassword = json.optString("password", null);
        String ip = json.optString("ip", null);
        if (nickname == null | hashedPassword == null){
            ctx.status(400).result("Invalid request");;
            return;
        }
        Config.passwords.put(nickname, hashedPassword);
        if (ip != null) Config.lastIps.put(nickname, ip);

    }
}
