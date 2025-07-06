package repository;

import entity.PageDTO;
import io.ebean.DB;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import models.User;

import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * user 数据查询
 */
public class UserRepository {

    private final DatabaseExecutionContext executionContext;

    @Inject
    public UserRepository(DatabaseExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public CompletionStage<PagedList<User>> userPagedList(PageDTO p) {
        return supplyAsync(
                ()-> DB.find(User.class).where()
                        .ilike(p.filterKeyword, "%" + p.filter + "%")
                        .orderBy(p.sortBy + " " + p.order)
                        .setFirstRow((p.page - 1) * p.pageSize)
                        .setMaxRows(p.pageSize)
                        .findPagedList(),executionContext
        );
    }
}
