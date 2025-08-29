package app;

import com.google.inject.AbstractModule;
import service.TaskSchedulerService;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        // 确保TaskSchedulerService在应用启动时被创建
        bind(TaskSchedulerService.class).asEagerSingleton();
    }
}
