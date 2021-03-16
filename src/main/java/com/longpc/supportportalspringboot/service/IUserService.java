package com.longpc.supportportalspringboot.service;

import com.longpc.supportportalspringboot.domain.User;
import com.longpc.supportportalspringboot.exception.domain.EmailNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.util.List;

public interface IUserService {
    User register(String firstName,String lastName,String username,String email) throws Exception;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User addNewUser(String firstName,String lastName,String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws Exception;
    User updateUser(String currentUsername,String newFirstName,String newLastName,String newUsername, String newEmail, String newRole, boolean isNonLocked, boolean isActive,MultipartFile profileImage) throws Exception;
    void deleteUser(long id);
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    User updateProfileImage(String username,MultipartFile newProfileImage) throws Exception;
}
