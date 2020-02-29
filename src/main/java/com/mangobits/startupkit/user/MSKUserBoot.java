package com.mangobits.startupkit.user;

import com.mangobits.startupkit.core.configuration.ConfigurationService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class MSKUserBoot {


    @EJB
    private ConfigurationService configurationService;


    @PostConstruct
    public void run(){

        try {
            configurationService.checkAndSave("PROJECT_URL", "https://www.startupkit.cloud");
            configurationService.checkAndSave("PROJECT_NAME", "Amazing Project");
            configurationService.checkAndSave("PROJECT_LOGO_URL", "http://admin.startupkit.mangotest.com/img/mango.png");
            configurationService.checkAndSave("USER_EMAIL_FORGOT_ID", "1080189");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
