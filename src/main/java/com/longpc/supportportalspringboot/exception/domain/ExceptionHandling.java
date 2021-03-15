package com.longpc.supportportalspringboot.exception.domain;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.longpc.supportportalspringboot.domain.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
@RestControllerAdvice
public class ExceptionHandling {
    private final Logger LOGGER= LoggerFactory.getLogger(getClass());
    private static final String ACCOUNT_LOCKED="Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED="This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG="An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS="Username/password incorrect. Please try again";
    private static final String ACCOUNT_DISABLE="Your account has bean disabled. If this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE="Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION="You do not have enough permission";

    @ExceptionHandler(DisabledException.class)
    private ResponseEntity<HttpResponse> accountDisabledException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST,ACCOUNT_DISABLE);
    }
    @ExceptionHandler(LockedException.class)
    private ResponseEntity<HttpResponse> accountLockedException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST,ACCOUNT_LOCKED);
    }
    @ExceptionHandler(MethodNotAllowedException.class)
    private ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST,METHOD_IS_NOT_ALLOWED);
    }
    @ExceptionHandler(Exception.class)
    private ResponseEntity<HttpResponse> internalServerErrorException(Exception e){
        LOGGER.error(e.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    private ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException e){
        return createHttpResponse(HttpStatus.NOT_FOUND,e.getMessage().toUpperCase());
    }
    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<HttpResponse> accessDeniedException(){
        return createHttpResponse(HttpStatus.FORBIDDEN,NOT_ENOUGH_PERMISSION);
    }
    @ExceptionHandler(UsernameExistedException.class)
    private ResponseEntity<HttpResponse> usernameExistedException(UsernameExistedException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST,e.getMessage().toUpperCase());
    }
    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException e){
        return createHttpResponse(HttpStatus.NOT_FOUND,e.getMessage().toUpperCase());
    }




    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<HttpResponse> badCredentialException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST,INCORRECT_CREDENTIALS);
    }
    @ExceptionHandler(IOException.class)
    private ResponseEntity<HttpResponse> processingFileException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST,ERROR_PROCESSING_FILE);
    }


    @ExceptionHandler(TokenExpiredException.class)
    private ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException e){
        return createHttpResponse(HttpStatus.UNAUTHORIZED,e.getMessage().toUpperCase());
    }
    @ExceptionHandler(EmailExistedException.class)
    private ResponseEntity<HttpResponse> emailExistedException(EmailExistedException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST,e.getMessage().toUpperCase());
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,String message){
        HttpResponse httpResponse=new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message);
        return new ResponseEntity<HttpResponse>(httpResponse,httpStatus);
    }
}
