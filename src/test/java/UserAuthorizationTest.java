import support.ApiRequests;
import support.ApiSteps;
import io.qameta.allure.Description;
import data.Response;
import data.UserResponse;
import data.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*; // Импорт статических методов assert для удобства

public class UserAuthorizationTest {

    private static final User USER_1 = new User(
            "denis_user@yandex.ru", "password", "Денис"); // Данные первого тестового пользователя
    private static final User USER_2 = new User(
            "Ignat_nouser@yandex.ru", "password", "Игнат"); // Данные второго тестового пользователя (для негативных тестов)
    String accessToken1; // Токен доступа первого пользователя
    String accessToken2; // Токен доступа второго пользователя
    UserResponse userResponse1; // Ответ при создании первого пользователя с данными и токенами

    @BeforeEach
    public void initEach() {
        userResponse1 = ApiSteps.createUser(USER_1); // Создаём первого пользователя и сохраняем ответ
        accessToken1 = userResponse1.getAccessToken(); // Сохраняем токен доступа первого пользователя
        accessToken2 = ApiSteps.createUser(USER_2).getAccessToken(); // Создаём второго пользователя и сохраняем токен
        ApiSteps.deleteUser(accessToken2); // Удаляем второго пользователя сразу (чтобы проверить авторизацию с несуществующим пользователем)
    }

    @Test
    @DisplayName("Авторизация пользователя") // Название теста
    @Description("Проверка авторизации существующего пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.") // Описание теста для отчёта Allure
    public void loginUser() {
        User user = new User(USER_1); // Создаём копию пользователя USER_1
        user.setName(null); // Убираем имя, т.к. для авторизации достаточно email и пароля
        UserResponse userResponse2 = ApiSteps.loginUser(user); // Выполняем авторизацию пользователя через API
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertEquals(userResponse1.getUser().getEmail(), userResponse2.getUser().getEmail(),
                        "Неверное значение поля email!"), // Проверяем email в ответе совпадает с ожидаемым
                () -> assertEquals(userResponse1.getUser().getName(), userResponse2.getUser().getName(),
                        "Неверное значение поля name!"), // Проверяем имя совпадает с ожидаемым
                () -> assertNull(userResponse2.getUser().getPassword(),
                        "Заполнено поле password!"), // Проверяем, что пароль не возвращается в ответе
                () -> assertNotNull(userResponse2.getAccessToken(),
                        "Не заполнено поле accessToken!"), // Проверяем, что в ответе есть accessToken
                () -> assertNotNull(userResponse2.getRefreshToken(),
                        "Не заполнено поле refreshToken!") // Проверяем, что в ответе есть refreshToken
        );
    }

    @Test
    @DisplayName("Авторизация пользователя c неверным логином") // Название теста
    @Description("Проверка неуспешной авторизации пользователя c неверным логином:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.") // Описание теста для отчёта Allure
    public void loginFailedUserWithWrongLogin() {
        User user = new User(USER_2.getEmail(), USER_1.getPassword(), null); // Создаём пользователя с неверным email (USER_2) и правильным паролем (USER_1)
        io.restassured.response.Response response = ApiRequests.sendPostRequestLoginUser(user); // Отправляем запрос авторизации
        response.then().statusCode(401); // Проверяем, что статус 401 Unauthorized
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа в модель Response
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // Проверяем, что success = false
                () -> assertEquals("email or password are incorrect", resp.getMessage(),
                        "Неверное значение поля message!") // Проверяем сообщение об ошибке
        );
    }

    @Test
    @DisplayName("Авторизация пользователя c неверным паролем") // Название теста
    @Description("Проверка неуспешной авторизации пользователя c неверным паролем:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.") // Описание теста для отчёта Allure
    public void loginFailedUserWithWrongPassword() {
        User user = new User(USER_1.getEmail(), USER_2.getPassword(), null); // Создаём пользователя с правильным email (USER_1) и неверным паролем (USER_2)
        io.restassured.response.Response response = ApiRequests.sendPostRequestLoginUser(user); // Отправляем запрос авторизации
        response.then().statusCode(401); // Проверяем, что статус 401 Unauthorized
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа в модель Response
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // Проверяем, что success = false
                () -> assertEquals("email or password are incorrect", resp.getMessage(),
                        "Неверное значение поля message!") // Проверяем сообщение об ошибке
        );
    }

    @AfterEach
    public void tearDown() {
        ApiSteps.deleteUser(accessToken1); // Удаляем первого пользователя после каждого теста
    }
}

