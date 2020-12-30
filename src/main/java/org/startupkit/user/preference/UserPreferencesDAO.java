package org.startupkit.user.preference;

import org.startupkit.core.dao.AbstractDAO;

public class UserPreferencesDAO extends AbstractDAO<UserPreferences> {

    public UserPreferencesDAO(){
        super(UserPreferences.class);
    }


    @Override
    public Object getId(UserPreferences obj) {
        return obj.getId();
    }
}
