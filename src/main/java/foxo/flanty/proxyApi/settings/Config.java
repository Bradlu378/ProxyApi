package foxo.flanty.proxyApi.settings;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.ProxyApi;
import foxo.flanty.proxyApi.utils.AuthPlayer;
import org.slf4j.Logger;

import java.util.*;

public class Config {
    public static ProxyApi proxy;
    public static ProxyServer proxyServer;
    public static Logger logger;
    public static Map<String,String> passwords = new HashMap<>();
    public static Map<String, AuthPlayer> authPlayers = new HashMap<>();
    public static Set<String> registeredPlayers = new HashSet<>();
    public static int authTime = 45;
    public static int registerTime = 90;
    public static boolean bossBar = true;
    public static boolean discordLoggingEnabled = true;
    public static int loginAttempts = 3;
    public static int loginSessionTime = 12;//hours
    public static int httpPort = 7000;


}