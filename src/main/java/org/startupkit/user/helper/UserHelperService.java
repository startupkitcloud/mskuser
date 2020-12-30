package org.startupkit.user.helper;

import javax.ejb.Local;

@Local
public interface UserHelperService {

	
	byte[] excelUsersDatabase() throws Exception;
}
