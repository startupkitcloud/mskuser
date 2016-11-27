package com.mangobits.msk.user;

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
