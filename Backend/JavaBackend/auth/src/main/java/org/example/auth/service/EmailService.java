package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import com.google.common.io.Files;
import org.example.auth.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${front.url}")
    private String fontendUrl;

    @Value("classpath:static/mail-aktywuj.html")
    private Resource activeTemplate;
    @Value("classpath:static/resetuj-haslo.html")
    private Resource recoveryTemplate;

    public void sendActivation(User user){
        log.info("--START sendActivation");
        try{
            String html = Files.toString(activeTemplate.getFile(), Charsets.UTF_8);
            html = html.replace("https://google.com",fontendUrl+"/aktywuj/"+user.getUuid());
            //emailConfiguration.sendMail(user.getEmail(), html,"Aktywacja konta",true);
        }catch (IOException e){
            log.info("Cant send mail");
            throw new RuntimeException(e);
        }
        log.info("--STOP sendActivation");
    }

    public void sendPasswordRecovery(User user,String uid){
        try{
            log.info("--START sendPasswordRecovery");
            String html = Files.toString(recoveryTemplate.getFile(), Charsets.UTF_8);
            html = html.replace("https://google.com",fontendUrl+"/odzyskaj-haslo/"+uid);
            //emailConfiguration.sendMail(user.getEmail(), html,"Odzyskanie hasła",true);
        }catch (IOException e){
            log.info("Cant send mail");
            throw new RuntimeException(e);
        }
        log.info("--STOP sendPasswordRecovery");
    }
}
