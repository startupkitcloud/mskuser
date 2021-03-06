package org.startupkit.user;

import org.startupkit.authkey.UserAuthKey;
import org.startupkit.authkey.UserAuthKeyService;
import org.startupkit.authkey.UserAuthKeyTypeEnum;
import org.startupkit.core.address.AddressInfo;
import org.startupkit.core.configuration.Configuration;
import org.startupkit.core.configuration.ConfigurationEnum;
import org.startupkit.core.configuration.ConfigurationService;
import org.startupkit.core.dao.OperationEnum;
import org.startupkit.core.dao.SearchBuilder;
import org.startupkit.core.exception.BusinessException;
import org.startupkit.core.photo.PhotoUpload;
import org.startupkit.core.photo.PhotoUploadTypeEnum;
import org.startupkit.core.utils.BusinessUtils;
import org.startupkit.core.utils.ImageUtil;
import org.startupkit.core.utils.MessageUtils;
import org.startupkit.core.utils.SecUtils;
import org.startupkit.notification.NotificationBuilder;
import org.startupkit.notification.NotificationService;
import org.startupkit.notification.TypeSendingNotificationEnum;
import org.startupkit.notification.email.data.EmailDataTemplate;
import org.startupkit.user.freezer.UserFreezerService;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.enterprise.inject.New;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserServiceImpl implements UserService {

    private static final int TOTAL_PAGE = 10;


    @EJB
    private ConfigurationService configurationService;


    @EJB
    private UserAuthKeyService userAuthKeyService;


    @EJB
    private NotificationService notificationService;


    @EJB
    private UserFreezerService userFreezerService;


    @Inject
    @New
    private UserDAO userDAO;


    //evita o mongo de gerar log
    @PostConstruct
    public void pos() {
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
    }


    @Override
    public User retrieveByEmail(String email) throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);

        User user = userDAO.retrieve(params);

        return user;
    }


    @Override
    public User retrieveByPhone(Long phoneNumber) throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phoneNumber", phoneNumber);

        User user = userDAO.retrieve(params);

        return user;
    }

    @Override
    public User saveByPhone(User user) throws Exception {

        if (user.getPhoneNumber() == null) {
            throw new BusinessException("missing_phone");
        }

        User userBase = retrieveByPhone(user.getPhoneNumber());

        if (userBase == null) {
            user = createNewUser(user);
            return user;
        } else {
            return userBase;
        }
    }


    @Override
    public void save(User user) throws Exception {

        if (user.getId() == null) {
            createNewUser(user);
        } else {
            updateFromClient(user);
        }
    }


    @Override
    public User createNewUser(User user) throws Exception {

        User userDB = null;
        boolean fgPhoneError = false;

        if (user.getEmail() != null && !user.getEmail().equals("")) {
            userDB = retrieveByEmail(user.getEmail());
        }

        if (userDB == null && user.getPhoneNumber() != null && user.getPhoneNumber() != 0) {
            userDB = retrieveByPhone(user.getPhoneNumber());
            fgPhoneError = true;
        }

        if (userDB != null) {

            if (user.getIdFacebook() != null && !user.getIdFacebook().equals("") && (userDB.getIdFacebook() == null
                    || userDB.getIdFacebook().equals(""))) {

                userDB.setIdFacebook(user.getIdFacebook());
                userDAO.update(userDB);

                return userDB;
            }

            if (user.getIdGoogle() != null && !user.getIdGoogle().equals("") && (userDB.getIdGoogle() == null
                    || userDB.getIdGoogle().equals(""))) {

                userDB.setIdGoogle(user.getIdGoogle());
                userDAO.update(userDB);

                return userDB;
            }

            if (user.getIdApple() != null && !user.getIdApple().equals("") && (userDB.getIdApple() == null
                    || userDB.getIdApple().equals(""))) {

                userDB.setIdApple(user.getIdApple());
                userDAO.update(userDB);

                return userDB;
            }

            if (fgPhoneError) {
                throw new BusinessException("phone_exists");
            } else {
                throw new BusinessException("email_exists");
            }
        }

        user.setCreationDate(new Date());
        user.setId(null);

        if (user.getPassword() != null) {
            user.setSalt(SecUtils.getSalt());
            user.setPassword(SecUtils.generateHash(user.getSalt(), user.getPassword()));
        }

        if (user.getStatus() == null) {
            user.setStatus(UserStatusEnum.ACTIVE);
        }


        if (user.getInfo() != null && user.getInfo().containsKey("idUserAnonymous") && user.getType().equals("anonymous")) {

            user.setType("user");

            user.setId(user.getInfo().get("idUserAnonymous"));

            new BusinessUtils<>(userDAO).basicSave(user);

        } else {
            userDAO.insert(user);
        }

        if (user.getPhone() != null && configurationService.loadByCode("CHECK_PHONE") != null && configurationService.loadByCode("CHECK_PHONE").getValueAsBoolean()) {
            confirmUserSMS(user.getId());
        }

        sendWelcomeEmail(user);

        createToken(user);

        return user;
    }

    @Override
    public User saveAnonymous(User user) throws Exception {

        if (user.getId() == null) {

            user.setCreationDate(new Date());
            user.setType("anonymous");

            this.createToken(user);

            userDAO.insert(user);

            if (StringUtils.isNotEmpty(user.getKeyAndroid()) || StringUtils.isNotEmpty(user.getKeyIOS())) {

                user.setInfo(new HashMap<>());

                if (StringUtils.isNotEmpty(user.getKeyAndroid())) {
                    user.getInfo().put("keyAndroid", user.getKeyAndroid());
                } else {
                    user.getInfo().put("keyIOS", user.getKeyIOS());
                }

                new BusinessUtils<>(userDAO).basicSave(user);
            }
        }

        return user;
    }


    @Override
    public void sendWelcomeEmail(User user) throws Exception {

        String configKeyLang = user.getLanguage() == null ? "" : "_" + user.getLanguage().toUpperCase();

        final String projectName = configurationService.loadByCode("PROJECT_NAME").getValue();

        Configuration confEmailId = configurationService.loadByCode("EMAIL_USER_WELCOME" + configKeyLang);

        if (confEmailId != null) {

            String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.email.welcome.title", projectName);

            final int emailTemplateId = confEmailId.getValueAsInt();

            notificationService.sendNotification(new NotificationBuilder()
                    .setTo(user)
                    .setTypeSending(TypeSendingNotificationEnum.EMAIL)
                    .setTitle(title)
                    .setFgAlertOnly(true)
                    .setEmailDataTemplate(new EmailDataTemplate() {

                        @Override
                        public Integer getTemplateId() {
                            return emailTemplateId;
                        }

                        @Override
                        public Map<String, Object> getData() {

                            Map<String, Object> params = new HashMap<>();
                            params.put("user_name", user.getName());
                            return params;
                        }
                    })
                    .build());
        }
    }


    protected void validateUser(User user) throws Exception {

        User userDB = null;

        if (user.getPhoneNumber() != null && user.getPhoneNumber() != 0) {

            Map<String, Object> params = new HashMap<>();
            params.put("phoneNumber", user.getPhoneNumber());

            userDB = userDAO.retrieve(params);

            if (userDB != null && !userDB.getId().equals(user.getId())) {
                throw new BusinessException("phone_exists");
            }
        }

        if (user.getEmail() != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("email", user.getEmail());

            userDB = userDAO.retrieve(params);

            if (userDB != null && !userDB.getId().equals(user.getId())) {
                throw new BusinessException("email_exists");
            }
        }
    }


    @Override
    public User retrieveByIdFacebook(String idFacebook) throws Exception {

        User user = userDAO.retrieveByIdFacebook(idFacebook);
        return user;
    }


    @Override
    public User loginFB(User user) throws Exception {
        User userDB = userDAO.retrieveByIdFacebook(user.getIdFacebook());
        user = loginSocial(user, userDB);
        return user;
    }


    @Override
    public User loginGoogle(User user) throws Exception {
        User userDB = userDAO.retrieveByIdGoogle(user.getIdGoogle());
        user = loginSocial(user, userDB);
        return user;
    }


    @Override
    public User loginApple(User user) throws Exception {
        User userDB = userDAO.retrieveByIdApple(user.getIdApple());
        user = loginSocial(user, userDB);
        return user;
    }


    protected User loginSocial(User user, User userDB) throws Exception {

        if (userDB == null) {
            user = createNewUser(user);
        } else {
            user = userDB;

            if (userDB.getStatus() != null && userDB.getStatus().equals(UserStatusEnum.BLOCKED)) {
                throw new BusinessException("user_blocked");
            }

            createToken(userDB);
        }
        return user;
    }


    @Override
    public User retrieve(String id) throws Exception {

        User user = userDAO.retrieve(new User(id));
        return user;
    }


    @Override
    public User autoLogin(User user) throws Exception {

        User userDB = userDAO.retrieve(new User(user.getId()));

        if (userDB == null || (user.getPassword() == null && user.getIdFacebook() == null && user.getIdGoogle() == null
                && user.getIdApple() == null)) {
            throw new BusinessException("user_not_found");
        }

        if (user.getPassword() != null && (userDB.getPassword() == null || !user.getPassword().equals(userDB.getPassword()))) {
            throw new BusinessException("invalid_user_password");
        }

        if (user.getIdFacebook() != null && (userDB.getIdFacebook() == null || !user.getIdFacebook().equals(userDB.getIdFacebook()))) {
            throw new BusinessException("invalid_user_password");
        }

        if (user.getIdGoogle() != null && (userDB.getIdGoogle() == null || !user.getIdGoogle().equals(userDB.getIdGoogle()))) {
            throw new BusinessException("invalid_user_password");
        }

        if (user.getIdApple() != null && (userDB.getIdApple() == null || !user.getIdApple().equals(userDB.getIdApple()))) {
            throw new BusinessException("invalid_user_password");
        }

        if (userDB.getStatus() != null && userDB.getStatus().equals(UserStatusEnum.BLOCKED)) {
            throw new BusinessException("user_blocked");
        }

        createToken(userDB);

        return userDB;
    }


    @Override
    public User login(User user) throws Exception {
        return login(user, user.getPassword());
    }


    protected User login(User user, String password) throws Exception {

        User userDB = retrieveByEmail(user.getEmail());

        if (userDB == null || password == null) {
            throw new BusinessException("invalid_user_password");
        }

        if (StringUtils.isNotEmpty(user.getType()) && !user.getType().equals(userDB.getType())) {
            throw new BusinessException("invalid_user_Type");
        }


        if (userDB.getStatus() != null && userDB.getStatus().equals(UserStatusEnum.BLOCKED)) {
            throw new BusinessException("user_blocked");
        }

        String passHash = SecUtils.generateHash(userDB.getSalt(), password);

        if (!userDB.getPassword().equals(passHash)) {

            throw new BusinessException("invalid_user_password");
        } else {

            user = userDB;
            createToken(userDB);
        }

        return user;
    }


    @Override
    public void logout(String idUser) throws Exception {

        User user = retrieve(idUser);
        user.setToken(null);
        user.setTokenExpirationDate(null);

        update(user);
    }


    @Override
    public void updateFromClient(User user) throws Exception {

        if (user.getId() == null) {
            throw new BusinessException("user_id_required");
        }

        User userBase = userDAO.retrieve(user);

        if (userBase == null) {
            throw new BusinessException("user_not_found");
        }

        validateUser(user);

        if (user.getPhone() != null && !userBase.getPhone().equals(user.getPhone())) {
            if (configurationService.loadByCode("CHECK_PHONE") != null && configurationService.loadByCode("CHECK_PHONE").getValueAsBoolean()) {
                confirmUserSMS(user.getId());
            }
        }

        (new BusinessUtils(this.userDAO)).basicSave(user);

    }


    @Override
    public void update(User user) throws Exception {
        userDAO.update(user);

    }


    @Override
    public String updatePassword(User user) throws Exception {

        User userBase = null;

        if (user.getOldPassword() != null) {
            userBase = login(user, user.getOldPassword());
        } else {
            userBase = userDAO.retrieve(new User(user.getId()));
        }

        if (userBase != null) {

            userBase.setSalt(SecUtils.getSalt());
            userBase.setPassword(SecUtils.generateHash(userBase.getSalt(), user.getPassword()));

            userDAO.update(userBase);

        }
        return userBase.getPassword();
    }


    @Override
    public void saveFacebookAvatar(PhotoUpload photoUpload) throws Exception {

        URL url = new URL(photoUpload.getUrl());
        BufferedImage image = ImageIO.read(url);

        Configuration confPath = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);
        File folder = new File(confPath.getValue() + "/user/" + photoUpload.getIdObject());
        folder.mkdirs();

        File destiny = new File(folder, "/original.jpg");
        ImageIO.write(image, "jpg", destiny);

        //escreve a versao reduzida
        Configuration confSize = configurationService.loadByCode("SIZE_DETAIL_MOBILE");

        String reduced = "reduced.jpg";

        FileInputStream fis = new FileInputStream(destiny);
        new ImageUtil().mudarTamanhoImagemProporcinal(reduced, fis, folder, confSize.getValueAsInt(), -1, false);
    }


    @Override
    public String pathImage(String idUser) throws Exception {

        Configuration confPath = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);

        String path = confPath.getValue() + "/user/" + idUser;
        return path;
    }


    @Override
    public List<User> listUserIn(List<String> userIds) throws Exception {

        List<User> users = null;

        if (userIds != null && userIds.size() > 0) {

            Map<String, Object> params = new HashMap<>();
            params.put("in:id", userIds);

            users = userDAO.search(params);
        }

        return users;
    }


    @Override
    public UserCard generateCard(String idUser) throws Exception {

        User user = userDAO.retrieve(new User(idUser));
        UserCard card = generateCard(user);
        return card;
    }


    @Override
    public UserCard generateCard(User user) {

        UserCard card = new UserCard();
        generateCard(user, card);

        return card;
    }


    protected void generateCard(User user, UserCard card) {

        card.setId(user.getId());
        card.setName(user.getName());
        card.setEmail(user.getEmail());
        card.setPhone(user.getPhone());
        card.setIdObj(user.getIdObj());
        card.setNameObj(user.getNameObj());
        card.setCreationDate(user.getCreationDate());
        card.setStatus(user.getStatus());
        card.setType(user.getType());
    }


    @Asynchronous
    @Override
    public void updateStartInfo(UserStartInfo userStartInfo) throws Exception {

        User user = userDAO.retrieve(new User(userStartInfo.getIdUser()));
        user.setLastLogin(new Date());

        if (userStartInfo.getKeyIOS() != null) {
            user.setKeyIOS(userStartInfo.getKeyIOS());
        }

        if (userStartInfo.getKeyAndroid() != null) {
            user.setKeyAndroid(userStartInfo.getKeyAndroid());
        }

        if (userStartInfo.getLanguage() != null) {
            user.setLanguage(userStartInfo.getLanguage());
        }

        if (userStartInfo.getLatitude() != null) {

            if (user.getLastAddress() == null) {
                user.setLastAddress(new AddressInfo());
            }

            user.getLastAddress().setLatitude(userStartInfo.getLatitude());
            user.getLastAddress().setLongitude(userStartInfo.getLongitude());
        }

        userDAO.update(user);
    }

    public User updatePhoneUser(User user) throws Exception {

        User userBase = null;

        userBase = userDAO.retrieve(new User(user.getId()));

        if (userBase != null) {

            userBase.setPhoneNumber(user.getPhoneNumber());

            userBase.setPhone(user.getPhone());

            userDAO.update(userBase);
        }
        return userBase;
    }

    @Override
    public void confirmUserSMS(String idUser) throws Exception {

        User user = retrieve(idUser);

        UserAuthKey key = userAuthKeyService.createKey(idUser, UserAuthKeyTypeEnum.SMS);

        String message = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.confirm.sms", key.getKey());

        notificationService.sendNotification(new NotificationBuilder()
                .setTo(user)
                .setTypeSending(TypeSendingNotificationEnum.SMS)
                .setMessage(message)
                .setFgAlertOnly(true)
                .build());
    }


    @Override
    public void confirmUserEmail(String idUser) throws Exception {

        User user = retrieve(idUser);

        UserAuthKey key = userAuthKeyService.createKey(idUser, UserAuthKeyTypeEnum.EMAIL);

        final String projectName = configurationService.loadByCode("PROJECT_NAME").getValue();

        String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.confirm.email.title", projectName);

        String configKeyLang = user.getLanguage() == null ? "" : "_" + user.getLanguage().toUpperCase();

        final int emailTemplateId = configurationService.loadByCode("USER_EMAIL_CONFIRM_ID" + configKeyLang).getValueAsInt();

        final String msg = "definir";

        final String link = configurationService.loadByCode("USER_EMAIL_CONFIRM_LINK").getValue()
                .replaceAll("__LANGUAGE__", user.getLanguage())
                .replaceAll("__KEY__", key.getKey())
                .replaceAll("__USER__", user.getId())
                .replaceAll("__TYPE__", key.getType().toString());

        sendNotification(user, title, emailTemplateId, link, msg);
    }


    @Override
    public Boolean validateKey(UserAuthKey key) throws Exception {

        boolean validate = userAuthKeyService.validateKey(key);

        if (validate) {
            User user = userDAO.retrieve(new User(key.getIdUser()));
            user.setUserConfirmed(true);

            if (key.getType().equals(UserAuthKeyTypeEnum.EMAIL)) {
                user.setEmailConfirmed(true);
            } else {
                user.setPhoneConfirmed(true);
            }

            userDAO.update(user);
        }

        return validate;
    }


    @Override
    public void forgotPassword(String email) throws Exception {

        User user = retrieveByEmail(email);

        if (user == null) {
            throw new BusinessException("user_not_found");
        }

        UserAuthKey key = userAuthKeyService.createKey(user.getId(), UserAuthKeyTypeEnum.EMAIL);

        if (user.getLanguage() == null) {
            user.setLanguage("PT_BR");
        }

        final String projectName = configurationService.loadByCode("PROJECT_NAME").getValue();

        String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.confirm.email.forgot.title", projectName);

        final int emailTemplateId = configurationService.loadByCode("USER_EMAIL_FORGOT_ID").getValueAsInt();

        String baseLink = "__BASE__/email_forgot_password_user?l=__LANGUAGE__&k=__KEY__&u=__USER__&t=__TYPE__";

        final String msg = "mudar";

        final String link = baseLink
                .replaceAll("__BASE__", configurationService.loadByCode("PROJECT_URL").getValue())
                .replaceAll("__LANGUAGE__", user.getLanguage())
                .replaceAll("__KEY__", key.getKey())
                .replaceAll("__USER__", user.getId())
                .replaceAll("__TYPE__", key.getType().toString());

        sendNotification(user, title, emailTemplateId, link, msg);
    }


    protected void sendNotification(User user, String title, int emailTemplateId, String link, String msg) throws Exception {

        final String projectName = configurationService.loadByCode("PROJECT_NAME").getValue();
        final String projectLogo = configurationService.loadByCode("PROJECT_LOGO_URL").getValue();

        notificationService.sendNotification(new NotificationBuilder()
                .setTo(user)
                .setTypeSending(TypeSendingNotificationEnum.EMAIL)
                .setTitle(title)
                .setFgAlertOnly(true)
                .setEmailDataTemplate(new EmailDataTemplate() {

                    @Override
                    public Integer getTemplateId() {
                        return emailTemplateId;
                    }

                    @Override
                    public Map<String, Object> getData() {

                        Map<String, Object> params = new HashMap<>();
                        params.put("user_name", user.getName());
                        params.put("confirmation_link", link);
                        params.put("msg", msg);
                        params.put("project_name", projectName);
                        params.put("project_logo", projectLogo);
                        return params;
                    }
                })
                .build());
    }


    @Override
    public List<UserCard> searchByName(String name) {

        List<UserCard> list = null;

        List<User> listUsers = userDAO.search(userDAO.createBuilder()
                .appendParamQuery("name", name, OperationEnum.LIKE)
                .setMaxResults(10)
                .build());

        if (listUsers != null) {
            list = new ArrayList<>();

            for (User user : listUsers) {
                list.add(generateCard(user));
            }
        }

        return list;
    }


    @Override
    public List<UserCard> listAll() {

        List<UserCard> list = null;
        List<User> listUsers = userDAO.listAll();

        if (listUsers != null) {
            list = new ArrayList<>();

            for (User user : listUsers) {
                list.add(generateCard(user));
            }
        }

        return list;
    }


    @Override
    public void cancelUser(String idUser) throws Exception {

        User user = retrieve(idUser);
        userFreezerService.moveToFreezer(user);
    }


    @Override
    public Boolean checkToken(String token) throws Exception {

        Boolean validated = true;
        User user = retrieveByToken(token);

        if (user == null || user.getToken() == null || !user.getToken().equals(token) || user.getTokenExpirationDate().before(new Date())) {
            validated = false;
        }

        return validated;
    }


    @Override
    public User retrieveByToken(String token) {

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);

        User user = userDAO.retrieve(params);

        return user;
    }


    protected void createToken(User userDB) throws Exception {

        userDB.setToken(UUID.randomUUID().toString());

        Calendar expCal = Calendar.getInstance();
        expCal.add(Calendar.HOUR, 12);
        userDB.setTokenExpirationDate(expCal.getTime());

        userDAO.update(userDB);
    }


    @Override
    public List<User> customersByRadius(Double latitude, Double longitude, Integer distanceKM) throws Exception {

        List<User> list = userDAO.search(new SearchBuilder()
                .appendParamQuery("lastAddress", new Double[]{latitude, longitude, new Double(distanceKM)},
                        OperationEnum.GEO)
                .build());

        return list;
    }


    @Override
    public void testNotification(String idUser, String msg) throws Exception {

        final String projectName = configurationService.loadByCode("PROJECT_NAME").getValue();
        final String projectLogo = configurationService.loadByCode("PROJECT_LOGO_URL").getValue();

        User user = retrieve(idUser);

        NotificationBuilder builder = new NotificationBuilder()
                .setTo(user)
                .setMessage(msg)
                .setTypeSending(TypeSendingNotificationEnum.APP_EMAIL);

        Configuration configuration = configurationService.loadByCode("MSG_EMAIL");

        if (configuration != null) {
            final int emailTemplateId = configuration.getValueAsInt();

            builder = builder.setEmailDataTemplate(new EmailDataTemplate() {

                @Override
                public Integer getTemplateId() {
                    return emailTemplateId;
                }

                @Override
                public Map<String, Object> getData() {

                    Map<String, Object> params = new HashMap<>();
                    params.put("user_name", user.getName());
                    params.put("msg", msg);
                    params.put("project_name", projectName);
                    params.put("project_logo", projectLogo);

                    return params;
                }
            });
        }

        notificationService.sendNotification(builder.build());
    }


    @Override
    public String pathGallery(String idUser, PhotoUploadTypeEnum photoUploadTypeEnum) throws Exception {

        String path = null;

        if (photoUploadTypeEnum == null) {
            path = configurationService.loadByCode("PATH_BASE").getValue() + "/user/" + idUser + "/gallery";
        } else if (PhotoUploadTypeEnum.IMAGE.equals(photoUploadTypeEnum)) {
            path = configurationService.loadByCode("PATH_BASE").getValue() + "/user/" + idUser + "/gallery/photo";
        } else if (PhotoUploadTypeEnum.VIDEO.equals(photoUploadTypeEnum)) {
            path = configurationService.loadByCode("PATH_BASE").getValue() + "/user/" + idUser + "/gallery/video";
        }

        return path;
    }


    @Override
    public List<User> listByFieldInfo(String field, String value) {

        List<User> listUser = userDAO.listByFieldInfo(field, value);

        return listUser;

    }


    @Override
    public void changeStatus(String idUser) throws Exception {

        User user = retrieve(idUser);

        if (user.getStatus().equals(UserStatusEnum.ACTIVE)) {
            user.setStatus(UserStatusEnum.BLOCKED);
        } else {
            user.setStatus(UserStatusEnum.ACTIVE);
        }

        userDAO.update(user);
    }


    @Override
    public void saveByAdmin(User user) throws Exception {


        boolean sendEmail = user.getId() == null;

        save(user);

        if (sendEmail) {

            if (user.getEmail() == null) {
                throw new BusinessException("missing_user_email");
            }

            UserAuthKey key = userAuthKeyService.createKey(user.getId(), UserAuthKeyTypeEnum.EMAIL);

            final String projectName = configurationService.loadByCode("PROJECT_NAME").getValue();

            String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.register.password.title", projectName);

            final String msg = "definir";

            final int emailTemplateId = configurationService.loadByCode("USER_WELCOME_SET_PASSWORD_ID").getValueAsInt();

            final String link = configurationService.loadByCode("USER_EMAIL_FORGOT_LINK").getValue()
                    .replaceAll("__LANGUAGE__", user.getLanguage())
                    .replaceAll("__KEY__", key.getKey())
                    .replaceAll("__USER__", user.getId())
                    .replaceAll("__TYPE__", key.getType().toString());

            sendNotification(user, title, emailTemplateId, link, msg);
        }
    }

    @Override
    public List<UserCard> search(UserSearch search) {

        SearchBuilder searchBuilder = new SearchBuilder();
        searchBuilder.appendParamQuery("status", UserStatusEnum.ACTIVE);
        if (search.getQueryString() != null && StringUtils.isNotEmpty(search.getQueryString().trim())) {
            searchBuilder.appendParamQuery("name", search.getQueryString());
        }

        if (search.getCode() != null) {
            searchBuilder.appendParamQuery("code", search.getCode());
        }

        searchBuilder.setFirst(TOTAL_PAGE * (search.getPage() - 1));
        searchBuilder.setMaxResults(TOTAL_PAGE);

        //ordena
        List<UserCard> list = null;
        List<User> listUsers = this.userDAO.search(searchBuilder.build());
        if (listUsers != null) {
            list = new ArrayList<>();

            for (User user : listUsers) {
                list.add(generateCard(user));
            }
        }
        return list;


    }

    @Override
    public List<UserCard> listActives() {

        List<User> listUsers;

        SearchBuilder searchBuilder = new SearchBuilder();
        searchBuilder.appendParamQuery("status", UserStatusEnum.ACTIVE);

        listUsers = userDAO.search(searchBuilder.build());
        List<UserCard> list = null;
        if (listUsers != null) {
            list = new ArrayList<>();

            for (User user : listUsers) {
                list.add(generateCard(user));
            }
        }

        return list;
    }


    @Override
    public UserResultSearch searchAdmin(UserSearch userSearch) throws Exception {
        if (userSearch.getPage() == null) {
            throw new BusinessException("missing_page");
        } else {
            SearchBuilder sb = this.userDAO.createBuilder();
            if (userSearch.getStatus() != null) {
                sb.appendParamQuery("status", userSearch.getStatus());
            }

            if (userSearch.getType() != null) {
                sb.appendParamQuery("type", userSearch.getType());
            }

            if (userSearch.getCode() != null) {
                sb.appendParamQuery("code", userSearch.getCode());
            }

            if (userSearch.getTypeIn() != null) {
                sb.appendParamQuery("type", userSearch.getTypeIn(), OperationEnum.IN);
            }

            if (userSearch.getQueryString() != null && !userSearch.getQueryString().isEmpty()) {
                sb.appendParamQuery("name|nameObj", userSearch.getQueryString(), OperationEnum.OR_FIELDS);
            }

            if (userSearch.getCreationDate() != null) {
                sb.appendParamQuery("creationDate", userSearch.getCreationDate(), OperationEnum.GT);
            }


            //sb.setQuery(qb.build());
            if (userSearch.getPageItensNumber() != null && userSearch.getPageItensNumber() > 0) {
                sb.setFirst(userSearch.getPageItensNumber() * (userSearch.getPage() - 1));
                sb.setMaxResults(userSearch.getPageItensNumber());
            } else {
                sb.setFirst(10 * (userSearch.getPage() - 1));
                sb.setMaxResults(10);
            }

            sb.appendSort("name", 1);

            List<User> listUsers = this.userDAO.search(sb.build());
            long totalAmount = this.totalAmount(sb);
            long pageQuantity;
            if (userSearch.getPageItensNumber() != null && userSearch.getPageItensNumber() > 0) {
                pageQuantity = this.pageQuantity(userSearch.getPageItensNumber(), totalAmount);
            } else {
                pageQuantity = this.pageQuantity(10, totalAmount);
            }

            List<UserCard> list = new ArrayList<>();

            if (listUsers != null && !listUsers.isEmpty()) {
                for (User user : listUsers) {
                    list.add(generateCard(user));
                }
            }

            UserResultSearch result = new UserResultSearch();
            result.setList(list);
            result.setTotalAmount(totalAmount);
            result.setPageQuantity(pageQuantity);
            return result;
        }
    }

    protected long totalAmount(SearchBuilder sb) throws Exception {
        long count = this.userDAO.count(sb.build());
        return count;
    }

    protected long pageQuantity(int numberOfItensByPage, long totalAmount) throws Exception {
        long pageQuantity;
        if (totalAmount % numberOfItensByPage != 0) {
            pageQuantity = totalAmount / numberOfItensByPage + 1;
        } else {
            pageQuantity = totalAmount / numberOfItensByPage;
        }

        return pageQuantity;
    }
}
