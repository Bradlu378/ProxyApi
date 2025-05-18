package foxo.flanty.proxyApi.modules.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.settings.Config;
import net.elytrium.limboapi.api.Limbo;
import okhttp3.*;
import org.slf4j.Logger;

import java.io.IOException;

import static foxo.flanty.proxyApi.settings.Endpoints.playerJoin;
import static foxo.flanty.proxyApi.utils.SkinRestorer.SRUtils.chechQueue;

public class ProxyEventListener {
    private ProxyApi proxy;
    private Logger logger;
    public ProxyEventListener(ProxyApi proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        com.velocitypowered.api.proxy.Player player = event.getPlayer();
        if (player == null) return;
        chechQueue(player.getUsername());

        //String playerName = player.getUsername();
        //String json = String.format("{\"player\": \"%s\"}", playerName);
//
        //OkHttpClient client = new OkHttpClient();
        //RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        //Request request = new Request.Builder().url(playerJoin).put(body).build();
//
        //client.newCall(request).enqueue(new Callback() {
        //    @Override
        //    public void onFailure(Call call, IOException e) {
        //        Config.logger.error("Failed to send join event: " + e.getMessage(), e);
        //    }
//
        //    @Override
        //    public void onResponse(Call call, Response response) {
        //        if (response.isSuccessful()) {
        //            Config.logger.info("Player join event sent successfully");
        //        } else {
        //            Config.logger.info("Failed to send join event: " + response.code());
        //        }
        //    }
        //});
    }
}
