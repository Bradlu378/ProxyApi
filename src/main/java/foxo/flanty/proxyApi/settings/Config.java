package foxo.flanty.proxyApi.settings;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.ProxyApi;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.*;

public class Config {
    public static ProxyApi proxy;
    public static ProxyServer proxyServer;
    public static Logger logger;
    public static MiniMessage miniMessage = MiniMessage.miniMessage();
    public static int httpPort = 7001;
}