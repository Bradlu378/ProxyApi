package foxo.flanty.proxyApi.auth;

public class Login {
    public boolean isWhitelisted;
    public boolean isLoggedIn;
    public String url;

    public Login(boolean isWhitelisted, boolean isLoggedIn, String url) {
        this.isWhitelisted = isWhitelisted;
        this.isLoggedIn = isLoggedIn;
        this.url = url;
    }
}
