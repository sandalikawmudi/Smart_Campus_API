package com.smartcampus.resources;

import com.smartcampus.database.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {

    private Map<String, Sensor> sensors = DataStore.getInstance().getSensors();
    private Map<String, Room> rooms = DataStore.getInstance().getRooms();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        Collection<Sensor> allSensors = sensors.values();

        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filtered = allSensors.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }

        return Response.ok(allSensors).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSensor(Sensor newSensor) {
        // Validation: Verify roomId exists
        String roomId = newSensor.getRoomId();
        if (roomId == null || !rooms.containsKey(roomId)) {
            throw new com.smartcampus.exceptions.LinkedResourceNotFoundException(
                    "Invalid or missing roomId. Room does not exist.");
        }

        if (newSensor.getId() == null || newSensor.getId().isEmpty()) {
            newSensor.setId(UUID.randomUUID().toString());
        }

        sensors.put(newSensor.getId(), newSensor);

        // Link sensor to room
        Room parentRoom = rooms.get(roomId);
        parentRoom.getSensorIds().add(newSensor.getId());

        return Response.status(Response.Status.CREATED).entity(newSensor).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensor(@PathParam("id") String id) {
        Sensor s = sensors.get(id);
        if (s == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(s).build();
    }

    // --- Sub-Resource Locator Pattern ---
    @Path("/{id}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("id") String id) {
        // Instead of returning JSON directly, we return another Resource CLASS
        // to handle all logic for /sensors/{id}/readings
        return new SensorReadingResource(id);
    }
}
