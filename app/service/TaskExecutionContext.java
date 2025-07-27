package service;

import org.apache.pekko.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

public class TaskExecutionContext extends CustomExecutionContext {

    @Inject
    public TaskExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "task.dispatcher");
    }
}
