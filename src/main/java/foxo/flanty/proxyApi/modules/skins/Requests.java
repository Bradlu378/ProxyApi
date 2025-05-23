package foxo.flanty.proxyApi.modules.skins;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.SkinRestorer.SRUtils;
import net.skinsrestorer.api.property.SkinProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static foxo.flanty.proxyApi.settings.Endpoints.skinUpdate;
import static foxo.flanty.proxyApi.settings.Language.apiResponseError;
import static foxo.flanty.proxyApi.settings.Language.apiUnavailable;

public class Requests {

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static CompletableFuture<Boolean> updateSkin(SkinProperty skinProperty, Player player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        String texture = skinProperty.getValue();
        String sign = skinProperty.getSignature();
        String[] decodedTexture = SRUtils.textureDecode(texture);

        if (decodedTexture == null || decodedTexture.length < 3) {
            Config.logger.error("Skin texture decode failed for player " + player.getUsername());
            future.complete(false);
            return future;
        }

        String json = String.format("""
            {
              "new_hash": "%s",
              "new_signature": "%s",
              "new_value": "%s",
              "new_variant": "%s"
            }""", decodedTexture[2], sign, texture, decodedTexture[1]);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(skinUpdate.replace("{nickname}", player.getUsername()))
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Config.logger.error(apiUnavailable + ": " + skinUpdate, e);
                future.complete(false);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        future.complete(true);
                    } else {
                        Config.logger.error(apiResponseError + " " + response.code());
                        future.complete(false);
                    }
                }
            }
        });

        return future;
    }
}
