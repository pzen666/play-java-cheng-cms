package repository;

import entity.PageDTO;
import entity.WhereDTO;
import entity.vo.DataSourceVO;
import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import models.DataSource;
import service.DynamicQueryService;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * user 数据查询
 */
public class DataSourceRepository {

    private final DatabaseExecutionContext executionContext;

    @Inject
    public DataSourceRepository(DatabaseExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public DataSource addDataSource(DataSource t) {
        int count = DataSource.find.query().where().eq("name", t.name).findCount();
        if (count > 0) {
            return null;
        }
        t.save();
        return t;
    }


    public DataSource updateDataSource(DataSource t) {
        DataSource ds = DataSource.find.byId(t.id);
        if (ds == null) {
            return null;
        }
        ds = t;
        ds.update();
        return ds;
    }

    public DataSource deleteDataSource(DataSource t) {
        DataSource ds = DataSource.find.byId(t.id);
        if (ds != null) {
            ds.delete();
            return ds;
        }
        return null;
    }


//    public CompletionStage<PagedList<DataSource>> pagedList(PageDTO p) {
//        ExpressionList<DataSource> query = DB.find(DataSource.class).where();
//        WhereDTO whereDTO = p.where;
//        query = DynamicQueryService.buildDynamicQuery(query, whereDTO, DataSource.class);
//        ExpressionList<DataSource> finalQuery = query;
//        return supplyAsync(
//                () -> finalQuery
//                        .orderBy(p.sortBy + " " + p.order)
//                        .setFirstRow((p.page - 1) * p.pageSize)
//                        .setMaxRows(p.pageSize)
//                        .findPagedList(), executionContext
//        );
//    }

    public PagedList<DataSource> pagedList(PageDTO p) {
        ExpressionList<DataSource> query = DB.find(DataSource.class).where();
        WhereDTO whereDTO = p.where;
        query = DynamicQueryService.buildDynamicQuery(query, whereDTO, DataSource.class);
        return query
                .orderBy(p.sortBy + " " + p.order)
                .setFirstRow((p.page - 1) * p.pageSize)
                .setMaxRows(p.pageSize)
                .findPagedList();
    }


    public List<DataSource> getDataSource(WhereDTO p) {
        ExpressionList<DataSource> query = DB.find(DataSource.class).where();
        query = DynamicQueryService.buildDynamicQuery(query, p, DataSource.class);
        return query.findList();
    }

    public DataSource findDataSourceById(Long id) {
        return DataSource.find.byId(id);
    }

}
