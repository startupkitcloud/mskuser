package com.mangobits.startupkit.user.preference;

import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.dao.SearchBuilder;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.status.SimpleStatusEnum;
import com.mangobits.startupkit.core.utils.BusinessUtils;
import com.mangobits.startupkit.user.User;
import com.mangobits.startupkit.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PreferenceServiceImpl implements PreferenceService {

    @New
    @Inject
    private PreferenceDAO preferenceDAO;

    @New
    @Inject
    private UserPreferencesDAO userPreferencesDAO;


    @EJB
    private ConfigurationService configurationService;

    @EJB
    private UserService userService;


    @Override
    public void changeStatus(String idPreference) throws Exception {

        Preference preference = retrieve(idPreference);

        if(preference.getStatus().equals(SimpleStatusEnum.ACTIVE)){
            preference.setStatus(SimpleStatusEnum.BLOCKED);
        }
        else{
            preference.setStatus(SimpleStatusEnum.ACTIVE);
        }

        save(preference);


    }

    @Override
    public void save(Preference preference) throws Exception {

        if(preference.getId() == null){
            preference.setCreationDate(new Date());
            preference.setStatus(SimpleStatusEnum.ACTIVE);
            preference.setSelected(false);
            preferenceDAO.insert(preference);
        }else {
            new BusinessUtils<Preference>(preferenceDAO).basicSave(preference);
        }

    }


    @Override
    public List<Preference> listAll() throws Exception {

        SearchBuilder builder = new SearchBuilder();
        builder.appendParam("status", SimpleStatusEnum.ACTIVE);

        Sort sort = new Sort(new SortField("name", SortField.Type.STRING, false));
        builder.setSort(sort);

        List<Preference> list = preferenceDAO.search(builder.build());

        return list;
    }

    @Override
    public List<Preference> listAllByIdUser(String idUser) throws Exception {

        List<Preference> list = null;

        SearchBuilder builder = new SearchBuilder();
        builder.appendParam("status", SimpleStatusEnum.ACTIVE);

        Sort sort = new Sort(new SortField("name", SortField.Type.STRING, false));
        builder.setSort(sort);

        list = preferenceDAO.search(builder.build());

        if (list!= null && list.size() > 0){
            UserPreferences userPreferences = userPreferencesDAO.retrieve(new UserPreferences(idUser));

            for(Preference preference : list){

                if( userPreferences != null
                        && CollectionUtils.isNotEmpty(userPreferences.getListPreferences())
                        && userPreferences.getListPreferences().contains(preference)){
                    preference.setSelected(true);
                }else{
                    preference.setSelected(false);
                }
            }
        }

        return list;
    }

    @Override
    public Preference retrieve(String idPreference) throws Exception {

        Preference preference = preferenceDAO.retrieve(new Preference(idPreference));

        return preference;
    }

    @Override
    public void saveUserPreferences(UserPreferences userPreferences) throws Exception {

        if(userPreferences.getId() == null){
            throw new BusinessException("missing_idUser");
        }
        User user = userService.retrieve(userPreferences.getId());
        if(user == null){
            throw new BusinessException("user_not_found");
        }

        UserPreferences userInfoBase = userPreferencesDAO.retrieve(new UserPreferences(userPreferences.getId()));

        if (userInfoBase == null){
            userPreferences.setCreationDate(new Date());
            userPreferencesDAO.insert(userPreferences);
        }else {
            new BusinessUtils<UserPreferences>(userPreferencesDAO).basicSave(userPreferences);

        }
    }
}
