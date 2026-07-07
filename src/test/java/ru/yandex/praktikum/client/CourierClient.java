package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.qameta.allure.restassured.AllureRestAssured;
import ru.yandex.praktikum.models.Courier;

import static io.restassured.RestAssured.given;

public class CourierClient {

    @Step("Создание курьера")
    public Response createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Логин курьера")
    public Response loginCourier(String login, String password) {
        Courier courier = new Courier(login, password, null);

        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Логин без тела запроса")
    public Response loginWithEmptyBody() {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Создание курьера без тела запроса")
    public Response createCourierWithEmptyBody() {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/courier");
    }
}

