package models;

import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scheduled_tasks")
public class ScheduledTask extends Model {

    public static final io.ebean.Finder<Long, ScheduledTask> find = new io.ebean.Finder<>(ScheduledTask.class);

    @Id
    private Long id;

    // 任务名称
    private String name;

    // 资源库ID（关联CmsConfig的ID）
    private Long resourceId;

    // 采集类型 (天,周,全部)
    private String collectionType;

    // 采集时间间隔数值
    private Integer intervalValue;

    // 采集时间间隔单位 (分钟,小时,天)
    private String intervalUnit;

    // 任务状态
    private String status;

    // Cron表达式
    private String cronExpression;

    // 任务类名（需要执行的任务类的全限定名）
    private String taskClass;

    // 是否启用
    private boolean enabled;

    // 上次执行时间（时间戳）
    private Long lastExecutionTime;

    // 下次执行时间（时间戳）
    private Long nextExecutionTime;

    // 创建时间
    private Long createdAt;

    // 更新时间
    private Long updatedAt;
    // 任务执行结果信息
    private String executionResult;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public Integer getIntervalValue() {
        return intervalValue;
    }

    public void setIntervalValue(Integer intervalValue) {
        this.intervalValue = intervalValue;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(Long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public Long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(Long nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(String executionResult) {
        this.executionResult = executionResult;
    }
}
