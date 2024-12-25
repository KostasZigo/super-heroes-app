package io.quarkus.workshop.superheroes.hero;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.workshop.superheroes.hero.api.HeroesApi;
import io.quarkus.workshop.superheroes.hero.api.model.Hero;
import io.quarkus.workshop.superheroes.hero.services.HeroService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.slf4j.LoggerFactory;

// Resource class for the Heroes API.
// Heroes API will not be using git submodule as the Villain one. But rather contain the OpenAPI specifications
// directly in its code.
@Path("/api/heroes")
@Tag(name = "heroes")
public class HeroResource implements HeroesApi {

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(HeroResource.class);

  @Inject
  private Logger logger;

  @Context
  UriInfo uriInfo;

  @Inject
  HeroService heroService;

  public Uni<Response> getRandomHero() {
    logger.info("Retrieving random hero...");
    return heroService.retrieveRandomHero().map(hero -> Response.ok(hero).build())
      .onFailure(NoContentException.class)
      .recoverWithItem(() -> Response.noContent().build());
  }

  public Uni<Response> getAllHeroes() {
    logger.info("Retrieving all heroes");
    return heroService.retrieveAllHeroes().map(heroList -> Response.ok(heroList).build());
  }

  public Uni<Response> getHero(@RestPath Long id) {
    logger.infof("Retrieving hero by id %d", id);
    return heroService.retrieveHeroById(id).map(hero -> Response.ok(hero).build())
      .onFailure(NoContentException.class)
      .recoverWithItem(Response.noContent().build());
  }

  @WithTransaction
  public Uni<Response> createHero(@Valid Hero hero) {
    logger.info("Creating hero..");
    return heroService.createHero(hero)
      .map(h -> {
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(h.getId()));
        logger.debug("New Hero created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
      });
  }

  @WithTransaction
  public Uni<Response> updateHero(@Valid Hero hero) {
    logger.info("Updating hero...");
    return heroService.updateHero(hero).map(h -> Response.ok(h).build());
  }

  @WithTransaction
  public Uni<Response> deleteHero(@RestPath Long id) {
    return heroService.deleteHeroById(id)
      .invoke(() -> logger.debugf("Hero deleted with %d", id))
      .replaceWith(Response.noContent().build());
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/hello")
  public Uni<Response> hello() {
    return Uni.createFrom().item(Response.ok("Hello Hero Resource").build());
  }
}
