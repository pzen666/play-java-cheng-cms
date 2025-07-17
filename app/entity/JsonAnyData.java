package entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态映射  @JsonAnyGetter   @JsonAnySetter
 */
public class JsonAnyData {

    private final Map<String, Object> otherFields = new HashMap<>();

    // 使用 @JsonAnyGetter 标记这个方法用来输出额外字段
    @JsonAnyGetter
    public Map<String, Object> getOtherFields() {
        return otherFields;
    }

    // 使用 @JsonAnySetter 标记这个方法用来接收额外字段
    @JsonAnySetter
    public void addOtherField(String key, Object value) {
        otherFields.put(key, value);
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // 示例 JSON 包含未知字段
        String json = "{ \"name\": \"Alice\", \"age\": 25, \"email\": \"alice@example.com\", \"city\": \"Beijing\" }";
        // 反序列化：JSON -> Java 对象
        JsonAnyData bean = mapper.readValue(json, JsonAnyData.class);
        System.out.println("Other fields:");
        for (Map.Entry<String, Object> entry : bean.getOtherFields().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        // 再序列化回去：Java 对象 -> JSON
        String outputJson = mapper.writeValueAsString(bean);
        System.out.println("Serialized JSON: " + outputJson);
    }

}
