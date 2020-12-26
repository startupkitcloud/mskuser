package org.startupkit.user;

import org.startupkit.core.configuration.ConfigurationService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.Map;

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
            configurationService.checkAndSave("MSG_EMAIL", "1267351");

            Map<String, String> mailData = new HashMap<>();
            mailData.put("host", "in-v3.mailjet.com");
            mailData.put("user", "3035e159bcfe348965116e5baaed4a08");
            mailData.put("password", "8a75c152b65f9e03671c879a22a02171");
            mailData.put("from", "info@mangobits.net");
            mailData.put("fromName", "Amazing Project");
            mailData.put("smtpHost", "in-v3.mailjet.com");
            mailData.put("smtpPort", "587");
            mailData.put("authorization", "true");
            configurationService.checkAndSave("MAIL_DATA", mailData);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
