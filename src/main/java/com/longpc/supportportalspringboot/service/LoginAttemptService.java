package com.longpc.supportportalspringboot.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    private static final int MAXIMUM_NUMBER_ATTEMPTS=5;
    private static final int ATTEMPTS_INCREMENT=1;
    private LoadingCache<String,Integer> loginAttemptCache;
    public LoginAttemptService(){
        loginAttemptCache= CacheBuilder
                .newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }
    public void evictUserFromLoginAttemptCache(String username){
        loginAttemptCache.invalidate(username);
    }
    public void addUserToLoginAttempCache(String username){
        int attempts=0;
        try{
            attempts=ATTEMPTS_INCREMENT+loginAttemptCache.get(username);
            loginAttemptCache.put(username,attempts);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean hasExceedMaxAttempts(String username){
        try{
            return loginAttemptCache.get(username)>=MAXIMUM_NUMBER_ATTEMPTS;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }
}
