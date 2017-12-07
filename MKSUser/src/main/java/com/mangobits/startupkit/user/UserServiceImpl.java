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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

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
import com.mangobits.startupkit.core.photo.PhotoUpload;
import com.mangobits.startupkit.core.utils.ImageUtil;
import com.mangobits.startupkit.core.utils.MessageUtils;
import com.mangobits.startupkit.core.utils.SecUtils;
import com.mangobits.startupkit.notification.NotificationBuilder;
import com.mangobits.startupkit.notification.NotificationService;
import com.mangobits.startupkit.notification.TypeSendingNotificationEnum;
import com.mangobits.startupkit.notification.email.data.EmailDataTemplate;
import com.mangobits.startupkit.user.freezer.UserFreezerService;

import net.coobird.thumbnailator.Thumbnails;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserServiceImpl implements UserService {
	
	
	
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
	
	
	
//	@Inject
//	@New
//	private UserInfoDAO userInfoDAO;
	
	
	
	
	@PostConstruct
	public void pos(){
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
	}
	
	
	
	
	
	@Override
	public User retrieveByEmail(String email) throws BusinessException, ApplicationException{
		
		User user = null;
		
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("email", email);
			
			user = userDAO.retrieve(params);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error retrieving an user by email", e);
		}
		
		
		return user;
	}
	
	
	
	
	@Override
	public User retrieveByPhone(Long phoneNumber) throws BusinessException, ApplicationException{
		
		User user = null;
		
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("phoneNumber", phoneNumber);
			
			user = userDAO.retrieve(params);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error retrieving an user by phone", e);
		}
		
		
		return user;
	}
	
	
	
	
	@Override
	public void save(User user) throws BusinessException, ApplicationException {
	
		if(user.getId() == null){
			createNewUser(user);
		}
		else{
			updateFromClient(user);
		}
	}
	
	
	
	@Override
	public User createNewUser(User user) throws BusinessException, ApplicationException {
		
		try {
			
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
				
				if(user.getIdFacebook() != null && !user.getIdFacebook().equals("") && (userDB.getIdFacebook() == null || userDB.getIdFacebook().equals(""))){
					
					userDB.setIdFacebook(user.getIdFacebook());
					userDAO.update(userDB);
					
					return userDB;
				}
				
				if(user.getIdGoogle() != null && !user.getIdGoogle().equals("") && (userDB.getIdGoogle() == null || userDB.getIdGoogle().equals(""))){
					
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
			
			userDAO.insert(user);
			
			sendWelcomeEmail(user);
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error inserting a new user", e);
		}
		
		return user;
	}
	
	
	
	private void sendWelcomeEmail(User user) throws ApplicationException, BusinessException{
		
		try {
			
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

		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error inserting a new user", e);
		}
	}
	
	
	private void validateUser(User user) throws BusinessException, ApplicationException{
		
		try {

			User userDB = null;
			
			if(user.getPhoneNumber() != null){
				
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
			
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error validating an user", e);
		}
	} 
	
	
	
	
	
	@Override
	public User retrieveByIdFacebook(String idFacebook) throws BusinessException, ApplicationException{
		
		User user = null;
		
		try {
			
			user = userDAO.retrieveByIdFacebook(idFacebook);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error retrieving an user by facebook id", e);
		}
		
		return user;
	}

	

	@Override
	public User loginFB(User user) throws BusinessException, ApplicationException{
		
		try {
			
			User userDB = userDAO.retrieveByIdFacebook(user.getIdFacebook());
			
			if(userDB == null){
				
				user = createNewUser(user);
			}
			else{
				
				user = userDB;
				
				createToken(userDB);
			}
		} 
		catch (Exception e) {

			throw new ApplicationException("Got an error log in with facebook", e);
		}
		
		return user;
	}
	
	
	
	
	
	@Override
	public User loginGoogle(User user) throws BusinessException, ApplicationException{
		
		try {
			
			User userDB = userDAO.retrieveByIdGoogle(user.getIdGoogle());
			
			if(userDB == null){
				
				user = createNewUser(user);
			}
			else{
				
				user = userDB;
				
				createToken(userDB);
			}
		} 
		catch (Exception e) {

			throw new ApplicationException("Got an error log in with facebook", e);
		}
		
		return user;
	}
	
	
	
	
	
	
	@Override
	public User retrieve(String id) throws BusinessException, ApplicationException {
		
		User user = null;
		
		try {
			
			user = userDAO.retrieve(new User(id));

		} catch (Exception e) {
			throw new ApplicationException("Got an exception retrieving an user by id", e);
		}
		
		return user;
	}
	
	
	
	@Override
	public User load(String id) throws BusinessException, ApplicationException {
		
		User user = null;
		
		try {
			
			user = userDAO.retrieve(new User(id));
		} catch (Exception e) {
			throw new ApplicationException("Got an exception retrieving an user by id", e);
		}
		
		return user;
	}
	
	
	
	@Override
	public User login(User user) throws BusinessException, ApplicationException{
		
		try {
			
			return login(user, user.getPassword());
		} 
		catch (BusinessException e) {

			throw e;
		}
		catch (Exception e) {

			throw new ApplicationException("Got an error logging an user", e);
		}
	}
	
	
	
	
	private User login(User user, String password) throws BusinessException, ApplicationException{
		
		try {
			
			User userDB = retrieveByEmail(user.getEmail());
			
			if(userDB == null || password == null){
				
				throw new BusinessException("invalid_user_password");
			}
			
			String passHash = SecUtils.generateHash(userDB.getSalt(), password);
			
			if(!userDB.getPassword().equals(passHash)){
				
				throw new BusinessException("invalid_user_password");
			}
			else{
				
				user = userDB;
				
				createToken(userDB);
			}
		} 
		catch (BusinessException e) {

			throw e;
		}
		catch (Exception e) {

			throw new ApplicationException("Got an error logging an user", e);
		}
		
		return user;
	}
	
	

	@Override
	public void logout(String idUser) throws BusinessException, ApplicationException {
		
		try {
			
			User user = load(idUser);
			
			user.setToken(null);
			user.setTokenExpirationDate(null);
			
			update(user);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error logouting an user", e);
		}
	}
	
	

	@Override
	public void updateFromClient(User user) throws BusinessException, ApplicationException {
		
		try {
			
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
			
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error updating an user from client", e);
		}
	}
	
	
	
	
	@Override
	public void update(User user) throws BusinessException, ApplicationException {
		
		try {
			
			userDAO.update(user);
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error updating an user", e);
		}
	}
	
	
	
	@Override
	public void updatePassword(User user) throws BusinessException, ApplicationException {
		
		try {
			
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
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error updating an user", e);
		}
	}
	
	
	
//	@Override
//	public void updateInfo(UserInfo userInfo) throws BusinessException, ApplicationException {
//		
//		try {
//			
//			userInfoDAO.update(userInfo);
//			
//		} catch (Exception e) {
//			throw new ApplicationException("Got an error updating an user info", e);
//		}
//	}





//	@Override
//	public UserInfo retrieveInfo(String id) throws BusinessException, ApplicationException {
//		
//		UserInfo userInfo = null;
//		
//		try {
//			
//			userInfo = userInfoDAO.retrieve(new UserInfo(id));
//			
//		} catch (Exception e) {
//			throw new ApplicationException("Got an error retrieving an user info", e);
//		}
//		
//		return userInfo;
//	}
	
	
	
	@Override
	public void saveFacebookAvatar(PhotoUpload photoUpload) throws BusinessException, ApplicationException{
		
		try {
			
			URL url = new URL(photoUpload.getUrl());
			BufferedImage image = ImageIO.read(url);
			
			Configuration confPath = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);
			File folder = new File(confPath.getValue() + "/user/" + photoUpload.getIdUser());
			folder.mkdirs();
			
			File destiny = new File(folder, "/original.jpg");
			ImageIO.write(image, "jpg", destiny);
			
			//escreve a versao reduzida
			Configuration confSize = configurationService.loadByCode("SIZE_DETAIL_MOBILE");
			
			String reduced = "reduced.jpg";
			
			FileInputStream fis = new FileInputStream(destiny);
			new ImageUtil().mudarTamanhoImagemProporcinal(reduced, fis, folder, confSize.getValueAsInt(), -1, false);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error saving a facebook avatar", e);
		}
	}
	
	
	
	
	
	
	@Override
	public void saveAvatar(PhotoUpload photoUpload) throws BusinessException, ApplicationException{
		
		try {
			
			Configuration confPath = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);
			
			File folder = new File(confPath.getValue() + "/user/" + photoUpload.getIdUser());
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
		
//			new ImageUtil().recortarImagemComAjuste("reduced.jpg", data, folder, confSize.getValueAsInt(), confSize.getValueAsInt(), false, TypeAdjustImageEnum.BASE_HEIGHT, -1);
			
			Thumbnails.of(destiny)
		    	.size(confSize.getValueAsInt(), confSize.getValueAsInt())
		    	.outputFormat("jpg")
		    	.toFile(destinyReduced);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error saving an avatar", e);
		}
	}
	
	
	
	@Override
	public String pathImageAvatar(String idUser) throws BusinessException, ApplicationException{
		
		String path = null;
		
		try {
			
			path = "/user/" + idUser + "/reduced.jpg";
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error getting the user image path", e);
		}
		
		return path;
	}





	@Override
	public User userProfile(String idUserRequest, String idUser, String idTravel) throws BusinessException, ApplicationException {
		
		User user = null;
		
		try {
			
			User userDB = userDAO.retrieve(new User(idUser));
			
			user = userDB;
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error getting the user profile", e);
		}
		
		return user;
	}





	
	
	@Override
	public List<User> listUserIn(List<String> userIds) throws BusinessException, ApplicationException {
		
		List<User> users = null;
		
		try {
			
			if(userIds != null && userIds.size() > 0){
				
				Map<String, Object> params = new HashMap<>();
				params.put("in:id", userIds);
				
				users = userDAO.search(params);
			}
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error searching users by id", e);
		}
		
		return users;
	}	



	@Override
	public UserCard generateCard(String idUser) throws BusinessException, ApplicationException {
		
		UserCard card = null;
		
		try {
			
			User user = userDAO.retrieve(new User(idUser));
			card = generateCard(user);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error creating an user card", e);
		}
		
		return card;
	}

	
	@Override
	public UserCard generateCard(User user) throws BusinessException, ApplicationException {
	
		UserCard card = new UserCard();
		
		try {
			
			generateCard(user, card);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error creating an user card", e);
		}
		
		return card;
	}

	
	
	private void generateCard(User user, UserCard card) throws BusinessException, ApplicationException {
		
		try {
			
			card.setId(user.getId());
			card.setName(user.getName());
			card.setEmail(user.getEmail());
			card.setPhone(user.getPhone());
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error creating an user card", e);
		}
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
	public void confirmUserSMS(String idUser) throws BusinessException, ApplicationException {
		
		try {
			
			User user = load(idUser);
			
			UserAuthKey key = userAuthKeyService.createKey(idUser, UserAuthKeyTypeEnum.SMS);
			
			String message = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.confirm.sms", key.getKey());
			
			notificationService.sendNotification(new NotificationBuilder()
					.setTo(user)
					.setTypeSending(TypeSendingNotificationEnum.SMS)
					.setMessage(message)
					.setFgAlertOnly(true)
					.build());
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error creating a SMS confirmation", e);
		}
	}





	@Override
	public void confirmUserEmail(String idUser) throws BusinessException, ApplicationException {
		
		try {
			
			User user = load(idUser);
			
			UserAuthKey key = userAuthKeyService.createKey(idUser, UserAuthKeyTypeEnum.EMAIL);
			
			String title = MessageUtils.message(LanguageEnum.localeByLanguage(user.getLanguage()), "user.confirm.email.title");
			
			String configKeyLang = user.getLanguage() == null ? "" : "_"+user.getLanguage().toUpperCase(); 
			
			final int emailTemplateId = configurationService.loadByCode("USER_EMAIL_CONFIRM_ID" + configKeyLang).getValueAsInt();
			
			final String link = configurationService.loadByCode("USER_EMAIL_CONFIRM_LINK").getValue()
					.replaceAll("__LANGUAGE__", user.getLanguage())
					.replaceAll("__KEY__", key.getKey())
					.replaceAll("__USER__", user.getId())
					.replaceAll("__TYPE__", key.getType().toString());
			
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
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error an creating an email confirmation", e);
		}
	}





	@Override
	public Boolean validateKey(UserAuthKey key)throws BusinessException, ApplicationException {
		
		boolean validate = false;
		
		try {
			
			validate = userAuthKeyService.validateKey(key);
			
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
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error validating a user", e);
		}
		
		return validate;
	}





	@Override
	public void forgotPassword(String email) throws BusinessException, ApplicationException {
		
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
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error creating a forgot password access", e);
		}
	}





	@Override
	public List<UserCard> searchByName(String name) throws BusinessException, ApplicationException {
		
		List<UserCard> list = null;
		
		try {
			
			Map<String, Object> params = new HashMap<>();
			params.put("like:name", name);
			
			List<User> listUsers = userDAO.search(params, null, 10, 0);
			
			if(listUsers != null){
				list = new ArrayList<>();
				
				for(User user : listUsers){
					list.add(generateCard(user));
				}
			}
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error searching users names", e);
		}
		
		return list;
	}





	@Override
	public List<UserCard> listAll() throws BusinessException, ApplicationException {
		
		List<UserCard> list = null;
		
		try {
			
			List<User> listUsers = userDAO.listAll();
			
			if(listUsers != null){
				list = new ArrayList<>();
				
				for(User user : listUsers){
					list.add(generateCard(user));
				}
			}
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error listing all users cards", e);
		}
		
		return list;
	}





	@Override
	public void cancelUser(String idUser) throws BusinessException, ApplicationException {
		
		try {
			
			User user = retrieve(idUser);
			
			userFreezerService.moveToFreezer(user);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error canceling an user", e);
		}
	}
	
	
	
	@Override
	public Boolean checkToken(String token) throws ApplicationException, BusinessException {
		
		Boolean validated = true;
		
		try {
			
			User user = retrieveByToken(token);
			
			if(user.getToken() == null || !user.getToken().equals(token) || user.getTokenExpirationDate().before(new Date())){
				validated = false;
			}
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error cheking a token", e);
		}
		
		return validated;
	}
	
	
	
	
	@Override
	public User retrieveByToken(String token) throws BusinessException, ApplicationException {
		
		User user = null;
		
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("token", token);
			
			user = userDAO.retrieve(params);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error retrieving a backoffice user by token", e);
		}
		
		return user;
	}
	
	
	
	private void createToken(User userDB) throws ApplicationException, BusinessException{
		
		try {
			
			userDB.setToken(UUID.randomUUID().toString());
			
			Calendar expCal = Calendar.getInstance();
			expCal.add(Calendar.HOUR, 12);
			userDB.setTokenExpirationDate(expCal.getTime());
			
			userDAO.update(userDB);
		} catch (Exception e) {
			throw new ApplicationException("Got an error retrieving creating an user token", e);
		}
	}





	@Override
	public List<User> customersByRadius(Double latitude, Double longitude, Integer distanceKM)
			throws ApplicationException, BusinessException {
	
		List<User> list = null;
		
		try {
			
			list = userDAO.search(new SearchBuilder()
					.appendParam("geo:lastAddress", new Double[]{latitude, longitude, new Double(distanceKM)})
					.build());
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error listing users by radius", e);
		}
		
		return list;
	}





	@Override
	public void testNotification(String idUser, String msg) throws ApplicationException, BusinessException {
		
		try {
			
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
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error sending a test notification", e);
		}
	}

}
