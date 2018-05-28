package com.mangobits.startupkit.user.freezer;

import javax.ejb.Local;

import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.user.User;

@Local
public interface UserFreezerService {
	
	
	void moveToFreezer(User user) throws Exception;
}
