package foxo.flanty.proxyApi.utils;

public class AuthPlayer {
    public boolean online = false;
    public String ip;
    public long timestamp;
    public AuthPlayer(String ip, long timestamp, boolean online) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.online = online;
    }
}
