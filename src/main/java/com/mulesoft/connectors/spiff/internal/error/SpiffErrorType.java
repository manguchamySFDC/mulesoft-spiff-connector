package com.mulesoft.connectors.spiff.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Optional;

public enum SpiffErrorType implements ErrorTypeDefinition<SpiffErrorType> {

    AUTHENTICATION,
    INVALID_REQUEST,
    NOT_FOUND,
    RATE_LIMIT,
    SERVER_ERROR,
    CONNECTIVITY,
    TIMEOUT;

    @Override
    public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
        return Optional.empty();
    }
}
