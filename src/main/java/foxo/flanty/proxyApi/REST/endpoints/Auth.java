package foxo.flanty.proxyApi.REST.endpoints;

import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import io.javalin.http.Context;
import org.json.JSONObject;

public class Auth {
    static void setPass(Context ctx) {
        JSONObject json = new JSONObject(ctx.body());

        String nickname = json.optString("nickname", null);
        String hashedPassword = json.optString("password", null);
        String ip = json.optString("ip", null);

        if (nickname == null | hashedPassword == null){
            ctx.status(400).result("Invalid request");
            return;
        }
        ctx.status(200);
        AuthPlayer authPlayer = Config.authPlayers.get(nickname);
        authPlayer.ip = ip;
        authPlayer.timestamp = System.currentTimeMillis();


        Config.passwords.put(nickname, hashedPassword);
        //todo last ip after registration. Вроде готово?!
    }
}
