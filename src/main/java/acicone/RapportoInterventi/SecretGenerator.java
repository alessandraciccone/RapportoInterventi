package acicone.RapportoInterventi;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    public static String generate (int lenght){
        byte[] bytes = new byte[lenght];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public static void main(String[] args) {
        System.out.println(generate(32));
        System.out.println("Generated secret key for JWT signing.");
    }
}
