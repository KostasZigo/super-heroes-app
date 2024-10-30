package io.quarkus.workshop.superheroes.villain;

import io.quarkus.workshop.superheroes.api.VillainsApi;
import io.quarkus.workshop.superheroes.api.model.Villain;
import io.quarkus.workshop.superheroes.villain.services.VillainService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

import java.util.List;



@Path("/api/villains")
@Tag(name = "villains")
public class VillainResource implements VillainsApi {

  @Context
  UriInfo uriInfo;

  @Inject
  private VillainService villainService;

  @Inject
  private Logger logger;

  // GET methods

  @Override
  public Response getAllVillains() {
    logger.info("All Villains are requested.");
    List<Villain> allVillains = villainService.findAllVillains();
    logger.debug("Number of Villains found: " + allVillains.size());
    return Response.ok(allVillains).build();
  }

  @Override
  public Response getVillainById(@RestPath Long id) {
    logger.info("Villain with ID " + id + " is requested.");
    Villain villain = villainService.findVillainById(id);
    if (villain == null) {
      logger.debug("Villain with ID " + id + " not found.");
      return Response.noContent().build();
    }
    logger.debug("Villain found: " + villain);
    return Response.ok(villain).build();
  }

  @Override
  public Response getRandomVillain() {
    logger.info("Random Villain is requested.");
    try {
      Villain randomVillain = villainService.findRandomVillain();
      logger.debug("Random Villain found: " + randomVillain);
      return Response.ok(randomVillain).build();
    }
    catch (IllegalArgumentException e) {
      logger.error(e.getMessage());
      return Response.noContent().build();
    }
  }

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public Response hello() {
      return Response.ok("Hello Villain Resource").build();
  }

  //POST methods

  @Override
  public Response createVillain(@Valid Villain villain) {
    logger.info("Villain is requested to be added: " + villain);
    villain = villainService.persistVillain(villain);
    UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.getId()));
    logger.debug("New villain created with URI " + builder.build().toString());
    return Response.created(builder.build()).build();
  }

  // PUT methods

  @Override
  public Response updateVillain(@Valid Villain villain) {
    logger.info("Villain is requested to be updated: " + villain);
    try {
      villain = villainService.updateVillain(villain);
      logger.debug("Villain updated: " + villain);
      return Response.ok(villain).build();
    } catch (IllegalArgumentException e) {
      logger.error(e.getMessage());
      return Response.noContent().build();
    }
  }

  // DELETE methods

  @Override
  public Response deleteVillain(Long id) {
    logger.info("Villain with ID " + id + " is requested to be deleted.");
    try {
      villainService.deleteVillain(id);
      logger.debug("Villain with ID " + id + " is deleted.");
      return Response.ok().build();
    }
    catch (IllegalArgumentException e) {
      logger.error(e.getMessage());
      return Response.noContent().build();
    }
  }

}
