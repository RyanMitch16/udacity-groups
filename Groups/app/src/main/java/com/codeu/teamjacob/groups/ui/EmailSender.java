package com.codeu.teamjacob.groups.ui;

/**
 * Created by saryal on 8/10/15.
 */

import java.util.Properties;
//import javax.
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by saryal on 8/5/2015.
 */
public class EmailSender extends javax.mail.Authenticator {

    public EmailSender() {
    }

    ;

    public boolean sendEmail(String to, String username, String userKey) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        String from = "gograspit@gmail.com";
        InternetAddress receiver = null;
        try {
            receiver = new InternetAddress(to);
        } catch (AddressException e) {
            e.printStackTrace();
        }
        String pass = "GoGraspItCodeU2015";
        String subject = "Welcome to Groups family :)";
        String body = "Hi " + username + "!\n\n"
                + "Welcome to Groups and thank you for signing up! You have joined over 500,000 individuals\n" +
                "just like you who manage their life using Groups and make it a way more easier\n" +
                "If you have any other questions and if you wish to share you feed back, please fell free to do so.\n" +
                "We will be more than happy to assist you!!!!\n\n" +
                "----------------------------------------------------------------------------------\n" +
                "If you did not sign up for Groups and your email is used than please click on the link below:\n" +
                "Sorry for the inconvenience."+
                "\nhttp://code-u-final.appspot.com/user/delete?user_key=" + userKey  +
                "\nElse ignore this link and enjoy your application.\n"+
                "----------------------------------------------------------------------------------\n\n" +
                "Regards,\n" +
                "Groups Team";

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, receiver);
            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            return true;
        } catch (AddressException ae) {
            ae.printStackTrace();
            return false;
        } catch (MessagingException me) {
            me.printStackTrace();
            return false;
        } catch (RuntimeException e) {
            e.getMessage();
            return false;
        }
    }
}



