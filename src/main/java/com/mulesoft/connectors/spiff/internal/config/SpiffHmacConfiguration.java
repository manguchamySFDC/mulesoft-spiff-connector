package com.mulesoft.connectors.spiff.internal.config;

import com.mulesoft.connectors.spiff.internal.connection.SpiffHmacConnectionProvider;
import com.mulesoft.connectors.spiff.internal.operation.SpiffImportOperations;
import com.mulesoft.connectors.spiff.internal.operation.SpiffObjectOperations;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

@Configuration(name = "hmac-config")
@DisplayName("SPIFF HMAC Configuration (Import & Object APIs)")
@Operations({SpiffImportOperations.class, SpiffObjectOperations.class})
@ConnectionProviders(SpiffHmacConnectionProvider.class)
public class SpiffHmacConfiguration {

}
