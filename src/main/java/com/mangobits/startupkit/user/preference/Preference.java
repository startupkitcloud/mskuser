package com.mangobits.startupkit.user.preference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mangobits.startupkit.core.annotation.MSKEntity;
import com.mangobits.startupkit.core.annotation.MSKId;
import com.mangobits.startupkit.core.status.SimpleStatusEnum;

import javax.persistence.Transient;
import java.util.Date;

@MSKEntity(name="preference")
public class Preference {

    @MSKId
    private String id;

    private String name;

    private String desc;

    @Transient
    private Boolean selected;

    @JsonIgnore
    private Date creationDate;

    private SimpleStatusEnum status;


    public Preference(){

    }

    public Preference(String id){
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public SimpleStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusEnum status) {
        this.status = status;
    }



    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
