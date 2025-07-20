// controllers/AuthController.java
package controllers;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

@Singleton
public class AuthController extends Controller {
    @Inject
    public AuthController() {
        // 无参构造函数或用于注入的构造函数
    }
    public CompletionStage<Result> login(Http.Request request) {
        return CompletableFuture.completedFuture(ok("Logged in"));
    }

    public CompletionStage<Result> secure(Http.Request request) {
        return CompletableFuture.completedFuture(ok("Secure page"));
    }
}