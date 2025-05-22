package foxo.flanty.proxyApi.auth;

import foxo.flanty.proxyApi.settings.Config;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static foxo.flanty.proxyApi.settings.Endpoints.*;

public class Requests {
    public static CompletableFuture<Login> getLoginRequest(String username, String uuid, String ip) {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<Login> future = new CompletableFuture<>();

        Request request = new Request.Builder().url(playerLoginRequest).post(
                RequestBody.create(
                new JSONObject().put("username", username).put("uuid", uuid).put("ip", ip).toString(),
                MediaType.parse("application/json")
        )).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Config.logger.error(e.getMessage());
                future.complete(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("api bad response: " + response.code()));
                    future.complete(null);
                }
                    JSONObject json = new JSONObject(response.body().string());
                Login login = new Login(
                            json.getBoolean("is_whitelisted"),
                            json.getBoolean("is_logged_in"),
                            json.getString("url"));

                    future.complete(login);
            }
        });
        return future;
    }
}
