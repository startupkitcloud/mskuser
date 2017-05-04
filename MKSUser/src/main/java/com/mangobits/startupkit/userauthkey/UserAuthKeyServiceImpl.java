package com.mangobits.startupkit.userauthkey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserAuthKeyServiceImpl implements UserAuthKeyService {
	

//	@EJB
//	private UserService userService;
	
	
	
	@Inject
	@New
	private UserAuthKeyDAO userAuthKeyDAO;
	
	

	@Override
	public UserAuthKey createKey(String idUser, UserAuthKeyTypeEnum type) throws BusinessException, ApplicationException {
		
		UserAuthKey key = null;
		
		try {
			
//			User user = userService.load(idUser);
//			
//			if(user != null){
				
				key = loadKeyByUser(idUser, type);
				
				if(key == null){
					
					key = new UserAuthKey();
					key.setCreationDate(new Date());
					key.setIdUser(idUser);
					key.setType(type);
					
					if(type.equals(UserAuthKeyTypeEnum.EMAIL)){
						//ramdom uuid para email
						key.setKey(UUID.randomUUID().toString());
					}
					else{
						//4 digitos ramdomicos para SMS
						key.setKey(generateSMSNumber());
					}
					
					userAuthKeyDAO.insert(key);
				}
//			}
//			else{
//				throw new ApplicationException("user_not_found");
//			}
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error generating an user auth key", e);
		}
		
		return key;
	}

	
	
	@Override
	public Boolean validateKey(UserAuthKey key) throws BusinessException, ApplicationException {
		
		Boolean validated = false;
		
		try {
			
			UserAuthKey keyBase = loadKeyByUser(key.getIdUser(), key.getType());
			
			if(keyBase != null && keyBase.getKey().equals(key.getKey())){
				validated = true; 
				userAuthKeyDAO.delete(keyBase);
			}
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error generating an user auth key", e);
		}
		
		return validated;
	}
	
	
	
	private UserAuthKey loadKeyByUser(String idUser, UserAuthKeyTypeEnum type) throws BusinessException, ApplicationException{
		
		UserAuthKey key = null;
		
		try {
		
			Map<String, Object> params = new HashMap<>();
			params.put("idUser", idUser);
			params.put("type", type);
			
			key = userAuthKeyDAO.retrieve(params);
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error loading an user auth key", e);
		}
		
		return key;
	}
	
	
	
	private String generateSMSNumber() throws BusinessException, ApplicationException {
		
		String numberStr = null;
		
		try {
			
			boolean numberFound = false;
			
			while(!numberFound){
				
				Integer number = (int)(Math.random() * 10000);
				numberStr = String.valueOf(number);
				
				Map<String, Object> params = new HashMap<>();
				params.put("key", numberStr);
				
				UserAuthKey key = userAuthKeyDAO.retrieve(params);
				numberFound = number > 999 && key == null;
			}
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error generating an order number", e);
		}
		
		return numberStr;
	}
}
