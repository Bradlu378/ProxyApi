package foxo.flanty.proxyApi;

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
        register("922540e1-4cf2-4427-ba1b-fc9975ef4d4d");
    }


}
