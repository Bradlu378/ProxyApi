package foxo.flanty.proxyApi.settings;

public class Endpoints {
    public static String MojangAPI = "https://api.minecraftservices.com/minecraft/profile/lookup/";
    //public static String MojangAPI2 = "https://sessionserver.mojang.com/session/minecraft/profile/";
    public static String skinUpdate = "http://127.0.0.1:8000/api/skins/{nickname}/minecraft";//PUT
    public static String playerPasswordsHashes = "http://127.0.0.1:8000/api/passwords";//GET
    public static String playerRegister = "http://127.0.0.1:8000/api/auth/login";//GET ?username=nickname
    public static String discordLogger = "http://127.0.0.1:8000/api/notify/log";//POST //channel, level, location, message
    public static String playerJoin = "http://127.0.0.1:8000/api/notify/player-join";
}
