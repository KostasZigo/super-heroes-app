package io.quarkus.workshop.superheroes.hero.mappers;

import io.quarkus.workshop.superheroes.hero.api.model.Hero;
import io.quarkus.workshop.superheroes.hero.entities.HeroEntity;
import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between HeroEntity and Hero DTO.
 * Utilizes MapStruct for automatic generation of mapping implementations.
 */
@ApplicationScoped
@Mapper(componentModel = "jakarta")
public interface HeroMapper {

  /**
   * Converts a HeroEntity to a Hero DTO.
   *
   * @param heroEntity the HeroEntity to convert
   * @return the converted Hero DTO
   */
  Hero toHeroDto(HeroEntity heroEntity);

  /**
   * Converts a Hero DTO to a HeroEntity.
   *
   * @param hero the Hero DTO to convert
   * @return the converted HeroEntity
   */
  HeroEntity toHeroEntity(Hero hero);

  /**
   * Converts a list of HeroEntity objects to a list of Hero DTOs.
   *
   * @param heroEntityList the list of HeroEntity objects to convert
   * @return the list of converted Hero DTOs
   */
  List<Hero> toHeroDtoList(List<HeroEntity> heroEntityList);
}
