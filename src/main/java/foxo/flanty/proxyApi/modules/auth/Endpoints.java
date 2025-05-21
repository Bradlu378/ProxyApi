package foxo.flanty.proxyApi.modules.auth;

import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import io.javalin.http.Context;
import org.json.JSONObject;

import static foxo.flanty.proxyApi.settings.Config.AuthedPlayers;

public class Endpoints {
    public static void setPass(Context ctx) {
        JSONObject json = new JSONObject(ctx.body());

        String nickname = json.optString("nickname", null);
        String ip = json.optString("ip", null);

        if (nickname == null && ip == null) {
            ctx.status(400);
            ctx.result("Zapros govna");
        }

        ctx.status(200);
        AuthPlayer authPlayer = Config.authPlayers.get(nickname);
        authPlayer.ip = ip;
        authPlayer.timestamp = System.currentTimeMillis();
        AuthedPlayers.add(nickname);
    }
}
