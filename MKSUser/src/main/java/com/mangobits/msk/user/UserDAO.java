package com.mangobits.msk.user;

import javax.persistence.NoResultException;

import com.mangobits.startupkit.core.exception.DAOException;
import com.mangobits.startupkit.core.utils.AbstractDAO;


public class UserDAO extends AbstractDAO<User> {
	
	public UserDAO(){
		super(User.class);
	}
	

	@Override
	protected Object getId(User usuario) {
		return usuario.getId();
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
}
