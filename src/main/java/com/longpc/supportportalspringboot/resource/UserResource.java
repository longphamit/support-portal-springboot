package com.longpc.supportportalspringboot.resource;

import com.longpc.supportportalspringboot.domain.User;
import com.longpc.supportportalspringboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserResource {
    @Autowired
    private IUserService userService;
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        User newUser= userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<User>(newUser, HttpStatus.OK);
    }
}
