package org.startupkit.user;

import org.startupkit.admin.userb.UserBService;
import org.startupkit.admin.util.SecuredAdmin;
import org.startupkit.authkey.UserAuthKey;
import org.startupkit.core.configuration.Configuration;
import org.startupkit.core.configuration.ConfigurationEnum;
import org.startupkit.core.configuration.ConfigurationService;
import org.startupkit.core.exception.BusinessException;
import org.startupkit.core.photo.GalleryItem;
import org.startupkit.core.photo.PhotoUpload;
import org.startupkit.core.photo.PhotoUploadTypeEnum;
import org.startupkit.core.photo.PhotoUtils;
import org.startupkit.core.utils.FileUtil;
import org.startupkit.notification.email.EmailService;
import org.startupkit.user.util.SecuredUser;
import org.startupkit.user.util.UserBaseRestService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Stateless
@Path("/user")
public class UserRestService extends UserBaseRestService {


    @EJB
    protected UserService userService;

    @EJB
    private ConfigurationService configurationService;

    @EJB
    private EmailService emailService;

    @EJB
    private UserBService userBService;

    @Context
    private HttpServletRequest requestB;



    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/loginFB")
    public User loginFB(User usMon) throws Exception {
        return userService.loginFB(usMon);
    }


    @SecuredAdmin
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json;charset=utf-8"})
    @Path("/searchAdmin")
    public UserResultSearch searchAdmin(UserSearch userSearch) throws Exception {
        String authorizationHeader = this.requestB.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer".length()).trim();
            userSearch.setCode(this.userBService.retrieveByToken(token).getIdObj());
        }
        return this.userService.searchAdmin(userSearch);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/loginGoogle")
    public User loginGoogle(User usMon) throws Exception {
        return userService.loginGoogle(usMon);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/loginApple")
    public User loginApple(User usMon) throws Exception {
        return userService.loginApple(usMon);
    }

    @GET
    @Path("/load/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public User load(@PathParam("idUser") String idUser) throws Exception {
        return userService.retrieve(idUser);
    }


    @SecuredUser
    @GET
    @Path("/loggedUser/{token}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public User loggedUser(@PathParam("token") String token) throws Exception {
        return userService.retrieveByToken(token);
    }


    @SecuredUser
    @GET
    @Path("/loggedUserCard")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public UserCard loggedUserCard() throws Exception {
        User user = getUserTokenSession();

        if (user != null) {
            UserCard card = userService.generateCard(user);
            return card;
        }
        else{
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/login")
    public User login(User usMon) throws Exception {
        User user = userService.login(usMon);
        return user;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/autoLogin")
    public User autoLogin(User usMon) throws Exception {
        return userService.autoLogin(usMon);
    }


    @SecuredUser
    @PUT
    @Path("/logout/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void logout(@PathParam("idUser") String idUser) throws Exception {
        userService.logout(idUser);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/newUser")
    public User newUser(User usMon) throws Exception {
        userService.createNewUser(usMon);
        return usMon;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/saveAnonymous")
    public User saveAnonymous(User user) throws Exception {
        return userService.saveAnonymous(user);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/saveByPhone")
    public User saveByPhone(User usMon) throws Exception {
        return userService.saveByPhone(usMon);
    }


    @SecuredUser
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/updatePhoneUser")
    public User updatePhoneUser(User usMon) throws Exception {
        return userService.updatePhoneUser(usMon);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/update")
    public void update(User usMon) throws Exception {
        userService.updateFromClient(usMon);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/updatePassword")
    public String updatePassword(User usMon) throws Exception {
        return userService.updatePassword(usMon);
    }


    @SecuredUser
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/updateStartInfo")
    public void updateStartInfo(UserStartInfo userStartInfo) throws Exception {
        userService.updateStartInfo(userStartInfo);
    }


    @SecuredUser
    @POST
    @Path("/saveFacebookAvatar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void saveFacebookAvatar(PhotoUpload fotoUpload) throws Exception {

        User user = userService.retrieve(fotoUpload.getIdObject());

        if (user == null) {
            throw new BusinessException("User with id  '" + fotoUpload.getIdObject() + "' not found to attach photo");
        }

        userService.saveFacebookAvatar(fotoUpload);
    }


    @SecuredUser
    @POST
    @Path("/saveUserImage")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveUserImage(PhotoUpload photoUpload) throws Exception {
        User user = userService.retrieve(photoUpload.getIdObject());

        if (user == null) {
            throw new BusinessException("User with id  '" + photoUpload.getIdObject() + "' not found to attach photo");
        }

        //get the final size
        int finalWidth = configurationService.loadByCode("SIZE_DETAIL_MOBILE").getValueAsInt();
        photoUpload.setFinalWidth(finalWidth);
        String idPhoto;

        //soh adiciona na galeria se nao tiver idSubObject
        if (photoUpload.getIdSubObject() == null) {

            idPhoto = UUID.randomUUID().toString();

            GalleryItem gi = new GalleryItem();
            gi.setId(idPhoto);

            if (user.getGallery() == null) {
                user.setGallery(new ArrayList<>());
            }

            user.setIdAvatar(idPhoto);
            user.getGallery().add(gi);

        } else {

            idPhoto = photoUpload.getIdSubObject();
        }

        String path = userService.pathImage(user.getId());

        new PhotoUtils().saveImage(photoUpload, path, idPhoto);
        userService.update(user);
    }


    @GET
    @Path("/userImage/{idUser}/{imageType}")
    @Produces("image/jpeg")
    public StreamingOutput userImage(final @PathParam("idUser") String idUser, final @PathParam("imageType") String imageType) throws Exception {

        return out -> {

            Configuration configuration = null;

            try {

                configuration = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);

                String base = configuration.getValue();

                String path = base + "/user/" + idUser + "/" + imageType + "_main.jpg";

                File file = new File(path);

                if (!file.exists()) {
                    path = base + "/user/" + "default" + "/" + "placeholder" + ".jpg";
                }

                ByteArrayInputStream in = new ByteArrayInputStream(FileUtil.readFile(path));

                byte[] buf = new byte[16384];

                int len = in.read(buf);

                while (len != -1) {

                    out.write(buf, 0, len);

                    len = in.read(buf);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }


    @GET
    @Path("/userImage/{idUser}")
    @Produces("image/jpeg")
    public StreamingOutput userImage(final @PathParam("idUser") String idUser) throws Exception {

        return out -> {

            Configuration configuration = null;

            try {

                User user = userService.retrieve(idUser);

                if (user.getGallery() != null && user.getGallery().size() > 0) {

                    configuration = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);

                    String base = configuration.getValue();

                    String path;

                    if (user.getIdAvatar() == null) {

                        path = base + "/user/" + idUser + "/" + user.getGallery().get(0).getId() + "_main.jpg";
                    } else {

                        path = base + "/user/" + idUser + "/" + user.getIdAvatar() + "_main.jpg";
                    }


                    ByteArrayInputStream in = new ByteArrayInputStream(FileUtil.readFile(path));

                    byte[] buf = new byte[16384];

                    int len = in.read(buf);

                    while (len != -1) {

                        out.write(buf, 0, len);

                        len = in.read(buf);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }


    @SecuredUser
    @GET
    @Path("/searchByName/{nameUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<UserCard> searchByName(final @PathParam("nameUser") String name) throws Exception {
        return userService.searchByName(name);
    }


    @SecuredAdmin
    @GET
    @Path("/listAll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<UserCard> listAll() throws Exception {
        return userService.listAll();
    }


    @SecuredAdmin
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/saveAdmin")
    public User saveAdmin(User user) throws Exception {
        userService.save(user);
        confirmUserEmail(user.getId());
        return user;
    }


    @SecuredUser
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/cancelUser")
    public void cancelUser(User user) throws Exception {
        userService.cancelUser(user.getId());
    }


    @SecuredUser
    @PUT
    @Path("/confirmUserSMS/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void confirmUserSMS(@PathParam("idUser") String idUser) throws Exception {
        userService.confirmUserSMS(idUser);
    }


    @SecuredUser
    @PUT
    @Path("/confirmUserEmail/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void confirmUserEmail(@PathParam("idUser") String idUser) throws Exception {
        userService.confirmUserEmail(idUser);
    }


    @PUT
    @Path("/forgotPassword/{email}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void forgotPassword(@PathParam("email") String email) throws Exception {
        userService.forgotPassword(email);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/validateKey")
    public Boolean validateKey(UserAuthKey userAuthKey) throws Exception {
        return userService.validateKey(userAuthKey);
    }


    @PUT
    @Path("/testNotification/{idUser}/{msg}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void testNotification(@PathParam("idUser") String idUser, @PathParam("msg") String msg) throws Exception {
        userService.testNotification(idUser, msg);
    }


    @POST
    @SecuredAdmin
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/changeStatus")
    public void changeStatus(User user) throws Exception {
        userService.changeStatus(user.getId());
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
                        } catch (Exception e) {

                        } finally {
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
                while (length != 0) {
                    int read = raf.read(buf, 0, buf.length > length ? length : buf.length);
                    outputStream.write(buf, 0, read);
                    length -= read;
                }
            } catch (Exception e) {

            } finally {
                raf.close();
            }
        }

        public int getLenth() {
            return length;
        }
    }

    @POST
    @SecuredAdmin
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/saveByAdmin")
    public User saveByAdmin(User user) throws Exception {
        userService.saveByAdmin(user);
        return user;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/search")
    public List<UserCard> search(UserSearch userSearch) throws Exception {
        return userService.search(userSearch);
    }
}
