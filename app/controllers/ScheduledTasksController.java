package controllers;

import entity.PageDTO;
import entity.result.Results;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import models.ScheduledTask;
import models.VideoDict;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ScheduledTasksRepository;

/**
 * 定时任务管理
 */
public class ScheduledTasksController extends Controller {

    //新增定时任务
    //删除定时任务
    //编辑定时任务
    //查询定时任务列表


    private static final Logger.ALogger logger = Logger.of(ScheduledTasksController.class);

    private final ScheduledTasksRepository scheduledTasksRepository;

    @Inject
    public ScheduledTasksController(ScheduledTasksRepository scheduledTasksRepository) {
        this.scheduledTasksRepository = scheduledTasksRepository;
    }


    // 新增采集源
    public Result addScheduledTasks(Http.Request request) {
        logger.info("新增采集源");
        ScheduledTask t = Json.fromJson(request.body().asJson(), ScheduledTask.class);
        ScheduledTask ds = scheduledTasksRepository.addScheduledTasks(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    //删除采集源
    public Result delScheduledTasks(Http.Request request) {
        ScheduledTask t = Json.fromJson(request.body().asJson(), ScheduledTask.class);
        ScheduledTask ds = scheduledTasksRepository.deleteScheduledTasks(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    //编辑采集源
    public Result upScheduledTasks(Http.Request request) {
        ScheduledTask t = Json.fromJson(request.body().asJson(), ScheduledTask.class);
        ScheduledTask ds = scheduledTasksRepository.updateScheduledTasks(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    //分页查询
    public Result pagedList(Http.Request request) {
        PageDTO t = Json.fromJson(request.body().asJson(), PageDTO.class);
        PagedList<ScheduledTask> pagedList = scheduledTasksRepository.pagedList(t);
        return ok(Json.toJson(Results.success(pagedList)));
    }

    //查询采集源
    public Result findScheduledTasks(Http.Request request) {
        ScheduledTask t = Json.fromJson(request.body().asJson(), ScheduledTask.class);
        ScheduledTask ds = scheduledTasksRepository.addScheduledTasks(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    public Result findScheduledTasksById(Http.Request request) {
        Long id = request.body().asJson().get("id").asLong();
        ScheduledTask ds = scheduledTasksRepository.findScheduledTasksById(id);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("选中数据获取失败")));
        }
        return ok(Json.toJson(Results.success(ds)));
    }

    //绑定菜单
    public Result addOrUpdateDict(Http.Request request) {
        VideoDict t = Json.fromJson(request.body().asJson(), VideoDict.class);
        return ok(Json.toJson(Results.success(t)));
    }


}
