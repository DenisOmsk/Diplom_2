package data;

import lombok.Data;

@Data
public class UserResponse {
    private boolean success; // Флаг успешности операции
    private User user; // Объект пользователя с данными
    private String accessToken; // Токен доступа для аутентификации
    private String refreshToken; // Токен обновления для продления сессии
}