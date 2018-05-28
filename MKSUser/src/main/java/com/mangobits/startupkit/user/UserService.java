package com.mangobits.startupkit.user;

import com.mangobits.startupkit.authkey.UserAuthKey;
import com.mangobits.startupkit.core.photo.PhotoUpload;
import com.mangobits.startupkit.core.photo.PhotoUploadTypeEnum;

import javax.ejb.Local;
import java.util.List;


@Local
public interface UserService {


	User loginFB(User user) throws Exception;
	
	
	User loginGoogle(User user) throws Exception;
	
	
	User login(User user) throws Exception;


	User autoLogin(String id, String password) throws Exception;
	
	
	void logout(String idUser) throws Exception;
	
	
	User createNewUser(User user) throws Exception;
	
	
	void updateFromClient(User user) throws Exception;
	
	
	void save(User user) throws Exception;
	
	
	void update(User user) throws Exception;
	
	
	void updatePassword(User user) throws Exception;
	
	
	User retrieveByEmail(String email) throws Exception;
	
	
	User retrieveByPhone(Long phoneNumber) throws Exception;
	
	
	User retrieveByIdFacebook(String idFB) throws Exception;
	
	
	User retrieve(String id) throws Exception;
	
	
	void saveFacebookAvatar(PhotoUpload photoUpload) throws Exception;
	

	@Deprecated
	void saveAvatar(PhotoUpload photoUpload) throws Exception;


	String pathImage(String idUser) throws Exception;
	

	@Deprecated
	String pathImageAvatar(String idUser) throws Exception;
	
	
	List<User> listUserIn(List<String> userIds) throws Exception;
	
	
	UserCard generateCard(String idUser) throws Exception;
	
	
	UserCard generateCard(User user) throws Exception;
	
	
	void updateStartInfo(UserStartInfo userStartInfo) throws Exception;
	
	
	void confirmUserSMS(String idUser) throws Exception;
	
	
	void confirmUserEmail(String idUser) throws Exception;
	
	
	Boolean validateKey(UserAuthKey key) throws Exception;
	
	
	void forgotPassword(String email) throws Exception;
	
	
	List<UserCard> searchByName(String name) throws Exception;
	
	
	List<UserCard> listAll() throws Exception;
	
	
	void cancelUser(String idUser) throws Exception;
	
	
	Boolean checkToken(String token) throws Exception;
	
	
	User retrieveByToken(String token) throws Exception;
	
	
	List<User> customersByRadius(Double latitude, Double longitude, Integer distanceKM) throws Exception;
	
	
	void testNotification(String idUser, String msg) throws Exception;


	@Deprecated
	void saveGallery(PhotoUpload photoUpload) throws Exception;


	@Deprecated
	String pathGallery(String idUser, PhotoUploadTypeEnum photoUploadTypeEnum) throws Exception;


	void changeStatus(String idUser) throws Exception;


	List<User> listByFieldInfo(String field, String value) throws Exception;
}
