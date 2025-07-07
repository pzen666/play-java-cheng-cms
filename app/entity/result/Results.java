package entity.result;

public class Results<T> {
    public int code;
    public String msg;
    public T data;

    public static <T> Results<T> success(T data) {
        Results<T> res = new Results<>();
        res.code = 200;
        res.msg = "Success";
        res.data = data;
        return res;
    }

    public static Object error(String msg) {
        Results<Object> res = new Results<>();
        res.code = 200;
        res.data = null;
        res.msg = msg;
        return res;
    }
}
