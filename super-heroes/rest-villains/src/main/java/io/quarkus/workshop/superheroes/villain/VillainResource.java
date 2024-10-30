package io.quarkus.workshop.superheroes.villain;

import io.quarkus.workshop.superheroes.villain.entities.Villain;
import io.quarkus.workshop.superheroes.villain.services.VillainService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/villains")
@Tag(name = "villains")
public class VillainResource {

  @Inject
  private VillainService villainService;

  @Inject
  private Logger logger;

  // GET methods

  @GET
  @Produces(APPLICATION_JSON)
  @Operation(summary = "Returns all the villains from the database")
  @APIResponse(
    responseCode = "200",
    content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class, type = SchemaType.ARRAY))
  )
  public RestResponse<List<Villain>> getAllVillains() {
    logger.info("All Villains are requested.");
    List<Villain> allVillains = villainService.findAllVillains();
    logger.debug("Number of Villains found: " + allVillains.size());
    return RestResponse.ok(allVillains);
  }

  @GET
  @Path("/{id}")
  @Produces(APPLICATION_JSON)
  @Operation(summary = "Returns a villain for a given identifier")
  @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
  @APIResponse(responseCode = "204", description = "The villain is not found for a given identifier")
  public RestResponse<Villain> getVillain(@RestPath Long id) {
    logger.info("Villain with ID " + id + " is requested.");
    Villain villain = villainService.findVillainById(id);
    if (villain == null) {
      logger.debug("Villain with ID " + id + " not found.");
      return RestResponse.noContent();
    }
    logger.debug("Villain found: " + villain);
    return RestResponse.ok(villain);
  }

  @GET
  @Path("/random")
  @Produces(APPLICATION_JSON)
  @Operation(summary = "Returns a random villain")
  @APIResponse(
    responseCode = "200",
    content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class, required = true))
  )
  @APIResponse(
    responseCode = "204",
    description = "No Content"
  )
  public RestResponse<Villain> getRandomVillain() {
    logger.info("Random Villain is requested.");
    try {
      Villain randomVillain = villainService.findRandomVillain();
      logger.debug("Random Villain found: " + randomVillain);
      return RestResponse.ok(randomVillain);
    }
    catch (IllegalArgumentException e) {
      logger.error(e.getMessage());
      return RestResponse.noContent();
    }
  }

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
      return "Hello Villain Resource";
  }

  //POST methods

  @POST
  @Produces(APPLICATION_JSON)
  @Operation(summary = "Creates a valid villain")
  @APIResponse( responseCode = "201", description = "The URI of the created villain",
    content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class))
  )
  public RestResponse<Void> createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
    logger.info("Villain is requested to be added: " + villain);
    villain = villainService.persistVillain(villain);
    UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.id));
    logger.debug("New villain created with URI " + builder.build().toString());
    return RestResponse.created(builder.build());
  }

  // PUT methods

  @PUT
  @Produces(APPLICATION_JSON)
  @Operation(summary = "Updates an exiting  villain")
  @APIResponse( responseCode = "200", description = "The updated villain",
    content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class))
  )
  @APIResponse(responseCode = "204", description = "No Content")
  public RestResponse<Villain> updateVillain(@Valid Villain villain) {
    logger.info("Villain is requested to be updated: " + villain);
    try {
      villain = villainService.updateVillain(villain);
      logger.debug("Villain updated: " + villain);
      return RestResponse.ok(villain);
    } catch (IllegalArgumentException e) {
      logger.error(e.getMessage());
      return RestResponse.noContent();
    }
  }

  // DELETE methods

  @DELETE
  @Path("/{id}")
  @Produces(APPLICATION_JSON)
  @Operation(summary = "Deletes an exiting villain")
  @APIResponse(responseCode = "200", description = "The villain is deleted")
  @APIResponse(responseCode = "204", description = "The villain is not found for a given identifier")
  public RestResponse<Void> deleteVillain(Long id) {
    logger.info("Villain with ID " + id + " is requested to be deleted.");
    try {
      villainService.deleteVillain(id);
      logger.debug("Villain with ID " + id + " is deleted.");
      return RestResponse.ok();
    }
    catch (IllegalArgumentException e) {
      logger.error(e.getMessage());
      return RestResponse.noContent();
    }
  }

}
