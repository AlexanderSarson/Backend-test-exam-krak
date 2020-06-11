/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dtos.HobbyDTO;
import errorhandling.UserException;
import facades.HobbyFacade;
import facades.PersonFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;

/**
 * REST Web Service
 *
 * @author root
 */
@Path("hobby")
public class HobbyResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/startcode",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final HobbyFacade FACADE = HobbyFacade.getHobbyFacade(EMF);

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PersonResource
     */
    public HobbyResource() {
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus() {
        return "{\"status\": \"it works\"}";
    }
    
    @Operation(summary = "Get all hobbies",
            tags = {"hobby"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = HobbyDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested hobbies"),
                @ApiResponse(responseCode = "404", description = "No people found with that hobby")})
    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public List<HobbyDTO> getAllHobbies() throws UserException {
        return FACADE.getAllHobbies();
    }
}
