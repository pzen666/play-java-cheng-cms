package entity.result;

import java.util.List;

public class PaginatedResult<T> {


    public int page;
    public int pageSize;
    public long totalItems;
    public List<T> items;

    public PaginatedResult(int page, int pageSize, long totalItems, List<T> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.items = items;
    }


}