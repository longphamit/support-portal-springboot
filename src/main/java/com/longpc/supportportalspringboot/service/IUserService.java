package com.longpc.supportportalspringboot.service;

import com.longpc.supportportalspringboot.domain.User;

import java.util.List;

public interface IUserService {
    User register(String firstName,String lastName,String username,String email);
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);

}
