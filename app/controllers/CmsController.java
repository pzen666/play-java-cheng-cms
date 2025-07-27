package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import entity.CmsConfigDTO;
import entity.PageDTO;
import entity.result.Results;
import entity.vo.VodVideoVO;
import jakarta.inject.Inject;
import models.CmsConfig;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.CmsConfigRepository;
import utils.VodVideoParser;

import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * CMS信息采集配置
 */
public class CmsController extends Controller {

    private final CmsConfigRepository cmsConfigRepository;
    private final VodVideoParser vodVideoParser;

    @Inject
    public CmsController(CmsConfigRepository cmsConfigRepository, VodVideoParser vodVideoParser) {
        this.cmsConfigRepository = cmsConfigRepository;
        this.vodVideoParser = vodVideoParser;
    }

    // 采集信息配置-增  删 改 查
    public Result cmsAddConfig(Http.Request request) {
        CmsConfig cms = Json.fromJson(request.body().asJson(), CmsConfig.class);
        cms.save();
        return ok(Json.toJson(Results.success(cms)));
    }

    public Result cmsUpdateConfig(Http.Request request) {
        CmsConfigDTO p = Json.fromJson(request.body().asJson(), CmsConfigDTO.class);
        CmsConfig cp = cmsConfigRepository.updateCmsConfig(p);
        if (cp == null) {
            return notFound(Json.toJson(Results.error("不存在")));
        }
        return ok(Json.toJson(Results.success(cp)));
    }

    public Result cmsDeleteConfig(Http.Request request) {
        CmsConfigDTO p = Json.fromJson(request.body().asJson(), CmsConfigDTO.class);
        CmsConfig cp = cmsConfigRepository.deleteCmsConfig(p);
        if (cp == null) {
            return notFound(Json.toJson(Results.error("不存在")));
        }
        return ok(Json.toJson(Results.success(cp)));
    }

    public CompletionStage<Result> cmsPageConfig(Http.Request request) {
        PageDTO p = Json.fromJson(request.body().asJson(), PageDTO.class);
        return cmsConfigRepository
                .pagedList(p)
                .thenApply(pagedList -> ok(Json.toJson(pagedList)));
    }

    //测试采集连接状态 获取采集信息 入参url 采集类型:json xml

    public Result cmsUrlTestStatus(Http.Request request) {
        Map m = Json.fromJson(request.body().asJson(), Map.class);
        String type = m.getOrDefault("type", "").toString();
        String url = m.getOrDefault("url", "").toString();
        VodVideoVO v = null;
        if (!type.isEmpty() && !url.isEmpty()) {
            if (type.equals("json")) {
                v = VodVideoParser.json(url);
            } else {
                v = VodVideoParser.xml(url);
            }
        }
        if (v == null) {
            return ok(Json.toJson(Results.success(v)));

        } else {
            return ok(Json.toJson(Results.error(null)));
        }
    }


}
