import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


// 通过BCryptPasswordEncoder中的加密方式生成密文
public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
