package com.mulesoft.connectors.spiff;

import com.mulesoft.connectors.spiff.internal.util.HmacSignatureUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class HmacSignatureUtilTest {

    private static final String TEST_SECRET = "test-secret-key-12345";

    @Test
    public void signatureHasCorrectFormat() {
        String signature = HmacSignatureUtil.generateSignature(TEST_SECRET, "{\"test\":true}");

        assertTrue("Signature must start with 't='", signature.startsWith("t="));
        assertTrue("Signature must contain ',v1='", signature.contains(",v1="));

        String[] parts = signature.split(",v1=");
        assertEquals(2, parts.length);
        String timestamp = parts[0].substring(2);
        assertFalse("Timestamp must not be empty", timestamp.isEmpty());
        Long.parseLong(timestamp);

        String digest = parts[1];
        assertTrue("Digest must be a hex string of length 64", digest.length() == 64);
        assertTrue("Digest must be hex", digest.matches("[0-9a-f]+"));
    }

    @Test
    public void sameInputsProduceSameDigest() {
        long fixedTimestamp = 1700000000L;
        String sig1 = HmacSignatureUtil.generateSignature(TEST_SECRET, "{\"data\":1}", fixedTimestamp);
        String sig2 = HmacSignatureUtil.generateSignature(TEST_SECRET, "{\"data\":1}", fixedTimestamp);
        assertEquals("Same inputs must produce identical signatures", sig1, sig2);
    }

    @Test
    public void differentBodiesProduceDifferentDigests() {
        long fixedTimestamp = 1700000000L;
        String sig1 = HmacSignatureUtil.generateSignature(TEST_SECRET, "{\"data\":1}", fixedTimestamp);
        String sig2 = HmacSignatureUtil.generateSignature(TEST_SECRET, "{\"data\":2}", fixedTimestamp);
        assertNotEquals("Different bodies must produce different digests", sig1, sig2);
    }

    @Test
    public void differentKeysProduceDifferentDigests() {
        long fixedTimestamp = 1700000000L;
        String body = "{\"data\":1}";
        String sig1 = HmacSignatureUtil.generateSignature("key-one", body, fixedTimestamp);
        String sig2 = HmacSignatureUtil.generateSignature("key-two", body, fixedTimestamp);
        assertNotEquals("Different keys must produce different digests", sig1, sig2);
    }

    @Test
    public void nullBodyHandledGracefully() {
        String signature = HmacSignatureUtil.generateSignature(TEST_SECRET, null);
        assertNotNull(signature);
        assertTrue(signature.startsWith("t="));
        assertTrue(signature.contains(",v1="));
    }

    @Test
    public void emptyBodyHandledGracefully() {
        String signature = HmacSignatureUtil.generateSignature(TEST_SECRET, "");
        assertNotNull(signature);
        assertTrue(signature.startsWith("t="));
    }

    @Test
    public void timestampOverrideWorks() {
        long timestamp = 1750092638L;
        String signature = HmacSignatureUtil.generateSignature(TEST_SECRET, "{}", timestamp);
        assertTrue(signature.startsWith("t=1750092638,v1="));
    }
}
