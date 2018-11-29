package com.mangobits.startupkit.user.preference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.notification.email.EmailService;
import com.mangobits.startupkit.user.UserService;
import com.mangobits.startupkit.user.util.SecuredUser;
import com.mangobits.startupkit.user.util.UserBaseRestService;
import com.mangobits.startupkit.ws.JsonContainer;

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

    @SecuredUser
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/savePreference")
    public String savePreference(Preference preference)  throws Exception{

        String resultStr = null;
        JsonContainer cont = new JsonContainer();

        try {

            preferenceService.save(preference);
            cont.setData("OK");

        } catch (Exception e) {

            if(!(e instanceof BusinessException)){
                e.printStackTrace();
            }

            cont.setSuccess(false);
            cont.setDesc(e.getMessage());

            emailService.sendEmailError(e);
        }


        ObjectMapper mapper = new ObjectMapper();
        resultStr = mapper.writeValueAsString(cont);

        return resultStr;
    }

    @GET
    @Path("/listAll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String listAll() throws Exception {

        String resultStr = null;
        JsonContainer cont = new JsonContainer();

        try {

            List<Preference> list = preferenceService.listAll();
            cont.setData(list);

        } catch (Exception e) {

            if(!(e instanceof BusinessException)){
                e.printStackTrace();
            }

            cont.setSuccess(false);
            cont.setDesc(e.getMessage());

            emailService.sendEmailError(e);
        }

        ObjectMapper mapper = new ObjectMapper();
        resultStr = mapper.writeValueAsString(cont);

        return resultStr;
    }

    @SecuredUser
    @GET
    @Path("/changeStatus/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String changeStatus(@PathParam("id") String id) throws Exception {

        String resultStr;
        JsonContainer cont = new JsonContainer();

        try {

            preferenceService.changeStatus(id);
            cont.setData("OK");

        } catch (Exception e) {
            handleException(cont, e, "changing preference status");
        }

        ObjectMapper mapper = new ObjectMapper();
        resultStr = mapper.writeValueAsString(cont);

        return resultStr;
    }

    @SecuredUser
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/saveUserPreferences")
    public String saveUserPreferences(UserPreferences userPreferences) throws Exception {

        String resultStr;
        JsonContainer cont = new JsonContainer();

        try {

            preferenceService.saveUserPreferences(userPreferences);
            cont.setData("OK");

        } catch (Exception e) {
            handleException(cont, e, "saving user preferences");
        }

        ObjectMapper mapper = new ObjectMapper();
        resultStr = mapper.writeValueAsString(cont);

        return resultStr;

    }

    @SecuredUser
    @GET
    @Path("/listAllByIdUser/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String listAllByIdUser(@PathParam("idUser") String idUser) throws Exception {

        String resultStr;
        JsonContainer cont = new JsonContainer();

        try {

            List<Preference> list = preferenceService.listAllByIdUser(idUser);
            cont.setData(list);

        } catch (Exception e) {
            handleException(cont, e, "listing user preferences");
        }

        ObjectMapper mapper = new ObjectMapper();
        resultStr = mapper.writeValueAsString(cont);

        return resultStr;
    }

}