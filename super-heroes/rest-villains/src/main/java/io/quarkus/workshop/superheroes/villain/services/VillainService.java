package io.quarkus.workshop.superheroes.villain.services;

import io.quarkus.workshop.superheroes.villain.entities.Villain;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Random;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

/**
 * Service class for managing Villain entities and their database operations.
 */
@ApplicationScoped
@Transactional
public class VillainService {

  @ConfigProperty(name = "level.multiplier", defaultValue = "1.0")
  private double levelMultiplier;

  @Transactional(SUPPORTS)
  public List<Villain> findAllVillains() {
    return Villain.listAll();
  }

  @Transactional(SUPPORTS)
  public Villain findVillainById(Long id) {
    return Villain.findById(id);
  }

  /**
   * Finds a random villain from the database by first counting the total number of villains in the database.
   * If there are no villains present, it throws an `IllegalArgumentException`.
   * Otherwise, it finds all villains in a paginated way and return the random page containing 1 villain.
   * @throws IllegalArgumentException if no villains are found in the database.
   */
  @Transactional(SUPPORTS)
  public Villain findRandomVillain() {
    long countVillains = Villain.count();
    if (countVillains == 0) {
      throw new IllegalArgumentException("No villains found in DB.");
    }
    Random random = new Random();
    int randomVillain = random.nextInt((int) countVillains);
    return Villain.findAll().page(randomVillain, 1).firstResult();
  }

  public Villain persistVillain(@Valid Villain villain) {
    villain.level = (int) Math.round(villain.level * levelMultiplier);
    villain.persist();
    return villain;
  }

  /**
   * Updates an existing villain in the database.
   * @param villain the villain with updated information.
   * @return the updated villain.
   * @throws IllegalArgumentException if the villain with the given ID does not exist.
   */
  public Villain updateVillain(@Valid Villain villain) {
    Villain villainEntity = Villain.findById(villain.id);
    if (villainEntity == null) {
      throw new IllegalArgumentException("Villain with id " + villain.id + " does not exist.");
    }
    return updateVillainFields(villainEntity, villain);
  }

  /**
   * Deletes a villain by its ID.
   * @throws IllegalArgumentException if the villain with the given ID does not exist.
   */
  public void deleteVillain(Long id) {
    Villain villain = Villain.findById(id);
    if (villain == null) {
      throw new IllegalArgumentException("Villain with id " + id + " does not exist.");
    }
    villain.delete();
  }

  /*
    Helper methods
   */

  private static Villain updateVillainFields(Villain villainEntity, Villain villain) {
    villainEntity.name = villain.name;
    villainEntity.otherName = villain.otherName;
    villainEntity.level = villain.level;
    villainEntity.picture = villain.picture;
    villainEntity.powers = villain.powers;
    return villainEntity;
  }
}
