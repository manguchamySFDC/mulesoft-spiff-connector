package com.mulesoft.connectors.spiff;

import com.mulesoft.connectors.spiff.internal.error.SpiffErrorType;
import com.mulesoft.connectors.spiff.internal.error.SpiffErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SpiffErrorTypeTest {

    @Test
    public void allSevenErrorTypesExist() {
        SpiffErrorType[] types = SpiffErrorType.values();
        assertEquals(7, types.length);
    }

    @Test
    public void errorTypeProviderReturnsAllTypes() {
        SpiffErrorTypeProvider provider = new SpiffErrorTypeProvider();
        Set<ErrorTypeDefinition> errors = provider.getErrorTypes();
        assertEquals(7, errors.size());
        assertTrue(errors.contains(SpiffErrorType.AUTHENTICATION));
        assertTrue(errors.contains(SpiffErrorType.INVALID_REQUEST));
        assertTrue(errors.contains(SpiffErrorType.NOT_FOUND));
        assertTrue(errors.contains(SpiffErrorType.RATE_LIMIT));
        assertTrue(errors.contains(SpiffErrorType.SERVER_ERROR));
        assertTrue(errors.contains(SpiffErrorType.CONNECTIVITY));
        assertTrue(errors.contains(SpiffErrorType.TIMEOUT));
    }

    @Test
    public void parentIsEmpty() {
        for (SpiffErrorType type : SpiffErrorType.values()) {
            assertTrue("All SPIFF error types should have no parent",
                    type.getParent().isEmpty());
        }
    }
}
