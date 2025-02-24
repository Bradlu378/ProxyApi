package foxo.flanty.proxyApi;

import org.mindrot.bcrypt.BCrypt;

public class Test {
    public static void main(String[] args) {

        System.out.println(BCrypt.checkpw("123hui", "$2a$12$qH3Nz8VS7ZsdwsKPCQJ6DOi8A6Gzua2Fwi9lNLATsxdgPkjKpu1KK"));
    }
}
//$2b$12$tYYtv.FefIwmoWWFdlYqiOZ1.oKLcUnFnVzOVAQlmk.a884lq6th.
//$2b$12$pNaAW5aaWl/1H7PV4oD.FOQnZ1Qm.PgRmMy1UVX8MIWxd8iHgvuQC
