package ru.yandex.praktikum.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Random;

@Feature("Логин курьера")
public class CourierLoginTest {

    private CourierClient courierClient;

    private String login;
    private String password;

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    @Before
    @Step("Подготовка данных для теста")
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        courierClient = new CourierClient();

        login = "ninja" + new Random().nextInt(1000000);
        password = "password123";
        String firstName = "saske";

        courierClient.createCourier(login, password, firstName)
                .then()
                .statusCode(SC_CREATED);
    }

    @Step("Генерация случайного логина")
    private String getRandomLogin() {
        return "ninja" + new Random().nextInt(1000000);
    }

    // Позитивные тесты
    @Test
    @DisplayName("Успешный логин курьера с валидными данными")
    @Description("Проверяем код 200 и возврат id курьера")
    public void courierCanLoginWithValidData() {


        courierClient.loginCourier(login, password)
                .then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    // Негативные тесты
    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверяем код 404 при неверном пароле")
    public void loginWithWrongPasswordReturnsError() {

        courierClient.loginCourier(login, "wrongPassword")
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Несуществующий логин")
    @Description("Проверяем код 404 при несуществующем логине")
    public void loginWithNotExistentLoginReturnsError() {
        String nonExistentLogin = getRandomLogin() + "_never_exists";

        courierClient.loginCourier(nonExistentLogin, password)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин без пароля возвращает ошибку")
    @Description("Проверяем код 400 при отсутствии обязательного поля password")
    public void loginWithoutPasswordReturnsError() {

        courierClient.loginCourier(login, null)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин с пустым паролем возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении пароля")
    public void loginWithEmptyPasswordReturnError() {
        courierClient.loginCourier(login, "")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без логина возвращает ошибку")
    @Description("Проверяем код 400 при отсутствии обязательного поля login")
    public void loginWithoutLoginReturnsError() {

        courierClient.loginCourier(null, password)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин с пустым логином возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении логина")
    public void loginWithEmptyLoginReturnError() {

        courierClient.loginCourier("", password)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без тела запроса")
    @Description("Проверяем код 400 при отсутствии тела запроса")
    public void loginWithEmptyBodyReturnError () {

        courierClient.loginWithEmptyBody()
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
