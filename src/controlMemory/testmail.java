package controlMemory;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class testmail
{
    private static final String SMTP_HOST_NAME = "gmail-smtp.l.google.com";
    private static final String SMTP_AUTH_USER = "GMAIL.ACCOUNT@gmail.com";
    private static final String SMTP_AUTH_PWD = "YOUR PASSWORD";
    private static final String emailMsgTxt = "Please visit my project at ";
    private static final String emailSubjectTxt = "Order Confirmation Subject";
    private static final String emailFromAddress = "GMAIL.ACCOUNT@gmail.com";
    private static final String[] emailList = { "FRIENDS.GMAIL.ACCOUNT.1@gmail.com" ,"FRIENDS.GMAIL.ACCOUNT.2@gmail.com"};
 
    public static void main(String args[]) throws Exception
    {
//         System.setProperty( "proxySet", "true" );
//         System.setProperty( "http.proxyHost", "wwwcache.ncl.ac.uk" );
//         System.setProperty( "http.proxyPort", "8080" );
        
        SendMailUsingAuthentication smtpMailSender = new SendMailUsingAuthentication();
        smtpMailSender.postMail(emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
        System.out.println("Sucessfully Sent mail to All Users");
    }
 
    public void postMail(String recipients[], String subject, String message, String from) throws MessagingException
    {
        boolean debug = false;
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                
        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
 
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getDefaultInstance(props, auth);
 
        session.setDebug(debug);
 
        // create a message
        Message msg = new MimeMessage(session);
 
        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);
 
        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++)
        {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);
 
        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }
    
    private class SMTPAuthenticator extends javax.mail.Authenticator
    {
        public PasswordAuthentication getPasswordAuthentication()
        {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }
}