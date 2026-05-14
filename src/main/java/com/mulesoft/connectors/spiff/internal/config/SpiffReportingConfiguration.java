package com.mulesoft.connectors.spiff.internal.config;

import com.mulesoft.connectors.spiff.internal.connection.SpiffOAuthConnectionProvider;
import com.mulesoft.connectors.spiff.internal.operation.SpiffReportingOperations;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

@Configuration(name = "oauth-config")
@DisplayName("OAuth (Reporting API)")
@Operations(SpiffReportingOperations.class)
@ConnectionProviders(SpiffOAuthConnectionProvider.class)
public class SpiffReportingConfiguration {

}
