package data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderCreateRequest {
    private List<String> ingredients; // Список идентификаторов ингредиентов для создания заказа
}