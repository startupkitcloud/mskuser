package com.mangobits.startupkit.user;

import javax.persistence.Embeddable;
import java.util.Date;
import java.util.List;

@Embeddable
public class UserSearch {

    private String queryString;

    private Double lat;

    private Double log;

    private Integer page;

    private String type;
    private String code;
    private List<String> typeIn;
    private String status;
    private Date creationDate;
    private Integer pageItensNumber;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPageItensNumber() {
        return pageItensNumber;
    }

    public void setPageItensNumber(Integer pageItensNumber) {
        this.pageItensNumber = pageItensNumber;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLog() {
        return log;
    }

    public void setLog(Double log) {
        this.log = log;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreationDateDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getTypeIn() {
        return typeIn;
    }

    public void setTypeIn(List<String> typeIn) {
        this.typeIn = typeIn;
    }
}
