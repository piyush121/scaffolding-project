package com.scaffolding.rest;

import com.scaffolding.service.MessageService;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * Served under /api (see Application), so the full paths are /api/messages/...
 * The entity passed to Response is converted to JSON by Jackson (JacksonFeature in Application).
 */
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
public class MessageRestController {

    private final MessageService messageService;

    public MessageRestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GET
    @Path("/latest")
    public Response getLatestMessages(@QueryParam("limit") @DefaultValue("10") String limit) {
        int parsedLimit;
        try {
            parsedLimit = Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            return badRequest("limit must be a whole number");
        }
        if (parsedLimit < 1) {
            return badRequest("limit must be at least 1");
        }
        return Response.ok(messageService.getLatestMessages(parsedLimit)).build();
    }

    @POST
    public Response createMessage() {
        return Response.ok(messageService.createMessage()).build();
    }

    private static Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", message))
                .build();
    }
}
