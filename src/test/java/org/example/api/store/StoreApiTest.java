package org.example.api.store;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.model.Order;
import org.example.model.Pet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import org.testng.annotations.Test;

public class StoreApiTest {
    public int orderId;
    public int petId;
    public int quantity;
    boolean complete;

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
        Random random = new Random();
        orderId = random.nextInt(9) + 1;
        petId = random.nextInt(100) + 1;
        quantity = 2;
        complete = true;
    }


    @Test
    public void createOrderTest() throws InterruptedException {
        Order order = new Order();
        order.setId(orderId);
        order.setPetId(petId);
        order.setQuantity(quantity);

        // todo: офoрмить заказ на питомца
        given()
                .body(order)
                .when()
                .post("/store/order/")
                .then()
                .statusCode(200);

        // todo: найти оформленный заказ
        Order actual =
                given()
                        .pathParam("orderId", orderId)
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Order.class);
        Assert.assertEquals(actual.getId(), order.getId());
        Assert.assertEquals(actual.getPetId(), order.getPetId());
    }

    @Test
    public void includeAllOrdersInMap() throws IOException, InterruptedException {
        Thread.currentThread().sleep(5000);
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        // todo: удалить заказ
        Map inventory = given()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .extract().body()
                .as(Map.class);
        Assert.assertTrue(inventory.containsKey("sold"), "Inventory не содержит статус sold" );
    }

    @Test
    public void testDeleteOrder() throws IOException, InterruptedException {
        Thread.currentThread().sleep(10500);
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        // todo: удалить заказ
        given()
                .pathParam("orderId", orderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
        // todo: проверить удаление заказа
        given()
                .pathParam("orderId", orderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }
}
