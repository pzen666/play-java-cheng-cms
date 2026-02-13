package filters;

import actions.filters.AuthenticationFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 组合过滤器 - 使用 AuthenticationFilter（已包含 CORS）
 */
@Singleton
public class Filters extends DefaultHttpFilters {

    @Inject
    public Filters(AuthenticationFilter authenticationFilter) {
        // AuthenticationFilter 已经包含了 CORS 过滤器
        super(authenticationFilter.getFilters().toArray(new play.mvc.EssentialFilter[0]));
    }
}