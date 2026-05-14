package com.mulesoft.connectors.spiff;

import com.mulesoft.connectors.spiff.internal.connection.SpiffHmacConnection;
import com.mulesoft.connectors.spiff.internal.operation.SpiffObjectOperations;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for SpiffObjectOperations.
 * Validates method signatures and operation count for the Object API.
 */
public class SpiffObjectOperationsTest {

    @Test
    public void objectOperationsClassHasFiveMethods() {
        SpiffObjectOperations ops = new SpiffObjectOperations();
        long operationCount = java.util.Arrays.stream(ops.getClass().getDeclaredMethods())
                .filter(m -> java.lang.reflect.Modifier.isPublic(m.getModifiers()))
                .count();
        assertEquals("Object operations should expose 5 public methods", 5, operationCount);
    }

    @Test
    public void getAllObjectsMethodExists() throws NoSuchMethodException {
        SpiffObjectOperations.class.getMethod("getAllObjects", SpiffHmacConnection.class);
    }

    @Test
    public void createObjectMethodExists() throws NoSuchMethodException {
        SpiffObjectOperations.class.getMethod("createObject",
                SpiffHmacConnection.class, String.class);
    }

    @Test
    public void getObjectMethodExists() throws NoSuchMethodException {
        SpiffObjectOperations.class.getMethod("getObject",
                SpiffHmacConnection.class, String.class);
    }

    @Test
    public void updateObjectMethodExists() throws NoSuchMethodException {
        SpiffObjectOperations.class.getMethod("updateObject",
                SpiffHmacConnection.class, String.class, String.class);
    }

    @Test
    public void deleteObjectMethodExists() throws NoSuchMethodException {
        SpiffObjectOperations.class.getMethod("deleteObject",
                SpiffHmacConnection.class, String.class);
    }
}
