package io.quarkus.workshop.superheroes.hero.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class HeroEntity extends PanacheEntity {

  @NotNull
  @Size(min = 3, max = 50)
  public String name;

  /**
   * The other name or alias of the hero.
   */
  public String otherName;

  @NotNull
  @Min(1)
  public int level;

  /**
   * The URL of the hero's picture.
   */
  public String picture;

  /**
   * The powers of the hero.
   * Stored as a TEXT column in the database.
   */
  @Column(columnDefinition = "TEXT")
  public String powers;

  @Override
  public String toString() {
    return "Hero{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", otherName='" + otherName + '\'' +
      ", level=" + level +
      ", picture='" + picture + '\'' +
      ", powers='" + powers + '\'' +
      '}';
  }
}
