package com.mangobits.startupkit.user.terms;

import com.mangobits.startupkit.core.utils.AbstractDAO;


public class TermsDAO extends AbstractDAO<Terms> {

	public TermsDAO(){
		super(Terms.class);
	}


	@Override
	public Object getId(Terms obj) {
		return obj.getLanguage();
	}
}
