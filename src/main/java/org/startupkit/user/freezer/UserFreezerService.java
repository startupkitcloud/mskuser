package org.startupkit.user.freezer;

import org.startupkit.user.User;

import javax.ejb.Local;

@Local
public interface UserFreezerService {
	
	
	void moveToFreezer(User user) throws Exception;
}
