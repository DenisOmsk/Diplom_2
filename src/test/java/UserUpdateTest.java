import support.ApiRequests;
import support.ApiSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import data.Response;
import data.UserResponse;
import data.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*; // Статические методы assert для удобства

public class UserUpdateTest {

    private static final User USER_1 = new User(
            "denis_user@yandex.ru", "password", "Денис"); // Тестовый пользователь 1
    private static final User USER_2 = new User(
            "ignat_nouser@yandex.ru", "password", "Игнат"); // Тестовый пользователь 2
    String accessToken1; // Токен доступа пользователя 1
    String accessToken2; // Токен доступа пользователя 2

    @BeforeEach
    public void initEach() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken(); // Создаём USER_1 и сохраняем токен
        accessToken2 = ApiSteps.createUser(USER_2).getAccessToken(); // Создаём USER_2 и сохраняем токен
        ApiSteps.deleteUser(accessToken2); // Удаляем USER_2, чтобы проверить обновление с email другого пользователя
    }

    @Test
    @DisplayName("Обновление email авторизованного пользователя")
    @Description("Обновление email авторизованного пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Пользователь обновлен.")
    public void updateUserEmail() {
        User user = new User(USER_1); // Создаём копию USER_1
        user.setEmail(USER_2.getEmail()); // Меняем email на email USER_2
        sendAndCheckCorrectRequestForUpdate(user); // Отправляем запрос на обновление и проверяем ответ
        accessToken1 = ApiSteps.loginUser(user).getAccessToken(); // Авторизуемся с обновлённым email и сохраняем новый токен
    }

    @Test
    @DisplayName("Обновление пароля авторизованного пользователя")
    @Description("Обновление пароля авторизованного пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Пользователь обновлен.")
    public void updateUserPassword() {
        User user = new User(USER_1); // Создаём копию USER_1
        user.setPassword(USER_2.getPassword()); // Меняем пароль на пароль USER_2
        sendAndCheckCorrectRequestForUpdate(user); // Отправляем запрос на обновление и проверяем ответ
        accessToken1 = ApiSteps.loginUser(user).getAccessToken(); // Авторизуемся с обновлённым паролем и сохраняем токен
    }

    @Test
    @DisplayName("Обновление имя авторизованного пользователя")
    @Description("Обновление имя авторизованного пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Пользователь обновлен.")
    public void updateUserName() {
        User user = new User(USER_1); // Создаём копию USER_1
        user.setName(USER_2.getName()); // Меняем имя на имя USER_2
        sendAndCheckCorrectRequestForUpdate(user); // Отправляем запрос на обновление и проверяем ответ
    }

    @Test
    @DisplayName("Обновление email неавторизованного пользователя")
    @Description("Проверка неуспешного обновления email неавторизованного пользователя:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void updateFailedUserEmailWithoutAuth() {
        User user = new User(USER_1); // Создаём копию USER_1
        user.setEmail(USER_2.getEmail()); // Меняем email на email USER_2
        sendAndCheckIncorrectRequestForUpdate(user); // Отправляем запрос без токена и проверяем ошибку
    }

    @Test
    @DisplayName("Обновление пароля неавторизованного пользователя")
    @Description("Проверка неуспешного обновления пароля неавторизованного пользователя:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void updateFailedUserPasswordWithoutAuth() {
        User user = new User(USER_1); // Создаём копию USER_1
        user.setPassword(USER_2.getPassword()); // Меняем пароль на пароль USER_2
        sendAndCheckIncorrectRequestForUpdate(user); // Отправляем запрос без токена и проверяем ошибку
    }

    @Test
    @DisplayName("Обновление имя неавторизованного пользователя")
    @Description("Проверка неуспешного обновления имя неавторизованного пользователя:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void updateFailedUserNameWithoutAuth() {
        User user = new User(USER_1); // Создаём копию USER_1
        user.setName(USER_2.getName()); // Меняем имя на имя USER_2
        sendAndCheckIncorrectRequestForUpdate(user); // Отправляем запрос без токена и проверяем ошибку
    }

    @Step("Отправить и проверить корректный запрос на обновление пользователя c авторизацией")
    private void sendAndCheckCorrectRequestForUpdate(User user) {
        UserResponse userResponse = ApiSteps.updateUser(user, accessToken1); // Отправляем запрос на обновление с токеном
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertEquals(user.getEmail(), userResponse.getUser().getEmail(),
                        "Неверное значение поля email!"), // Проверяем email в ответе
                () -> assertEquals(user.getName(), userResponse.getUser().getName(),
                        "Неверное значение поля name!"), // Проверяем имя в ответе
                () -> assertNull(userResponse.getUser().getPassword(),
                        "Заполнено поле password!"), // Проверяем, что пароль не возвращается
                () -> assertNull(userResponse.getAccessToken(),
                        "Заполнено поле accessToken!"), // Проверяем, что токены не возвращаются при обновлении
                () -> assertNull(userResponse.getRefreshToken(),
                        "Заполнено поле refreshToken!") // Проверяем, что токены не возвращаются при обновлении
        );
    }

    @Step("Отправить и проверить некорректный запрос на обновление пользователя без авторизации")
    private static void sendAndCheckIncorrectRequestForUpdate(User user) {
        io.restassured.response.Response response = ApiRequests.sendPostRequestUpdateUser(user, ""); // Отправляем запрос без токена
        response.then().statusCode(401); // Проверяем статус 401 Unauthorized
        Response resp = response.body().as(Response.class); // Десериализуем тело ответа
        assertAll("Проверка полей ответа", // Группируем проверки
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"), // success = false
                () -> assertEquals("You should be authorised", resp.getMessage(),
                        "Неверное значение поля message!") // Проверяем сообщение об ошибке авторизации
        );
    }

    @AfterEach
    public void tearDown() {
        ApiSteps.deleteUser(accessToken1); // Удаляем пользователя после каждого теста
    }
}
