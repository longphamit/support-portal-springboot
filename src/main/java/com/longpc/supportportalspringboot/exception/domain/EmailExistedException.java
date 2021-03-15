package com.longpc.supportportalspringboot.exception.domain;

public class EmailExistedException extends  Exception {
    public EmailExistedException(String message){
        super(message);
    }
}
