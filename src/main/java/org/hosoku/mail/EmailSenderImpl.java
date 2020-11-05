package org.hosoku.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderImpl  {

    @Autowired
    JavaMailSender emailSender;

    public void sendSimpleMessage(  String to, String subject, String text) {
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            mimeMessage.setContent(text, "text/html");
            MimeMessageHelper messageHelper  = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("developer3@stableshield.com");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);

        };

        try{
            emailSender.send(mimeMessagePreparator);
        }catch(MailException mailException){
            mailException.printStackTrace();
        }
    }

}
