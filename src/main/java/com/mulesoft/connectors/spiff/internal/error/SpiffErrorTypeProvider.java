package com.mulesoft.connectors.spiff.internal.error;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

public class SpiffErrorTypeProvider implements ErrorTypeProvider {

    @Override
    public Set<ErrorTypeDefinition> getErrorTypes() {
        Set<ErrorTypeDefinition> errors = new HashSet<ErrorTypeDefinition>();
        errors.add(SpiffErrorType.AUTHENTICATION);
        errors.add(SpiffErrorType.INVALID_REQUEST);
        errors.add(SpiffErrorType.NOT_FOUND);
        errors.add(SpiffErrorType.RATE_LIMIT);
        errors.add(SpiffErrorType.SERVER_ERROR);
        errors.add(SpiffErrorType.CONNECTIVITY);
        errors.add(SpiffErrorType.TIMEOUT);
        return errors;
    }
}
