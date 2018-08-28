package com.mangobits.startupkit.user.preference;

import com.mangobits.startupkit.core.dao.AbstractDAO;

public class UserPreferencesDAO extends AbstractDAO<UserPreferences> {

    public UserPreferencesDAO(){
        super(UserPreferences.class);
    }


    @Override
    public Object getId(UserPreferences obj) {
        return obj.getId();
    }
}
