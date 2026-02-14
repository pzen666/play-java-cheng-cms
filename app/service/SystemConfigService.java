package service;

import com.google.inject.Inject;
import models.SystemConfig;

public class SystemConfigService {

    private final RedisService redisService;

    @Inject
    public SystemConfigService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 获取系统配置信息
     *
     * @param key 配置项的键
     * @return 配置项的值
     */
    public String getConfig(String key) {
        // 1. 尝试从 Redis 获取配置
        String cachedValue = redisService.getValue(key);
        if (cachedValue != null) {
            return cachedValue; // Redis 中存在，直接返回
        }
        // 2. Redis 中不存在，从数据库查询
        SystemConfig s = SystemConfig.find.query().where().eq("key", key).findOne();
        if (s == null) {
            throw new RuntimeException("配置项 [" + key + "] 未找到");
        }
        String values = s.getValue().toString();
        // 3. 查询到数据后写入 Redis
        redisService.setValue(key, values);
        return values;
    }


}
