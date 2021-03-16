package com.longpc.supportportalspringboot.service;

import com.longpc.supportportalspringboot.constant.EmailConstant;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {
    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message= new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email,false));
        message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(EmailConstant.CC_EMAIL,false));
        message.setSubject(EmailConstant.EMAIl_SUBJECT);
        message.setText("Hello "+firstName +"\n \n Your new Password is: "+password +"\n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return  message;
    }
    private Session getEmailSession(){
        Properties props = new Properties();
        props.put(EmailConstant.SMTP_AUTH,true);
        props.put(EmailConstant.SMTP_HOST,EmailConstant.GMAIL_SMTP_SERVER);
        props.put(EmailConstant.SMTP_STARTTLS_ENABLE,true);
        props.put(EmailConstant.SMTP_STARTTLS_REQUIRED,true);
        props.put(EmailConstant.SMTP_PORT, EmailConstant.DEFAULT_PORT);
        return Session.getInstance(props);
    }
    public void sendNewPasswordEmail(String firstName,String password,String email) throws MessagingException {
        Message message=createEmail(firstName, password, email);
        SMTPTransport transport= (SMTPTransport) getEmailSession().getTransport(EmailConstant.MAIL_PROTOCOL);
        transport.connect(EmailConstant.GMAIL_SMTP_SERVER,EmailConstant.USERNAME,EmailConstant.PASSWORD);
        transport.sendMessage(message,message.getAllRecipients());
        transport.close();
    }


}
