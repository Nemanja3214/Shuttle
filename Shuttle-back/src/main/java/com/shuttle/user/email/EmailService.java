package com.shuttle.user.email;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import com.shuttle.MailConfig;
import com.shuttle.passenger.Passenger;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements IEmailService{
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Override
	public void sendDummyMessage() throws UnsupportedEncodingException, jakarta.mail.MessagingException {
//		TODO change to user mail
		String toAddress = "nemanja.majstorovic3214@gmail.com";
	    String senderName = "Firma";
	    
	    String subject = "Please verify your registration";
	    String content = "Dear [[name]],<br>"
	            + "Please click the link below to verify your registration:<br>"
	            + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
	            + "Thank you,<br>"
	            + "Your company name.";
	    content = content.replace("[[name]]", "Pera");
//	    TODO dodaj token
	    content = content.replace("[[URL]]",  "token");
//	     
	    Session session = MailConfig.getSession();
		
		sendEmail(session, toAddress, subject, content);
	}

	@Override
	public void sendVerificationEmail(Passenger passenger, String url) throws UnsupportedEncodingException, MessagingException, jakarta.mail.MessagingException {
		String toAddress = passenger.getEmail();
//		TODO
	    String fromAddress = "shuttle.mailing.service2023@gmail.com";
	    String senderName = "Firma";
	    
	    
	    String subject = "Please verify your registration";
	    String content = "Dear [[name]],<br>"
	            + "Please click the link below to verify your registration:<br>"
	            + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
	            + "Thank you,<br>"
	            + "Your company name.";
	    
	    content.replace("[[name]]", passenger.getName());
//	    TODO dodaj token
	    content.replace("[[URL]]", url + "token");
	     
		Session session = MailConfig.getSession();
		
		sendEmail(session, toAddress, subject, content);
		
	}
	
	private void sendEmail(Session session, String toEmail, String subject, String body) throws jakarta.mail.MessagingException, UnsupportedEncodingException{
	      MimeMessage msg = new MimeMessage(session);
	      Properties props = MailConfig.getMailProperties();
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress(props.getProperty("mail.user"), "Shuttle"));

	      msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));

	      msg.setSubject(subject, "UTF-8");

	      msg.setText(body, "UTF-8");

	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
	      System.out.println("Message is ready");
    	  Transport.send(msg);  

	      System.out.println("EMail Sent Successfully!!");
	}

}
