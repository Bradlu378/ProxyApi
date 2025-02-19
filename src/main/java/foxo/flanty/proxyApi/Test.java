package foxo.flanty.proxyApi;

import foxo.flanty.proxyApi.utils.AuthUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static foxo.flanty.proxyApi.utils.AuthUtils.convertMillisToDate;

public class Test {

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        System.out.println(bytesToHex(AuthUtils.generateChallengeBytes()));
    }
}
