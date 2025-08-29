package service;

import models.ScheduledTask;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Cancellable;
import org.apache.pekko.actor.Scheduler;
import play.Logger;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * 轻量级基于数据库的任务调度服务
 */
@Singleton
public class TaskSchedulerService {

    private static final Logger.ALogger log = Logger.of(TaskSchedulerService.class);

    private final ActorSystem actorSystem;
    private final Scheduler scheduler;
    private final TaskExecutionContext taskExecutionContext;
    private final Map<Long, Cancellable> scheduledTasks = new ConcurrentHashMap<>();

    @Inject
    public TaskSchedulerService(ActorSystem actorSystem, TaskExecutionContext taskExecutionContext) {
        // 获取Pekko ActorSystem
        this.actorSystem = actorSystem;
        this.scheduler = actorSystem.scheduler();
        this.taskExecutionContext = taskExecutionContext;

        // 启动调度器
        startScheduler();

        log.info("TaskSchedulerService initialized at: " + System.currentTimeMillis());
    }

    /**
     * 启动调度器
     */
    private void startScheduler() {
        // 每30秒检查一次需要执行的任务（比原来更频繁）
        FiniteDuration interval = Duration.create(30, TimeUnit.SECONDS);

        Cancellable cancellable = scheduler.scheduleWithFixedDelay(
                Duration.create(10, TimeUnit.SECONDS), // 10秒后开始首次检查
                interval,  // 间隔时间
                () -> checkAndExecuteTasks(),
                actorSystem.dispatcher()  // 使用actorSystem的dispatcher
        );

        log.info("Task scheduler started with 30 seconds interval at: " + System.currentTimeMillis());
    }

    /**
     * 检查并执行到期的任务
     */
    private void checkAndExecuteTasks() {
        try {
            log.info("Checking for tasks to execute at: " + System.currentTimeMillis());

            // 获取所有启用的任务
            List<ScheduledTask> tasks = ScheduledTask.find.query()
                    .where()
                    .eq("enabled", true)
                    .findList();

            log.info("Found " + tasks.size() + " enabled tasks");

            long now = System.currentTimeMillis();

            for (ScheduledTask task : tasks) {
                log.info("Checking task ID: " + task.getId() + ", Name: " + task.getName() +
                        ", NextExecutionTime: " + task.getNextExecutionTime() + ", Now: " + now);

                // 检查任务是否到期需要执行
                if (task.getNextExecutionTime() != null && task.getNextExecutionTime() <= now) {
                    log.info("Task " + task.getId() + " is due for execution");
                    executeTaskAsync(task);
                } else {
                    log.info("Task " + task.getId() + " is not due yet");
                }
            }
        } catch (Exception e) {
            log.error("Error in checkAndExecuteTasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 异步执行单个任务
     */
    private void executeTaskAsync(ScheduledTask task) {
        try {
            log.info("Scheduling task " + task.getId() + " for execution");

            // 使用自定义的ExecutionContext异步执行任务
            runAsync(() -> {
                try {
                    log.info("Executing task " + task.getId());
                    // 创建采集任务实例并执行
                    CollectionTask collectionTask = new CollectionTask(task.getId());
                    collectionTask.run();
                    log.info("Task " + task.getId() + " execution completed");
                } catch (Exception e) {
                    log.error("Error executing task " + task.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }, taskExecutionContext);
        } catch (Exception e) {
            log.error("Error scheduling task " + task.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 重新加载任务调度（当任务配置发生变化时调用）
     */
    public void reloadTaskSchedule() {
        // 取消所有已调度的任务
        for (Cancellable cancellable : scheduledTasks.values()) {
            cancellable.cancel();
        }
        scheduledTasks.clear();

        // 重新加载任务
        checkAndExecuteTasks();
    }
}
