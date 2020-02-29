package com.mangobits.startupkit.user.terms;

import javax.ejb.Local;

@Local
public interface TermsService {
	
	Terms retrieveByLanguage(String language) throws Exception;
}
