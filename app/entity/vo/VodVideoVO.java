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

}
