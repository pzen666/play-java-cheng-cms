package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entity.PageDTO;
import entity.result.Results;
import models.CmsConfig;
import models.ScheduledTask;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class TaskController extends Controller {

    private static final Logger.ALogger log = Logger.of(TaskController.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取所有任务列表
     */
    public Result listTasks(Http.Request request) {
        List<ScheduledTask> tasks = ScheduledTask.find.query().findList();
        return ok(Json.toJson(Results.success(tasks)));
    }

    /**
     * 获取任务详情
     */
    public Result getTaskDetail(Http.Request request) {
        JsonNode json = request.body().asJson();
        Long taskId = json.get("taskId").asLong();

        ScheduledTask task = ScheduledTask.find.byId(taskId);
        if (task == null) {
            return notFound(Json.toJson(Results.error("任务不存在")));
        }

        // 构造详细信息
        ObjectNode result = Json.newObject();
        result.put("id", task.getId());
        result.put("name", task.getName());
        result.put("resourceId", task.getResourceId());
        result.put("collectionType", task.getCollectionType());
        result.put("intervalValue", task.getIntervalValue());
        result.put("intervalUnit", task.getIntervalUnit());
        result.put("status", task.getStatus());
        result.put("enabled", task.isEnabled());

        // 格式化时间
        if (task.getLastExecutionTime() != null) {
            result.put("lastExecutionTime", DATE_FORMAT.format(new Date(task.getLastExecutionTime())));
        } else {
            result.putNull("lastExecutionTime");
        }

        if (task.getNextExecutionTime() != null) {
            result.put("nextExecutionTime", DATE_FORMAT.format(new Date(task.getNextExecutionTime())));
        } else {
            result.putNull("nextExecutionTime");
        }

        if (task.getCreatedAt() != null) {
            result.put("createdAt", DATE_FORMAT.format(new Date(task.getCreatedAt())));
        }

        if (task.getUpdatedAt() != null) {
            result.put("updatedAt", DATE_FORMAT.format(new Date(task.getUpdatedAt())));
        }

        result.put("executionResult", task.getExecutionResult());

        // 获取资源库名称
        CmsConfig cmsConfig = CmsConfig.find.byId(task.getResourceId());
        if (cmsConfig != null) {
            result.put("resourceName", cmsConfig.getName());
        }

        return ok(Json.toJson(Results.success(result)));
    }

    /**
     * 创建新任务
     */
    public Result createTask(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            ScheduledTask task = new ScheduledTask();
            task.setName(json.get("name").asText());
            int count = ScheduledTask.find.query().where().eq("name", task.getName()).findCount();
            if (count > 0) {
                return ok(Json.toJson(Results.error("任务名称已存在")));
            }
            task.setResourceId(json.get("resourceId").asLong());
            task.setCollectionType(json.get("collectionType").asText());
            task.setIntervalValue(json.get("intervalValue").asInt());
            task.setIntervalUnit(json.get("intervalUnit").asText());
            task.setStatus(json.get("status").asText());
            task.setTaskClass("service.CollectionTask"); // 设置采集任务类
            task.setEnabled(json.get("enabled").asBoolean(true));
            task.setCreatedAt(System.currentTimeMillis());

            // 根据采集类型和间隔生成cron表达式
            String cronExpression = generateCronExpression(
                    task.getCollectionType(),
                    task.getIntervalValue(),
                    task.getIntervalUnit()
            );
            task.setCronExpression(cronExpression);

            // 计算下次执行时间 - 立即执行或稍后执行
            long nextExecutionTime = calculateNextExecutionTime(cronExpression);
            // 如果是新建任务，可以设置为立即执行或稍后执行（例如1分钟后）
            task.setNextExecutionTime(Math.max(nextExecutionTime, System.currentTimeMillis() + 60 * 1000));

            // 初始化执行结果
            task.setExecutionResult("任务已创建，等待执行");

            task.save();

            log.info("Created new task with ID: " + task.getId() +
                    ", next execution time: " + task.getNextExecutionTime());

            return ok(Json.toJson(Results.success("任务创建成功")));
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(Json.toJson(Results.error("任务创建失败: " + e.getMessage())));
        }
    }

    /**
     * 更新任务
     */
    public Result updateTask(Http.Request request) {
        JsonNode json = request.body().asJson();
        Long taskId = json.get("taskId").asLong();
        try {
            ScheduledTask task = ScheduledTask.find.byId(taskId);
            if (task == null) {
                return notFound(Json.toJson(Results.error("任务不存在")));
            }
            task.setName(json.get("name").asText());
            task.setResourceId(json.get("resourceId").asLong());
            task.setCollectionType(json.get("collectionType").asText());
            task.setIntervalValue(json.get("intervalValue").asInt());
            task.setIntervalUnit(json.get("intervalUnit").asText());
            task.setStatus(json.get("status").asText());
            task.setTaskClass("service.CollectionTask"); // 确保任务类正确
            task.setEnabled(json.get("enabled").asBoolean(task.isEnabled()));
            task.setUpdatedAt(System.currentTimeMillis());

            // 根据采集类型和间隔生成cron表达式
            String cronExpression = generateCronExpression(
                    task.getCollectionType(),
                    task.getIntervalValue(),
                    task.getIntervalUnit()
            );
            task.setCronExpression(cronExpression);

            // 计算下次执行时间
            task.setNextExecutionTime(calculateNextExecutionTime(cronExpression));

            // 更新执行结果状态
            task.setExecutionResult("任务已更新，等待执行");

            task.update();

            return ok(Json.toJson(Results.success("任务更新成功")));
        } catch (Exception e) {
            return badRequest(Json.toJson(Results.error("任务更新失败: " + e.getMessage())));
        }
    }

    /**
     * 启用/禁用任务
     */
    public Result toggleTask(Http.Request request) {
        JsonNode json = request.body().asJson();
        Long taskId = json.get("taskId").asLong();
        ScheduledTask task = ScheduledTask.find.byId(taskId);
        if (task != null) {
            task.setEnabled(!task.isEnabled());
            task.setUpdatedAt(System.currentTimeMillis());
            task.setExecutionResult("任务状态已更新为: " + (task.isEnabled() ? "启用" : "禁用"));
            task.update();
            return ok(Json.toJson(Results.success("任务状态已更新")));
        } else {
            return notFound(Json.toJson(Results.error("任务不存在")));
        }
    }

    /**
     * 删除任务
     */
    public Result deleteTask(Http.Request request) {
        JsonNode json = request.body().asJson();
        Long taskId = json.get("taskId").asLong();
        ScheduledTask task = ScheduledTask.find.byId(taskId);
        if (task != null) {
            task.delete();
            return ok(Json.toJson(Results.success("任务已删除")));
        } else {
            return notFound(Json.toJson(Results.error("任务不存在")));
        }
    }

    /**
     * 获取资源库列表（用于前端下拉框）
     */
    public CompletionStage<Result> getResourceList(Http.Request request) {
        try {
            PageDTO pageDTO = Json.fromJson(request.body().asJson(), PageDTO.class);
            // 这里应该调用CmsController.cmsPageConfig接口获取资源库列表
            // 为了简化，我们直接查询所有CmsConfig
            List<CmsConfig> cmsConfigs = CmsConfig.find.query().findList();

            // 构造返回数据
            ObjectNode result = Json.newObject();
            ArrayNode arrayNode = Json.newArray();

            for (CmsConfig config : cmsConfigs) {
                ObjectNode node = Json.newObject();
                node.put("id", config.getId());
                node.put("name", config.getName());
                arrayNode.add(node);
            }

            result.set("data", arrayNode);
            return java.util.concurrent.CompletableFuture.completedFuture(ok(Json.toJson(Results.success(result))));
        } catch (Exception e) {
            return java.util.concurrent.CompletableFuture.completedFuture(
                    badRequest(Json.toJson(Results.error("获取资源库列表失败: " + e.getMessage()))));
        }
    }

    /**
     * 根据采集类型和间隔生成cron表达式
     */
    private String generateCronExpression(String collectionType, Integer intervalValue, String intervalUnit) {
        // 简化实现，实际应该根据具体的业务需求完善
        switch (intervalUnit) {
            case "分钟":
                return "0 */" + intervalValue + " * * * ?";
            case "小时":
                return "0 0 */" + intervalValue + " * * ?";
            case "天":
                return "0 0 0 */" + intervalValue + " * ?";
            default:
                return "0 0 0 * * ?"; // 默认每天执行
        }
    }

    /**
     * 根据cron表达式计算下次执行时间（简化实现）
     */
    private Long calculateNextExecutionTime(String cronExpression) {
        // 简化实现，实际应该解析cron表达式并计算下次执行时间
        // 这里只是简单地设置为1分钟后执行
        return System.currentTimeMillis() + 60 * 1000;
    }
}
