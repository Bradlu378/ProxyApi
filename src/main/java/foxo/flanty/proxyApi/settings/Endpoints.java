package foxo.flanty.proxyApi.settings;

public class Endpoints {
    public static final String MojangAPI = "https://sessionserver.mojang.com/session/minecraft/profile/";
    public static final String skinUpdate = "http://localhost:7001/api/player-skinchange";//PUT
    public static final String playerPasswordsHashes = "http://localhost:8000/api/auth/passwords";//GET
    public static final String playerRegister = "http://localhost:8000/api/auth/login";//GET ?username=nickname
    public static final String discordLogger = "http://localhost:8000/api/log";//POST //channel, level, location, message
}
