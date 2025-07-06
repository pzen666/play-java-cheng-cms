package controllers;

import entity.PageDTO;
import entity.result.Results;
import io.ebean.DB;
import jakarta.inject.Inject;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.UserRepository;

import java.util.List;
import java.util.concurrent.CompletionStage;


public class UserController extends Controller {

    private final UserRepository userRepository;

    @Inject
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * 创建用户
     * @param request
     * @return
     */
    public Result createUser(Http.Request request) {
        User p = Json.fromJson(request.body().asJson(), User.class);
        p.save();
        return ok(Json.toJson(Results.success(p)));
    }

    /**
     * 删除用户
     * @param request
     * @return
     */
    public Result deleteUser(Http.Request request) {
        User p = Json.fromJson(request.body().asJson(), User.class);
        p.delete();
        return ok(Json.toJson(Results.success(p)));
    }

    /**
     * 修改用户
     * @param request
     * @return
     */
    public Result updateUser(Http.Request request) {
        User p = Json.fromJson(request.body().asJson(), User.class);
        p.update();
        return ok(Json.toJson(Results.success(p)));
    }

    /**
     * 查询用户
     * @param request
     * @return
     */
    public Result getUser(Http.Request request) {
        PageDTO p = Json.fromJson(request.body().asJson(), PageDTO.class);
        List<User> userList = DB.find(User.class).where().eq(p.filterKeyword, p.filter).findList();
        return ok(Json.toJson(Results.success(userList)));

    }

    /**
     * 分页查询用户(异步查询)
     * @param request
     * @return
     */
    public CompletionStage<Result> userPagedList(Http.Request request) {
        PageDTO p = Json.fromJson(request.body().asJson(), PageDTO.class);
        return userRepository
                .userPagedList(p)
                .thenApply(pagedList -> ok(Json.toJson(pagedList)));
    }


}
