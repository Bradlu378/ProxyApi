package foxo.flanty.proxyApi.modules.skins;

import foxo.flanty.proxyApi.SkinRestorer.SRUtils;
import io.javalin.http.Context;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import org.json.JSONObject;


public class Endpoints {
    /**
     * url+nickname+skinType/texture+sign+nickname
     * 400 - bad request
     * 404 - no player online
     * 500 - skin set error
     */
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
                short code = (short) SRUtils.setSkin(url, player, SkinVariant.valueOf(variant.toUpperCase())).ordinal();
                ctx.status(code == 1 ? 404 : code == 2 ? 500 : 200);
                return;
            } catch (IllegalArgumentException ignored) {
                ctx.status(400).result("Invalid request");
            }
        }

        if (texture != null && sign != null) {
            short code = (short) SRUtils.setSkin(SkinProperty.of(texture, sign), player).ordinal();
            ctx.status(code == 1 ? 404 : code == 2 ? 500 : 200);
            return;
        }
        ctx.status(400).result("Invalid request");
    }
}
