package org.startupkit.user.terms;

import org.startupkit.core.dao.AbstractDAO;


public class TermsDAO extends AbstractDAO<Terms> {
	
	public TermsDAO(){
		super(Terms.class);
	}
	

	@Override
	public Object getId(Terms obj) {
		return obj.getLanguage();
	}
}
