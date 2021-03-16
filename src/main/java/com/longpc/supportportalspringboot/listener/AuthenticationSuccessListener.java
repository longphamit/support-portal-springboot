package com.longpc.supportportalspringboot.listener;

import com.longpc.supportportalspringboot.domain.User;
import com.longpc.supportportalspringboot.service.LoginAttemptService;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService){
        this.loginAttemptService=loginAttemptService;
    }
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal=event.getAuthentication().getPrincipal();
        if(principal instanceof User){
            User user=(User) principal;
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
