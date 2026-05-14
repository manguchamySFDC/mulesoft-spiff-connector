package com.mulesoft.connectors.spiff.internal.operation;

import com.mulesoft.connectors.spiff.internal.connection.SpiffOAuthConnection;
import com.mulesoft.connectors.spiff.internal.error.SpiffErrorTypeProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

public class SpiffReportingOperations {

    @DisplayName("Get Reports")
    @Summary("List all custom reports available in SPIFF")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String getReports(@Connection SpiffOAuthConnection connection) {
        return connection.get("/reports");
    }

    @DisplayName("Request Export")
    @Summary("Initiate an asynchronous export of a SPIFF report")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String requestExport(
            @Connection SpiffOAuthConnection connection,
            @DisplayName("Report ID") @Summary("ID of the report to export") String reportId,
            @DisplayName("Format") @Summary("Export format: 'csv' or 'csv.gz'") @Optional(defaultValue = "csv") String format) {

        String body = "{\"format\":\"" + format + "\"}";
        return connection.post("/reports/" + reportId + "/exports", body);
    }

    @DisplayName("Get Export Status")
    @Summary("Check the status of a report export job")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String getExportStatus(
            @Connection SpiffOAuthConnection connection,
            @DisplayName("Report ID") @Summary("ID of the report") String reportId,
            @DisplayName("Export ID") @Summary("ID of the export job") String exportId) {
        return connection.get("/reports/" + reportId + "/exports/" + exportId);
    }

    @DisplayName("Download Report")
    @Summary("Download a completed report export as CSV")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "text/csv", strict = false)
    public String downloadReport(
            @Connection SpiffOAuthConnection connection,
            @DisplayName("Report ID") @Summary("ID of the report") String reportId,
            @DisplayName("Export ID") @Summary("ID of the completed export job") String exportId) {
        return connection.get("/reports/" + reportId + "/exports/" + exportId + "/download");
    }
}
