package auth;

import com.marruky.Json.JsonParser;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JwtService {

    private final String secret;

    public JwtService() {
        String temp;
        try {
            Properties props = new Properties();
            props.load(JwtService.class.getResourceAsStream("/config.properties"));
            temp = props.getProperty("jwt.secret");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        secret = temp;
    }

    public String generate(String username, String role) {
        String header = "{\"alg\": \"HS256\",\"typ\":\"JWT\"}";
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        String headerBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(headerBytes);
        long exp = System.currentTimeMillis() / 1000 + 86400;
        String payload = "{\"sub\":\"" + username + "\", \"role\":\"" + role + "\",\"exp\":" + exp + "}";
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        String payloadBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadBytes);

        try {
            //Create the object
            Mac mac = Mac.getInstance("HmacSHA256");
            //encapsulates the secret key in bytes
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            //inicialize the key
            mac.init(keySpec);
            //calc the hash, returns byte[]
            byte[] hash = mac.doFinal((headerBase64 + "." + payloadBase64).getBytes(StandardCharsets.UTF_8));
            String signatureBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            return headerBase64 + "." + payloadBase64 + "." + signatureBase64;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(String token) {
       String[] parts = token.split("\\.");
       String header = parts[0];
       String payload = parts[1];
       String signatureToken = parts[2];
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal((header + "." + payload).getBytes(StandardCharsets.UTF_8));
            String signatureBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            if (!signatureBase64.equals(signatureToken)) {
                return false;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(payload));
            JsonParser parser = new JsonParser();
            Map<String, Object> map = parser.parser(payloadJson);
            double exp = Double.parseDouble(map.get("exp").toString());

            if (exp < ((double) System.currentTimeMillis() /1000)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        public String getUsername(String token){
            String[] parts = token.split("\\.");
            String payload = parts[1];
            String payloadJson = new String(Base64.getUrlDecoder().decode(payload));
            Map<String, Object> map = new JsonParser().parser(payloadJson);
            return map.get("sub").toString();
        }
    }
