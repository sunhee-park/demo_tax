package com.example.demo.util;

import com.example.demo.config.EncryptionConfig;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class RrnEncryptionUtil {

    private final String secretKey;

    public RrnEncryptionUtil(EncryptionConfig encryptionConfig) {
        this.secretKey = encryptionConfig.getSecretKey();
    }

    public String encrypt(String rrn) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = getKey();
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(rrn.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedRrn) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = getKey();
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedRrn);
        byte[] originalBytes = cipher.doFinal(decodedBytes);
        return new String(originalBytes);
    }

    private SecretKey getKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, 0, 16, "AES"); // 16바이트 키
    }
}
