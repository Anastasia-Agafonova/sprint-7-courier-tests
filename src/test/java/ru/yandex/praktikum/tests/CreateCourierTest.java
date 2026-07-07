package ru.yandex.praktikum.tests;
import ru.yandex.praktikum.client.CourierClient;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;


import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.hamcrest.Matchers.equalTo;

import java.util.Random;

@Feature("Создание курьера")
public class CreateCourierTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private CourierClient courierClient;

    @Before
    @Step("Настройка базового URL перед тестами")
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        courierClient = new CourierClient();
    }

    @Step("Генерация случайного логина")
    private String getRandomLogin() {
        return "ninja" + new Random().nextInt(1000000);
    }

// Позитивные тесты
    @Test
    @DisplayName("Успешное создание курьера со всеми полями")
    @Description("Проверяем код ответа 201 и тело ответа {'ok': true}\"")
    public void courierCanBeCreatedWithValidData() {
        String login = getRandomLogin();
        String password = "password123";
        String firstName = "saske";

        courierClient.createCourier(login, password, firstName)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

// Негативные тесты
    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверяем код 409 и сообщение об ошибке для дубликата")
    public void cannotCreateTwoIdenticalCouriers() {
        String login = getRandomLogin();
        String password = "password123";
        String firstName = "saske";

         courierClient.createCourier(login, password, firstName)
                .then()
                .statusCode(SC_CREATED);

        courierClient.createCourier(login, password, firstName)
                .then()
                .statusCode(SC_CONFLICT)
                .body("message",
                        equalTo("Этот логин уже используется. Попробуйте другой."));
        }

    @Test
    @DisplayName("Создание курьера без пароля возвращает ошибку")
    @Description("Проверяем код 400 при отсутствии обязательного поля")
    public void createCourierWithoutPasswordReturnsError() {

        courierClient.createCourier(getRandomLogin(), null, "saske")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message",
                        equalTo("Недостаточно данных для создания учетной записи"));

    }

    @Test
    @DisplayName("Создание курьера без логина возвращает ошибку")
    @Description("Проверяем код 400 при отсутствии обязательного поля login")
    public void createCourierWithoutLoginReturnsError() {

        courierClient.createCourier(null, "password123", "saske")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message",
                        equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера с пустым паролем возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении пароля")
    public void createCourierWithEmptyPasswordReturnsError() {

        courierClient.createCourier(getRandomLogin(), "", "saske")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message",
                        equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера с пустым логином возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении логина")
    public void createCourierWithEmptyLoginReturnsError() {

        courierClient.createCourier("", "password123", "saske")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message",
                        equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без тела запроса")
    @Description("Проверяем код 400 при пустом теле запроса")
    public void createCourierWithEmptyBodyReturnsError() {

        courierClient.createCourierWithEmptyBody()
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message",
                        equalTo("Недостаточно данных для создания учетной записи"));
    }
}

