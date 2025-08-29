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

    public String getVodId() {
        return vodId;
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    public String getVodName() {
        return vodName;
    }

    public void setVodName(String vodName) {
        this.vodName = vodName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getVodEn() {
        return vodEn;
    }

    public void setVodEn(String vodEn) {
        this.vodEn = vodEn;
    }

    public String getVodTime() {
        return vodTime;
    }

    public void setVodTime(String vodTime) {
        this.vodTime = vodTime;
    }

    public String getVodRemarks() {
        return vodRemarks;
    }

    public void setVodRemarks(String vodRemarks) {
        this.vodRemarks = vodRemarks;
    }

    public String getVodPlayFrom() {
        return vodPlayFrom;
    }

    public void setVodPlayFrom(String vodPlayFrom) {
        this.vodPlayFrom = vodPlayFrom;
    }
}
