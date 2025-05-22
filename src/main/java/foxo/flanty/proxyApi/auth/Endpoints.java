package foxo.flanty.proxyApi.auth;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.settings.Language;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import io.javalin.http.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.json.JSONObject;

import java.util.Optional;

import static foxo.flanty.proxyApi.settings.Config.AuthedPlayers;

public class Endpoints {
    public static void loginRequest(Context ctx) {
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

    public static void logoutRequest(Context ctx) {
        JSONObject json = new JSONObject(ctx.body());

        String nickname = json.optString("nickname", null);

        if (nickname != null) {
            ctx.status(400);
            ctx.result("Zapros govna");
        }

        ctx.status(200);
        Optional<Player> player = Config.proxyServer.getPlayer(nickname);
        if (player == null) {
            ctx.status(404);
            ctx.result("Zapros govna");
        }
        player.get().disconnect(MiniMessage.miniMessage().deserialize(Language.logoutReason));

        AuthedPlayers.remove(nickname);
    }
}
