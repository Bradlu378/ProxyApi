package foxo.flanty.proxyApi.settings;

import com.velocitypowered.api.proxy.ProxyServer;
import foxo.flanty.proxyApi.ProxyApi;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Config {
    public static ProxyApi proxy;
    public static ProxyServer proxyServer;
    public static Logger logger;
    public static Map<String,String> passwords = new HashMap<>();
    public static Map<String,String> lastIps = new HashMap<>();
    public static int authTime = 45;
    public static boolean bossBar = true;
}