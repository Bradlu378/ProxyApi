package foxo.flanty.proxyApi.settings;

public class Endpoints {
    public static final String MojangAPI = "https://api.minecraftservices.com/minecraft/profile/lookup/";
    public static final String MojangAPI2 = "https://sessionserver.mojang.com/session/minecraft/profile/";
    public static final String skinUpdate = "http://localhost:7001/api/player-skinchange";//PUT
    public static final String playerPasswordsHashes = "http://localhost:8000/api/auth/passwords";//GET
    public static final String playerRegister = "http://localhost:8000/api/auth/login";//GET ?username=nickname
    public static final String discordLogger = "http://localhost:8000/api/notify/log";//POST //channel, level, location, message
    public static final String playerJoin = "http://localhost:7001/api/notify/player-join";
}
