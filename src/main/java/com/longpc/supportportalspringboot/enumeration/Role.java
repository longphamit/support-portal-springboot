package com.longpc.supportportalspringboot.enumeration;

import com.longpc.supportportalspringboot.constant.Authority;
import com.longpc.supportportalspringboot.constant.SecurityConstant;

public enum Role {
    ROLE_USER(Authority.USER_AUTHORITES),
    ROLE_HR(Authority.HR_AUTHORITES),
    ROLE_MANAGER(Authority.MANAGER_AUTHORITES),
    ROLE_ADMIN(Authority.ADMIN_AUTHORITES),
    ROLE_SUPER(Authority.SUPER_USER_AUTHORITES);
    private String[] authorities;
    Role(String... authorities){
        this.authorities=authorities;
    }
    public String[] getAuthorities(){
        return authorities;
    }
}
