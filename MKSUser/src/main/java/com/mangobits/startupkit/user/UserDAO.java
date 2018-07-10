package com.mangobits.startupkit.user;

import com.mangobits.startupkit.core.exception.DAOException;
import com.mangobits.startupkit.core.utils.AbstractDAO;

import javax.persistence.NoResultException;
import java.util.List;


public class UserDAO extends AbstractDAO<User> {
	
	public UserDAO(){
		super(User.class);
	}
	

	@Override
	public Object getId(User obj) {
		return obj.getId();
	}

	
	
	public User retrieveByIdFacebook(String idFacebook) throws DAOException{
		
		User usuario = null;
		
		try {
			
			javax.persistence.Query query = entityManager.createNativeQuery("{idFacebook: '"  + idFacebook + "'}", User.class);
			
			usuario = (User) query.getSingleResult();
			
			
		} catch (NoResultException e) {
			
			//nao faz nada
		}
		catch (Exception e) {
			
			throw new DAOException("Got an error loading the user", e);
		}
		
		return usuario;
	}
	
	
	
	
	public User retrieveByIdGoogle(String idGoogle) throws DAOException{
		
		User usuario = null;
		
		try {
			
			javax.persistence.Query query = entityManager.createNativeQuery("{idGoogle: '"  + idGoogle + "'}", User.class);
			
			usuario = (User) query.getSingleResult();
			
			
		} catch (NoResultException e) {
			
			//nao faz nada
		}
		catch (Exception e) {
			
			throw new DAOException("Got an error loading the user", e);
		}
		
		return usuario;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> listByFieldInfo(String field, String value) throws DAOException{
		
		List<User> listUser = null;
		
		try {
			
			javax.persistence.Query query = entityManager.createNativeQuery("{'info." + field + "' : '"  + value + "'}", User.class);
			
			listUser = query.getResultList();
			
			
		} catch (NoResultException e) {
			
			//nao faz nada
		}
		catch (Exception e) {
			
			throw new DAOException("Got an error listByFieldInfo the user", e);
		}
		
		return listUser;
	}
}
