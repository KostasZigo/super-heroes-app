package io.quarkus.workshop.superheroes.villain.services;


import io.quarkus.workshop.superheroes.villain.api.model.Villain;
import io.quarkus.workshop.superheroes.villain.entities.VillainEntity;
import io.quarkus.workshop.superheroes.villain.mappers.VillainMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

  @Inject
  VillainMapper villainMapper;

  @ConfigProperty(name = "level.multiplier", defaultValue = "1.0")
  private double levelMultiplier;

  @Transactional(SUPPORTS)
  public List<Villain> findAllVillains() {
    return villainMapper.toVillainDTOs(VillainEntity.listAll());
  }

  @Transactional(SUPPORTS)
  public Villain findVillainById(Long id) {
    return villainMapper.toVillainDTO(VillainEntity.findById(id));
  }

  /**
   * Finds a random villain from the database by first counting the total number of villains in the database.
   * If there are no villains present, it throws an `IllegalArgumentException`.
   * Otherwise, it finds all villains in a paginated way and return the random page containing 1 villain.
   * @throws IllegalArgumentException if no villains are found in the database.
   */
  @Transactional(SUPPORTS)
  public Villain findRandomVillain() {
    long countVillains = VillainEntity.count();
    if (countVillains == 0) {
      throw new IllegalArgumentException("No villains found in DB.");
    }
    Random random = new Random();
    int randomVillain = random.nextInt((int) countVillains);
    return villainMapper.toVillainDTO(VillainEntity.findAll().page(randomVillain, 1).firstResult());
  }

  public Villain persistVillain(@Valid Villain villain) {
    VillainEntity villainEntity = villainMapper.toVillainEntity(villain);
    villainEntity.level = (int) Math.round(villainEntity.level * levelMultiplier);
    villainEntity.persist();
    return villainMapper.toVillainDTO(villainEntity);
  }

  /**
   * Updates an existing villain in the database.
   * @param villain the villain with updated information.
   * @return the updated villain.
   * @throws IllegalArgumentException if the villain with the given ID does not exist.
   */
  public Villain updateVillain(@Valid Villain villain) {
    VillainEntity villainEntity = VillainEntity.findById(villain.getId());
    if (villainEntity == null) {
      throw new IllegalArgumentException("Villain with id " + villain.getId() + " does not exist.");
    }
    return villainMapper.toVillainDTO(updateVillainFields(villainEntity, villain));
  }

  /**
   * Deletes a villain by its ID.
   * @throws IllegalArgumentException if the villain with the given ID does not exist.
   */
  public void deleteVillain(Long id) {
    VillainEntity villain = VillainEntity.findById(id);
    if (villain == null) {
      throw new IllegalArgumentException("Villain with id " + id + " does not exist.");
    }
    villain.delete();
  }

  /*
    Helper methods
   */

  private static VillainEntity updateVillainFields(VillainEntity villainEntity, Villain villain) {
    villainEntity.name = villain.getName();
    villainEntity.otherName = villain.getOtherName();
    villainEntity.level = villain.getLevel();
    villainEntity.picture = villain.getPicture();
    villainEntity.powers = villain.getPowers();
    return villainEntity;
  }
}
