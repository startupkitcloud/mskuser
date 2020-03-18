package com.mangobits.startupkit.user.preference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mangobits.startupkit.user.preference.Preference;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(value={"hibernateLazyInitializer", "handler"}, ignoreUnknown=true)
@Entity(name="userPreferences")
@Indexed
public class UserPreferences {

    public UserPreferences(String id) {
        this.id = id;
    }

    public UserPreferences() {
    }


    @Id
    @DocumentId
    private String id;


    private Date creationDate;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<Preference> listPreferences;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<Preference> getListPreferences() {
        return listPreferences;
    }

    public void setListPreferences(List<Preference> listPreferences) {
        this.listPreferences = listPreferences;
    }
}
