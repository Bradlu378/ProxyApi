package foxo.flanty.proxyApi.modules.skins;

import foxo.flanty.proxyApi.utils.SRUtils;
import io.javalin.http.Context;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import org.json.JSONObject;


public class Endpoints {

    public static void setSkin(Context ctx) {
        JSONObject json = new JSONObject(ctx.body());

        String url = json.optString("url", null);
        String texture = json.optString("texture", null);
        String sign = json.optString("sign", null);
        String player = json.optString("player", null);
        String variant = json.optString("variant", null);
        if (player == null && variant == null) {
            ctx.status(400).result("Invalid request");
            return;
        }

        if (url != null) {
            try {
                byte code = SRUtils.setSkin(url, player, SkinVariant.valueOf(variant));
                ctx.status(code == 1 ? 404 : code == 2 ? 500 : 200);
                return;
            } catch (IllegalArgumentException ignored) {
                ctx.status(400).result("Invalid request");
            }
        }

        if (texture != null && sign != null) {
            byte code = SRUtils.setSkin(SkinProperty.of(texture, sign), player);
            ctx.status(code == 1 ? 404 : code == 2 ? 500 : 200);
            return;
        }
        ctx.status(400).result("Invalid request");
    }
}
