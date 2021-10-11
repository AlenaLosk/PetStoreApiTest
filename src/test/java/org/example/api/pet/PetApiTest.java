package org.example.api.pet;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.model.Pet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class PetApiTest {
    public String name;
    public int id;

    @BeforeClass
    public void prepare() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        RestAssured.filters(new ResponseLoggingFilter());
        name = "Pet_" + UUID.randomUUID().toString();
        id = new Random().nextInt(500);
    }

    @Test
    public void checkObjectSave() {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);

        given()
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200);

        Pet actual =
                given()
                        .pathParam("petId", id)
                        .when()
                        .get("/pet/{petId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Pet.class);
        Assert.assertEquals(actual.getId(), pet.getId());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        Thread.currentThread().sleep(10000);
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        given()
                .pathParam("petId", id)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200);
        given()
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404);
    }
}
