package com.mangobits.startupkit.user.terms;

import com.mangobits.startupkit.user.util.UserBaseRestService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Stateless
@Path("/terms")
public class TermsRestService extends UserBaseRestService {
	
	
	@EJB
	private TermsService termsService;
	
		
	@GET
	@Path("/load/{language}")
	@Produces(MediaType.TEXT_HTML + ";charset=utf-8")
	public String load(@PathParam("language") String language) throws Exception {
		
		String resultStr = null;
		
		try {
			
			Terms terms = termsService.retrieveByLanguage(language);
			resultStr = terms.getTerms();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultStr;
	}
}
