package data;

import java.util.List;

public class IngredientsResponse {
    private boolean success;
    private List<Ingredient> data;

    // геттеры и сеттеры
    public boolean isSuccess() {
        return success;
    }
    public List<Ingredient> getData() {
        return data;
    }
    public void setData(List<Ingredient> data) {
        this.data = data;
    }
}
