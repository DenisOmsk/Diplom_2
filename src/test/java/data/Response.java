package data;

import lombok.Data;

@Data
public class Response {
    private boolean success; // Флаг успешности операции
    private String message; // Сообщение с описанием результата операции
}