package com.smartcampus.resources;

import com.smartcampus.database.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Path("/rooms")
public class RoomResource {

    private Map<String, Room> rooms = DataStore.getInstance().getRooms();
    private Map<String, Sensor> sensors = DataStore.getInstance().getSensors();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        if (newRoom.getId() == null || newRoom.getId().isEmpty()) {
            newRoom.setId(UUID.randomUUID().toString());
        }
        rooms.put(newRoom.getId(), newRoom);
        return Response.status(Response.Status.CREATED).entity(newRoom).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("id") String id) {
        Room room = rooms.get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = rooms.get(id);
        if (room == null) {
             return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Logic check: cannot delete if sensors are currently assigned
        boolean hasActiveSensors = sensors.values().stream()
                .anyMatch(sensor -> sensor.getRoomId().equals(id));
                
        if (hasActiveSensors) {
            throw new com.smartcampus.exceptions.RoomNotEmptyException("Cannot delete room with active sensors.");
        }

        rooms.remove(id);
        return Response.noContent().build();
    }
}
