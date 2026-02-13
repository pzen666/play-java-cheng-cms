package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import service.SystemConfigService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ClickHouse数据操作工具类
 */
public class ClickHouseUtils {

    private final SystemConfigService systemConfigService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)) // 设置连接超时
            .build();

    @Inject
    public ClickHouseUtils(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }


    /**
     * 通过 HTTP 方式请求 ClickHouse，执行 SQL 并返回结果
     *
     * @param sql 要执行的 SQL 语句
     * @return 查询结果，以 List<Map<String, Object>> 形式返回
     */
    public List<Map<String, Object>> executeClickHouseQueryViaHttp(String sql) {
        try {
            HttpRequest request = buildRequest(sql);
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {
                });
            } else {
                throw new RuntimeException("ClickHouse HTTP 请求失败，状态码：" + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("执行 ClickHouse 查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 通过 HTTP 方式异步请求 ClickHouse，执行 SQL 并返回结果
     *
     * @param sql 要执行的 SQL 语句
     * @return 查询结果的 CompletableFuture，包含 List<Map<String, Object>>
     */
    public CompletableFuture<List<Map<String, Object>>> executeClickHouseQueryViaHttpAsync(String sql) {
        try {
            HttpRequest request = buildRequest(sql);
            return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                return objectMapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {
                                });
                            } catch (Exception e) {
                                throw new RuntimeException("解析 ClickHouse 响应失败: " + e.getMessage(), e);
                            }
                        } else {
                            throw new RuntimeException("ClickHouse HTTP 请求失败，状态码：" + response.statusCode());
                        }
                    })
                    .exceptionally(throwable -> {
                        throw new RuntimeException("执行 ClickHouse 查询失败: " + throwable.getMessage(), throwable);
                    });
        } catch (Exception e) {
            throw new RuntimeException("初始化 ClickHouse 请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建 ClickHouse HTTP 请求的基础信息
     *
     * @param sql 要执行的 SQL 语句
     * @return HttpRequest 对象
     */
    private HttpRequest buildRequest(String sql) throws Exception {
        // 1. 从配置中获取 ClickHouse HTTP 接口信息
        String clickhouseConfig = systemConfigService.getConfig("clickhouse");
        Map<String, Object> configMap = objectMapper.readValue(clickhouseConfig, Map.class);

        String host = (String) configMap.get("host");
        int port = (int) configMap.get("port");
        String username = (String) configMap.get("username");
        String password = (String) configMap.get("password");

        // 2. 构建 HTTP 请求 URL
        String url = String.format("http://%s:%d/", host, port);
        String auth = username + ":" + password;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());

        // 3. 构建请求对象
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "text/plain")
                .timeout(Duration.ofSeconds(10)) // 设置请求超时
                .POST(HttpRequest.BodyPublishers.ofString(sql))
                .build();
    }

}
