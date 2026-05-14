package com.mulesoft.connectors.spiff.api;

import java.io.Serializable;

/**
 * Metadata attributes returned alongside SPIFF API responses.
 * Carries HTTP status code and content type for downstream inspection.
 */
public class SpiffAttributes implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String contentType;

    public SpiffAttributes(int statusCode, String contentType) {
        this.statusCode = statusCode;
        this.contentType = contentType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }
}
