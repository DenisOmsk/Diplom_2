package data;

import lombok.Data;

import java.util.List;

@Data
public class OrderListGetResponse {
    private boolean success; // Флаг успешности получения списка заказов
    private List<Order> orders; // Список заказов
    private Integer total; // Общее количество заказов
    private Integer totalToday; // Количество заказов, сделанных сегодня
}