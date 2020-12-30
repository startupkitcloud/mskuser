package org.startupkit.user.preference;

import javax.ejb.Local;
import java.util.List;

@Local
public interface PreferenceService {

    void changeStatus(String idPreference) throws Exception;

    void save(Preference preference) throws Exception;

    List<Preference> listAll() throws Exception;

    Preference retrieve(String idPreference) throws Exception;

    List<Preference> listAllByIdUser(String idUser) throws Exception;

    void saveUserPreferences(UserPreferences userPreferences) throws Exception;

    List<Preference> listByUser (String idUser) throws Exception;

    void saveUserBPreferences(UserPreferences userPreferences) throws Exception;

}
