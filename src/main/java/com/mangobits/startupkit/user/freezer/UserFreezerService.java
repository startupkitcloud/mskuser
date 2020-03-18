package com.mangobits.startupkit.user.freezer;

import com.mangobits.startupkit.user.User;

import javax.ejb.Local;

@Local
public interface UserFreezerService {
	
	
	void moveToFreezer(User user) throws Exception;
}
