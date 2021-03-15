package com.longpc.supportportalspringboot.exception.domain;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message){
        super(message);
    }
}
