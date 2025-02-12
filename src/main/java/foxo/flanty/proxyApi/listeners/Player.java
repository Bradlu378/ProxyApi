package foxo.flanty.proxyApi.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.settings.Config;
import net.elytrium.limboapi.api.Limbo;
import okhttp3.*;
import org.slf4j.Logger;

import java.io.IOException;

public class Player {
    private final Limbo limbo;
    private ProxyApi proxy;
    private Logger logger;
    public Player(ProxyApi proxy, Limbo limbo, Logger logger) {
        this.proxy = proxy;
        this.limbo = limbo;
        this.logger = logger;
    }
    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        com.velocitypowered.api.proxy.Player player = event.getPlayer();
        if (player == null) return;

        String playerName = player.getUsername();
        String json = String.format("{\"player\": \"%s\"}", playerName);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url("http://localhost:7001/bot/player-join").put(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Config.logger.error("Failed to send player join event: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Config.logger.info("Player join event sent successfully");
                } else {
                    Config.logger.info("Failed to send player join event: " + response.code());
                }
            }
        });
    }
}
