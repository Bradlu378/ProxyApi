package foxo.flanty.proxyApi.modules.auth;

public class Login {
    public boolean is_whitelisted;
    public boolean is_logged_in;
    public String url;

    public Login(boolean is_whitelisted, boolean is_logged_in, String url) {
        this.is_whitelisted = is_whitelisted;
        this.is_logged_in = is_logged_in;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Login{" +
                "is_whitelisted=" + is_whitelisted +
                ", is_logged_in=" + is_logged_in +
                ", url='" + url + '\'' +
                '}';
    }
}
