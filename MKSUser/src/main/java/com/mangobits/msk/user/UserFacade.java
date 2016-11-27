package com.mangobits.msk.user;

import java.util.List;

import javax.ejb.Local;

import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.utils.PhotoUpload;


@Local
public interface UserFacade {

	
	
	User loginFB(User user) throws BusinessException, ApplicationException;
	
	
	User loginGoogle(User user) throws BusinessException, ApplicationException;
	
	
	User login(User user) throws BusinessException, ApplicationException;
	
	
	User createNewUser(User user) throws BusinessException, ApplicationException;
	
	
	void updateFromClient(User user) throws BusinessException, ApplicationException;
	
	
	void update(User user) throws BusinessException, ApplicationException;
	
	
	void updateInfo(UserInfo userInfo) throws BusinessException, ApplicationException;
	
	
	User retrieveByEmail(String email) throws BusinessException, ApplicationException;
	
	
	User retrieveByIdFacebook(String idFB) throws BusinessException, ApplicationException;
	
	
	User retrieve(String id) throws BusinessException, ApplicationException;
	
	
	User load(String id) throws BusinessException, ApplicationException;
	
	
	UserInfo retrieveInfo(String id) throws BusinessException, ApplicationException;
	
	
	void saveFacebookAvatar(PhotoUpload photoUpload) throws BusinessException, ApplicationException;
	
	
	void saveAvatar(PhotoUpload photoUpload) throws BusinessException, ApplicationException;
	
	
	String pathImageAvatar(String idUser) throws BusinessException, ApplicationException;
	
	
	User userProfile(String idUserRequest, String idUser, String idTravel) throws BusinessException, ApplicationException;
	
	
	List<User> listUserIn(List<String> userIds) throws BusinessException, ApplicationException;
	
	
	UserCard generateCard(String idUser) throws BusinessException, ApplicationException;
	
	
	UserCard generateCard(User user) throws BusinessException, ApplicationException;
	
	
	Integer combinationProfile(User user1, User user2) throws BusinessException, ApplicationException;
	
	
	void updateStartInfo(UserStartInfo userStartInfo) throws BusinessException, ApplicationException;
}
