package controlMemory;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendMailUsingAuthentication {
	public static void main(String args[]) {
		/*Properties props = new Properties();
        props.put("mail.smtp.host" , "smtp.gmail.com");
        props.put("mail.stmp.user" , "evonne.chen0211@gmail.com");

        //To use TLS
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.password", "cs960629");
        //To use SSL
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", 
            "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");


        Session session = Session.getDefaultInstance(props, 
        	    new javax.mail.Authenticator(){
        	        protected PasswordAuthentication getPasswordAuthentication() {
        	            return new PasswordAuthentication(
        	                "evonne.chen0211@gmail.com", "cs960629");// Specify the Username and the PassWord
        	        }
        	});
        String to = "00748@dinyi.com.tw";
        String from = "evonne.chen0211@gmail.com";
        String subject = "Testing...";
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, 
                new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText("Working fine..!");
            Transport transpt = session.getTransport("smtp");
            transport.connect("smtp.gmail.com" , 465 , "evonne.chen0211@gmail.com", "cs960629");
            transport.send(msg);
            System.out.println("fine!!");
        }
        catch(Exception exc) {
            System.out.println(exc);
        }*/
        /*
		String host = "smtp.gmail.com";
		String port = "465";
		final String username = "evonne00748@gmail.com";
		final String password = "cs750211";//your password
	
		Properties props = new Properties();
		/*tls
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", port);
		/*
		props.put("mail.smtp.host", "smtp.gmail.com");  
        props.put("mail.smtp.socketFactory.port", "465");  
        props.put("mail.smtp.socketFactory.class",  
                "javax.net.ssl.SSLSocketFactory");  
        props.put("mail.smtp.auth", "true");  
        props.put("mail.smtp.port", "465");  
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
		  		return new PasswordAuthentication(username, password);
		  	}
		});

	  try {	
		   Message message = new MimeMessage(session);
		   message.setFrom(new InternetAddress("evonne00748@gmail.com"));
		   message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("00748@dinyi.com.tw"));
		   message.setSubject("測試寄信.");
		   message.setText("Dear DINYI, \n\n MISFAX 傳真測試!");
		
		   Transport transport = session.getTransport("smtp");
		   transport.connect(host, Integer.parseInt(port), username, password);
		
		   Transport.send(message);
		
		   System.out.println("寄送email結束.");
		
		  } catch (MessagingException e) {
			  System.out.println(e);
		  }*/
		final String username = "evonne00748@gmail.com";
		final String password = "cs750211";

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");  
        props.put("mail.smtp.socketFactory.port", "465");  
        props.put("mail.smtp.socketFactory.class",  
                "javax.net.ssl.SSLSocketFactory");  
        props.put("mail.smtp.auth", "true");  
        props.put("mail.smtp.port", "465");  
        
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("evonne00748@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("00748@dinyi.com.tw"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n No spam to my email, please!");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	 }
	
}