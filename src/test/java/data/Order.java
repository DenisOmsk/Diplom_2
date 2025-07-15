package data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data //
@AllArgsConstructor
public class Order {
    private User owner; // Владелец заказа
    private Integer number; // Номер заказа
}