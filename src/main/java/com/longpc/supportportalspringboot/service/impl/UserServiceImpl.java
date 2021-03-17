package com.longpc.supportportalspringboot.service.impl;

import com.longpc.supportportalspringboot.constant.FileConstant;
import com.longpc.supportportalspringboot.constant.SecurityConstant;
import com.longpc.supportportalspringboot.constant.UserImplConstant;
import com.longpc.supportportalspringboot.domain.User;
import com.longpc.supportportalspringboot.domain.UserPrincipal;
import com.longpc.supportportalspringboot.enumeration.Role;
import com.longpc.supportportalspringboot.exception.domain.EmailExistedException;
import com.longpc.supportportalspringboot.exception.domain.EmailNotFoundException;
import com.longpc.supportportalspringboot.exception.domain.UserNotFoundException;
import com.longpc.supportportalspringboot.exception.domain.UsernameExistedException;
import com.longpc.supportportalspringboot.repository.UserRepository;
import com.longpc.supportportalspringboot.service.EmailService;
import com.longpc.supportportalspringboot.service.IUserService;
import com.longpc.supportportalspringboot.service.LoginAttemptService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@Qualifier("userDetailService")
public class UserServiceImpl implements IUserService, UserDetailsService {
    public static final String USERNAME_ALREADY_EXISTED = "Username already existed !";
    public static final String EMAIL_ALREADY_EXISTED = "Email already existed !";
    private Logger LOGGER= LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private EmailService emailService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findUserByUsername(username);
        if(user==null){
            LOGGER.error("User not found by username: "+username);
            throw new UsernameNotFoundException("User not found by username: "+username);
        }else{
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal=new UserPrincipal(user);
            LOGGER.info("Returning found user by username: "+username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws Exception {
        if(null==validateNewUsernameAndEmail(StringUtils.EMPTY,username,email)){
            String password=generatePassword();
            String encodedPassword=encodedPassword(password);
            User user= new User();
            user.setUserId(generateUserId());
            user.setPassword(encodedPassword);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setJoinDate(new Date());
            user.setRoles(Role.ROLE_USER.name());
            user.setAuthorities(Role.ROLE_USER.getAuthorities());
            user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
            user.setNotLocked(true);
            user.setActive(true);
            userRepository.save(user);
            LOGGER.info("New user password: "+password);
            return user;
        }
        return null;
    }
    private String getTemporaryProfileImageUrl(String username){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH+username).toUriString();
    }
    private String generateUserId(){
        return RandomStringUtils.randomNumeric(10);
    }
    private String generatePassword(){
        return RandomStringUtils.randomAlphanumeric(10);
    }
    private String encodedPassword(String password){
        return bCryptPasswordEncoder.encode(password);
    }
    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws Exception{
        User userByNewUsername=findUserByUsername(newUsername);
        User userByNewEmail=findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)){
            User currentUser=findUserByUsername(currentUsername);
            if(currentUser==null){
                throw new UserNotFoundException("No user found by username = "+currentUsername);
            }

            if(userByNewUsername!=null&&currentUser.getId().equals(userByNewUsername.getId())){
                throw new UsernameExistedException(USERNAME_ALREADY_EXISTED);
            }
            if(userByNewEmail!=null&&currentUser.getId().equals(userByNewEmail.getId())){
                throw new EmailExistedException(EMAIL_ALREADY_EXISTED);
            }
            return currentUser;
        }else{
            if(userByNewUsername!=null){
                throw new UsernameExistedException(USERNAME_ALREADY_EXISTED);
            }
            if(userByNewEmail!=null){
                throw new EmailExistedException(EMAIL_ALREADY_EXISTED);
            }
            return null;
        }

    }
    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws Exception {
        if(null==validateNewUsernameAndEmail(StringUtils.EMPTY,username,email)){
            User user= new User();
            String password=generatePassword();
            String encodedPassword=encodedPassword(password);
            user.setUserId(generateUserId());
            user.setPassword(encodedPassword);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setJoinDate(new Date());
            user.setRoles(getRoleEnumName(role).name());
            user.setAuthorities(getRoleEnumName(role).getAuthorities());
            user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
            user.setNotLocked(isNonLocked);
            user.setActive(isActive);
            userRepository.save(user);
            saveProfileImage(user,profileImage);
            return user;
        }
        return null;
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if(null!=profileImage){
            //user/home/supportportal/user
            //
            Path userFolder= Paths.get(FileConstant.USER_FOLDER+user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                LOGGER.info(FileConstant.DIRECTORY_CREATED);
            }
            Files.deleteIfExists(Paths.get(userFolder+user.getUsername()+FileConstant.DOT+FileConstant.JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(),userFolder.resolve(user.getUsername()+FileConstant.DOT+FileConstant.JPG_EXTENSION),REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FileConstant.FILE_SAVED_IN_FILE_SYSTEM);
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.USER_IMAGE_PATH+username+FileConstant.FORWARD_SLASH+username+FileConstant.DOT+FileConstant.JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }


    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName,String newUsername, String newEmail, String newRole, boolean isNonLocked, boolean isActive,MultipartFile profileImage) throws Exception {
        User user=validateNewUsernameAndEmail(currentUsername,newUsername,newEmail);
        if(null!=user){
            user.setUsername(newUsername);
            user.setEmail(newEmail);
            user.setFirstName(newFirstName);
            user.setLastName(newLastName);
            user.setJoinDate(new Date());
            user.setRoles(getRoleEnumName(newRole).name());
            user.setAuthorities(getRoleEnumName(newRole).getAuthorities());
            user.setNotLocked(isNonLocked);
            user.setActive(isActive);
            userRepository.save(user);
            saveProfileImage(user,profileImage);
            return user;
        }
        return null;
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user=userRepository.findUserByEmail(email);
        if(user==null){
            throw new EmailNotFoundException(UserImplConstant.NO_USER_FOUND_BY_EMAIL);
        }
        String password=generatePassword();
        user.setPassword(encodedPassword(password));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(),password,user.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile newProfileImage) throws Exception {
        User user=validateNewUsernameAndEmail(username,null,null);
        saveProfileImage(user,newProfileImage);
        return user;
    }

    private void validateLoginAttempt(User user){
        if(user.isNotLocked()){
            if(loginAttemptService.hasExceedMaxAttempts(user.getUsername())){
                user.setNotLocked(false);
            }else {
                user.setNotLocked(true);
            }
        }else{
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
