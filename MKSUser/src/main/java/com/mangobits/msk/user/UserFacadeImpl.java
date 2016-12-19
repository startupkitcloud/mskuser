package com.mangobits.msk.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.mangobits.startupkit.core.configuration.Configuration;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.utils.ImageUtil;
import com.mangobits.startupkit.core.utils.PhotoUpload;

import net.coobird.thumbnailator.Thumbnails;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserFacadeImpl implements UserFacade {
	
	
	
	@EJB
	private ConfigurationService configurationService;
	
	
	
	@Inject
	@New
	private UserDAO userDAO;
	
	
	
	@Inject
	@New
	private UserInfoDAO userInfoDAO;
	
	
	
	
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
	public User createNewUser(User user) throws BusinessException, ApplicationException {
		
		try {
			
			User userDB = null;
			
			if(user.getEmail() != null && !user.getEmail().equals("")){
				userDB = retrieveByEmail(user.getEmail());
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
				
				throw new BusinessException("This email is already registred");
			}
			
			user.setCreationDate(new Date());
			user.setId(null);
			
			userDAO.insert(user);
			
			UserInfo userInfo = new UserInfo(user.getId());
			userInfoDAO.insert(userInfo);
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationException("Got an error inserting a new user", e);
		}
		
		return user;
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
			
			User userDB = retrieveByEmail(user.getEmail());
			
			if(userDB == null || !userDB.getPassword().equals(user.getPassword())){
				
				throw new BusinessException("invalid_user_password");
			}
			else{
				
				user = userDB;
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
	public void updateFromClient(User user) throws BusinessException, ApplicationException {
		
		try {
			
			validateUser(user);
			
			userDAO.update(user);
			
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
	public void updateInfo(UserInfo userInfo) throws BusinessException, ApplicationException {
		
		try {
			
			userInfoDAO.update(userInfo);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error updating an user info", e);
		}
	}





	@Override
	public UserInfo retrieveInfo(String id) throws BusinessException, ApplicationException {
		
		UserInfo userInfo = null;
		
		try {
			
			userInfo = userInfoDAO.retrieve(new UserInfo(id));
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error retrieving an user info", e);
		}
		
		return userInfo;
	}
	
	
	
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
			
			card.setIdUser(user.getId());
			card.setNameUser(user.getName());
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error creating an user card", e);
		}
	}




	@Override
	public Integer combinationProfile(User user1, User user2) throws BusinessException, ApplicationException {
		// TODO Auto-generated method stub
		return null;
	}





	@Asynchronous
	@Override
	public void updateStartInfo(UserStartInfo userStartInfo) throws BusinessException, ApplicationException {
		
		try {
			
//			AddressInfo addressInfo = new AddressInfo();
//			addressInfo.setLatitude(userStartInfo.getLatitude());
//			addressInfo.setLongitude(userStartInfo.getLongitude());
			
			UserInfo userInfo = userInfoDAO.retrieve(new UserInfo(userStartInfo.getIdUser()));
			userInfo.setLastLogin(new Date());
//			userInfo.setAddressInfo(addressInfo);
			
			userInfoDAO.update(userInfo);
			
			User user = userDAO.retrieve(new User(userStartInfo.getIdUser()));
			user.setKeyIOS(userStartInfo.getKeyIOS());
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error updating an user info", e);
		}
	}
}
