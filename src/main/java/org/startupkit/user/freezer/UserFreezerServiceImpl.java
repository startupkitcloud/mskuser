package org.startupkit.user.freezer;

import org.startupkit.user.User;
import org.startupkit.user.UserDAO;
import org.apache.commons.beanutils.BeanUtils;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;

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
	public void moveToFreezer(User user) throws Exception {

		UserFreezer freezer = new UserFreezer();
		BeanUtils.copyProperties(freezer, user);
		freezer.setId(null);
		userFreezerDAO.insert(freezer);

		User userBase = userDAO.retrieve(user);
		userDAO.delete(userBase);
	}
}
