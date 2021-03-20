package com.longpc.supportportalspringboot.resource;

import com.longpc.supportportalspringboot.constant.FileConstant;
import com.longpc.supportportalspringboot.constant.SecurityConstant;
import com.longpc.supportportalspringboot.constant.UserImplConstant;
import com.longpc.supportportalspringboot.domain.HttpResponse;
import com.longpc.supportportalspringboot.domain.User;
import com.longpc.supportportalspringboot.domain.UserPrincipal;
import com.longpc.supportportalspringboot.exception.domain.EmailNotFoundException;
import com.longpc.supportportalspringboot.exception.domain.ExceptionHandling;
import com.longpc.supportportalspringboot.service.IUserService;
import com.longpc.supportportalspringboot.util.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(path = {"/","/user"})
@CrossOrigin
public class UserResource extends ExceptionHandling {
    @Autowired
    private IUserService userService;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws Exception{
        User newUser= userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<User>(newUser, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) throws Exception{
        authenticate(user.getUsername(),user.getPassword());
        User loginUser= userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal=new UserPrincipal(loginUser);
        HttpHeaders jwtHeader=getJwtHeader(userPrincipal);
        return new ResponseEntity<User>(loginUser, jwtHeader,HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName")String firstName,
                                           @RequestParam("lastName")String lastName,
                                           @RequestParam("username")String username,
                                           @RequestParam("email")String email,
                                           @RequestParam("role")String role,
                                           @RequestParam("isActive")String isActive,
                                           @RequestParam("isNonLocked")String isNonLocked,
                                           @RequestParam(value = "profileImage",required = false)MultipartFile profileImage
                                           ) throws Exception {
        User user= userService.addNewUser(firstName,lastName,username,email,role,Boolean.parseBoolean(isActive),Boolean.parseBoolean(isNonLocked),profileImage);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    @PostMapping("/update")
    public ResponseEntity<User> addNewUser(@RequestParam("currentUsername")String currentUsername,
                                           @RequestParam("firstname")String firstname,
                                           @RequestParam("lastname")String lastname,
                                           @RequestParam("username")String username,
                                           @RequestParam("email")String email,
                                           @RequestParam("role")String role,
                                           @RequestParam("isActive")String isActive,
                                           @RequestParam("isNonLocked")String isNonLocked,
                                           @RequestParam(value = "profileImage",required = false)MultipartFile profileImage
                                           ) throws Exception {
        User user= userService.updateUser(currentUsername,firstname,lastname,username,email,role,Boolean.parseBoolean(isActive),Boolean.parseBoolean(isNonLocked),profileImage);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username")String username,
                                                   @RequestParam(value = "profileImage")MultipartFile profileImage) throws Exception {
        User user=userService.updateProfileImage(username,profileImage);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username")String username){
        User user= userService.findUserByUsername(username);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUser(){
        List<User> listUser= userService.getUsers();
        return new ResponseEntity<>(listUser,HttpStatus.OK);
    }
    @GetMapping("/reset-password/{email}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(HttpStatus.OK,"Email sent to: "+email);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id")String id){
        userService.deleteUser(Long.parseLong(id));
        return response(HttpStatus.NO_CONTENT, UserImplConstant.DELETE_USER_SUCCESS);
    }
    @GetMapping(path = "/image/{username}/{fileName}",produces = {MediaType.IMAGE_JPEG_VALUE})
    public byte[] getProfileImage(@PathVariable("username")String username,@PathVariable("fileName")String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(FileConstant.USER_FOLDER+username+FileConstant.FORWARD_SLASH+fileName));
    }
    @GetMapping(path = "/image/profile/{fileName}",produces = {MediaType.IMAGE_JPEG_VALUE})
    public byte[] getTempProfileImage(@PathVariable("username")String username) throws IOException {
        URL url=new  URL(FileConstant.TEMP_PROFILE_IMAGE_BASE_URL+username);
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        try(InputStream inputStream=url.openStream()){
            int bytesRead;
            byte[] chunk =new byte[1024];
            while((bytesRead=inputStream.read(chunk))>0){
                byteArrayOutputStream.write(chunk,0,bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),message),httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers=new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

}
