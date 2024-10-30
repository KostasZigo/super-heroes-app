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
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/villains")
public class VillainResource {

  @Inject
  private VillainService villainService;

  @Inject
  private Logger logger;

  // GET methods

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<List<Villain>> getAllVillains() {
    logger.info("All Villains are requested.");
    List<Villain> allVillains = villainService.findAllVillains();
    logger.debug("Number of Villains found: " + allVillains.size());
    return RestResponse.ok(allVillains);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
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
  @Produces(MediaType.APPLICATION_JSON)
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
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<Void> createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
    logger.info("Villain is requested to be added: " + villain);
    villain = villainService.persistVillain(villain);
    UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.id));
    logger.debug("New villain created with URI " + builder.build().toString());
    return RestResponse.created(builder.build());
  }

  // PUT methods

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
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
  @Produces(MediaType.APPLICATION_JSON)
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
