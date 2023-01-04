package com.shuttle;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

@Configuration
public class MailConfig {



    @Value("${spring.mail.host}")
    private static String host;

    @Value("${spring.mail.port}")
    private static Integer port;

    @Value("${spring.mail.username}")
    private static String user;

    @Value("${spring.mail.password}")
    private static String password;
    
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private static boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private static boolean starttls;

    public static Properties getMailProperties(){
//    	TODO returns null
    	Properties props = new Properties();
		props.put("mail.smtp.host", host); //SMTP Host
		props.put("mail.smtp.port", port); //TLS Port
		props.put("mail.smtp.auth", auth); //enable authentication
		props.put("mail.smtp.starttls.enable", starttls); //enable STARTTLS
		props.setProperty("mail.user", user);
		props.setProperty("mail.password", password);
		return props;
    }
    
    public static Session getSession() {
    	Properties props = getMailProperties();
    	Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
			
				return new PasswordAuthentication(props.getProperty("mail.user"), props.getProperty("mail.password"));
			}
		};
		return Session.getInstance(props, auth);
    }
    

}