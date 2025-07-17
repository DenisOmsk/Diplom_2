package support;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import data.OrderCreateRequest;
import data.User;

import static io.restassured.RestAssured.given;

public class ApiRequests {
    private final static RequestSpecification SPEC = new RequestSpecBuilder() // Строим базовую спецификацию запроса
            .setBaseUri(URL.getHost()) // Базовый URI для всех запросов
            .addHeader("Content-type", "application/json") // Заголовок Content-type для JSON
            .setRelaxedHTTPSValidation() // Отключаем строгую проверку HTTPS сертификатов
            .addFilter(new RequestLoggingFilter()) // Логируем отправляемые запросы
            .addFilter(new ResponseLoggingFilter()) // Логируем получаемые ответы
            .addFilter(new ErrorLoggingFilter()) // Логируем ошибки запросов
            .build(); // Собираем спецификацию

    @Step("Отправить POST запрос /api/auth/register") // Шаг для отчёта Allure
    public static Response sendPostRequestCreateUser(User user) {
        return given()
                .spec(SPEC) // Используем базовую спецификацию
                .body(user) // Тело запроса — объект пользователя
                .post("/api/auth/register") // POST запрос на регистрацию
                .thenReturn(); // Возвращаем ответ
    }

    @Step("Отправить POST запрос /api/auth/login") // Шаг для отчёта Allure
    public static Response sendPostRequestLoginUser(User user) {
        return given()
                .spec(SPEC) // Используем базовую спецификацию
                .body(user) // Тело запроса — объект пользователя
                .post("/api/auth/login") // POST запрос на логин
                .thenReturn(); // Возвращаем ответ
    }

    @Step("Отправить PATCH запрос /api/auth/user") // Шаг для отчёта Allure
    public static Response sendPostRequestUpdateUser(User user, String accessToken) {
        return given()
                .spec(SPEC) // Используем базовую спецификацию
                .headers("Authorization", accessToken) // Добавляем заголовок авторизации
                .body(user) // Тело запроса — объект пользователя с обновлёнными данными
                .patch("/api/auth/user") // PATCH запрос на обновление пользователя
                .thenReturn(); // Возвращаем ответ
    }

    @Step("Отправить DELETE запрос /api/auth/user") // Шаг для отчёта Allure
    public static Response sendPostRequestDeleteUser(String accessToken) {
        return given()
                .spec(SPEC) // Используем базовую спецификацию
                .headers("Authorization", accessToken) // Добавляем заголовок авторизации
                .delete("/api/auth/user") // DELETE запрос на удаление пользователя
                .thenReturn(); // Возвращаем ответ
    }

    @Step("Отправить POST запрос /api/orders") // Шаг для отчёта Allure
    public static Response sendPostRequestCreateOrder(String accessToken, OrderCreateRequest orderCreateRequest) {
        return given()
                .spec(SPEC) // Используем базовую спецификацию
                .headers("Authorization", accessToken) // Добавляем заголовок авторизации
                .body(orderCreateRequest) // Тело запроса — объект с данными заказа
                .post("/api/orders") // POST запрос на создание заказа
                .thenReturn(); // Возвращаем ответ
    }

    @Step("Отправить GET запрос /api/orders") // Шаг для отчёта Allure
    public static Response sendPostRequestGetOrders(String accessToken) {
        return given()
                .spec(SPEC) // Используем базовую спецификацию
                .headers("Authorization", accessToken) // Добавляем заголовок авторизации
                .get("/api/orders") // GET запрос на получение списка заказов
                .thenReturn(); // Возвращаем ответ
    }
}

