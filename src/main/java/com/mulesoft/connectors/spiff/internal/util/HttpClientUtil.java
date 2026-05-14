package com.mulesoft.connectors.spiff.internal.util;

import com.mulesoft.connectors.spiff.internal.error.SpiffErrorType;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Shared HTTP client utility for all SPIFF API calls.
 * Uses HttpURLConnection for Java 8 compatibility.
 */
public final class HttpClientUtil {

    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;

    private HttpClientUtil() {
    }

    public static String get(String url, Map<String, String> headers) {
        return execute("GET", url, null, headers);
    }

    public static String post(String url, String body, Map<String, String> headers) {
        return execute("POST", url, body, headers);
    }

    public static String postForm(String url, String formBody, Map<String, String> headers) {
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return execute("POST", url, formBody, headers);
    }

    public static String put(String url, String body, Map<String, String> headers) {
        return execute("PUT", url, body, headers);
    }

    public static String delete(String url, Map<String, String> headers) {
        return execute("DELETE", url, null, headers);
    }

    private static String execute(String method, String urlString, String body, Map<String, String> headers) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (body != null && !body.isEmpty()) {
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                try {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                } finally {
                    os.close();
                }
            }

            int statusCode = connection.getResponseCode();

            if (statusCode >= 200 && statusCode < 300) {
                return readStream(connection);
            }

            String errorBody = readErrorStream(connection);
            throw mapToModuleException(statusCode, errorBody);

        } catch (ModuleException e) {
            throw e;
        } catch (IOException e) {
            throw new ModuleException(
                    "Failed to connect to SPIFF API: " + e.getMessage(),
                    SpiffErrorType.CONNECTIVITY, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String readStream(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        try {
            return readAll(reader);
        } finally {
            reader.close();
        }
    }

    private static String readErrorStream(HttpURLConnection connection) {
        try {
            if (connection.getErrorStream() == null) {
                return "";
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            try {
                return readAll(reader);
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            return "";
        }
    }

    private static String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static ModuleException mapToModuleException(int statusCode, String body) {
        String message = "SPIFF API returned HTTP " + statusCode + ": " + body;

        if (statusCode == 400) {
            return new ModuleException(message, SpiffErrorType.INVALID_REQUEST);
        } else if (statusCode == 401 || statusCode == 403) {
            return new ModuleException(message, SpiffErrorType.AUTHENTICATION);
        } else if (statusCode == 404) {
            return new ModuleException(message, SpiffErrorType.NOT_FOUND);
        } else if (statusCode == 429) {
            return new ModuleException(message, SpiffErrorType.RATE_LIMIT);
        } else if (statusCode >= 500) {
            return new ModuleException(message, SpiffErrorType.SERVER_ERROR);
        } else {
            return new ModuleException(message, SpiffErrorType.INVALID_REQUEST);
        }
    }
}
