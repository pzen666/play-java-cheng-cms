package models;

import io.ebean.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "t_cms_data_source", schema = "public")
public class DataSource extends Model {

    public static final io.ebean.Finder<Long, DataSource> find = new io.ebean.Finder<>(DataSource.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    //名称
    public String name;
    //接口类型
    public String interfaceType;
    //地址
    public String sourceUrl;
    //认证方式
    public String authType;
    //请求方法
    public String requestMethod;
    //请求头 (JSON格式)
    public String headers;
    //请求参数 (JSON格式)
    public String params;
    //简介
    public String description;
    //状态
    public String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
