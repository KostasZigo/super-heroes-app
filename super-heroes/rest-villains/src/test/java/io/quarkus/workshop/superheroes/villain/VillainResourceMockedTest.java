package io.quarkus.workshop.superheroes.villain;

import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.workshop.superheroes.villain.entities.Villain;
import io.quarkus.workshop.superheroes.villain.services.VillainService;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@QuarkusTest
public class VillainResourceMockedTest {

  @Test
  void testGetRandomVillainOnEmptyDB() {
    // Install the mock
    VillainService mockVillainService = new VillainService() {
      @Override
      public Villain findRandomVillain() {
        // Override to throw an exception
        throw new IllegalArgumentException("No villains found in DB.");
      }
    };

    // Replace the real service with the mock using QuarkusMock
    QuarkusMock.installMockForType(mockVillainService, VillainService.class);

    given()
      .when()
      .contentType(APPLICATION_JSON)
      .get("/api/villains/random")
      .then()
      .statusCode(NO_CONTENT.getStatusCode());
  }
}
