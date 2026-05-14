package com.mulesoft.connectors.spiff.internal.connection;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

@Alias("hmac")
@DisplayName("SPIFF HMAC Connection")
public class SpiffHmacConnectionProvider implements CachedConnectionProvider<SpiffHmacConnection> {

    @Parameter
    @DisplayName("Subdomain")
    @Summary("SPIFF environment subdomain: 'us1' for US, 'eu1' for EU")
    @Placement(order = 1)
    private String subdomain;

    @Parameter
    @DisplayName("Company ID")
    @Summary("Your company's UUID from Admin > Settings > Company Settings > Company Identifiers")
    @Placement(order = 2)
    private String companyId;

    @Parameter
    @DisplayName("Secret Key")
    @Summary("HMAC-SHA256 secret key obtained from Salesforce Customer Support")
    @Placement(order = 3)
    private String secretKey;

    @Override
    public SpiffHmacConnection connect() throws ConnectionException {
        return new SpiffHmacConnection(subdomain, companyId, secretKey);
    }

    @Override
    public void disconnect(SpiffHmacConnection connection) {
        connection.invalidate();
    }

    @Override
    public ConnectionValidationResult validate(SpiffHmacConnection connection) {
        try {
            connection.validate();
            return ConnectionValidationResult.success();
        } catch (Exception e) {
            return ConnectionValidationResult.failure(
                    "Failed to validate SPIFF HMAC connection: " + e.getMessage(), e);
        }
    }
}
