package org.startupkit.user.helper;

import org.startupkit.user.util.SecuredUser;
import org.startupkit.user.util.UserBaseRestService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Stateless
@Path("/userHelper")
public class UserHelperRestService extends UserBaseRestService {

	
	
	@EJB
	private UserHelperService userHelperService;
	
	
	
	
	@SecuredUser
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
