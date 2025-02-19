package foxo.flanty.proxyApi.settings;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import org.slf4j.Logger;

import javax.net.ssl.HandshakeCompletedEvent;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static ProxyApi proxy;
    public static ProxyServer proxyServer;
    public static Logger logger;
    public static Map<String,String> passwords = new HashMap<>();
    public static int authTime = 45;
    public static boolean bossBar = true;
    public static boolean discordLoggingEnabled = true;
    public static Map<String, AuthPlayer> authPlayers = new HashMap<>();
}