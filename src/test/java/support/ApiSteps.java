package support;

import io.qameta.allure.Step;
import data.*;

import static org.junit.jupiter.api.Assertions.*;

public class ApiSteps {

    @Step("Регистрация пользователя") // Шаг для отчёта Allure: регистрация пользователя
    public static UserResponse createUser(User user) {
        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateUser(user); // Отправляем запрос регистрации
        response.then().statusCode(200); // Проверяем, что статус ответа 200 OK
        UserResponse userResponse = response.body().as(UserResponse.class); // Десериализуем тело ответа в UserResponse
        assertTrue(userResponse.isSuccess()); // Проверяем, что операция успешна
        return userResponse; // Возвращаем объект ответа
    }

    @Step("Авторизация пользователя") // Шаг для отчёта Allure: авторизация пользователя
    public static UserResponse loginUser(User user) {
        io.restassured.response.Response response = ApiRequests.sendPostRequestLoginUser(user); // Отправляем запрос логина
        response.then().statusCode(200); // Проверяем статус 200 OK
        UserResponse userResponse = response.body().as(UserResponse.class); // Десериализуем ответ
        assertTrue(userResponse.isSuccess()); // Проверяем успешность операции
        return userResponse; // Возвращаем ответ
    }

    @Step("Обновление пользователя") // Шаг для отчёта Allure: обновление данных пользователя
    public static UserResponse updateUser(User user, String accessToken) {
        io.restassured.response.Response response = ApiRequests.sendPostRequestUpdateUser(user, accessToken); // Отправляем запрос обновления
        response.then().statusCode(200); // Проверяем статус 200 OK
        UserResponse userResponse = response.body().as(UserResponse.class); // Десериализуем ответ
        assertTrue(userResponse.isSuccess()); // Проверяем успешность операции
        return userResponse; // Возвращаем ответ
    }

    @Step("Удаление пользователя") // Шаг для отчёта Allure: удаление пользователя
    public static void deleteUser(String accessToken) {
        io.restassured.response.Response response = ApiRequests.sendPostRequestDeleteUser(accessToken); // Отправляем запрос удаления
        response.then().statusCode(202); // Проверяем статус 202 Accepted
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа в объект Response
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertTrue(resp.isSuccess()), // Проверяем, что операция успешна
                () -> assertEquals("User successfully removed", resp.getMessage()) // Проверяем сообщение в ответе
        );
    }

    @Step("Создание заказа") // Шаг для отчёта Allure: создание заказа
    public static OrderResponse createOrder(String accessToken, OrderCreateRequest orderCreateRequest) {
        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateOrder(accessToken, orderCreateRequest); // Отправляем запрос создания заказа
        response.then().statusCode(200); // Проверяем статус 200 OK
        OrderResponse orderResponse = response.body().as(OrderResponse.class); // Десериализуем ответ
        assertTrue(orderResponse.isSuccess()); // Проверяем успешность операции
        return orderResponse; // Возвращаем ответ
    }

    @Step("Получение заказов") // Шаг для отчёта Allure: получение списка заказов
    public static OrderListGetResponse getOrders(String accessToken) {
        io.restassured.response.Response response = ApiRequests.sendPostRequestGetOrders(accessToken); // Отправляем запрос получения заказов
        response.then().statusCode(200); // Проверяем статус 200 OK
        OrderListGetResponse orderListGetResponse = response.body().as(OrderListGetResponse.class); // Десериализуем ответ
        assertTrue(orderListGetResponse.isSuccess()); // Проверяем успешность операции
        return orderListGetResponse; // Возвращаем ответ
    }
}
