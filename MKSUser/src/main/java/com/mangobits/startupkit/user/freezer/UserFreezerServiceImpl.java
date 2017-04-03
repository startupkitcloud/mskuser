package com.mangobits.startupkit.user.freezer;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;

import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.user.User;
import com.mangobits.startupkit.user.UserDAO;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserFreezerServiceImpl implements UserFreezerService {

	
	
	@Inject
	@New
	private UserFreezerDAO userFreezerDAO;
	
	
	
	@Inject
	@New
	private UserDAO userDAO;
	
	
	
	@Override
	public void moveToFreezer(User user) throws BusinessException, ApplicationException {
		
		try {
			
			UserFreezer freezer = new UserFreezer();
			BeanUtils.copyProperties(freezer, user);
			freezer.setId(null);
			userFreezerDAO.insert(freezer);
			
			User userBase = userDAO.retrieve(user);
			userDAO.delete(userBase);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error moving an user to freezer", e);
		}
	}
}
