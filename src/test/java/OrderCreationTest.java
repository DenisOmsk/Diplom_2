import data.Ingredient;
import data.IngredientsResponse;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class OrderCreationTest {
    private static final String USER_EMAIL = "denis_user@yandex.ru";
    private static final String USER_PASSWORD = "password";
    private static final String USER_NAME = "Денис";
    private static final User USER_1 = new User(USER_EMAIL, USER_PASSWORD, USER_NAME);

    private static final OrderCreateRequest ORDER_EMPTY = new OrderCreateRequest(List.of());
    private static final OrderCreateRequest ORDER_WRONG = new OrderCreateRequest(
            List.of("1", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"));

    private String accessToken;

    // Получаем список id ингредиентов из API
    private List<String> getIngredientIds() {
        var response = ApiRequests.sendGetRequestIngredients();
        response.then().statusCode(200);
        var ingredientsResponse = response.body().as(IngredientsResponse.class);
        assertTrue(ingredientsResponse.isSuccess(), "Не удалось получить ингредиенты");
        return ingredientsResponse.getData().stream()
                .map(Ingredient::get_id)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с авторизацией:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.")
    public void createOrder() {
        accessToken = ApiSteps.createUser(USER_1).getAccessToken();

        List<String> ingredientIds = getIngredientIds();
        OrderCreateRequest orderRequest = new OrderCreateRequest(ingredientIds);

        OrderResponse orderResponse = ApiSteps.createOrder(accessToken, orderRequest);
        assertAll("Проверка полей ответа",
                () -> assertNotNull(orderResponse.getName(), "Не заполнено поле name!"),
                () -> assertNotNull(orderResponse.getOrder().getNumber(), "Не заполнено поле order.number!"),
                () -> assertEquals(USER_1.getName(), orderResponse.getOrder().getOwner().getName(),
                        "Неверное значение поля order.owner.name!"),
                () -> assertEquals(USER_1.getEmail(), orderResponse.getOrder().getOwner().getEmail(),
                        "Неверное значение поля order.owner.email!")
        );
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    @Description("Проверка неуспешного создания заказа с авторизацией без ингредиентов:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedWithoutIngredients() {
        accessToken = ApiSteps.createUser(USER_1).getAccessToken();

        var response = ApiRequests.sendPostRequestCreateOrder(accessToken, ORDER_EMPTY);
        response.then().statusCode(400);
        Response resp = response.body().as(Response.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(), "Неверное значение поля success!"),
                () -> assertEquals("Ingredient ids must be provided", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хешем ингредиентов")
    @Description("Проверка неуспешного создания заказа с авторизацией и с неверным хешем ингредиентов:\n " +
            "1. Код и статус ответа 500 Internal Server Error.")
    public void createFailedWithWrongIngredient() {
        accessToken = ApiSteps.createUser(USER_1).getAccessToken();

        var response = ApiRequests.sendPostRequestCreateOrder(accessToken, ORDER_WRONG);
        response.then().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.")
    public void createOrderWithoutAuth() {
        List<String> ingredientIds = getIngredientIds();
        OrderCreateRequest orderRequest = new OrderCreateRequest(ingredientIds);

        OrderResponse orderResponse = ApiSteps.createOrder("", orderRequest);
        assertAll("Проверка полей ответа",
                () -> assertNotNull(orderResponse.getName(), "Не заполнено поле name!"),
                () -> assertNotNull(orderResponse.getOrder().getNumber(), "Не заполнено поле order.number!"),
                () -> assertNull(orderResponse.getOrder().getOwner(), "Заполнено поле order.owner!")
        );
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Проверка неуспешного создания заказа без авторизации и без ингредиентов:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedWithoutAuthWithoutIngredients() {
        var response = ApiRequests.sendPostRequestCreateOrder("", ORDER_EMPTY);
        response.then().statusCode(400);
        Response resp = response.body().as(Response.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(), "Неверное значение поля success!"),
                () -> assertEquals("Ingredient ids must be provided", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиентов")
    @Description("Проверка неуспешного создания заказа без авторизации с неверным хешем ингредиентов:\n " +
            "1. Код и статус ответа 500 Internal Server Error.")
    public void createFailedWithoutAuthWithWrongIngredient() {
        var response = ApiRequests.sendPostRequestCreateOrder("", ORDER_WRONG);
        response.then().statusCode(500);
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) ApiSteps.deleteUser(accessToken);
    }
}

