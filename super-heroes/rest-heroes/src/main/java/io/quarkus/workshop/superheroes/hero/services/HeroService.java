package io.quarkus.workshop.superheroes.hero.services;

import io.quarkus.workshop.superheroes.hero.api.model.Hero;
import io.quarkus.workshop.superheroes.hero.mappers.HeroMapper;
import io.quarkus.workshop.superheroes.hero.repositories.HeroRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.NoContentException;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Service class for managing heroes.
 */
@ApplicationScoped
public class HeroService {

  @Inject
  Logger logger;

  @Inject
  HeroRepository heroRepository;

  @Inject
  HeroMapper heroMapper;

  /**
   * Retrieves a random hero.
   *
   * @return a Uni containing the random hero or a NoContentException if no hero is found.
   */
  public Uni<Hero> retrieveRandomHero() {
    return heroRepository.findRandom()
      .onItem().ifNotNull().transform(hero -> {
        logger.debugf("Found random hero: %s", hero);
        return heroMapper.toHeroDto(hero);
      })
      .onItem().ifNull().failWith(() -> {
        logger.debug("No random hero found");
        return new NoContentException("No random hero found");
      });
  }

  /**
   * Retrieves all heroes.
   *
   * @return a Uni containing a list of all heroes.
   */
  public Uni<List<Hero>> retrieveAllHeroes() {
    return heroRepository.listAll()
      .onItem().transform(heroEntities -> heroMapper.toHeroDtoList(heroEntities));
  }

  /**
   * Retrieves a hero by its ID.
   *
   * @param id the ID of the hero to retrieve.
   * @return a Uni containing the hero or a NoContentException if no hero is found.
   */
  public Uni<Hero> retrieveHeroById(Long id) {
    return heroRepository.findById(id)
      .onItem().ifNotNull().transform( hero -> {
        logger.debugf("Found hero with id %s", id.toString());
        return heroMapper.toHeroDto(hero);
      })
      .onItem().ifNull().failWith(() -> {
        logger.debugf("No Hero found with id %d", id);
        return new NoContentException("No Hero found with id " + id);
      });
  }

  /**
   * Creates a new hero.
   *
   * @param hero the hero to create.
   * @return a Uni containing the created hero.
   */
  public Uni<Hero> createHero(Hero hero) {
    return heroRepository.persist(heroMapper.toHeroEntity(hero))
      .map(h -> heroMapper.toHeroDto(h));
  }

  /**
   * Updates an existing hero.
   *
   * @param hero the hero to update.
   * @return a Uni containing the updated hero.
   */
  public Uni<Hero> updateHero(Hero hero) {
    return heroRepository.findById(hero.getId())
      .map(retrieved -> {
        retrieved.name = hero.getName();
        retrieved.otherName = hero.getOtherName();
        retrieved.level = hero.getLevel();
        retrieved.picture = hero.getPicture();
        retrieved.powers = hero.getPowers();
        return retrieved;
      })
      .map(h -> {
        logger.debugf("Hero updated with new valued %s", h);
        return heroMapper.toHeroDto(h);
      });
  }

  /**
   * Deletes a hero by its ID.
   *
   * @param id the ID of the hero to delete.
   * @return a Uni containing true if the hero was deleted, otherwise throws an InternalError.
   * @throws InternalError if the hero could not be deleted.
   */
  public Uni<Boolean> deleteHeroById(Long id) throws InternalError{
    return heroRepository.deleteById(id).onItem().transform(success -> {
      if(!success) {
        throw new InternalError();
      }
      return true;
    });
  }
}
