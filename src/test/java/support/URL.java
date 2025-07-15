package support;

public class URL {
    private final static String HOST_TEST = "https://stellarburgers.nomoreparties.site"; // Базовый URL тестового окружения

    public static String getHost() {
        // Возвращает значение системного свойства "host", если оно задано,
        // иначе возвращает значение по умолчанию HOST_TEST
        if (System.getProperty("host") != null) {
            return System.getProperty("host"); // Используем URL из системного свойства
        } else {
            return HOST_TEST; // Используем дефолтный тестовый URL
        }
    }
}