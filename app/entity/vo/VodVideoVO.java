package entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 获取视频返回列表
 */
@Data
public class VodVideoVO {

    //
    private String code;
    //
    private String msg;
    //当前页
    private String page;
    //页数
    private String pageCount;
    //分页条目
    private String limit;
    //总数
    private String total;
    //影视列表
    private List<VodVideoListVO> list;
    //影视分类列表
    private List<VodVideoClassVO> classType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<VodVideoListVO> getList() {
        return list;
    }

    public void setList(List<VodVideoListVO> list) {
        this.list = list;
    }

    public List<VodVideoClassVO> getClassType() {
        return classType;
    }

    public void setClassType(List<VodVideoClassVO> classType) {
        this.classType = classType;
    }
}
