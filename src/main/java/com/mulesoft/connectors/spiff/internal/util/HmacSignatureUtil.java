package com.mulesoft.connectors.spiff.internal.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Generates HMAC-SHA256 signatures required by SPIFF Import and Object APIs.
 * Signature format: t={timestamp},v1={hexDigest}
 */
public final class HmacSignatureUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private HmacSignatureUtil() {
    }

    public static String generateSignature(String secretKey, String requestBody) {
        long timestamp = Instant.now().getEpochSecond();
        return generateSignature(secretKey, requestBody, timestamp);
    }

    public static String generateSignature(String secretKey, String requestBody, long timestamp) {
        String payload = timestamp + "." + (requestBody != null ? requestBody : "");
        String digest = hmacSha256(secretKey, payload);
        return "t=" + timestamp + ",v1=" + digest;
    }

    private static String hmacSha256(String key, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
