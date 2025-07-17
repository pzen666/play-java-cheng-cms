package entity.vo;

import lombok.Data;

/**
 * 影视列表
 */
@Data
public class VodVideoListVO {

    //id
    private String vodId;
    //剧集名称
    private String vodName;
    //分类ID
    private String typeId;
    //分类名称
    private String typeName;
    //拼音名称
    private String vodEn;
    //时间 更新时间
    private String vodTime;
    //总集数说明
    private String vodRemarks;
    //播放器
    private String vodPlayFrom;


}
