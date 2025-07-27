package repository;

import org.apache.pekko.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import jakarta.inject.Inject;

public class DatabaseExecutionContext  extends CustomExecutionContext {

    @Inject
    public DatabaseExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "database.dispatcher");
    }
}