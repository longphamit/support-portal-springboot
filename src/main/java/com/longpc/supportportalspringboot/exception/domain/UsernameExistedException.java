package com.longpc.supportportalspringboot.exception.domain;

public class UsernameExistedException  extends  Exception{
    public UsernameExistedException(String message){
        super(message);
    }
}
