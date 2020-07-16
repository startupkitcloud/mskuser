package com.mangobits.startupkit.user;

import com.mangobits.startupkit.core.dao.AbstractDAO;

import java.util.List;


public class UserDAO extends AbstractDAO<User> {
	
	public UserDAO(){
		super(User.class);
	}
	

	@Override
	public Object getId(User obj) {
		return obj.getId();
	}

	
	public User retrieveByIdFacebook(String idFacebook) {
		return retrieve(createBuilder()
				.appendParamQuery("idFacebook", idFacebook)
				.build());
	}
	

	public User retrieveByIdGoogle(String idGoogle) {
		return retrieve(createBuilder()
				.appendParamQuery("idGoogle", idGoogle)
				.build());
	}


	public User retrieveByIdApple(String idApple) {
		return retrieve(createBuilder()
				.appendParamQuery("idApple", idApple)
				.build());
	}
	

	public List<User> listByFieldInfo(String field, String value) {
		return search(createBuilder()
				.appendParamQuery("info." + field, value)
				.build());
	}
}
