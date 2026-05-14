package com.mulesoft.connectors.spiff.internal.connection;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpiffOAuthConnectionTest {

    @Test
    public void extractJsonStringValueFindsToken() {
        String json = "{\"success\":true,\"access_token\":\"abc123xyz\",\"token_type\":\"Bearer\"}";
        String token = SpiffOAuthConnection.extractJsonStringValue(json, "access_token");
        assertEquals("abc123xyz", token);
    }

    @Test
    public void extractJsonStringValueFindsTokenType() {
        String json = "{\"success\":true,\"access_token\":\"abc\",\"token_type\":\"Bearer\"}";
        String type = SpiffOAuthConnection.extractJsonStringValue(json, "token_type");
        assertEquals("Bearer", type);
    }

    @Test
    public void extractJsonStringValueReturnsNullForMissingKey() {
        String json = "{\"success\":true}";
        assertNull(SpiffOAuthConnection.extractJsonStringValue(json, "access_token"));
    }

    @Test
    public void extractJsonNumericValueFindsExpiresIn() {
        String json = "{\"success\":true,\"expires_in\":604799,\"scope\":\"reportingApi\"}";
        String value = SpiffOAuthConnection.extractJsonNumericValue(json, "expires_in");
        assertEquals("604799", value);
    }

    @Test
    public void extractJsonNumericValueReturnsNullForMissingKey() {
        String json = "{\"success\":true}";
        assertNull(SpiffOAuthConnection.extractJsonNumericValue(json, "expires_in"));
    }

    @Test
    public void extractJsonNumericValueHandlesSpaces() {
        String json = "{\"expires_in\":  3600}";
        String value = SpiffOAuthConnection.extractJsonNumericValue(json, "expires_in");
        assertEquals("3600", value);
    }

    @Test
    public void baseUrlConstructedCorrectly() {
        SpiffOAuthConnection conn = new SpiffOAuthConnection("us1", "id", "secret");
        assertEquals("https://us1.spiff.com/api/v1", conn.getBaseUrl());
    }

    @Test
    public void baseUrlWithEuSubdomain() {
        SpiffOAuthConnection conn = new SpiffOAuthConnection("eu1", "id", "secret");
        assertEquals("https://eu1.spiff.com/api/v1", conn.getBaseUrl());
    }
}
