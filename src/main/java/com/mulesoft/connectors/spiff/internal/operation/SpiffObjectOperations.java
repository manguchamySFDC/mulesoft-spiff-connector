package com.mulesoft.connectors.spiff.internal.operation;

import com.mulesoft.connectors.spiff.internal.connection.SpiffHmacConnection;
import com.mulesoft.connectors.spiff.internal.error.SpiffErrorTypeProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

public class SpiffObjectOperations {

    @DisplayName("Get All Objects")
    @Summary("Retrieve all objects and their fields from SPIFF")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String getAllObjects(@Connection SpiffHmacConnection connection) {
        return connection.get("/objects");
    }

    @DisplayName("Create Object")
    @Summary("Create a new custom object in SPIFF")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String createObject(
            @Connection SpiffHmacConnection connection,
            @DisplayName("Object Definition") @Summary("JSON body with label, label_plural, and fields array") String objectBody) {
        return connection.post("/objects", objectBody);
    }

    @DisplayName("Get Object")
    @Summary("Retrieve a single SPIFF object by ID")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String getObject(
            @Connection SpiffHmacConnection connection,
            @DisplayName("Object ID") @Summary("UUID of the SPIFF object") String objectId) {
        return connection.get("/objects/" + objectId);
    }

    @DisplayName("Update Object")
    @Summary("Update an existing SPIFF object")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String updateObject(
            @Connection SpiffHmacConnection connection,
            @DisplayName("Object ID") @Summary("UUID of the SPIFF object to update") String objectId,
            @DisplayName("Object Definition") @Summary("JSON body with updated properties") String objectBody) {
        return connection.put("/objects/" + objectId, objectBody);
    }

    @DisplayName("Delete Object")
    @Summary("Delete an empty SPIFF object (must have no records)")
    @Throws(SpiffErrorTypeProvider.class)
    @MediaType(value = "application/json", strict = false)
    public String deleteObject(
            @Connection SpiffHmacConnection connection,
            @DisplayName("Object ID") @Summary("UUID of the empty SPIFF object to delete") String objectId) {
        return connection.delete("/objects/" + objectId);
    }
}
