package com.mangobits.startupkit.user;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangobits.startupkit.authkey.UserAuthKey;
import com.mangobits.startupkit.core.configuration.Configuration;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.photo.PhotoUpload;
import com.mangobits.startupkit.core.photo.PhotoUploadTypeEnum;
import com.mangobits.startupkit.core.utils.FileUtil;
import com.mangobits.startupkit.notification.email.EmailService;
import com.mangobits.startupkit.service.admin.util.Secured;
import com.mangobits.startupkit.user.util.SecuredUser;
import com.mangobits.startupkit.user.util.UserBaseRestService;
import com.mangobits.startupkit.ws.JsonContainer;


@Stateless
@Path("/user")
public class UserRestService extends UserBaseRestService{
	
	
	@EJB
	private UserService userService;
	
	
	
	@EJB
	private ConfigurationService configurationService;
	

	
	@EJB
	private EmailService emailService;
	
	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/loginFB")
	public String loginFB(User usMon)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			User user = userService.loginFB(usMon);
			cont.setData(user);

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
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
	@Path("/loginGoogle")
	public String loginGoogle(User usMon)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			User user = userService.loginGoogle(usMon);
			cont.setData(user);

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	

	@GET
	@Path("/load/{idUser}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String load(@PathParam("idUser") String idUser) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			User user = userService.load(idUser);
			
			cont.setData(user);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	
	@SecuredUser
	@GET
	@Path("/loggedUser/{token}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String loggedUser(@PathParam("token") String token) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			User user = userService.retrieveByToken(token);
			cont.setData(user);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	
	@SecuredUser
	@GET
	@Path("/loggedUserCard")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String loggedUserCard() throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			User user = getUserTokenSession();
			
			if(user != null){
				UserCard card = userService.generateCard(user);
				cont.setData(card);
			}
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
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
	@Path("/login")
	public String login(User usMon)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			User user = userService.login(usMon);
			cont.setData(user);

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}

	
	
	@SecuredUser
	@GET
	@Path("/logout/{idUser}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String logout(@PathParam("idUser") String idUser) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			userService.logout(idUser);
			cont.setData("OK");
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
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
	@Path("/newUser")
	public String newUser(User usMon)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			userService.createNewUser(usMon);
			cont.setData(usMon);

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	
	@SecuredUser
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/update")
	public String update(User usMon)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			userService.updateFromClient(usMon);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
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
	@Path("/updatePassword")
	public String updatePassword(User usMon)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			userService.updatePassword(usMon);
			cont.setDesc("OK");
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@SecuredUser
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/updateStartInfo")
	public String updateStartInfo(UserStartInfo userStartInfo)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			userService.updateStartInfo(userStartInfo);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@SecuredUser
	@POST
	@Path("/saveFacebookAvatar")
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveFacebookAvatar(PhotoUpload fotoUpload) throws Exception{
		
		
		try {
			
			User user = userService.retrieve(fotoUpload.getIdObject());
			
			if(user == null){
				throw new BusinessException("User with id  '" + fotoUpload.getIdObject() + "' not found to attach photo");
			}
			
			userService.saveFacebookAvatar(fotoUpload);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	@SecuredUser
	@POST
	@Path("/saveAvatar")
	@Consumes(MediaType.APPLICATION_JSON)
	public String saveAvatar(PhotoUpload fotoUpload) throws Exception{
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			User user = userService.retrieve(fotoUpload.getIdObject());
			
			if(user == null){
				throw new BusinessException("User with id  '" + fotoUpload.getIdObject() + "' not found to attach photo");
			}
			
			userService.saveAvatar(fotoUpload);
			cont.setData("OK");
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	
	@GET
	@Path("/showAvatar/{idUser}/{extra}")
	@Produces("image/jpeg")
	public StreamingOutput showAvatar(final @PathParam("idUser") String idUser, final @PathParam("extra") String extra) throws Exception {
		
		return new StreamingOutput() { 
			
			@Override
			public void write(final OutputStream out) throws IOException {
				
				Configuration config = null;
				
				try {
					
					config = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);
					
					String base = config.getValue();
					
					String path = base + userService.pathImageAvatar(idUser);
					
					if(!new File(path).exists()){
						path = base + "/user/default.png";
					}
					
					ByteArrayInputStream in =  new ByteArrayInputStream(FileUtil.readFile(path));
							
					byte[] buf = new byte[16384]; 
					
					int len = in.read(buf);
					
					while(len!=-1) { 
						out.write(buf,0,len); 
						len = in.read(buf); 
					} 
				} catch (Exception e) {
					
				}	
			}
		};
	}
	

	
	@SecuredUser
	@POST
	@Path("/saveGallery")
	@Consumes(MediaType.APPLICATION_JSON)
	public String saveGallery(PhotoUpload fotoUpload) throws Exception{
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			userService.saveGallery(fotoUpload);
			cont.setData(fotoUpload);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String test() throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			cont.setData("Hello Test!");
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	@SecuredUser
	@GET
	@Path("/searchByName")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String searchByName(@QueryParam("q") String name) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			List<UserCard> list = userService.searchByName(name);
			
			cont.setData(list);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	
	@Secured
	@GET
	@Path("/listAll")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String listAll() throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			List<UserCard> list = userService.listAll();
			cont.setData(list);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@Secured
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/save")
	public String save(User user)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			userService.save(user);
			
			confirmUserEmail(user.getId());
			
			cont.setData(user);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@SecuredUser
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/cancelUser")
	public String cancelUser(User user)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			userService.cancelUser(user.getId());
			cont.setData("OK");

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@SecuredUser
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
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@SecuredUser
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
				emailService.sendEmailError(e);
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
				emailService.sendEmailError(e);
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
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@GET
	@Path("/testNotification/{idUser}/{msg}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String testNotification(@PathParam("idUser") String idUser, @PathParam("msg") String msg) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			userService.testNotification(idUser, msg);
			cont.setData("OK");
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
				emailService.sendEmailError(e);
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	@GET
	@Path("/loadImageByIndex/{idUser}/{index}")
	@Produces("image/jpeg")
	public StreamingOutput loadImageByIndex(final @PathParam("idUser") String idUser, final @PathParam("index") Integer index) throws Exception {
		
		return new StreamingOutput() { 
			
			@Override
			public void write(final OutputStream out) throws IOException {
				
				try {
					
					User user = userService.retrieve(idUser);
					
					if(user == null){
						throw new BusinessException("User with id  '" + idUser + "' not found to attach photo");
					}
					
					if(CollectionUtils.isNotEmpty(user.getListPhotoUpload())){
						
						PhotoUpload photoUpload = user.getListPhotoUpload().stream()
								.filter(p -> p.getIndex().equals(index))
								.findFirst()
								.orElse(null);
						
						if(photoUpload != null){
							
							String path = userService.pathGallery(idUser, photoUpload.getType());
							
							path = path + "/" + photoUpload.getId() + "_main.jpg";
							
							ByteArrayInputStream in =  new ByteArrayInputStream(FileUtil.readFile(path));
									
							byte[] buf = new byte[16384]; 
							
							int len = in.read(buf);
							
							while(len!=-1) { 
								
								out.write(buf,0,len); 
							
								len = in.read(buf); 
							} 
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		};
	}
	
	
	@GET
	@Path("/video/{idUser}/{name}")
	@Produces("video/mp4")
    public Response video(@HeaderParam("Range") String range, final @PathParam("name") String name, final @PathParam("idUser") String idUser) throws Exception {
        return buildStream(new File(userService.pathGallery(idUser, PhotoUploadTypeEnum.VIDEO) + "/" + name), range);
    }
	
	private Response buildStream(final File asset, final String range) throws Exception {
		
		Response.ResponseBuilder res = null;
		
        try {
			
        	// range not requested : Firefox, Opera, IE do not send range headers
            if (range == null) {
                StreamingOutput streamer = new StreamingOutput() {
                    @Override
                    public void write(final OutputStream output) throws IOException, WebApplicationException {

                    	FileInputStream fos = new FileInputStream(asset);
                        final FileChannel inputChannel = fos.getChannel();
                        final WritableByteChannel outputChannel = Channels.newChannel(output);
                        try {
                            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                        } 
                        catch (Exception e){
                        	
                        }finally {
                            // closing the channels
                            try {
                            	inputChannel.close();
                                outputChannel.close();
                                fos.close();
							} catch (Exception e2) {
								// Jesus, no exceptions! =)
							}
                        }
                    }
                };
                return Response.ok(streamer).header(HttpHeaders.CONTENT_LENGTH, asset.length()).build();
            }

            String[] ranges = range.split("=")[1].split("-");
            final int from = Integer.parseInt(ranges[0]);
            /**
             * Chunk media if the range upper bound is unspecified. Chrome sends "bytes=0-"
             */
            int to = 1000 + from;
            if (to >= asset.length()) {
                to = (int) (asset.length() - 1);
            }
            if (ranges.length == 2) {
                to = Integer.parseInt(ranges[1]);
            }

            final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());
            final RandomAccessFile raf = new RandomAccessFile(asset, "r");
            raf.seek(from);

            final int len = to - from + 1;
            final MediaStreamer streamer = new MediaStreamer(len, raf);
            res = Response.status(Status.PARTIAL_CONTENT).entity(streamer)
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Range", responseRange)
                    .header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
                    .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()));
           
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        return res.build();
    }
	
	class MediaStreamer implements StreamingOutput {

	    private int length;
	    private RandomAccessFile raf;
	    final byte[] buf = new byte[4096];

	    public MediaStreamer(int length, RandomAccessFile raf) {
	        this.length = length;
	        this.raf = raf;
	    }

	    @Override
	    public void write(OutputStream outputStream) throws IOException, WebApplicationException {
	        try {
	            while( length != 0) {
	                int read = raf.read(buf, 0, buf.length > length ? length : buf.length);
	                outputStream.write(buf, 0, read);
	                length -= read;
	            }
	        } catch(Exception e){
	        	
	        }finally {
	            raf.close();
	        }
	    }

	    public int getLenth() {
	        return length;
	    }
	}
}
