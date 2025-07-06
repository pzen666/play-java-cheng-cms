package dto;

public class Results<T> {
    public int code;
    public String message;
    public T data;

    public static <T> Results<T> success(T data) {
        Results<T> res = new Results<>();
        res.code = 200;
        res.message = "Success";
        res.data = data;
        return res;
    }
}
