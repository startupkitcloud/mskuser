package com.mangobits.startupkit.userauthkey;

import com.mangobits.startupkit.core.utils.AbstractDAO;


public class UserAuthKeyDAO extends AbstractDAO<UserAuthKey> {
	
	public UserAuthKeyDAO(){
		super(UserAuthKey.class);
	}
	

	@Override
	protected Object getId(UserAuthKey obj) {
		return obj.getId();
	}
}
