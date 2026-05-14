package com.mulesoft.connectors.spiff;

import com.mulesoft.connectors.spiff.internal.connection.SpiffOAuthConnection;
import com.mulesoft.connectors.spiff.internal.operation.SpiffReportingOperations;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for SpiffReportingOperations.
 * Validates method signatures and operation count for the Reporting API.
 */
public class SpiffReportingOperationsTest {

    @Test
    public void reportingOperationsClassHasFourMethods() {
        SpiffReportingOperations ops = new SpiffReportingOperations();
        long operationCount = java.util.Arrays.stream(ops.getClass().getDeclaredMethods())
                .filter(m -> java.lang.reflect.Modifier.isPublic(m.getModifiers()))
                .count();
        assertEquals("Reporting operations should expose 4 public methods", 4, operationCount);
    }

    @Test
    public void getReportsMethodExists() throws NoSuchMethodException {
        SpiffReportingOperations.class.getMethod("getReports", SpiffOAuthConnection.class);
    }

    @Test
    public void requestExportMethodExists() throws NoSuchMethodException {
        SpiffReportingOperations.class.getMethod("requestExport",
                SpiffOAuthConnection.class, String.class, String.class);
    }

    @Test
    public void getExportStatusMethodExists() throws NoSuchMethodException {
        SpiffReportingOperations.class.getMethod("getExportStatus",
                SpiffOAuthConnection.class, String.class, String.class);
    }

    @Test
    public void downloadReportMethodExists() throws NoSuchMethodException {
        SpiffReportingOperations.class.getMethod("downloadReport",
                SpiffOAuthConnection.class, String.class, String.class);
    }
}
