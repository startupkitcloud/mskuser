package com.mangobits.startupkit.user.preference;

import com.mangobits.startupkit.core.dao.AbstractDAO;
import com.mangobits.startupkit.user.terms.Terms;

public class PreferenceDAO extends AbstractDAO<Preference> {

    public PreferenceDAO(){
        super(Preference.class);
    }


    @Override
    public Object getId(Preference obj) {
        return obj.getId();
    }
}
