package foxo.flanty.proxyApi.REST.requests;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.utils.SRUtils;
import net.skinsrestorer.api.property.SkinProperty;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static foxo.flanty.proxyApi.settings.Endpoints.playerPasswordsHashes;
import static foxo.flanty.proxyApi.settings.Endpoints.playerRegister;
import static foxo.flanty.proxyApi.settings.Language.apiResponseError;

public class Auth {
    public static CompletableFuture<HashMap<String, String>> getPasswords() {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<HashMap<String, String>> future = new CompletableFuture<>();

        Request request = new Request.Builder().url(playerPasswordsHashes).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    JSONObject playersJson = json.getJSONObject("players");

                    HashMap<String, String> players = new HashMap<>();
                    for (String key : playersJson.keySet()) {
                        players.put(key, playersJson.getString(key));
                    }

                    future.complete(players);
                } else {
                    future.completeExceptionally(new IOException("HTTP error: " + response.code()));
                }
            }
        });
        return future;
    }
    public static CompletableFuture<String> register(String nickname) {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<String> future = new CompletableFuture<>();

        Request request = new Request.Builder().url(playerRegister + "?username=" + nickname).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    String url = json.getString("url");

                    future.complete(url);
                } else {
                    future.complete(null);
                    throw new IOException(apiResponseError + response.code());
                }
            }
        });
        return future;
    }
}
