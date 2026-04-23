package com.smartcampus.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Log the actual server error trace internally, but DONT expose it!
        exception.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // 500
                .entity("{\"error\": \"An unexpected internal error occurred. Please contact administration.\"}")
                .type("application/json")
                .build();
    }
}
