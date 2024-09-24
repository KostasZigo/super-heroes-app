package io.quarkus.workshop.superheroes.villain;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.workshop.superheroes.api.model.Villain;
import io.restassured.common.mapper.TypeRef;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VillainResourceTest {

  private static final String JSON = "application/json;charset=UTF-8";

  private static final long FIRST_VILLAIN_ID = 50l;
  private static final String FIRST_VILLAIN_NAME = "Buuccolo";
  private static final String DEFAULT_NAME = "Super Chocolatine";
  private static final String UPDATED_NAME = "Super Chocolatine (updated)";
  private static final String DEFAULT_OTHER_NAME = "Super Chocolatine chocolate in";
  private static final String UPDATED_OTHER_NAME = "Super Chocolatine chocolate in (updated)";
  private static final String DEFAULT_PICTURE = "super_chocolatine.png";
  private static final String UPDATED_PICTURE = "super_chocolatine_updated.png";
  private static final String DEFAULT_POWERS = "does not eat pain au chocolat";
  private static final String UPDATED_POWERS = "does not eat pain au chocolat (updated)";
  private static final int DEFAULT_LEVEL = 42;
  private static final int UPDATED_LEVEL = 43;

  private static final int NB_VILLAINS = 570;
  private static String villainId;

  @Test
  public void testHelloEndpoint() {
    given()
      .header(ACCEPT, TEXT_PLAIN)
      .when()
      .get("/api/villains/hello")
      .then()
      .statusCode(OK.getStatusCode())
      .body(is("Hello Villain Resource"));
  }

  @Test
  void shouldNotGetUnknownVillain() {
    Long randomId = new Random().nextLong();
    given()
      .pathParam("id", randomId)
      .when()
      .get("/api/villains/{id}")
      .then()
      .statusCode(NO_CONTENT.getStatusCode());
  }

  @Test
  void shouldGetFirstVillain() {
    Villain firstVillain = given()
      .pathParam("id", FIRST_VILLAIN_ID)
      .when()
      .get("/api/villains/{id}")
      .then()
      .statusCode(OK.getStatusCode())
      .extract()
      .body()
      .as(Villain.class);
    assertEquals(firstVillain.getName(), FIRST_VILLAIN_NAME);
  }

  @Test
  void shouldGetRandomVillain() {
    Villain randomVillain = given()
      .when()
      .get("/api/villains/random")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .extract()
      .body()
      .as(Villain.class);
    assertNotNull(randomVillain);
    assertNotNull(randomVillain.getName());
    assertNotNull(randomVillain.getLevel());
  }

  @Test
  void shouldNotAddInvalidItem() {
    Villain villain = new Villain();
    villain.setName(null);
    villain.setOtherName(DEFAULT_OTHER_NAME);
    villain.setPicture(DEFAULT_PICTURE);
    villain.setPowers(DEFAULT_POWERS);
    villain.setLevel(0);

    given()
      .body(villain)
      .header(CONTENT_TYPE, JSON)
      .header(ACCEPT, JSON)
      .when()
      .post("/api/villains")
      .then()
      .statusCode(BAD_REQUEST.getStatusCode());
  }

  @Test
  @Order(1)
  void shouldGetInitialItems() {
    List<Villain> villains =
      given()
        .when()
        .get("/api/villains")
        .then()
        .statusCode(OK.getStatusCode())
        .contentType(APPLICATION_JSON)
        .extract()
        .body()
        .as(getVillainTypeRef());
    assertEquals(NB_VILLAINS, villains.size());
  }

  @Test
  @Order(2)
  void shouldAddAnItem() {
    Villain villain = new Villain();
    villain.setName(DEFAULT_NAME);
    villain.setOtherName(DEFAULT_OTHER_NAME);
    villain.setPicture(DEFAULT_PICTURE);
    villain.setPowers(DEFAULT_POWERS);
    villain.setLevel(DEFAULT_LEVEL);

    String location = given()
      .body(villain)
      .header(CONTENT_TYPE, JSON)
      .header(ACCEPT, JSON)
      .when()
      .post("/api/villains")
      .then()
      .statusCode(CREATED.getStatusCode())
      .extract()
      .header("Location");
    assertTrue(location.contains("/api/villains"));

    // Stores the id
    String[] segments = location.split("/");
    villainId = segments[segments.length - 1];
    assertNotNull(villainId);

    given()
      .pathParam("id", villainId)
      .when()
      .get("/api/villains/{id}")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .body("name", Is.is(DEFAULT_NAME))
      .body("otherName", Is.is(DEFAULT_OTHER_NAME))
      .body("level", Is.is(DEFAULT_LEVEL))
      .body("picture", Is.is(DEFAULT_PICTURE))
      .body("powers", Is.is(DEFAULT_POWERS));

    List<Villain> villains = get("/api/villains")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .extract()
      .body()
      .as(getVillainTypeRef());
    assertEquals(NB_VILLAINS + 1, villains.size());
  }

  @Test
  @Order(3)
  void testUpdatingAnItem() {
    Villain villain = new Villain();
    villain.setId(Long.valueOf(villainId));
    villain.setName(UPDATED_NAME);
    villain.setOtherName(UPDATED_OTHER_NAME);
    villain.setPicture(UPDATED_PICTURE);
    villain.setPowers(UPDATED_POWERS);
    villain.setLevel(UPDATED_LEVEL);

    given()
      .body(villain)
      .header(CONTENT_TYPE, JSON)
      .header(ACCEPT, JSON)
      .when()
      .put("/api/villains")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .body("name", Is.is(UPDATED_NAME))
      .body("otherName", Is.is(UPDATED_OTHER_NAME))
      .body("level", Is.is(UPDATED_LEVEL))
      .body("picture", Is.is(UPDATED_PICTURE))
      .body("powers", Is.is(UPDATED_POWERS));

    List<Villain> villains = get("/api/villains")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .extract()
      .body()
      .as(getVillainTypeRef());
    assertEquals(NB_VILLAINS + 1, villains.size());
  }


  @Test
  void testUpdatingAnUnknownItem() {
    Villain villain = new Villain();
    villain.setId(new Random().nextLong());
    villain.setName(UPDATED_NAME);
    villain.setOtherName(UPDATED_OTHER_NAME);
    villain.setPicture(UPDATED_PICTURE);
    villain.setPowers(UPDATED_POWERS);
    villain.setLevel(UPDATED_LEVEL);

    given()
      .body(villain)
      .header(CONTENT_TYPE, JSON)
      .header(ACCEPT, JSON)
      .when()
      .put("/api/villains")
      .then()
      .statusCode(NO_CONTENT.getStatusCode());
  }

  @Test
  void testUpdatingAnInvalidItem() {
    Villain villain = new Villain();
    villain.setId(Long.valueOf(villainId));
    villain.setName(UPDATED_NAME);
    villain.setOtherName(UPDATED_OTHER_NAME);
    villain.setPicture(UPDATED_PICTURE);
    villain.setPowers(UPDATED_POWERS);
    villain.setLevel(0); // 0 is invalid value

    given()
      .body(villain)
      .header(CONTENT_TYPE, JSON)
      .header(ACCEPT, JSON)
      .when()
      .put("/api/villains")
      .then()
      .statusCode(BAD_REQUEST.getStatusCode());
  }


  @Test
  @Order(4)
  void shouldRemoveAnItem() {
    given().
      pathParam("id", villainId)
      .when()
      .delete("/api/villains/{id}")
      .then()
      .statusCode(OK.getStatusCode());

    List<Villain> villains = get("/api/villains")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .extract()
      .body()
      .as(getVillainTypeRef());
    assertEquals(NB_VILLAINS, villains.size());
  }

  @Test
  void testRemoveAnUnknownItem() {
    given().
      pathParam("id", new Random().nextLong())
      .when()
      .delete("/api/villains/{id}")
      .then()
      .statusCode(NO_CONTENT.getStatusCode());
  }

  private TypeRef<List<Villain>> getVillainTypeRef() {
    return new TypeRef<List<Villain>>() {
      // Kept empty on purpose
    };
  }

}
