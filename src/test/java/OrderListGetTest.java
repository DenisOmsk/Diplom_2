import support.ApiRequests;
import support.ApiSteps;
import io.qameta.allure.Description;
import data.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*; // Импорт статических методов assert для удобства

public class OrderListGetTest {

    private static final User USER_1 = new User("denis_user1@yandex.ru", "password", "Денис"); // Данные тестового пользователя
    private static final OrderCreateRequest ORDER_1 = new OrderCreateRequest( // Данные для создания заказа с ингредиентами
            List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"));
    String accessToken1; // Токен доступа пользователя, создаваемого в тестах

    @Test
    @DisplayName("Получение заказа пользователя") // Имя теста
    @Description("Проверка получения заказа с авторизацией:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Заказ показан.") // Описание для отчёта Allure
    public void getOrders() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken(); // Создаём пользователя и получаем токен

        OrderResponse orderResponse = ApiSteps.createOrder(accessToken1, ORDER_1); // Создаём заказ с авторизацией
        Integer expectedNumberOrder = orderResponse.getOrder().getNumber(); // Получаем номер созданного заказа

        OrderListGetResponse orderListGetResponse = ApiSteps.getOrders(accessToken1); // Получаем список заказов пользователя

        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertEquals(expectedNumberOrder, orderListGetResponse.getOrders().get(0).getNumber(),
                        "Не заполнено поле orders!"), // Проверяем, что первый заказ в списке совпадает по номеру
                () -> assertNotNull(orderListGetResponse.getTotal(),
                        "Не заполнено поле total!"), // Проверяем, что поле total не пустое
                () -> assertNotNull(orderListGetResponse.getTotalToday(),
                        "Не заполнено поле totalToday!") // Проверяем, что поле totalToday не пустое
        );
    }

    @Test
    @DisplayName("Получение пустого списка заказов пользователя") // Имя теста
    @Description("Проверка получения пустого списка заказов пользователя с авторизацией:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Заказов нет.") // Описание для отчёта Allure
    public void getOrdersEmpty() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken(); // Создаём пользователя и получаем токен

        OrderListGetResponse orderListGetResponse = ApiSteps.getOrders(accessToken1); // Получаем список заказов (пока пустой)

        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertTrue(orderListGetResponse.getOrders().isEmpty(),
                        "Поле orders не пустое!"), // Проверяем, что список заказов пуст
                () -> assertNotNull(orderListGetResponse.getTotal(),
                        "Не заполнено поле total!"), // Проверяем, что поле total не пустое
                () -> assertNotNull(orderListGetResponse.getTotalToday(),
                        "Не заполнено поле totalToday!") // Проверяем, что поле totalToday не пустое
        );
    }

    @Test
    @DisplayName("Получение заказа конкретного пользователя без авторизации") // Имя теста
    @Description("Проверка неуспешного получения заказа без авторизации:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.") // Описание для отчёта Allure
    public void getOrdersWithoutAuth() {
        io.restassured.response.Response response = ApiRequests.sendPostRequestGetOrders(""); // Отправляем запрос без токена
        response.then().statusCode(401); // Проверяем, что статус 401 Unauthorized
        Response resp = response.body().as(Response.class); // Десериализуем ответ в модель Response

        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // Проверяем, что success = false
                () -> assertEquals("You should be authorised", resp.getMessage(),
                        "Неверное значение поля message!") // Проверяем сообщение об ошибке
        );
    }

    @AfterEach
    public void tearDown() {
        if (accessToken1 != null) ApiSteps.deleteUser(accessToken1); // Удаляем пользователя после каждого теста, если он был создан
    }
}