package foxo.flanty.proxyApi.utils;

public class AuthPlayer {
    public String ip;
    public long timestamp;
    public String licensedUUID;
    public long licenseTimestamp;
    public AuthPlayer(String ip, long timestamp, String licensedUUID, long licenseTimestamp) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.licensedUUID = licensedUUID;
        this.licenseTimestamp = licenseTimestamp;

    }
}
