package com.longpc.supportportalspringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.longpc.supportportalspringboot.constant.SecurityConstant;
import com.longpc.supportportalspringboot.domain.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JWTTokenProvider {
    @Value("jwt.secret")
    private String secret;
    public String generateJwtToken(UserPrincipal userPrincipal){
            String[] claims=getClaimsFromUser(userPrincipal);
            return JWT.create()
                    .withIssuer(SecurityConstant.GET_ARRAYS_LLC)
                    .withAudience(SecurityConstant.GET_ARRAYS_ADMINISTRATION)
                    .withIssuedAt(new Date())
                    .withSubject(userPrincipal.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstant.EXPIRATION_TIME))
                    .withArrayClaim(SecurityConstant.AUTHORITIES,claims)
                    .sign(Algorithm.HMAC256(secret.getBytes()));
    }
    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims= getClaimsFromToken(token);
        List<GrantedAuthority> list= new ArrayList<>();
        for(String s:claims){
            SimpleGrantedAuthority simpleGrantedAuthority= new SimpleGrantedAuthority(s);
            list.add(simpleGrantedAuthority);
        }
        return list;
    }
    public String[] getClaimsFromToken(String token){
        JWTVerifier  jwtVerifier= getJwtVerifier();
        return jwtVerifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }
    public JWTVerifier getJwtVerifier(){
        JWTVerifier jwtVerifier=null;
        try{
            Algorithm algorithm=Algorithm.HMAC256(secret);
            jwtVerifier=JWT
                    .require(algorithm)
                    .withIssuer(SecurityConstant.GET_ARRAYS_LLC)
                    .withAudience(SecurityConstant.GET_ARRAYS_ADMINISTRATION)
                    .build();
        }catch (Exception e){
            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
        }
        return jwtVerifier;
    }
    public String[] getClaimsFromUser(UserPrincipal userPrincipal){
        List<String> authorities=new ArrayList<>();
        for(GrantedAuthority grantedAuthority:userPrincipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(username,null,authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;

    }
    public boolean isTokenValid(String username,String token){
        JWTVerifier verifier=getJwtVerifier();
        return StringUtils.isNotEmpty(username)&&!isTokenExpired(verifier,token);
    }
    public boolean isTokenExpired(JWTVerifier verifier,String token){
        Date expiration=verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }
    public String getSubject(String token){
        JWTVerifier verifier=getJwtVerifier();
        return verifier.verify(token).getSubject();
    }
}
