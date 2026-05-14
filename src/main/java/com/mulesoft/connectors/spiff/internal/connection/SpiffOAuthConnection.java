package com.mulesoft.connectors.spiff.internal.connection;

import com.mulesoft.connectors.spiff.internal.error.SpiffErrorType;
import com.mulesoft.connectors.spiff.internal.util.HttpClientUtil;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds OAuth 2.0 connection state for the SPIFF Reporting API.
 * Manages token lifecycle with automatic refresh before expiry.
 */
public class SpiffOAuthConnection {

    private static final long TOKEN_REFRESH_BUFFER_SECONDS = 60;

    private final String subdomain;
    private final String clientId;
    private final String clientSecret;

    private String accessToken;
    private long tokenExpiresAt;

    public SpiffOAuthConnection(String subdomain, String clientId, String clientSecret) {
        this.subdomain = subdomain;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenExpiresAt = 0;
    }

    public String getBaseUrl() {
        return "https://" + subdomain + ".spiff.com/api/v1";
    }

    public String get(String path) {
        ensureValidToken();
        Map<String, String> headers = buildAuthHeaders();
        return HttpClientUtil.get(getBaseUrl() + path, headers);
    }

    public String post(String path, String body) {
        ensureValidToken();
        Map<String, String> headers = buildAuthHeaders();
        headers.put("Content-Type", "application/json");
        return HttpClientUtil.post(getBaseUrl() + path, body, headers);
    }

    public void validate() {
        ensureValidToken();
        get("/reports");
    }

    public void invalidate() {
        accessToken = null;
        tokenExpiresAt = 0;
    }

    private void ensureValidToken() {
        if (accessToken != null && Instant.now().getEpochSecond() < tokenExpiresAt) {
            return;
        }
        refreshToken();
    }

    private void refreshToken() {
        String tokenUrl = getBaseUrl() + "/oauth2/token";
        String audience = getBaseUrl();

        String formBody = "client_id=" + urlEncode(clientId)
                + "&client_secret=" + urlEncode(clientSecret)
                + "&audience=" + urlEncode(audience)
                + "&grant_type=client_credentials";

        String response = HttpClientUtil.postForm(tokenUrl, formBody, new HashMap<String, String>());
        parseTokenResponse(response);
    }

    private void parseTokenResponse(String responseBody) {
        String token = extractJsonStringValue(responseBody, "access_token");
        if (token == null || token.isEmpty()) {
            throw new ModuleException(
                    "Failed to obtain access token from SPIFF OAuth endpoint",
                    SpiffErrorType.AUTHENTICATION);
        }
        this.accessToken = token;

        String expiresInStr = extractJsonNumericValue(responseBody, "expires_in");
        long expiresIn = expiresInStr != null ? Long.parseLong(expiresInStr) : 3600;
        this.tokenExpiresAt = Instant.now().getEpochSecond() + expiresIn - TOKEN_REFRESH_BUFFER_SECONDS;
    }

    private Map<String, String> buildAuthHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + accessToken);
        return headers;
    }

    static String extractJsonStringValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(':', keyIndex + searchKey.length());
        if (colonIndex == -1) return null;

        int quoteStart = json.indexOf('"', colonIndex + 1);
        if (quoteStart == -1) return null;

        int quoteEnd = json.indexOf('"', quoteStart + 1);
        if (quoteEnd == -1) return null;

        return json.substring(quoteStart + 1, quoteEnd);
    }

    static String extractJsonNumericValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(':', keyIndex + searchKey.length());
        if (colonIndex == -1) return null;

        int start = colonIndex + 1;
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\t')) {
            start++;
        }

        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) {
            end++;
        }

        return start < end ? json.substring(start, end) : null;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    public String getSubdomain() {
        return subdomain;
    }
}
