package repository;

import entity.PageDTO;
import entity.WhereDTO;
import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import models.DataSource;
import models.ScheduledTask;
import service.DynamicQueryService;

import java.util.List;

/**
 * user 数据查询
 */
public class ScheduledTasksRepository {

    private final DatabaseExecutionContext executionContext;

    @Inject
    public ScheduledTasksRepository(DatabaseExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public ScheduledTask addScheduledTasks(ScheduledTask t) {
        int count = ScheduledTask.find.query().where().eq("name", t.name).findCount();
        if (count > 0) {
            return null;
        }
        t.save();
        return t;
    }


    public ScheduledTask updateScheduledTasks(ScheduledTask t) {
        ScheduledTask ds = ScheduledTask.find.byId(t.id);
        if (ds == null) {
            return null;
        }
        ds = t;
        ds.update();
        return ds;
    }

    public ScheduledTask deleteScheduledTasks(ScheduledTask t) {
        ScheduledTask ds = ScheduledTask.find.byId(t.id);
        if (ds != null) {
            ds.delete();
            return ds;
        }
        return null;
    }
    

    public PagedList<ScheduledTask> pagedList(PageDTO p) {
        ExpressionList<ScheduledTask> query = DB.find(ScheduledTask.class).where();
        WhereDTO whereDTO = p.where;
        query = DynamicQueryService.buildDynamicQuery(query, whereDTO, ScheduledTask.class);
        return query
                .orderBy(p.sortBy + " " + p.order)
                .setFirstRow((p.page - 1) * p.pageSize)
                .setMaxRows(p.pageSize)
                .findPagedList();
    }


    public List<ScheduledTask> getScheduledTasks(WhereDTO p) {
        ExpressionList<ScheduledTask> query = DB.find(ScheduledTask.class).where();
        query = DynamicQueryService.buildDynamicQuery(query, p, ScheduledTask.class);
        return query.findList();
    }

    public ScheduledTask findScheduledTasksById(Long id) {
        return ScheduledTask.find.byId(id);
    }

}
