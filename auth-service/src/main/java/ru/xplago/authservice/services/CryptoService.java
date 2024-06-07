package ru.xplago.authservice.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final int SALT_LENGTH = 32;

    public String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String encode(String password, String salt) {
        return encoder.encode((salt + ":" + password));
    }

    public boolean verify(String password, String salt, String hash) {
        return encoder.matches(salt + ":" + password, hash);
    }
}
