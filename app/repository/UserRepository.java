package repository;

import entity.PageDTO;
import entity.WhereDTO;
import entity.dto.UserDTO;
import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import models.User;
import service.DynamicQueryService;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * user 数据查询
 */
public class UserRepository {

    private final DatabaseExecutionContext executionContext;
    private final DynamicQueryService dynamicQueryService;

    @Inject
    public UserRepository(DatabaseExecutionContext executionContext, DynamicQueryService dynamicQueryService) {
        this.executionContext = executionContext;
        this.dynamicQueryService = dynamicQueryService;
    }

    public CompletionStage<PagedList<User>> userPagedList(PageDTO p) {
        ExpressionList<User> query = DB.find(User.class).where();
        WhereDTO whereDTO = p.where;
        query = DynamicQueryService.buildDynamicQuery(query, whereDTO, User.class);
        ExpressionList<User> finalQuery = query;
        return supplyAsync(
                () -> finalQuery
                        .orderBy(p.sortBy + " " + p.order)
                        .setFirstRow((p.page - 1) * p.pageSize)
                        .setMaxRows(p.pageSize)
                        .findPagedList(), executionContext
        );
    }

    public User deleteUser(UserDTO p) {
        User u = User.find.byId(p.id);
        if (u != null) {
            u.delete();
            return u;
        }
        return null;
    }

    public User updateUser(UserDTO p) {
        User u = User.find.byId(p.id);
        if (u == null)
            return null;
        u.setPassword(p.password);
        u.update();
        return u;
    }

    public List<User> getUser(WhereDTO p) {
        ExpressionList<User> query = DB.find(User.class).where();
        query = DynamicQueryService.buildDynamicQuery(query, p, User.class);
        return query.findList();
    }
}
