package service;

import entity.vo.VodVideoVO;
import models.CmsConfig;
import models.ScheduledTask;
import play.Logger;
import utils.VodVideoParser;

public class CollectionTask implements Runnable {

    private static final Logger.ALogger log = Logger.of(CollectionTask.class);

    private Long taskId;

    public CollectionTask() {
        // 默认构造函数
    }

    public CollectionTask(Long taskId) {
        this.taskId = taskId;
        log.info("Creating CollectionTask for taskId: " + taskId);
    }

    @Override
    public void run() {
        log.info("Starting CollectionTask.run() for taskId: " + taskId);

        ScheduledTask task = null;
        try {
            // 获取任务信息
            task = ScheduledTask.find.byId(taskId);
            if (task == null) {
                String errorMsg = "任务不存在，ID: " + taskId;
                log.error(errorMsg);
                return;
            }

            log.info("Found task: " + task.getName() + " with resourceId: " + task.getResourceId());

            // 获取关联的资源库配置
            CmsConfig cmsConfig = CmsConfig.find.byId(task.getResourceId());
            if (cmsConfig == null) {
                String errorMsg = "资源库配置不存在，ID: " + task.getResourceId();
                log.error(errorMsg);
                task.setExecutionResult("执行失败: " + errorMsg);
                task.update();
                return;
            }

            // 获取采集地址
            String url = cmsConfig.getUrl();
            if (url == null || url.isEmpty()) {
                String errorMsg = "资源库采集地址为空，资源库ID: " + cmsConfig.getId();
                log.error(errorMsg);
                task.setExecutionResult("执行失败: " + errorMsg);
                task.update();
                return;
            }

            log.info("Starting data collection from URL: " + url);

            // 根据接口类型执行采集
            VodVideoVO videoVO = null;
            if ("json".equalsIgnoreCase(cmsConfig.getType())) {
                log.info("Parsing JSON data from: " + url);
                videoVO = VodVideoParser.json(url);
            } else if ("xml".equalsIgnoreCase(cmsConfig.getType())) {
                log.info("Parsing XML data from: " + url);
                videoVO = VodVideoParser.xml(url);
            } else {
                String errorMsg = "不支持的接口类型: " + cmsConfig.getType();
                log.error(errorMsg);
                task.setExecutionResult("执行失败: " + errorMsg);
                task.update();
                return;
            }

            if (videoVO != null) {
                // TODO: 在这里实现数据入库逻辑
                // 您可以根据videoVO对象中的数据将其保存到数据库中

                String successMsg = "采集任务执行成功，任务ID: " + taskId +
                        "，采集到 " + (videoVO.getList() != null ? videoVO.getList().size() : 0) + " 条视频数据";
                log.info(successMsg);

                // 更新任务执行时间
                task.setLastExecutionTime(System.currentTimeMillis());
                // 这里应该根据cron表达式重新计算下次执行时间
                task.setNextExecutionTime(System.currentTimeMillis() +
                        calculateNextExecutionTime(task.getIntervalValue(), task.getIntervalUnit()));
                task.setExecutionResult("执行成功: " + successMsg);
                task.update();

                log.info("Task " + taskId + " completed and updated in database");
            } else {
                String errorMsg = "采集任务执行失败，任务ID: " + taskId;
                log.error(errorMsg);
                task.setExecutionResult("执行失败: " + errorMsg);
                task.update();
            }
        } catch (Exception e) {
            String errorMsg = "采集任务执行异常，任务ID: " + taskId + "，错误信息: " + e.getMessage();
            log.error(errorMsg);
            e.printStackTrace();

            // 记录异常信息到数据库
            if (task != null) {
                task.setExecutionResult("执行异常: " + errorMsg);
                task.update();
            }
        }

        log.info("Finished CollectionTask.run() for taskId: " + taskId);
    }

    /**
     * 根据间隔值和单位计算下次执行时间（毫秒）
     */
    private long calculateNextExecutionTime(Integer intervalValue, String intervalUnit) {
        if (intervalValue == null) {
            // 默认1小时
            log.info("Using default interval: 1 hour");
            return 60 * 60 * 1000;
        }

        long intervalMillis = 0;
        switch (intervalUnit) {
            case "分钟":
                intervalMillis = intervalValue * 60 * 1000;
                break;
            case "小时":
                intervalMillis = intervalValue * 60 * 60 * 1000;
                break;
            case "天":
                intervalMillis = intervalValue * 24 * 60 * 60 * 1000;
                break;
            default:
                intervalMillis = intervalValue * 60 * 60 * 1000; // 默认小时
                break;
        }

        log.info("Calculated next execution time: now + " + intervalMillis + "ms");
        return intervalMillis;
    }
}
