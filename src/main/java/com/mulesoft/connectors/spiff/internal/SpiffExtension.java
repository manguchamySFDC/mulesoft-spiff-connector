package com.mulesoft.connectors.spiff.internal;

import com.mulesoft.connectors.spiff.internal.config.SpiffHmacConfiguration;
import com.mulesoft.connectors.spiff.internal.config.SpiffReportingConfiguration;
import com.mulesoft.connectors.spiff.internal.error.SpiffErrorType;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;

@Extension(name = "Salesforce SPIFF", vendor = "MuleSoft")
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
@Xml(prefix = "spiff")
@Configurations({SpiffHmacConfiguration.class, SpiffReportingConfiguration.class})
@ErrorTypes(SpiffErrorType.class)
public class SpiffExtension {

}
