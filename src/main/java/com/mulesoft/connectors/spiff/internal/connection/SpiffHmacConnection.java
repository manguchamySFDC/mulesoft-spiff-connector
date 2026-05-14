package com.mulesoft.connectors.spiff.internal.connection;

import com.mulesoft.connectors.spiff.internal.util.HmacSignatureUtil;
import com.mulesoft.connectors.spiff.internal.util.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds HMAC-SHA256 connection state for SPIFF Import and Object APIs.
 * Each request generates a fresh signature from the secret key.
 */
public class SpiffHmacConnection {

    private final String subdomain;
    private final String companyId;
    private final String secretKey;

    public SpiffHmacConnection(String subdomain, String companyId, String secretKey) {
        this.subdomain = subdomain;
        this.companyId = companyId;
        this.secretKey = secretKey;
    }

    public String getBaseUrl() {
        return "https://" + subdomain + ".spiff.com/api/external_data/" + companyId;
    }

    public String get(String path) {
        String url = getBaseUrl() + path;
        Map<String, String> headers = buildHeaders(null);
        return HttpClientUtil.get(url, headers);
    }

    public String post(String path, String body) {
        String url = getBaseUrl() + path;
        Map<String, String> headers = buildHeaders(body);
        return HttpClientUtil.post(url, body, headers);
    }

    public String put(String path, String body) {
        String url = getBaseUrl() + path;
        Map<String, String> headers = buildHeaders(body);
        return HttpClientUtil.put(url, body, headers);
    }

    public String delete(String path) {
        String url = getBaseUrl() + path;
        Map<String, String> headers = buildHeaders(null);
        return HttpClientUtil.delete(url, headers);
    }

    private Map<String, String> buildHeaders(String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("signature", HmacSignatureUtil.generateSignature(secretKey, body));
        return headers;
    }

    public void validate() {
        get("/objects");
    }

    public void invalidate() {
        // HMAC connections are stateless; nothing to clean up
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getCompanyId() {
        return companyId;
    }
}
