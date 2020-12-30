package org.startupkit.user.preference;

import org.startupkit.core.annotation.MSKEntity;
import org.startupkit.core.annotation.MSKId;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import java.util.Date;
import java.util.List;

@MSKEntity(name="userPreferences")
public class UserPreferences {

    public UserPreferences(String id) {
        this.id = id;
    }

    public UserPreferences() {
    }

    @MSKId
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
