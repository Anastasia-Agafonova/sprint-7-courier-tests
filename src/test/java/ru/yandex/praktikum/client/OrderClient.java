package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.yandex.praktikum.models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Отмена заказа")
    public Response cancelOrder(int track) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body("{\"track\":" + track + "}")
                .when()
                .put("/api/v1/orders/cancel");
    }
}