package com.smartcampus.resources;

import com.smartcampus.database.DataStore;
import com.smartcampus.models.SensorReading;
import com.smartcampus.models.Sensor;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SensorReadingResource {

    private String sensorId;
    private Map<String, List<SensorReading>> allReadings = DataStore.getInstance().getSensorReadings();
    private Map<String, Sensor> sensors = DataStore.getInstance().getSensors();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        return allReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = sensors.get(sensorId);
        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\": \"Parent sensor not found.\"}")
                           .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new com.smartcampus.exceptions.SensorUnavailableException("Cannot add reading to a sensor in MAINTENANCE status.");
        }

        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Side-Effect: update parent sensor's current value
        parentSensor.setCurrentValue(reading.getValue());

        allReadings.putIfAbsent(sensorId, new ArrayList<>());
        allReadings.get(sensorId).add(reading);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
