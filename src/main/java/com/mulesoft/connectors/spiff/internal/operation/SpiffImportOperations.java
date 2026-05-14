package com.mulesoft.connectors.spiff.internal.operation;

import com.mulesoft.connectors.spiff.internal.connection.SpiffHmacConnection;
import com.mulesoft.connectors.spiff.internal.error.SpiffErrorTypeProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

public class SpiffImportOperations {

    @DisplayName("List Imports")
    @Summary("Get a list of all import jobs")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String listImports(@Connection SpiffHmacConnection connection) {
        return connection.get("/imports");
    }

    @DisplayName("Create or Delete Records")
    @Summary("Upsert or delete records for a SPIFF object via import")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String createOrDeleteRecords(
            @Connection SpiffHmacConnection connection,
            @DisplayName("Import Target") @Summary("Object ID or name: teams, sub_teams, team_members, plan_assignments") String importTarget,
            @DisplayName("Import Action") @Summary("Operation to perform: 'upsert' or 'delete'") String importAction,
            @DisplayName("Payload") @Summary("JSON array of records") String payload,
            @DisplayName("Foreign Keys") @Summary("Optional JSON array of foreign key identifiers") @Optional String foreignKeys) {

        StringBuilder body = new StringBuilder();
        body.append("{\"import_target\":\"").append(escapeJson(importTarget)).append("\",");
        body.append("\"import_action\":\"").append(escapeJson(importAction)).append("\",");
        body.append("\"payload\":").append(payload);
        if (foreignKeys != null && !foreignKeys.trim().isEmpty()) {
            body.append(",\"foreign_keys\":").append(foreignKeys);
        }
        body.append("}");

        return connection.post("/imports", body.toString());
    }

    @DisplayName("Get Import")
    @Summary("Get details and status of a single import job")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String getImport(
            @Connection SpiffHmacConnection connection,
            @DisplayName("Import ID") @Summary("UUID of the import job") String importId) {
        return connection.get("/imports/" + importId);
    }

    @DisplayName("Test Import")
    @Summary("Validate HMAC-SHA256 signature with the test endpoint")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String testImport(
            @Connection SpiffHmacConnection connection,
            @DisplayName("Test Body") @Summary("Optional JSON body for signature test") @Optional(defaultValue = "{}") String testBody) {
        return connection.post("/imports/test", testBody);
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
