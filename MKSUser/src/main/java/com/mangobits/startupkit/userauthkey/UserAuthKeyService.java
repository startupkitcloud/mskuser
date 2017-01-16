package com.mangobits.startupkit.userauthkey;

import javax.ejb.Local;

import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;

@Local
public interface UserAuthKeyService {

	
	UserAuthKey createKey(String idUser, UserAuthKeyTypeEnum type) throws BusinessException, ApplicationException;
	
	
	Boolean validateKey(UserAuthKey key) throws BusinessException, ApplicationException;
}
