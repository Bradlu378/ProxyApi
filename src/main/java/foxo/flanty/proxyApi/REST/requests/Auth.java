package foxo.flanty.proxyApi.REST.requests;

import foxo.flanty.proxyApi.settings.Config;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static foxo.flanty.proxyApi.settings.Endpoints.*;
import static foxo.flanty.proxyApi.settings.Language.apiResponseError;

public class Auth {
    public static CompletableFuture<HashMap<String, String>> getPasswords() {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<HashMap<String, String>> future = new CompletableFuture<>();

        Request request = new Request.Builder().url(playerPasswordsHashes).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Config.logger.error(e.getMessage());
                future.complete(new HashMap<>());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() && response.body() == null) {
                    future.completeExceptionally(new IOException("ГАВНО РЕСПОНС, ХУЙНЯ ДАВАЙ ПО НОВОЙ: " + response.code()));
                    future.complete(new HashMap<>());
                }
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray players = json.optJSONArray("players");

                    HashMap<String, String> playersMap = new HashMap<>();
                    for (int i = 0; i < players.length(); i++) {
                        JSONObject player = players.getJSONObject(i);
                        playersMap.put(
                                player.optString("username", null),
                                player.optString("password_hash", null));
                    }
                    future.complete(playersMap);
            }
        });
        return future;
    }
    public static CompletableFuture<String> register(String nickname, String uuid) {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<String> future = new CompletableFuture<>();

        Request request = new Request.Builder().url(playerRegister + "?username=" + nickname + "&uuid=" + uuid).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String responseBody = body.string();
                        JSONObject json = new JSONObject(responseBody);
                        String url = json.getString("url");

                        future.complete(url);
                    } else {
                        future.complete(null);
                        throw new IOException(apiResponseError + response.code());
                    }
                }
            }
        });
        return future;
    }
    public static CompletableFuture<Boolean> isLicense(String uuid) {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(MojangAPI + uuid).get().build();
        /*
        200 OK
        204 no profile
        400 wrong uuid?
        404 no profile
        429 rate limit, console spam + webhook?
        500 пизда крч
        */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                Config.logger.error("500",this);
                Config.logger.error(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        future.complete(true);
                    } else {
                        if (response.code() == 429 || response.code() == 500) {
                            Config.logger.error("Mojang api return 429/500, need to check!");
                        }
                        future.complete(false);
                    }
                }
            }
        });
        return future;
    }
}
