package org.startupkit.user.preference;

import org.startupkit.core.dao.AbstractDAO;
import org.startupkit.user.terms.Terms;

public class PreferenceDAO extends AbstractDAO<Preference> {

    public PreferenceDAO(){
        super(Preference.class);
    }


    @Override
    public Object getId(Preference obj) {
        return obj.getId();
    }
}
