package io.quarkus.workshop.superheroes.hero.repositories;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.workshop.superheroes.hero.entities.HeroEntity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Random;

@WithSession
@ApplicationScoped
public class HeroRepository implements PanacheRepository<HeroEntity> {

  /**
   * Finds a random hero entity by retrieving the total count of heroes, generating a random number in that range
   * and using it as an offset of paginated result containing a single hero per page.
   *
   * @return a Uni containing the random hero entity.
   */
  public Uni<HeroEntity> findRandom() {
    Random random = new Random();
    return count()
      .map(count -> random.nextInt(count.intValue()))
      .chain(randomHero -> findAll().page(randomHero, 1)
        .firstResult());
  }

}
