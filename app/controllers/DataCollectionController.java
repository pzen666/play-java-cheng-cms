package controllers;

import entity.PageDTO;
import entity.result.Results;
import entity.vo.DataSourceVO;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import models.DataSource;
import models.VideoDict;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.DataSourceRepository;

import java.util.concurrent.CompletionStage;

/**
 * 数据采集功能
 */
public class DataCollectionController extends Controller {

    private static final Logger.ALogger logger = Logger.of(DataCollectionController.class);

    private final DataSourceRepository dataSourceRepository;

    @Inject
    public DataCollectionController(DataSourceRepository dataSourceRepository) {
        this.dataSourceRepository = dataSourceRepository;
    }


    // 新增采集源
    public Result addDataSource(Http.Request request) {
        logger.info("新增采集源");
        DataSource t = Json.fromJson(request.body().asJson(), DataSource.class);
        DataSource ds = dataSourceRepository.addDataSource(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    //删除采集源
    public Result delDataSource(Http.Request request) {
        DataSource t = Json.fromJson(request.body().asJson(), DataSource.class);
        DataSource ds = dataSourceRepository.deleteDataSource(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    //编辑采集源
    public Result upDataSource(Http.Request request) {
        DataSource t = Json.fromJson(request.body().asJson(), DataSource.class);
        DataSource ds = dataSourceRepository.updateDataSource(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    //分页查询
    public Result pagedList(Http.Request request) {
        PageDTO t = Json.fromJson(request.body().asJson(), PageDTO.class);
//        CompletionStage<PagedList<DataSource>> pagedList = dataSourceRepository.pagedList(t);
        PagedList<DataSource> pagedList = dataSourceRepository.pagedList(t);
        return ok(Json.toJson(Results.success(pagedList)));
    }

    //查询采集源
    public Result findDataSource(Http.Request request) {
        DataSource t = Json.fromJson(request.body().asJson(), DataSource.class);
        DataSource ds = dataSourceRepository.addDataSource(t);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("采集源新增失败")));
        }
        return ok(Json.toJson(Results.success(t)));
    }

    public Result findDataSourceById(Http.Request request) {
        Long id = request.body().asJson().get("id").asLong();
        DataSource ds = dataSourceRepository.findDataSourceById(id);
        if (ds == null) {
            return badRequest(Json.toJson(Results.error("选中数据获取失败")));
        }
        return ok(Json.toJson(Results.success(ds)));
    }

    //绑定菜单
    public Result addOrUpdateDict(Http.Request request) {
        VideoDict t = Json.fromJson(request.body().asJson(), VideoDict.class);
        return ok(Json.toJson(Results.success(t)));
    }


}
