package controllers;

import entity.result.Results;
import models.SystemConfig;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class SystemConfigController extends Controller {

    private static final Logger.ALogger logger1 = Logger.of(SystemConfigController.class);

    /**
     * 创建系统配置信息
     * @param request
     * @return
     */
    public Result saveOrUpdateSystemConfig(Http.Request request) {
        logger1.info("创建系统配置信息");
        SystemConfig sc = Json.fromJson(request.body().asJson(), SystemConfig.class);
        SystemConfig systemConfig = SystemConfig.find.query().where().eq("key", sc.getKey()).findOne();
        if (systemConfig != null) {
            systemConfig = sc;
            systemConfig.update();
            return ok(Json.toJson(Results.success(systemConfig)));
        } else {
            sc.save();
        }
        return ok(Json.toJson(Results.success(sc)));
    }


}
