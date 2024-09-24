package io.quarkus.workshop.superheroes.villain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Entity class representing a Villain in the system.
 * Extends PanacheEntity to leverage Panache's active record pattern.
 */
@Entity
public class VillainEntity extends PanacheEntity {


  //The name of the villain.
  @NotNull
  @Size(min = 3, max = 50)
  public String name;

  public String otherName;

  @NotNull
  @Min(1)
  public int level;


  // A URL or path to an image representing the villain.
    public String picture;

  /**
   * A description of the villain's powers.
   * Stored as a TEXT column in the database.
   */
  @Column(columnDefinition = "TEXT")
  public String powers;

  @Override
  public String toString() {
    return "Villain{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", otherName='" + otherName + '\'' +
      ", level=" + level +
      ", picture='" + picture + '\'' +
      ", powers='" + powers + '\'' +
      '}';
  }
}
