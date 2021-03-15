package com.longpc.supportportalspringboot.service.impl;

import com.longpc.supportportalspringboot.domain.User;
import com.longpc.supportportalspringboot.domain.UserPrincipal;
import com.longpc.supportportalspringboot.enumeration.Role;
import com.longpc.supportportalspringboot.repository.UserRepository;
import com.longpc.supportportalspringboot.service.IUserService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Transactional
@Qualifier("userDetailService")
public class UserServiceImpl implements IUserService, UserDetailsService {
    private Logger LOGGER= LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByUsername(username);
        if(user==null){
            LOGGER.error("User not found by username: "+username);
            throw new UsernameNotFoundException("User not found by username: "+username);
        }else{
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal=new UserPrincipal(user);
            LOGGER.info("Returning found user by username: "+username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) {
        if(null!=validateNewUsernameAndEmail(StringUtils.EMPTY,username,email)){
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
            user.setProfileImageUrl(getTemporaryProfileImageUrl());
            userRepository.save(user);
            LOGGER.info("New user password: "+password);
            return user;
        }
        return null;
    }
    private String getTemporaryProfileImageUrl(){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
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
    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail){
        if(StringUtils.isNotBlank(currentUsername)){
            User currentUser=findUserByUsername(currentUsername);
            if(currentUser==null){
                return null;
            }
            User userByNewUsername=findUserByUsername(newUsername);
            if(userByNewUsername!=null&&currentUser.getId().equals(userByNewUsername.getId())){
                return null;
            }
            User userByNewEmail=findUserByEmail(newEmail);
            if(userByNewEmail!=null&&currentUser.getId().equals(userByNewEmail.getId())){
                return null;
            }
            return currentUser;
        }else{
            User userByNewUsername=findUserByUsername(newUsername);
            if(userByNewUsername!=null){
                return null;
            }
            User userByNewEmail=findUserByEmail(newEmail);
            if(userByNewEmail!=null){
                return null;
            }
        }
        return null;
    }
    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
