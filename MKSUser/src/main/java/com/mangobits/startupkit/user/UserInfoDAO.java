package com.mangobits.startupkit.user;

import com.mangobits.startupkit.core.utils.AbstractDAO;

public class UserInfoDAO extends AbstractDAO<UserInfo> {
	
	public UserInfoDAO(){
		super(UserInfo.class);
	}
	

	@Override
	protected Object getId(UserInfo obj) {
		return obj.getIdUser();
	}
}
