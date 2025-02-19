package foxo.flanty.proxyApi.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class AuthUtils {
    public static String generateChallengeString() {
        SecureRandom random = new SecureRandom();
        byte[] challengeBytes = new byte[16];
        random.nextBytes(challengeBytes);
        String challenge = Base64.getEncoder().encodeToString(challengeBytes);
        return challenge;
    }
    public static byte[] generateChallengeBytes() {
        SecureRandom random = new SecureRandom();
        byte[] challengeBytes = new byte[16];
        random.nextBytes(challengeBytes);
        return challengeBytes;
    }
    public static String convertMillisToDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(new Date(millis));
    }
    public static String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return "default";
        }
    }
}
