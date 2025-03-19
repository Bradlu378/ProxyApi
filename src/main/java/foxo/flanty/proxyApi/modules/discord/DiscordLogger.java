package foxo.flanty.proxyApi.modules.discord;

import foxo.flanty.proxyApi.settings.Config;
import okhttp3.*;
import java.io.IOException;

import static foxo.flanty.proxyApi.settings.Endpoints.discordLogger;
import static foxo.flanty.proxyApi.settings.Language.apiUnavailable;


//todo: нахуя я это писал?
//в любом случае хуета не дописанная, так и не согласованная с авиком

@Deprecated(forRemoval = true)
public class DiscordLogger {

    public static void log(String message, String channel, LogLevel logLevel) {
        String json = String.format(
                "{\"channel\": \"%s\", \"level\": \"%s\", \"location\": \"%s\", \"message\": \"%s\"}",
                channel, logLevel.level, "ProxyApi", message
        );

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(discordLogger).put(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Config.logger.error(apiUnavailable, e);
            }

            @Override
            public void onResponse(Call call, Response response) {}
        });
    }

    public enum LogLevel {
        INFO(0),
        DEBUG(1),
        WARN(2),
        ERROR(3);

        public int level;

        LogLevel(int level) {
            this.level = level;
        }
    }

}
