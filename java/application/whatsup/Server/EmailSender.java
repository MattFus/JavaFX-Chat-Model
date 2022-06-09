package application.whatsup.Server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;



public class EmailSender{
    private EmailSender instance = null;
    //private String password = "whatsupHelper@123123"
    private EmailSender(){
    }

    public EmailSender getInstance() {
        if(instance == null)
            instance = new EmailSender();
        return instance;
    }

    public static void send(String to, String sub, String msg){
        //Get properties object
        String from = "whatsuphelper@gmail.com";
        String password = "whatshupHelper@123123";
        Properties set = new Properties();
        //Set values to the property
        set.put("mail.smtp.starttls.enable", "true");
        set.put("mail.smtp.auth", "true");
        set.put("mail.smtp.host", "smtp.gmail.com");
        set.put("mail.smtp.port", "587");
        //get Session
        Session session = Session.getDefaultInstance(set, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from,password);
            }
        });
        System.out.println("ACCESSO OK");
        //compose message

    }
}