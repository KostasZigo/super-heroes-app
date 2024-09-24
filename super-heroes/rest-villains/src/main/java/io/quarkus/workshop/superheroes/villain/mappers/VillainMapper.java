package io.quarkus.workshop.superheroes.villain.mappers;

import io.quarkus.workshop.superheroes.api.model.Villain;
import io.quarkus.workshop.superheroes.villain.entities.VillainEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between VillainEntity and Villain DTO.
 * Utilizes MapStruct for automatic implementation generation.
 */
@Mapper(componentModel = "jakarta")
public interface VillainMapper {

  /**
   * Converts a VillainEntity to a Villain DTO.
   *
   * @param villainEntity the entity to convert
   * @return the converted Villain DTO
   */
  Villain toVillainDTO(VillainEntity villainEntity);

  /**
   * Converts a Villain DTO to a VillainEntity.
   *
   * @param villain the DTO to convert
   * @return the converted VillainEntity
   */
  VillainEntity toVillainEntity(Villain villain);

  /**
   * Converts a list of VillainEntity objects to a list of Villain DTOs.
   *
   * @param villainEntities the list of entities to convert
   * @return the list of converted Villain DTOs
   */
  List<Villain> toVillainDTOs(List<VillainEntity> villainEntities);
}
