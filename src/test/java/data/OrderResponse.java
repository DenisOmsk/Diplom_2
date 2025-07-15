package data;

import lombok.Data;

@Data
public class OrderResponse {
    private String name; // Имя заказа или связанного объекта
    private Order order; // Объект заказа с деталями
    private boolean success; // Флаг успешности операции
}