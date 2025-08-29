package entity.vo;

import lombok.Data;

/**
 * 影视分类列表
 */
@Data
public class VodVideoClassVO {

    //分类id
    private String typeId;
    //分类名称
    private String typeName;

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
}
