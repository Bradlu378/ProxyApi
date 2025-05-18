package foxo.flanty.proxyApi.modules.skins;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.SkinRestorer.SRUtils;
import net.skinsrestorer.api.property.SkinProperty;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static foxo.flanty.proxyApi.settings.Endpoints.skinUpdate;
import static foxo.flanty.proxyApi.settings.Language.apiResponseError;
import static foxo.flanty.proxyApi.settings.Language.apiUnavailable;

public class Requests {

    public static CompletableFuture<Boolean> updateSkin(SkinProperty skinProperty, Player player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        String texture = skinProperty.getValue();
        String sign = skinProperty.getSignature();
        String[] decodedTexture = SRUtils.textureDecode(texture);

        String json = String.format(
                "{\"new_hash\": \"%s\", \"new_signature\": \"%s\", \"new_value\": \"%s\", \"new_variant\": \"%s\"}",
                decodedTexture[2], sign, texture, decodedTexture[1]
        );

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(skinUpdate.replace("{nickname}",player.getUsername())).put(body).build();//кто укажет конфиг не правильно, тот долбаеб

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Config.logger.error(apiUnavailable + ": " + skinUpdate);
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
