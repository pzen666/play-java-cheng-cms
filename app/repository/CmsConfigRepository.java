package repository;

import entity.CmsConfigDTO;
import entity.PageDTO;
import entity.WhereDTO;
import entity.dto.UserDTO;
import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import models.CmsConfig;
import models.User;
import service.DynamicQueryService;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * user 数据查询
 */
public class CmsConfigRepository {

    private final DatabaseExecutionContext executionContext;
    private final DynamicQueryService dynamicQueryService;

    @Inject
    public CmsConfigRepository(DatabaseExecutionContext executionContext, DynamicQueryService dynamicQueryService) {
        this.executionContext = executionContext;
        this.dynamicQueryService = dynamicQueryService;
    }

    public CmsConfig updateCmsConfig(CmsConfigDTO p) {
        CmsConfig u = CmsConfig.find.byId(p.getId());
        if (u == null)
            return null;
        u.setName(p.getName());
        u.setUrl(p.getUrl());
        u.setClassType(p.getClassType());
        u.setType(p.getType());
        u.setSiteId(p.getSiteId());
        u.setDescription(p.getDescription());
        u.setStatus(p.getStatus());
        u.setProgress(p.getProgress());
        u.setRecord(p.getRecord());
        u.update();
        return u;
    }

    public CmsConfig deleteCmsConfig(CmsConfigDTO p) {
        CmsConfig u = CmsConfig.find.byId(p.getId());
        if (u != null) {
            u.delete();
            return u;
        }
        return null;
    }


    public CompletionStage<PagedList<CmsConfig>> pagedList(PageDTO p) {
        ExpressionList<CmsConfig> query = DB.find(CmsConfig.class).where();
        WhereDTO whereDTO = p.where;
        query = DynamicQueryService.buildDynamicQuery(query, whereDTO, CmsConfig.class);
        ExpressionList<CmsConfig> finalQuery = query;
        return supplyAsync(
                () -> finalQuery
                        .orderBy(p.sortBy + " " + p.order)
                        .setFirstRow((p.page - 1) * p.pageSize)
                        .setMaxRows(p.pageSize)
                        .findPagedList(), executionContext
        );
    }

}
