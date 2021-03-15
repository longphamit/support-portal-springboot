package com.longpc.supportportalspringboot.constant;

public class Authority {
    public static final String[] USER_AUTHORITES={"user:read"};
    public static final String[] HR_AUTHORITES={"user:read","user:update"};
    public static final String[] MANAGER_AUTHORITES={"user:read","user:update"};
    public static final String[] ADMIN_AUTHORITES={"user:read","user:create","user:update"};
    public static final String[] SUPER_USER_AUTHORITES={"user:read","user:create","user:update","user:delete"};
}
