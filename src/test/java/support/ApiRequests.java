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
    private final static RequestSpecification SPEC = new RequestSpecBuilder()
            .setBaseUri(URL.getHost())
            .addHeader("Content-type", "application/json")
            .setRelaxedHTTPSValidation() //
            .addFilter(new RequestLoggingFilter()) //
            .addFilter(new ResponseLoggingFilter()) //
            .addFilter(new ErrorLoggingFilter()) //
            .build(); // Собираем спецификацию

    @Step("Отправить POST запрос /api/auth/register")
    public static Response sendPostRequestCreateUser(User user) {
        return given()
                .spec(SPEC)
                .body(user)
                .post("/api/auth/register")
                .thenReturn();
    }

    @Step("Отправить POST запрос /api/auth/login")
    public static Response sendPostRequestLoginUser(User user) {
        return given()
                .spec(SPEC)
                .body(user)
                .post("/api/auth/login")
                .thenReturn();
    }

    @Step("Отправить PATCH запрос /api/auth/user")
    public static Response sendPostRequestUpdateUser(User user, String accessToken) {
        return given()
                .spec(SPEC)
                .headers("Authorization", accessToken)
                .body(user)
                .patch("/api/auth/user")
                .thenReturn();
    }

    @Step("Отправить DELETE запрос /api/auth/user")
    public static Response sendPostRequestDeleteUser(String accessToken) {
        return given()
                .spec(SPEC)
                .headers("Authorization", accessToken)
                .delete("/api/auth/user")
                .thenReturn();
    }

    @Step("Отправить POST запрос /api/orders")
    public static Response sendPostRequestCreateOrder(String accessToken, OrderCreateRequest orderCreateRequest) {
        var request = given()
                .spec(SPEC)
                .body(orderCreateRequest);
        if (accessToken != null && !accessToken.isEmpty()) {
            request.header("Authorization", accessToken);
        }
        return request.post("/api/orders").thenReturn();
    }

    @Step("Отправить GET запрос /api/orders")
    public static Response sendPostRequestGetOrders(String accessToken) {
        return given()
                .spec(SPEC)
                .headers("Authorization", accessToken)
                .get("/api/orders")
                .thenReturn();
    }

    // Новый метод для получения списка ингредиентов
    @Step("Отправить GET запрос /api/ingredients")
    public static Response sendGetRequestIngredients() {
        return given()
                .spec(SPEC)
                .get("/api/ingredients")
                .thenReturn();
    }
}

