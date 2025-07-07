package entity;

/**
 * 分页入参
 */
public class PageDTO {

    //页码
    public int page;
    //行数
    public int pageSize;
    //查询条件
    public WhereDTO where;
    //排序字段
    public String sortBy;
    //排序方式
    public String order;


}
