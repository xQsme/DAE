package ejbs;

import java.security.Security;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class EmailBean {

    //@Resource(name="mail/gmail")  //I did not use, to not cause problems to all
    //private Session session;      //This session is based on the glashfish version
                                    //Basicaly it sets the property i set as Properties
    private String mailhost = "smtp.gmail.com";
    //private String mailhost= "mail.ipleiria.pt"; 
    
    public synchronized void send(String userEmail, String password , String sender, String subject, 
            String body, List<String> recipients) throws AddressException, MessagingException 
    {
        
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", true);
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props,
                   new javax.mail.Authenticator() 
        {
              protected PasswordAuthentication getPasswordAuthentication()
              { return new PasswordAuthentication(userEmail,password);}     
        });
        
        MimeMessage message = new MimeMessage(session);
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");
        

        for(String recipient:recipients){
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        }
     
        Transport.send(message);        
    }
}