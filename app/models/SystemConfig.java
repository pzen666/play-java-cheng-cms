package models;

import io.ebean.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_cms_system_config", schema = "public")
public class SystemConfig extends Model {

    public static final io.ebean.Finder<Long, SystemConfig> find = new io.ebean.Finder<>(SystemConfig.class);

    @Id
    @Column(name = "key", unique = true, nullable = false)
    public String key;
    public String name;
    public String value;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
