package foxo.flanty.proxyApi.settings;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.Auth;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import org.slf4j.Logger;

import java.util.*;

public class Config {
    public static Auth proxy;
    public static ProxyServer proxyServer;
    public static Logger logger;
    public static Map<String, AuthPlayer> authPlayers = new HashMap<>();
    public static Set<String> AuthedPlayers = new HashSet<>();
    public static int authTime = 45;
    public static boolean bossBar = true;
    public static int httpPort = 7000;
    public static String uuidGenerateKey = "super_secret_key";
}