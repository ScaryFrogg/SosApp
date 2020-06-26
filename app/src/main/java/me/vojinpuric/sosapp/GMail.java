package me.vojinpuric.sosapp;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMail {

    final String emailHost = "smtp.gmail.com";


    private String fromEmail="sosapphelp@gmail.com";
    private String fromPassword="sosapppassword";
    private String toEmail;
    private String emailSubject="Help";
    private String emailBody;

    private Properties emailProperties;
    private Session mailSession;
    private MimeMessage emailMessage;


    public GMail(String toEmail,String lat,String lon, String trackingId) {
        this.toEmail = toEmail;
        lat = lat.replace("°","%C2%B0");
        lat = lat.replace("\"","%E2%80%9C");
        lon = lon.replace("°","%C2%B0");
        lon = lon.replace("\"","%%E2%80%9C");
        this.emailBody = String.format("My location is https://www.google.com/maps/place/%s+%s \n Link for tracking: http://192.168.0.111:8080/?id=%s", lat, lon ,trackingId);
        final String emailPort = "587";// gmail's smtp port
        final String smtpAuth = "true";
        final String starttls = "true";

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.e("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));

        Log.e("GMail", "toEmail: " + toEmail);
        emailMessage.addRecipient(Message.RecipientType.TO,
                new InternetAddress(toEmail));


        emailMessage.setSubject(emailSubject);
        emailMessage.setContent(emailBody, "text/html");// for a html email
        // emailMessage.setText(emailBody);// for a text email
        Log.e("GMail", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.e("GMail", "Email sent successfully.");
    }
}