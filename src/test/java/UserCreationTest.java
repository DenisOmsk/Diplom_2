import support.ApiRequests;
import support.ApiSteps;
import io.qameta.allure.Description;
import data.Response;
import data.UserResponse;
import data.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*; // Импорт статических методов assert для удобства

public class UserCreationTest {

    private static final User USER_1 = new User("denis_user@yandex.ru", "password", "Денис"); // Данные тестового пользователя
    String accessToken1; // Токен доступа созданного пользователя, для удаления после тестов

    @Test
    @DisplayName("Регистрация пользователя") // Название теста
    @Description("Проверка создания пользователя с корректными данными:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.") // Описание теста для отчёта Allure
    public void createUser() {
        UserResponse userResponse = ApiSteps.createUser(USER_1); // Создаём пользователя через API и получаем ответ
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertEquals(USER_1.getEmail(), userResponse.getUser().getEmail(),
                        "Неверное значение поля email!"), // Проверяем email в ответе
                () -> assertEquals(USER_1.getName(), userResponse.getUser().getName(),
                        "Неверное значение поля name!"), // Проверяем имя в ответе
                () -> assertNull(userResponse.getUser().getPassword(),
                        "Заполнено поле password!"), // Проверяем, что пароль не возвращается в ответе
                () -> assertNotNull(userResponse.getAccessToken(),
                        "Не заполнено поле accessToken!"), // Проверяем наличие accessToken
                () -> assertNotNull(userResponse.getRefreshToken(),
                        "Не заполнено поле refreshToken!") // Проверяем наличие refreshToken
        );
        accessToken1 = userResponse.getAccessToken(); // Сохраняем токен для удаления пользователя после теста
    }

    @Test
    @DisplayName("Регистрация пользователя, который уже зарегистрирован") // Название теста
    @Description("Проверка неуспешного создания пользователя, который уже зарегистрирован:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.") // Описание теста для отчёта Allure
    public void createFailedExistUser() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken(); // Создаём пользователя для проверки повторной регистрации

        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateUser(USER_1); // Отправляем запрос на повторное создание пользователя
        response.then().statusCode(403); // Проверяем, что статус 403 Forbidden
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа в модель Response
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // Проверяем, что success = false
                () -> assertEquals("User already exists", resp.getMessage(),
                        "Неверное значение поля message!") // Проверяем сообщение об ошибке
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с незаполненным полем email") // Название теста
    @Description("Проверка неуспешного создания пользователя с незаполненным полем email:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.") // Описание теста для отчёта Allure
    public void createFailedUserWithoutEmail() {
        User user = new User(USER_1); // Создаём копию пользователя USER_1
        user.setEmail(null); // Обнуляем поле email
        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateUser(user); // Отправляем запрос на создание пользователя с пустым email
        response.then().statusCode(403); // Проверяем, что статус 403 Forbidden
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // success = false
                () -> assertEquals("Email, password and name are required fields", resp.getMessage(),
                        "Неверное значение поля message!") // Сообщение об ошибке обязательных полей
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с незаполненным полем name") // Название теста
    @Description("Проверка неуспешного создания пользователя с незаполненным полем name:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.") // Описание теста для отчёта Allure
    public void createFailedUserWithoutName() {
        User user = new User(USER_1); // Создаём копию пользователя USER_1
        user.setName(null); // Обнуляем поле name
        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateUser(user); // Отправляем запрос на создание пользователя с пустым name
        response.then().statusCode(403); // Проверяем, что статус 403 Forbidden
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // success = false
                () -> assertEquals("Email, password and name are required fields", resp.getMessage(),
                        "Неверное значение поля message!") // Сообщение об ошибке обязательных полей
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с незаполненным полем password") // Название теста
    @Description("Проверка неуспешного создания пользователя с незаполненным полем password:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.") // Описание теста для отчёта Allure
    public void createFailedUserWithoutPassword() {
        User user = new User(USER_1); // Создаём копию пользователя USER_1
        user.setPassword(null); // Обнуляем поле password
        io.restassured.response.Response response = ApiRequests.sendPostRequestCreateUser(user); // Отправляем запрос на создание пользователя с пустым password
        response.then().statusCode(403); // Проверяем, что статус 403 Forbidden
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // success = false
                () -> assertEquals("Email, password and name are required fields", resp.getMessage(),
                        "Неверное значение поля message!") // Сообщение об ошибке обязательных полей
        );
    }

    @AfterEach
    public void tearDown() {
        if (accessToken1 != null) ApiSteps.deleteUser(accessToken1); // Если пользователь создан, удаляем его после теста
    }
}
