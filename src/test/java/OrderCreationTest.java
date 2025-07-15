import support.ApiRequests;
import support.ApiSteps;
import io.qameta.allure.Description;
import data.OrderCreateRequest;
import data.Response;
import data.OrderResponse;
import data.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*; // Импорт статических методов assert для удобства

public class OrderCreationTest {

    // Данные пользователя для тестов
    private static final User USER_1 = new User(
            "denis_user@yandex.ru", "password", "Денис");
    // Корректный заказ с тремя ингредиентами
    private static final OrderCreateRequest ORDER_1 = new OrderCreateRequest(
            List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"));
    // Пустой заказ (без ингредиентов)
    private static final OrderCreateRequest ORDER_EMPTY = new OrderCreateRequest(List.of());
    // Заказ с одним неверным id ингредиента
    private static final OrderCreateRequest ORDER_WRONG = new OrderCreateRequest(
            List.of("1", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"));
    String accessToken1; // Токен доступа пользователя, создаваемого в тестах

    @Test
    @DisplayName("Создание заказа с авторизацией") // Имя теста
    @Description("Проверка создания заказа с авторизацией:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.") // Описание для отчёта Allure
    public void createOrder() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken(); // Регистрируем пользователя и получаем токен

        OrderResponse orderResponse = ApiSteps.createOrder(accessToken1, ORDER_1); // Создаём заказ с авторизацией
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertNotNull(orderResponse.getName(),
                        "Не заполнено поле name!"), // Проверяем, что поле name не пустое
                () -> assertNotNull(orderResponse.getOrder().getNumber(),
                        "Не заполнено поле order.number!"), // Проверяем, что номер заказа есть
                () -> assertEquals(USER_1.getName(), orderResponse.getOrder().getOwner().getName(),
                        "Неверное значение поля order.owner.name!"), // Проверяем имя владельца заказа
                () -> assertEquals(USER_1.getEmail(), orderResponse.getOrder().getOwner().getEmail(),
                        "Неверное значение поля order.owner.email!") // Проверяем email владельца заказа
        );
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    @Description("Проверка неуспешного создания заказа с авторизацией без ингредиентов:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedWithoutIngredients() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken(); // Регистрируем пользователя и получаем токен

        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateOrder(accessToken1, ORDER_EMPTY); // Создаём заказ без ингредиентов
        response.then().statusCode(400); // Проверяем, что статус 400 Bad Request
        Response resp = response.body().as(Response.class); // Десериализуем ответ
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // Проверяем, что success = false
                () -> assertEquals("Ingredient ids must be provided", resp.getMessage(),
                        "Неверное значение поля message!") // Проверяем сообщение об ошибке
        );
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хешем ингредиентов")
    @Description("Проверка неуспешного создания заказа с авторизацией и с неверным хешем ингредиентов:\n " +
            "1. Код и статус ответа 500 Internal Server Error.")
    public void createFailedWithWrongIngredient() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken(); // Регистрируем пользователя и получаем токен

        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateOrder(accessToken1, ORDER_WRONG); // Создаём заказ с неверным ингредиентом
        response.then().statusCode(500); // Проверяем, что статус 500 Internal Server Error
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.")
    public void createOrderWithoutAuth() {
        OrderResponse orderResponse = ApiSteps.createOrder("", ORDER_1); // Создаём заказ без токена (без авторизации)
        assertAll("Проверка полей ответа",
                () -> assertNotNull(orderResponse.getName(),
                        "Не заполнено поле name!"), // Проверяем, что поле name не пустое
                () -> assertNotNull(orderResponse.getOrder().getNumber(),
                        "Не заполнено поле order.number!"), // Проверяем, что номер заказа есть
                () -> assertNull(orderResponse.getOrder().getOwner(),
                        "Заполнено поле order.owner!") // Проверяем, что поле owner пустое (нет авторизации)
        );
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Проверка неуспешного создания заказа без авторизации и без ингредиентов:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedWithoutAuthWithoutIngredients() {
        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateOrder("", ORDER_EMPTY); // Создаём заказ без авторизации и ингредиентов
        response.then().statusCode(400); // Проверяем статус 400 Bad Request
        Response resp = response.body().as(Response.class); // Десериализуем ответ
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // Проверяем success = false
                () -> assertEquals("Ingredient ids must be provided", resp.getMessage(),
                        "Неверное значение поля message!") // Проверяем сообщение об ошибке
        );
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиентов")
    @Description("Проверка неуспешного создания заказа без авторизации с неверным хешем ингредиентов:\n " +
            "1. Код и статус ответа 500 Internal Server Error.")
    public void createFailedWithoutAuthWithWrongIngredient() {
        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateOrder("", ORDER_WRONG); // Создаём заказ без авторизации с неверным ингредиентом
        response.then().statusCode(500); // Проверяем статус 500 Internal Server Error
    }

    @AfterEach
    public void tearDown() {
        if (accessToken1 != null) ApiSteps.deleteUser(accessToken1); // Удаляем пользователя после каждого теста, если он был создан
    }
}

