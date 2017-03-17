package com.mangobits.startupkit.user.terms;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TermsServiceImpl implements TermsService {

	
	
	@Inject
	@New
	private TermsDAO termsDAO;
	
	
	
	@Override
	public Terms retrieveByLanguage(String language) throws ApplicationException, BusinessException {
		
		Terms terms = null;
		
		try {
			
			terms = termsDAO.retrieve(new Terms(language));
			
		} catch (Exception e) {
			throw new ApplicationException("Got an error loading a terms by labguage", e);
		}
		
		return terms;
	}
}
