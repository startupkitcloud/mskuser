package org.startupkit.user.freezer;

import org.startupkit.core.dao.AbstractDAO;


public class UserFreezerDAO extends AbstractDAO<UserFreezer> {
	
	public UserFreezerDAO(){
		super(UserFreezer.class);
	}
	

	@Override
	public Object getId(UserFreezer obj) {
		return obj.getId();
	}
}
