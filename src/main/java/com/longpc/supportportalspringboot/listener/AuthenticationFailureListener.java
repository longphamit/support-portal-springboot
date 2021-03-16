package com.longpc.supportportalspringboot.listener;

import com.longpc.supportportalspringboot.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener {
    private LoginAttemptService loginAttemptService;
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService){
        this.loginAttemptService=loginAttemptService;
    }
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event){
        Object principal=event.getAuthentication().getPrincipal();
        if(principal instanceof String){
            String username=(String) principal;
            loginAttemptService.addUserToLoginAttempCache(username);
        }
    }
}
