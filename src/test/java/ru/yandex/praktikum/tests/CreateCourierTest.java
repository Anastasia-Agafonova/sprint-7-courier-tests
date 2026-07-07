package ru.yandex.praktikum.tests;
import ru.yandex.praktikum.models.Courier;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Feature("Создание курьера")
public class CreateCourierTest {

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

// Позитивные тесты
    @Test
    @DisplayName("Успешное создание курьера со всеми полями")
    @Description("Шаг 1: Проверяем код 201 и тело {'ok': true}")
    public void courierCanBeCreatedWithValidData() {
        String login = getRandomLogin();
        String password = "password123";
        String firstName = "saske";

        Courier courier = new Courier(login, password, firstName);

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

// Негативные тесты
    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Шаг 2: Проверяем код 409 и сообщение об ошибке для дубликата")
    public void cannotCreateTwoIdenticalCouriers() {
        String sharedLogin = getRandomLogin();
        String password = "password123";
        String firstName = "saske";

        Courier courier1 = new Courier(sharedLogin, password, firstName);
        Courier courier2 = new Courier(sharedLogin, password, firstName);

// Создаем первого курьера
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier1)
                .post("/api/v1/courier")
                .then()
                .statusCode(201);


// Создаем второго курьера с таким же логином
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier2)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

    }

    @Test
    @DisplayName("Создание курьера без пароля возвращает ошибку")
    @Description("Шаг 3: Проверяем код 400 при отсутствии обязательного поля")
    public void createCourierWithoutPasswordReturnsError() {
        Courier courier = new Courier(getRandomLogin(), null, "saske");

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без логина возвращает ошибку")
    @Description("Проверяем код 400 при отсутствии обязательного поля login")
    public void createCourierWithoutLoginReturnsError() {
        Courier courier = new Courier(null, "password123", "saske");

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера с пустым паролем возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении пароля")
    public void createCourierWithEmptyPasswordReturnsError() {
        Courier courier = new Courier(getRandomLogin(), "", "saske");

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера с пустым логином возвращает ошибку")
    @Description("Проверяем код 400 при пустом значении логина")
    public void createCourierWithEmptyLoginReturnsError() {
        Courier courier = new Courier("", "password123", "saske");

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без тела запроса")
    @Description("Проверяем код 400 при пустом теле запроса")
    public void createCourierWithEmptyBodyReturnsError() {
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}

