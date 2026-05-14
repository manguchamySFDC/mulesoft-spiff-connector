package com.mulesoft.connectors.spiff.internal.connection;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

@Alias("oauth")
@DisplayName("SPIFF OAuth Connection")
public class SpiffOAuthConnectionProvider implements CachedConnectionProvider<SpiffOAuthConnection> {

    @Parameter
    @DisplayName("Subdomain")
    @Summary("SPIFF environment subdomain: 'us1' for US, 'eu1' for EU")
    @Placement(order = 1)
    private String subdomain;

    @Parameter
    @DisplayName("Client ID")
    @Summary("OAuth Client ID from SPIFF Admin > Settings > API Access Management")
    @Placement(order = 2)
    private String clientId;

    @Parameter
    @DisplayName("Client Secret")
    @Summary("OAuth Client Secret from SPIFF Admin > Settings > API Access Management")
    @Placement(order = 3)
    private String clientSecret;

    @Override
    public SpiffOAuthConnection connect() throws ConnectionException {
        return new SpiffOAuthConnection(subdomain, clientId, clientSecret);
    }

    @Override
    public void disconnect(SpiffOAuthConnection connection) {
        connection.invalidate();
    }

    @Override
    public ConnectionValidationResult validate(SpiffOAuthConnection connection) {
        try {
            connection.validate();
            return ConnectionValidationResult.success();
        } catch (Exception e) {
            return ConnectionValidationResult.failure(
                    "Failed to validate SPIFF OAuth connection: " + e.getMessage(), e);
        }
    }
}
