package com.mangobits.startupkit.user.terms;

import javax.ejb.Local;

import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;

@Local
public interface TermsService {
	
	Terms retrieveByLanguage(String language) throws Exception;
}
