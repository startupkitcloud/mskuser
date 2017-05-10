package com.mangobits.startupkit.user.helper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mangobits.startupkit.service.admin.util.Secured;

@Stateless
@Path("/userHelper")
public class UserHelperRestService {

	
	
	@EJB
	private UserHelperService userHelperService;
	
	
	
	
	@Secured
	@GET
	@Path("/excelUsersDatabase")
	@Produces("application/excel")
	public Response passWallet() throws Exception {

		Response resp = null;
		
		try {
			
			byte[] data = userHelperService.excelUsersDatabase();
		
			ResponseBuilder response = Response.ok(data)
					.header("Content-Disposition","attachment; filename=users.xlsx");
			
			return response.build();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resp;
	}
}
