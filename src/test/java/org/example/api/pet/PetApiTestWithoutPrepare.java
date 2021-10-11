package org.example.api.pet;

import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.example.model.Pet;
import org.testng.annotations.Test;

import javax.imageio.plugins.jpeg.JPEGQTable;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;

//В этом классе реализован пример отправки запроса GET без использования спецификации запроса new RequestSpecBuilder(),
//то есть все необходимые параметры переданы одинм методом
public class PetApiTestWithoutPrepare {
    @Test
    public void testGet() throws IOException {
        // Читаем конфигурационный файл в System.properties
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        JsonObject pet = new JsonObject();
        int id = new Random().nextInt(50000); // просто нужно создать произвольный айди
        String name = "Pet_" + UUID.randomUUID().toString();
        pet.addProperty("name", name);
        pet.addProperty("id", id);

        given()//ДАНО:
                .baseUri("https://petstore.swagger.io/v2/") // задаём базовый адрес каждого ресурса
                .header(new Header("api_key", System.getProperty("api.key")))// задаём заголовок с токеном для авторизации
                .accept(ContentType.JSON)// задаём заголовок accept
                .contentType(ContentType.JSON)
                .body(pet)
                //(так как это просто пример, нужно убедиться, что объект с таким Id существует)
                .log().all()//задаём логгирование запроса
                .when()//КОГДА:
                .post("/pet") // переменная petId подставится в путь ресурса перед выполнением запроса GET
                .then()// ТОГДА:
                .statusCode(200) //проверка кода ответа
                .log().all(); //задаём логгирование ответа

        given()//ДАНО:
                .baseUri("https://petstore.swagger.io/v2/") // задаём базовый адрес каждого ресурса
                .header(new Header("api_key", System.getProperty("api.key")))// задаём заголовок с токеном для авторизации
                .accept(ContentType.JSON)// задаём заголовок accept
                .contentType(ContentType.JSON)
                .pathParam("petId", id)// заранее задаём переменную petId
                //(так как это просто пример, нужно убедиться, что объект с таким Id существует)
                .log().all()//задаём логгирование запроса
                .when()//КОГДА:
                .get("/pet/{petId}") // переменная petId подставится в путь ресурса перед выполнением запроса GET
                .then()// ТОГДА:
                .statusCode(200) //проверка кода ответа
                .log().all(); //задаём логгирование ответа
    }
}
