package com.mangobits.startupkit.user.terms;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TermsServiceImpl implements TermsService {

	
	
	@Inject
	@New
	private TermsDAO termsDAO;
	
	
	
	@Override
	public Terms retrieveByLanguage(String language) throws Exception {
		
		Terms terms = termsDAO.retrieve(new Terms(language));

		return terms;
	}
}
