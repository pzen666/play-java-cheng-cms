package controllers;

import entity.result.Results;
import io.ebean.DB;
import io.ebean.Database;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo
 */
public class DemoController extends Controller {

    //数据源切换查询
    public Result findByDatabase() {
        // 获取主库和从库的现有实例
        Database master = DB.getDefault(); // 主库
        Database slave = DB.byName("slave"); // 从库

        // 动态创建主库实例并设置 schema
//        Database masterDb = Database.builder()
//                .name("default") // 设置唯一名称
//                .dataSource(master.dataSource())
//                .setDbSchema("public").loadFromProperties()
//                .build();
        // 查询主库数据
//        List<User> defaultUsers = masterDb.find(User.class).findList();
        // 动态创建从库实例并设置 schema
//        Database slaveDb = Database.builder()
//                .name("slave") // 设置唯一名称
//                .dataSource(slave.dataSource())
//                .setDbSchema("public1").loadFromProperties()
//                .build();



        // 查询从库数据
//        List<User> slaveUsers = slaveDb.find(User.class).findList();
        List<User> defaultUsers = master.find(User.class).findList();
        List<User> slaveUsers = slave.find(User.class).findList();
        // 构造返回结果
        Map<String, Object> m = new HashMap<>();
        m.put("default", defaultUsers);
        m.put("slave", slaveUsers);

        return ok(Json.toJson(Results.success(m)));
    }

    //分页查询
    public Result findByPage() {
        return ok("Hello World");
    }


}
