package com.mangobits.startupkit.userauthkey;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.user.UserService;
import com.mangobits.startupkit.ws.JsonContainer;



@Stateless
@Path("/userAuthKey")
public class UserAuthKeyRestService {

	
	
	@EJB
	private UserService userService;
	
	
	
	@GET
	@Path("/confirmUserSMS/{idUser}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String confirmUserSMS(@PathParam("idUser") String idUser) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			userService.confirmUserSMS(idUser);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	
	@GET
	@Path("/confirmUserEmail/{idUser}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String confirmUserEmail(@PathParam("idUser") String idUser) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			userService.confirmUserEmail(idUser);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@GET
	@Path("/forgotPassword/{email}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String forgotPassword(@PathParam("email") String email) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			userService.forgotPassword(email);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/validateKey")
	public String validateKey(UserAuthKey userAuthKey)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			Boolean validated = userService.validateKey(userAuthKey);
			cont.setData(validated);

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
}
