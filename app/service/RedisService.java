package service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisService {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> syncCommands;

    public RedisService() {
        // 从 application.conf 读取 Redis 配置
        Config config = ConfigFactory.load();
        String host = config.getString("redis.host");
        int port = config.getInt("redis.port");
        String password = config.getString("redis.password");
        int database = config.getInt("redis.database");

        // 构建 Redis URI
        String redisUri = String.format("redis://%s:%d/%d", host, port, database);
        if (!password.isEmpty()) {
            redisUri = String.format("redis://:%s@%s:%d/%d", password, host, port, database);
        }

        this.redisClient = RedisClient.create(redisUri);
        this.connection = redisClient.connect();
        this.syncCommands = connection.sync();
    }

    public void setValue(String key, String value) {
        syncCommands.set(key, value);
    }

    public String getValue(String key) {
        return syncCommands.get(key);
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }

}
   