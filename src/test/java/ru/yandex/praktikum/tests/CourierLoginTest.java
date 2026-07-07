package ru.yandex.praktikum.tests;

import ru.yandex.praktikum.models.Courier;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Random;

@Feature("Логин курьера")
public class CourierLoginTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    @Before
    @Step("Настройка базового URL перед тестами")
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Генерация случайного логина")
    private String getRandomLogin() {
        return "ninja" + new Random().nextInt(1000000);
    }

    @Step("Создание курьера для теста")
    private void createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(201);
    }

    // Позитивные тесты
    @Test
    @DisplayName("Успешный логин курьера с валидными данными")
    @Description("Проверяем код 200 и возврат id курьера")
    public void courierCanLoginWithValidData() {
        String login = getRandomLogin(); // создаем курьера
        String password = "password123";
        String firstName = "saske";

        Courier newCourier = new Courier(login, password, firstName);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(newCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .statusCode(201);

        Courier loginData = new Courier(login, password, null);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", notNullValue());
    }

    // Негативные тесты
    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверяем код 404 при неверном пароле")
    public void loginWithWrongPasswordReturnsError() {
        String login = getRandomLogin();

        Courier loginData = new Courier(login, "wrongPassword", null);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Несуществующий логин")
    @Description("Проверяем код 404 при несуществующем логине")
    public void loginWithNotExistentLoginReturnsError() {
        String nonExistentLogin = getRandomLogin() + "_never_exists";

        Courier loginData = new Courier(nonExistentLogin, "password123", null);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин без пароля возвращает ошибку")
    @Description("Проверяем код 400 при отсутствии обязательного поля password")
    public void loginWithoutPasswordReturnsError() {
        String login = getRandomLogin(); // создаем курьера

        Courier loginData = new Courier(login, "", null);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без логина возвращает ошибку")
    @Description("Проверяем код 400 при отсутствии обязательного поля login")
    public void LoginWithoutLoginReturnsError() {
        String password = "password123";

        Courier loginData = new Courier(null, password, null);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин с пустым паролем возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении пароля")
    public void lofinWithEmptyPasswordReturnError() {
        String login = getRandomLogin();

        Courier loginData = new Courier(login, "", null);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин с пустым логином возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении логина")
    public void lofinWithEmptyLoginReturnError() {
            String password = "password123";

        Courier loginData = new Courier("", password, null);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без тела запроса")
    @Description("Проверяем код 400 при отсутствии тела запроса")
    public void lofinWithEmptyBodyReturnError() {

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
