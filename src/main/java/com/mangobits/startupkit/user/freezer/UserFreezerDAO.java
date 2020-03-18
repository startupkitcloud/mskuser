package com.mangobits.startupkit.user.freezer;

import com.mangobits.startupkit.core.dao.AbstractDAO;


public class UserFreezerDAO extends AbstractDAO<UserFreezer> {
	
	public UserFreezerDAO(){
		super(UserFreezer.class);
	}
	

	@Override
	public Object getId(UserFreezer obj) {
		return obj.getId();
	}
}
