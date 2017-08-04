package com.mangobits.startupkit.user;

import java.util.List;

import javax.ejb.Local;

import com.mangobits.startupkit.authkey.UserAuthKey;
import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.photo.PhotoUpload;


@Local
public interface UserService {

	
	
	User loginFB(User user) throws BusinessException, ApplicationException;
	
	
	User loginGoogle(User user) throws BusinessException, ApplicationException;
	
	
	User login(User user) throws BusinessException, ApplicationException;
	
	
	User createNewUser(User user) throws BusinessException, ApplicationException;
	
	
	void updateFromClient(User user) throws BusinessException, ApplicationException;
	
	
	void save(User user) throws BusinessException, ApplicationException;
	
	
	void update(User user) throws BusinessException, ApplicationException;
	
	
	void updatePassword(User user) throws BusinessException, ApplicationException;
	
	
	User retrieveByEmail(String email) throws BusinessException, ApplicationException;
	
	
	User retrieveByPhone(Long phoneNumber) throws BusinessException, ApplicationException;
	
	
	User retrieveByIdFacebook(String idFB) throws BusinessException, ApplicationException;
	
	
	User retrieve(String id) throws BusinessException, ApplicationException;
	
	
	User load(String id) throws BusinessException, ApplicationException;
	
	
	void saveFacebookAvatar(PhotoUpload photoUpload) throws BusinessException, ApplicationException;
	
	
	void saveAvatar(PhotoUpload photoUpload) throws BusinessException, ApplicationException;
	
	
	String pathImageAvatar(String idUser) throws BusinessException, ApplicationException;
	
	
	User userProfile(String idUserRequest, String idUser, String idTravel) throws BusinessException, ApplicationException;
	
	
	List<User> listUserIn(List<String> userIds) throws BusinessException, ApplicationException;
	
	
	UserCard generateCard(String idUser) throws BusinessException, ApplicationException;
	
	
	UserCard generateCard(User user) throws BusinessException, ApplicationException;
	
	
	void updateStartInfo(UserStartInfo userStartInfo) throws Exception;
	
	
	void confirmUserSMS(String idUser) throws BusinessException, ApplicationException;
	
	
	void confirmUserEmail(String idUser) throws BusinessException, ApplicationException;
	
	
	Boolean validateKey(UserAuthKey key) throws BusinessException, ApplicationException;
	
	
	void forgotPassword(String email) throws BusinessException, ApplicationException;
	
	
	List<UserCard> searchByName(String name) throws BusinessException, ApplicationException;
	
	
	List<UserCard> listAll() throws BusinessException, ApplicationException;
	
	
	void cancelUser(String idUser) throws BusinessException, ApplicationException;
	
	
	Boolean checkToken(String token) throws ApplicationException, BusinessException;
	
	
	User retrieveByToken(String token) throws BusinessException, ApplicationException;
	
	
	List<User> customersByRadius(Double latitude, Double longitude, Integer distanceKM) throws ApplicationException, BusinessException;

}
