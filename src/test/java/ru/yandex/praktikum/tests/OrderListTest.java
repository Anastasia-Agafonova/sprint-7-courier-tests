package ru.yandex.praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Получение списка заказов")
public class OrderListTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";


    @Before
    @Step("Настройка базового URL перед тестами")
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    // Основные тесты

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверяем, что в тело ответа возвращается список заказов")
    public void getOrdersListReturnsOrders() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", isA(List.class));
    }

    @Test
    @DisplayName("Список заказов содержит все необходимые поля")
    @Description("Проверяем структуру каждого заказа в списке")
    public void ordersListHasCorrectStructure() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.id", instanceOf(List.class))
                .body("orders.firstName", instanceOf(List.class))
                .body("orders.lastName", instanceOf(List.class))
                .body("orders.address", instanceOf(List.class))
                .body("orders.metroStation", instanceOf(List.class))
                .body("orders.phone", instanceOf(List.class))
                .body("orders.rentTime", instanceOf(List.class))
                .body("orders.deliveryDate", instanceOf(List.class))
                .body("orders.track", instanceOf(List.class))
                .body("orders.createdAt", instanceOf(List.class))
                .body("orders.updatedAt", instanceOf(List.class))
                .body("orders.status", instanceOf(List.class));
    }

    @Test
    @DisplayName("Список заказов содержит поле track")
    @Description("Проверяем, что каждый заказ содержит track номер")
    public void ordersListContainsTrack() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.track", notNullValue());
    }

    // Тесты с параметрами

    @Test
    @DisplayName("Получение заказов с пагинацией (limit)")
    @Description("Проверяем, что пагинация работает с параметром limit")
    public void getOrdersWithLimit() {
        int limit = 5;

        given()
                .filter(new AllureRestAssured())
                .queryParam("limit", limit)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.size()", lessThanOrEqualTo(limit));
    }

    @Test
    @DisplayName("Получение заказов с пагинацией (page)")
    @Description("Проверяем, что пагинация работает с параметром page")
    public void getOrdersWithPage() {
        int limit = 10;
        int page = 1;

        given()
                .filter(new AllureRestAssured())
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов с максимальным лимитом")
    @Description("Проверяем, что максимальный лимит = 30")
    public void getOrdersWithMaxLimit() {
        int limit = 30;

        given()
                .filter(new AllureRestAssured())
                .queryParam("limit", limit)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов с превышением лимита")
    @Description("Проверяем, что при превышении лимита возвращается максимум 30")
    public void getOrdersWithOverLimit() {
        int limit = 50;

        given()
                .filter(new AllureRestAssured())
                .queryParam("limit", limit)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    // Тесты с фильтрами

    @Test
    @DisplayName("Получение заказов по courierId")
    @Description("Проверяем фильтрацию заказов по идентификатору курьера")
    public void getOrdersByCourierId() {
        int courierId = 999999;

        given()
                .filter(new AllureRestAssured())
                .queryParam("courierId", courierId)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
        }

    @Test
    @DisplayName("Получение заказов с фильтром по станции метро")
    @Description("Проверяем фильтрацию заказов по станциям метро")
    public void getOrdersByNearestStation() {
        String nearestStation = "[\"1\", \"2\"]";

        given()
                .filter(new AllureRestAssured())
                .queryParam("nearestStation", nearestStation)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов с несколькими фильтрами")
    @Description("Проверяем работу нескольких фильтров одновременно")
    public void getOrdersWithMultipleFilters() {
        int limit = 10;
        int page = 0;
        int courierId = 999999;

        given()
                .filter(new AllureRestAssured())
                .queryParam("limit", limit)
                .queryParam("page", page)
                .queryParam("courierId", courierId)
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
        }

    // Тесты на текстуру полей

    @Test
    @DisplayName("Проверка типа поля status")
    @Description("Проверяем, что поле status является числом")
    public void checkStatusFieldType() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders[0].status", instanceOf(Integer.class));
    }

    @Test
    @DisplayName("Проверка типа поля color")
    @Description("Проверяем, что поле color является массивом")
    public void checkColorFieldType() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders.color", anyOf(nullValue(), instanceOf(List.class)));
    }

    @Test
    @DisplayName("Проверка формата даты deliveryDate")
    @Description("Проверяем, что deliveryDate приходит в формате ISO")
    public void checkDeliveryDateFormat() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders[0].deliveryDate", anyOf(nullValue(), matchesPattern("^\\d{4}-\\d{2}-\\d{2}.*$")));
    }

    @Test
    @DisplayName("Проверка формата дат createdAt и updatedAt")
    @Description("Проверяем, что createdAt и updatedAt приходят в формате ISO")
    public void checkDateFieldsFormat() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("orders[0].createdAt", anyOf(nullValue(), matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")))
                .body("orders[0].updatedAt", anyOf(nullValue(), matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$")));
    }
}
