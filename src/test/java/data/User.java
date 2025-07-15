package data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String email; // Электронная почта пользователя
    private String password; // Пароль пользователя
    private String name; // Имя пользователя

    // Конструктор копирования для создания нового объекта на основе существующего
    public User(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
    }
}