package ru.yandex.praktikum.tests;

import org.junit.After;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.models.Order;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Feature("Создание заказа")
@RunWith(Parameterized.class)
public class OrderCreateTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private OrderClient orderClient;
    private int track;

    // Параметры для параметризации
    private final List<String> color;
    private final String testName;

    public OrderCreateTest(List<String> color, String testName) {
        this.color = color;
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {Collections.singletonList("BLACK"), "Только BLACK"},
                {Collections.singletonList("GREY"), "Только GREY"},
                {Arrays.asList("BLACK", "GREY"), "BLACK и GREY вместе"},
                {null, "Без цвета (поле отсутствует)"}
        });
    }

    @Before
    @Step("Настройка перед тестами")
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderClient = new OrderClient();
    }

    @After
    @Step("Отмена созданного заказа")
    public void tearDown() {
        if (track != 0) {
            orderClient.cancelOrder(track)
                    .then()
                    .statusCode(SC_OK);
        }
    }

    @Step("Создание заказа с цветами: {color}")
    private Order createOrder(List<String> color) {
        return new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                color
        );
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверяем создание заказа с различными комбинациями цветов и возврат track")
    public void createOrderWithDifferentColors() {
        logTestScenario(testName);
        Order order = createOrder(color);

        Response response = orderClient.createOrder(order);

        track = response.then()
                .extract()
                .path("track");

        response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());
    }
    @Step("Запуск сценария: {scenarioName}")
    private void logTestScenario(String scenarioName) {
        System.out.println("Выполняется тест: " + scenarioName);
    }
}