package com.mulesoft.connectors.spiff;

import com.mulesoft.connectors.spiff.internal.connection.SpiffHmacConnection;
import com.mulesoft.connectors.spiff.internal.operation.SpiffImportOperations;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for SpiffImportOperations.
 * These tests verify parameter construction and delegation logic.
 * Integration tests against the live API require valid HMAC credentials.
 */
public class SpiffImportOperationsTest {

    @Test
    public void hmacConnectionBaseUrlUsSubdomain() {
        SpiffHmacConnection conn = new SpiffHmacConnection("us1", "company-123", "secret");
        assertEquals("https://us1.spiff.com/api/external_data/company-123", conn.getBaseUrl());
    }

    @Test
    public void hmacConnectionBaseUrlEuSubdomain() {
        SpiffHmacConnection conn = new SpiffHmacConnection("eu1", "company-456", "secret");
        assertEquals("https://eu1.spiff.com/api/external_data/company-456", conn.getBaseUrl());
    }

    @Test
    public void importOperationsClassHasFourMethods() {
        SpiffImportOperations ops = new SpiffImportOperations();
        long operationCount = java.util.Arrays.stream(ops.getClass().getDeclaredMethods())
                .filter(m -> java.lang.reflect.Modifier.isPublic(m.getModifiers()))
                .count();
        assertEquals("Import operations should expose 4 public methods", 4, operationCount);
    }

    @Test
    public void listImportsMethodExists() throws NoSuchMethodException {
        SpiffImportOperations.class.getMethod("listImports", SpiffHmacConnection.class);
    }

    @Test
    public void createOrDeleteRecordsMethodExists() throws NoSuchMethodException {
        SpiffImportOperations.class.getMethod("createOrDeleteRecords",
                SpiffHmacConnection.class, String.class, String.class, String.class, String.class);
    }

    @Test
    public void getImportMethodExists() throws NoSuchMethodException {
        SpiffImportOperations.class.getMethod("getImport",
                SpiffHmacConnection.class, String.class);
    }

    @Test
    public void testImportMethodExists() throws NoSuchMethodException {
        SpiffImportOperations.class.getMethod("testImport",
                SpiffHmacConnection.class, String.class);
    }
}
