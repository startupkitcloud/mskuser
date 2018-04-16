package com.mangobits.startupkit.user.terms;

import com.mangobits.startupkit.core.dao.AbstractDAO;


public class TermsDAO extends AbstractDAO<Terms> {
	
	public TermsDAO(){
		super(Terms.class);
	}
	

	@Override
	public Object getId(Terms obj) {
		return obj.getLanguage();
	}
}
