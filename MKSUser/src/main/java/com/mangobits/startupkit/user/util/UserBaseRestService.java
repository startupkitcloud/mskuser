package com.mangobits.startupkit.user.util;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import com.mangobits.startupkit.user.User;
import com.mangobits.startupkit.user.UserService;

public class UserBaseRestService {

	@Context
    private HttpServletRequest request;
	
	
	
	@EJB
	private UserService userExtService;
	
	
	
	protected User getUserTokenSession() throws Exception{
		
		// Get the HTTP Authorization header from the request
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();
        
        User user = userExtService.retrieveByToken(token);
        
        return user;
	}
}
