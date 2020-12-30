package org.startupkit.user.preference;

import org.startupkit.core.configuration.ConfigurationService;
import org.startupkit.notification.email.EmailService;
import org.startupkit.user.UserService;
import org.startupkit.user.util.UserBaseRestService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Stateless
@Path("/preference")
public class PreferenceRestService extends UserBaseRestService {

    @EJB
    private PreferenceService preferenceService;

    @EJB
    private UserService userService;

    @EJB
    private ConfigurationService configurationService;

    @EJB
    private EmailService emailService;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/savePreference")
    public void savePreference(Preference preference)  throws Exception{
        preferenceService.save(preference);
    }


    @GET
    @Path("/listAll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<Preference> listAll() throws Exception {
        return preferenceService.listAll();
    }


    @PUT
    @Path("/changeStatus/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void changeStatus(@PathParam("id") String id) throws Exception {
        preferenceService.changeStatus(id);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/saveUserPreferences")
    public void saveUserPreferences(UserPreferences userPreferences) throws Exception {
        preferenceService.saveUserPreferences(userPreferences);
    }


    @GET
    @Path("/listAllByIdUser/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<Preference> listAllByIdUser(@PathParam("idUser") String idUser) throws Exception {
        return preferenceService.listAllByIdUser(idUser);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/saveUserBPreferences")
    public void saveUserBPreferences(UserPreferences userPreferences) throws Exception {
        preferenceService.saveUserBPreferences(userPreferences);
    }
}