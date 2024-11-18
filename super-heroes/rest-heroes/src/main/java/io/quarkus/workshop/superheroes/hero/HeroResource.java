package io.quarkus.workshop.superheroes.hero;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.workshop.superheroes.hero.entities.HeroEntity;
import io.smallrye.mutiny.Uni;
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

@Path("/api/heroes")
@Tag(name = "heroes")
public class HeroResource {

  @Inject
  private Logger logger;

  @Operation(summary = "Returns a random hero")
  @GET
  @Path("/random")
  @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = HeroEntity.class, required = true)))
  public Uni<RestResponse<HeroEntity>> getRandomHero() {
    return HeroEntity.findRandom()
      .onItem().ifNotNull().transform(h -> {
        this.logger.debugf("Found random hero: %s", h);
        return RestResponse.ok(h);
      })
      .onItem().ifNull().continueWith(() -> {
        this.logger.debug("No random hero found");
        return RestResponse.notFound();
      });
  }

  @Operation(summary = "Returns all the heroes from the database")
  @GET
  @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = HeroEntity.class, type = SchemaType.ARRAY)))
  public Uni<List<HeroEntity>> getAllHeroes() {
    return HeroEntity.listAll();
  }

  @Operation(summary = "Returns a hero for a given identifier")
  @GET
  @Path("/{id}")
  @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = HeroEntity.class)))
  @APIResponse(responseCode = "204", description = "The hero is not found for a given identifier")
  public Uni<RestResponse<HeroEntity>> getHero(@RestPath Long id) {
    return HeroEntity.<HeroEntity>findById(id)
      .map(hero -> {
        if (hero != null) {
          return RestResponse.ok(hero);
        }
        logger.debugf("No Hero found with id %d", id);
        return RestResponse.noContent();
      });
  }

  @Operation(summary = "Creates a valid hero")
  @POST
  @APIResponse(responseCode = "201", description = "The URI of the created hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
  @WithTransaction
  public Uni<RestResponse<URI>> createHero(@Valid HeroEntity hero, @Context UriInfo uriInfo) {
    return hero.<HeroEntity>persist()
      .map(h -> {
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(h.id));
        logger.debug("New Hero created with URI " + builder.build().toString());
        return RestResponse.created(builder.build());
      });
  }

  @Operation(summary = "Updates an exiting hero")
  @PUT
  @APIResponse(responseCode = "200", description = "The updated hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = HeroEntity.class)))
  @WithTransaction
  public Uni<HeroEntity> updateHero(@Valid HeroEntity hero) {
    return HeroEntity.<HeroEntity>findById(hero.id)
      .map(retrieved -> {
        retrieved.name = hero.name;
        retrieved.otherName = hero.otherName;
        retrieved.level = hero.level;
        retrieved.picture = hero.picture;
        retrieved.powers = hero.powers;
        return retrieved;
      })
      .map(h -> {
        logger.debugf("Hero updated with new valued %s", h);
        return h;
      });

  }

  @Operation(summary = "Deletes an exiting hero")
  @DELETE
  @Path("/{id}")
  @APIResponse(responseCode = "204")
  @WithTransaction
  public Uni<RestResponse<Void>> deleteHero(@RestPath Long id) {
    return HeroEntity.deleteById(id)
      .invoke(() -> logger.debugf("Hero deleted with %d", id))
      .replaceWith(RestResponse.noContent());
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/hello")
  public String hello() {
    return "Hello Hero Resource";
  }
}
