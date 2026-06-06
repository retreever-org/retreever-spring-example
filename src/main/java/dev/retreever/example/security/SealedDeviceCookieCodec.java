package dev.retreever.example.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Component
public class SealedDeviceCookieCodec {

    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_SIZE_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;

    private final SecretKeySpec secretKeySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public SealedDeviceCookieCodec(
            @Value("${retreever.example.device-cookie.secret:retreever-example-device-cookie-secret-change-me}") String secret
    ) {
        this.secretKeySpec = new SecretKeySpec(deriveAesKey(secret), "AES");
    }

    public String encode(ResolvedDevice resolvedDevice) {
        try {
            byte[] iv = new byte[IV_SIZE_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(toPayload(resolvedDevice).getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to encode device cookie.", ex);
        }
    }

    public Optional<ResolvedDevice> decode(String sealedCookie) {
        if (!StringUtils.hasText(sealedCookie)) {
            return Optional.empty();
        }

        try {
            byte[] payload = Base64.getUrlDecoder().decode(sealedCookie);
            if (payload.length <= IV_SIZE_BYTES) {
                return Optional.empty();
            }

            ByteBuffer buffer = ByteBuffer.wrap(payload);
            byte[] iv = new byte[IV_SIZE_BYTES];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(GCM_TAG_BITS, iv));
            String decrypted = new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
            String[] segments = decrypted.split("\\|", 2);
            if (segments.length != 2 || !StringUtils.hasText(segments[0]) || !StringUtils.hasText(segments[1])) {
                return Optional.empty();
            }

            return Optional.of(new ResolvedDevice(segments[0].trim(), segments[1].trim()));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private static String toPayload(ResolvedDevice resolvedDevice) {
        return resolvedDevice.deviceId() + "|" + resolvedDevice.clientIp();
    }

    private static byte[] deriveAesKey(String secret) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(secret.getBytes(StandardCharsets.UTF_8));
            byte[] key = new byte[16];
            System.arraycopy(digest, 0, key, 0, key.length);
            return key;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to derive device cookie key.", ex);
        }
    }
}
