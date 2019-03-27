package com.mangobits.startupkit.user;

import java.util.Date;

public class UserSearch {

    private String queryString;

    private Double lat;

    private Double log;

    private Integer page;

    private String type;


    private String status;

    private Date creationDate;

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getPageItensNumber() {
        return pageItensNumber;
    }

    public void setPageItensNumber(Integer pageItensNumber) {
        this.pageItensNumber = pageItensNumber;
    }

    private Integer pageItensNumber;



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

    public void setCreationDateDate(Date creationDate) {
        this.creationDate = creationDate;
    }


}
