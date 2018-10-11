package com.mangobits.startupkit.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.DELETE;

import com.mangobits.startupkit.core.status.SimpleStatusEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.mangobits.startupkit.authkey.UserAuthKey;
import com.mangobits.startupkit.authkey.UserAuthKeyService;
import com.mangobits.startupkit.authkey.UserAuthKeyTypeEnum;
import com.mangobits.startupkit.core.address.AddressInfo;
import com.mangobits.startupkit.core.configuration.Configuration;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.dao.SearchBuilder;
import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.photo.InfoUrl;
import com.mangobits.startupkit.core.photo.PhotoUpload;
import com.mangobits.startupkit.core.photo.PhotoUploadStatusEnum;
import com.mangobits.startupkit.core.photo.PhotoUploadTypeEnum;
import com.mangobits.startupkit.core.photo.PhotoUtils;
import com.mangobits.startupkit.core.utils.ImageUtil;
import com.mangobits.startupkit.core.utils.MessageUtils;
import com.mangobits.startupkit.core.utils.SecUtils;
import com.mangobits.startupkit.notification.NotificationBuilder;
import com.mangobits.startupkit.notification.NotificationService;
import com.mangobits.startupkit.notification.TypeSendingNotificationEnum;
import com.mangobits.startupkit.notification.email.data.EmailDataTemplate;
import com.mangobits.startupkit.user.freezer.UserFreezerService;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;


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
	public void pos(){
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
	}
	
	
	
	@Override
	public User retrieveByEmail(String email) throws Exception{

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", email);

		User user = userDAO.retrieve(params);
		
		return user;
	}
	

	@Override
	public User retrieveByPhone(Long phoneNumber) throws Exception{

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phoneNumber", phoneNumber);

		User user = userDAO.retrieve(params);

		return user;
	}

	@Override
	public User saveByPhone(User user) throws Exception{

		if (user.getPhoneNumber() == null){
			throw new BusinessException("missing_phone");
		}

		User userBase = retrieveByPhone(user.getPhoneNumber());

		if (userBase == null){
			user = createNewUser(user);
			return user;
		}else {
			return  userBase;
		}
	}
	

	@Override
	public void save(User user) throws Exception {
	
		if(user.getId() == null){
			createNewUser(user);
		}
		else{
			updateFromClient(user);
		}
	}
	
	
	
	@Override
	public User createNewUser(User user) throws Exception {

		User userDB = null;
		boolean fgPhoneError = false;

		if(user.getEmail() != null && !user.getEmail().equals("")){
			userDB = retrieveByEmail(user.getEmail());
		}

		if(userDB == null && user.getPhoneNumber() != null && user.getPhoneNumber() != 0){
			userDB = retrieveByPhone(user.getPhoneNumber());
			fgPhoneError = true;
		}

		if(userDB != null){

			if(user.getIdFacebook() != null && !user.getIdFacebook().equals("") && (userDB.getIdFacebook() == null
					|| userDB.getIdFacebook().equals(""))){

				userDB.setIdFacebook(user.getIdFacebook());
				userDAO.update(userDB);

				return userDB;
			}

			if(user.getIdGoogle() != null && !user.getIdGoogle().equals("") && (userDB.getIdGoogle() == null
					|| userDB.getIdGoogle().equals(""))){

				userDB.setIdGoogle(user.getIdGoogle());
				userDAO.update(userDB);

				return userDB;
			}

			if(fgPhoneError){
				throw new BusinessException("phone_exists");
			}
			else{
				throw new BusinessException("email_exists");
			}
		}

		user.setCreationDate(new Date());
		user.setId(null);

		if(user.getPassword() != null){
			user.setSalt(SecUtils.getSalt());
			user.setPassword(SecUtils.generateHash(user.getSalt(), user.getPassword()));
		}

		if(user.getStatus() == null){
			user.setStatus(UserStatusEnum.ACTIVE);
		}

		userDAO.insert(user);

		sendWelcomeEmail(user);
		
		return user;
	}
	
	
	
	private void sendWelcomeEmail(User user) throws Exception{

		String configKeyLang = user.getLanguage() == null ? "" : "_"+user.getLanguage().toUpperCase();

		Configuration confEmailId = configurationService.loadByCode("EMAIL_USER_WELCOME" + configKeyLang);

		if(confEmailId != null){

			String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.email.welcome.title");

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
						public Map<String, String> getData() {

							Map<String, String> params = new HashMap<>();
							params.put("user_name", user.getName());

							return params;
						}
					})
					.build());
		}
	}
	
	
	private void validateUser(User user) throws Exception{

		User userDB = null;

		if(user.getPhoneNumber() != null && user.getPhoneNumber() != 0){

			Map<String, Object> params = new HashMap<>();
			params.put("phoneNumber", user.getPhoneNumber());

			userDB = userDAO.retrieve(params);

			if(userDB != null && !userDB.getId().equals(user.getId())){
				throw new BusinessException("phone_exists");
			}
		}

		if(user.getEmail() != null){

			Map<String, Object> params = new HashMap<>();
			params.put("email", user.getEmail());

			userDB = userDAO.retrieve(params);

			if(userDB != null && !userDB.getId().equals(user.getId())){
				throw new BusinessException("email_exists");
			}
		}
	} 
	

	@Override
	public User retrieveByIdFacebook(String idFacebook) throws Exception{
		
		User user = userDAO.retrieveByIdFacebook(idFacebook);
		return user;
	}

	

	@Override
	public User loginFB(User user) throws Exception{
		User userDB = userDAO.retrieveByIdFacebook(user.getIdFacebook());
		user = loginSocial(user, userDB);
		return user;
	}

	
	@Override
	public User loginGoogle(User user) throws Exception{
		User userDB = userDAO.retrieveByIdGoogle(user.getIdGoogle());
		user = loginSocial(user, userDB);
		return user;
	}


	private User loginSocial(User user, User userDB) throws Exception {

		if(userDB == null){
			user = createNewUser(user);
		}
		else{
			user = userDB;

			if(userDB.getStatus() != null && userDB.getStatus().equals(UserStatusEnum.BLOCKED)){
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

		if(userDB == null || (user.getPassword() == null && user.getIdFacebook() == null && user.getIdGoogle() == null)){
			throw new BusinessException("user_not_found");
		}

		if(user.getPassword() != null && (userDB.getPassword() == null || !user.getPassword().equals(userDB.getPassword()))){
			throw new BusinessException("invalid_user_password");
		}

		if(user.getIdFacebook() != null && (userDB.getIdFacebook() == null || !user.getIdFacebook().equals(userDB.getIdFacebook()))){
			throw new BusinessException("invalid_user_password");
		}

		if(user.getIdGoogle() != null && (userDB.getIdGoogle() == null || !user.getIdGoogle().equals(userDB.getIdGoogle()))){
			throw new BusinessException("invalid_user_password");
		}

		if(userDB.getStatus() != null && userDB.getStatus().equals(UserStatusEnum.BLOCKED)){
			throw new BusinessException("user_blocked");
		}

		createToken(userDB);

		return userDB;
	}
	
	
	@Override
	public User login(User user) throws Exception{
		return login(user, user.getPassword());
	}
	


	private User login(User user, String password) throws Exception{

		User userDB = retrieveByEmail(user.getEmail());

		if(userDB == null || password == null){
			throw new BusinessException("invalid_user_password");
		}

		if(StringUtils.isNotEmpty(user.getType()) && !user.getType().equals(userDB.getType())){
			throw new BusinessException("invalid_user_Type");
		}


		if(userDB.getStatus() != null && userDB.getStatus().equals(UserStatusEnum.BLOCKED)){
			throw new BusinessException("user_blocked");
		}

		String passHash = SecUtils.generateHash(userDB.getSalt(), password);

		if(!userDB.getPassword().equals(passHash)){

			throw new BusinessException("invalid_user_password");
		}
		else{

			user = userDB;
			createToken(userDB);
		}
		
		return user;
	}
	
	

	@Override
	public void logout(String idUser) throws Exception{

		User user = retrieve(idUser);
		user.setToken(null);
		user.setTokenExpirationDate(null);

		update(user);
	}
	
	

	@Override
	public void updateFromClient(User user) throws Exception {

		if(user.getId() == null){
			throw new BusinessException("user_id_required");
		}

		User userBase = userDAO.retrieve(user);

		if(userBase == null){
			throw new BusinessException("user_not_found");
		}

		validateUser(user);

		if(userBase != null){

			if(user.getBirthDate() != null){
				userBase.setBirthDate(user.getBirthDate());
			}

			if(user.getEmail() != null){
				userBase.setEmail(user.getEmail());
			}

			if(user.getType() != null){
				userBase.setType(user.getType());
			}

			if(user.getGender() != null){
				userBase.setGender(user.getGender());
			}

			if(user.getLanguage() != null){
				userBase.setLanguage(user.getLanguage());
			}

			if(user.getName() != null){
				userBase.setName(user.getName());
			}

			if(user.getInfo() != null){
				userBase.setInfo(user.getInfo());
			}

			if(user.getPassword() != null && user.getPassword().length() != 88){

				userBase.setSalt(SecUtils.getSalt());
				userBase.setPassword(SecUtils.generateHash(userBase.getSalt(), user.getPassword()));
			}

			if(user.getPhone() != null){
				userBase.setPhone(user.getPhone());
			}

			if(user.getPhoneCountryCode() != null){
				userBase.setPhoneCountryCode(user.getPhoneCountryCode());
			}

			if(user.getPhoneNumber() != null){
				userBase.setPhoneNumber(user.getPhoneNumber());
			}

			if(user.getKeyAndroid() != null){
				userBase.setKeyAndroid(user.getKeyAndroid());
			}

			if(user.getKeyIOS() != null){
				userBase.setKeyIOS(user.getKeyIOS());
			}

			if(user.getIdFacebook() != null){
				userBase.setIdFacebook(user.getIdFacebook());
			}

			if(user.getIdGoogle() != null){
				userBase.setIdGoogle(user.getIdGoogle());
			}

			userDAO.update(userBase);
		}
	}
	

	@Override
	public void update(User user) throws Exception {
		userDAO.update(user);
	}
	

	@Override
	public void updatePassword(User user) throws Exception {

		User userBase = null;

		if(user.getOldPassword() != null){
			userBase = login(user, user.getOldPassword());
		}
		else{
			userBase = userDAO.retrieve(new User(user.getId()));
		}

		if(userBase != null){

			userBase.setSalt(SecUtils.getSalt());
			userBase.setPassword(SecUtils.generateHash(userBase.getSalt(), user.getPassword()));

			userDAO.update(userBase);
		}
	}


	@Override
	public void saveFacebookAvatar(PhotoUpload photoUpload) throws Exception{

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
	

	@Deprecated
	@Override
	public void saveAvatar(PhotoUpload photoUpload) throws Exception{

		Configuration confPath = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);

		File folder = new File(confPath.getValue() + "/user/" + photoUpload.getIdObject());
		folder.mkdirs();

		File destiny = new File(folder, "/original.jpg");

		//processa a imagem
		byte[] data = Base64.decodeBase64(photoUpload.getPhoto());
		//escreve o original do disco
		FileOutputStream output = new FileOutputStream(destiny);
		IOUtils.write(data, output);

		//escreve a versao reduzida
		Configuration confSize = configurationService.loadByCode("SIZE_DETAIL_MOBILE");
		File destinyReduced = new File(folder, "/reduced.jpg");

		Thumbnails.of(destiny)
				.size(confSize.getValueAsInt(), confSize.getValueAsInt())
				.outputFormat("jpg")
				.toFile(destinyReduced);
	}


	@Deprecated
	@Override
	public void saveGallery(PhotoUpload photoUpload) throws Exception{

		User user = retrieve(photoUpload.getIdObject());

		if(user == null){
			throw new BusinessException("User with id  '" + photoUpload.getIdObject() + "' not found to attach PhotoUpload");
		}else if(photoUpload.getType() == null){
			throw new BusinessException("TypeFile is required");
		}

		if(photoUpload.getStatus() == null || !photoUpload.getStatus().equals(PhotoUploadStatusEnum.BLOCKED)){

			if(user.getListPhotoUpload() == null){
				user.setListPhotoUpload(new ArrayList<>());
			}

			createGalleryPhotoUpload(photoUpload, user);

		}else{

			PhotoUpload photoUploadBase = user.getListPhotoUpload().stream()
					.filter(p -> p.getIndex().equals(photoUpload.getIndex()))
					.findFirst()
					.orElse(null);

			if(photoUploadBase != null){
				user.getListPhotoUpload().remove(photoUploadBase);
			}
		}

		save(user);
	}

	@Deprecated
	private void createGalleryPhotoUpload (PhotoUpload photoUpload, User user) throws Exception {
		
		//get the final size
        int finalWidth = configurationService.loadByCode("SIZE_DETAIL_MOBILE").getValueAsInt();
        photoUpload.setFinalWidth(finalWidth);
        
        String idPhoto = photoUpload.getId() != null ? photoUpload.getId() : UUID.randomUUID().toString();
        photoUpload.setId(idPhoto);
        
        if(photoUpload.getIndex() == null){
        	photoUpload.setIndex(createIndexPhotoUpload(user));
        }
		
        if(!photoUpload.getType().equals(PhotoUploadTypeEnum.YOUTUBE)){
        	
        	String path = pathGallery(photoUpload.getIdObject(), photoUpload.getType());
    		
    		if(photoUpload.getType().equals(PhotoUploadTypeEnum.IMAGE)){
    			new PhotoUtils().saveImage(photoUpload, path, idPhoto);
    		}else if(photoUpload.getType().equals(PhotoUploadTypeEnum.VIDEO)){
    			new PhotoUtils().saveVideo(photoUpload, path, idPhoto);
    		}
        }else{
        	InfoUrl infoUrl = new PhotoUtils().createInfoUrlYoutube(photoUpload.getUrl());
        	photoUpload.setInfoUrl(infoUrl);
        }
        
        
		PhotoUpload photoUploadBase = user.getListPhotoUpload().stream()
				.filter(p -> p.getIndex().equals(photoUpload.getIndex()))
				.findFirst()
				.orElse(null);
		
		if(photoUploadBase != null){
			user.getListPhotoUpload().remove(photoUploadBase);
		}
		
		if(photoUpload.getStatus() == null){
			photoUpload.setStatus(PhotoUploadStatusEnum.ACTIVE);
		}
		
		user.getListPhotoUpload().add(photoUpload);
	}
	

	@Deprecated
	private int createIndexPhotoUpload(User user){
		
		int index = 0;
		
		if(CollectionUtils.isNotEmpty(user.getListPhotoUpload())){
			
			for(PhotoUpload photoUpload : user.getListPhotoUpload()){
				index = index > photoUpload.getIndex() ? index : photoUpload.getIndex() +1;
			}
		}
		
		return index;
	}
	

	@Deprecated
	@Override
	public String pathImageAvatar(String idUser){
		
		String path = "/user/" + idUser + "/reduced.jpg";
		return path;
	}



	@Override
	public String pathImage(String idUser){

		String path = "/user/" + idUser;
		return path;
	}

	
	
	@Override
	public List<User> listUserIn(List<String> userIds) throws Exception {
		
		List<User> users = null;

		if(userIds != null && userIds.size() > 0){

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
	public UserCard generateCard(User user){
	
		UserCard card = new UserCard();
		generateCard(user, card);

		return card;
	}

	
	
	private void generateCard(User user, UserCard card){

		card.setId(user.getId());
		card.setName(user.getName());
		card.setEmail(user.getEmail());
		card.setPhone(user.getPhone());
	}



	@Asynchronous
	@Override
	public void updateStartInfo(UserStartInfo userStartInfo) throws Exception {
		
		User user = userDAO.retrieve(new User(userStartInfo.getIdUser()));
		user.setLastLogin(new Date());
		
		if(userStartInfo.getKeyIOS() != null){
			user.setKeyIOS(userStartInfo.getKeyIOS());
		}
		
		if(userStartInfo.getKeyAndroid() != null){
			user.setKeyAndroid(userStartInfo.getKeyAndroid());
		}
		
		if(userStartInfo.getLanguage() != null){
			user.setLanguage(userStartInfo.getLanguage());
		}
		
		if(userStartInfo.getLatitude() != null){
			
			if(user.getLastAddress() == null){
				user.setLastAddress(new AddressInfo());
			}
			
			user.getLastAddress().setLatitude(userStartInfo.getLatitude());
			user.getLastAddress().setLongitude(userStartInfo.getLongitude());
		}
		
		userDAO.update(user);
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

		String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.confirm.email.title");

		String configKeyLang = user.getLanguage() == null ? "" : "_"+user.getLanguage().toUpperCase();

		final int emailTemplateId = configurationService.loadByCode("USER_EMAIL_CONFIRM_ID" + configKeyLang).getValueAsInt();

		final String link = configurationService.loadByCode("USER_EMAIL_CONFIRM_LINK").getValue()
				.replaceAll("__LANGUAGE__", user.getLanguage())
				.replaceAll("__KEY__", key.getKey())
				.replaceAll("__USER__", user.getId())
				.replaceAll("__TYPE__", key.getType().toString());

		sendNotification(user, title, emailTemplateId, link);
	}



	@Override
	public Boolean validateKey(UserAuthKey key)throws Exception {
		
		boolean validate = userAuthKeyService.validateKey(key);

		if(validate){
			User user = userDAO.retrieve(new User(key.getIdUser()));
			user.setUserConfirmed(true);

			if(key.getType().equals(UserAuthKeyTypeEnum.EMAIL)){
				user.setEmailConfirmed(true);
			}
			else{
				user.setPhoneConfirmed(true);
			}

			userDAO.update(user);
		}
		
		return validate;
	}


	@Override
	public void forgotPassword(String email) throws Exception {
		
		try {
			
			User user = retrieveByEmail(email);
			
			if(user == null){
				throw new BusinessException("user_not_found");
			}
			
			UserAuthKey key = userAuthKeyService.createKey(user.getId(), UserAuthKeyTypeEnum.EMAIL);
			
			String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.confirm.email.forgot.title");
			
			String configKeyLang = user.getLanguage() == null ? "" : "_"+user.getLanguage().toUpperCase(); 
			
			final int emailTemplateId = configurationService.loadByCode("USER_EMAIL_FORGOT_ID" + configKeyLang).getValueAsInt();
			
			final String link = configurationService.loadByCode("USER_EMAIL_FORGOT_LINK").getValue()
					.replaceAll("__LANGUAGE__", user.getLanguage())
					.replaceAll("__KEY__", key.getKey())
					.replaceAll("__USER__", user.getId())
					.replaceAll("__TYPE__", key.getType().toString());

			sendNotification(user, title, emailTemplateId, link);

		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error creating a forgot password access", e);
		}
	}


	private void sendNotification(User user, String title, int emailTemplateId, String link) throws Exception {
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
					public Map<String, String> getData() {

						Map<String, String> params = new HashMap<>();
						params.put("user_name", user.getName());
						params.put("confirmation_link", link);

						return params;
					}
				})
				.build());
	}


	@Override
	public List<UserCard> searchByName(String name) throws Exception {
		
		List<UserCard> list = null;

		Map<String, Object> params = new HashMap<>();
		params.put("like:name", name);

		List<User> listUsers = userDAO.search(params, null, 10, 0);

		if(listUsers != null){
			list = new ArrayList<>();

			for(User user : listUsers){
				list.add(generateCard(user));
			}
		}
		
		return list;
	}



	@Override
	public List<UserCard> listAll() throws Exception {
		
		List<UserCard> list = null;
		List<User> listUsers = userDAO.listAll();

		if(listUsers != null){
			list = new ArrayList<>();

			for(User user : listUsers){
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

		if(user == null || user.getToken() == null || !user.getToken().equals(token) || user.getTokenExpirationDate().before(new Date())){
			validated = false;
		}
		
		return validated;
	}
	
	

	@Override
	public User retrieveByToken(String token) throws Exception {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);

		User user = userDAO.retrieve(params);
		
		return user;
	}
	
	
	
	private void createToken(User userDB) throws Exception{

		userDB.setToken(UUID.randomUUID().toString());

		Calendar expCal = Calendar.getInstance();
		expCal.add(Calendar.HOUR, 12);
		userDB.setTokenExpirationDate(expCal.getTime());

		userDAO.update(userDB);
	}



	@Override
	public List<User> customersByRadius(Double latitude, Double longitude, Integer distanceKM) throws Exception {
	
		List<User> list = userDAO.search(new SearchBuilder()
				.appendParam("geo:lastAddress", new Double[]{latitude, longitude, new Double(distanceKM)})
				.build());
		
		return list;
	}



	@Override
	public void testNotification(String idUser, String msg) throws Exception {

		User user = retrieve(idUser);

		NotificationBuilder builder = new NotificationBuilder()
				.setTo(user)
				.setMessage(msg)
				.setTypeSending(TypeSendingNotificationEnum.APP_EMAIL);

		Configuration configuration = configurationService.loadByCode("MSG_EMAIL");

		if(configuration != null){
			final int emailTemplateId = configuration.getValueAsInt();

			builder = builder.setEmailDataTemplate(new EmailDataTemplate() {

				@Override
				public Integer getTemplateId() {
					return emailTemplateId;
				}

				@Override
				public Map<String, String> getData() {

					Map<String, String> params = new HashMap<>();
					params.put("user_name", user.getName());
					params.put("msg", msg);

					return params;
				}
			});
		}

		notificationService.sendNotification(builder.build());
	}
	


	@Override
	public String pathGallery(String idUser, PhotoUploadTypeEnum photoUploadTypeEnum) throws Exception {
		
		String path = null;

		if(photoUploadTypeEnum == null){
			path = configurationService.loadByCode("PATH_BASE").getValue() + "/user/" + idUser + "/gallery";
		}else if(PhotoUploadTypeEnum.IMAGE.equals(photoUploadTypeEnum)){
			path = configurationService.loadByCode("PATH_BASE").getValue() + "/user/" + idUser + "/gallery/photo";
		}else if(PhotoUploadTypeEnum.VIDEO.equals(photoUploadTypeEnum)){
			path = configurationService.loadByCode("PATH_BASE").getValue() + "/user/" + idUser + "/gallery/video";
		}

		return path;
	}
	
	
	@Override
	public List<User> listByFieldInfo(String field, String value) throws Exception {
		
		List<User> listUser = userDAO.listByFieldInfo(field, value);

		return listUser;
		
	}
	

	@Override
	public void changeStatus(String idUser) throws Exception {

		User user = retrieve(idUser);

		if(user.getStatus().equals(UserStatusEnum.ACTIVE)){
			user.setStatus(UserStatusEnum.BLOCKED);
		}
		else{
			user.setStatus(UserStatusEnum.ACTIVE);
		}

		userDAO.update(user);
	}


	@Override
	public void saveByAdmin(User user) throws Exception {


		boolean sendEmail = user.getId() == null;

		save(user);

//		Thread.sleep(2000);

		if(sendEmail){

			if (user.getEmail() == null){
				throw new BusinessException("missing_user_email");
			}

			//forgotPassword(user);
			UserAuthKey key = userAuthKeyService.createKey(user.getId(), UserAuthKeyTypeEnum.EMAIL);

			String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.register.password.title");

			String configKeyLang = user.getLanguage() == null ? "" : "_"+user.getLanguage().toUpperCase();

			final int emailTemplateId = configurationService.loadByCode("USER_EMAIL_FORGOT_ID" + configKeyLang).getValueAsInt();

			final String link = configurationService.loadByCode("USER_EMAIL_FORGOT_LINK").getValue()
					.replaceAll("__LANGUAGE__", user.getLanguage())
					.replaceAll("__KEY__", key.getKey())
					.replaceAll("__USER__", user.getId())
					.replaceAll("__TYPE__", key.getType().toString());

			sendNotification(user, title, emailTemplateId, link);
		}
	}

	@Override
	public List<UserCard> search(UserSearch search) throws Exception {

		SearchBuilder searchBuilder = new SearchBuilder();
		searchBuilder.appendParam("status", UserStatusEnum.ACTIVE);
		if (search.getQueryString() != null && StringUtils.isNotEmpty(search.getQueryString().trim())) {
			searchBuilder.appendParam("name", search.getQueryString());
		}
		searchBuilder.setFirst(TOTAL_PAGE * (search.getPage() -1));
		searchBuilder.setMaxResults(TOTAL_PAGE);
//		Sort sort = new Sort(new SortField("creationDate", SortField.Type.LONG, true));
//		searchBuilder.setSort(sort);

		//ordena
		List<UserCard> list = null;
		List<User> listUsers = this.userDAO.search(searchBuilder.build());
		if(listUsers != null){
			list = new ArrayList<>();

			for(User user : listUsers){
				list.add(generateCard(user));
			}
		}
		return list;
	}

}
