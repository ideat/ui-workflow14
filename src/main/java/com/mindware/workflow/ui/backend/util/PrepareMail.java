package com.mindware.workflow.ui.backend.util;

import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.entity.email.Mail;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.rest.email.MailRestTemplate;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PrepareMail {
    private static UserRestTemplate userRestTemplate = new UserRestTemplate();
    private static MailRestTemplate mailRestTemplate = new MailRestTemplate();


    public PrepareMail(){

    }

    public static void sendMailWorkflowUser(String login, Integer numberRequest, String userPrev){
        Users user = userRestTemplate.getByIdUser(login);
        Users userSend = userRestTemplate.getByIdUser(userPrev);
        Mail mail = new Mail();
        mail.setLoginUser(login);
        mail.setNumberRequest(numberRequest);
        mail.setSendDate(LocalDateTime.now());
        mail.setMailFrom(user.getEmail());
        mail.setMailTo(userSend.getEmail());
        mail.setMailSubject("Workflow respuesta");
        mail.setMailContent("Respuesta observacion, solicitud nro: "+numberRequest.toString());
        mailRestTemplate.add(mail);


    }

    public static void sendMailWorkflowGoForward(String login, Integer numberRequest, String rolNextStage, String type){
        Users user = userRestTemplate.getByIdUser(login);
        List<Users> usersNextStage = userRestTemplate.getByRol(rolNextStage);

        for(Users u:usersNextStage){
            Mail mail = new Mail();
            mail.setLoginUser(login);
            mail.setNumberRequest(numberRequest);
            mail.setSendDate(LocalDateTime.now());
            mail.setMailFrom(user.getEmail());
            mail.setMailTo(u.getEmail());
            mail.setMailCc(user.getEmail());
            mail.setMailSubject("Workflow");
            mail.setMailContent("Operacion se encuentra en su area, solicitud nro: " + numberRequest.toString());
            mailRestTemplate.add(mail);
        }
    }

    public static void sendMailWorkflowGoBackward(List<StageHistory> stageHistoryList, String login, Integer numberRequest, String comesFrom){

        Users user = userRestTemplate.getByIdUser(login);
        String[] comeFromList = comesFrom.split(",");

        for(String c:comeFromList){
            Mail mail = new Mail();
            StageHistory stageHistory = stageHistoryList.stream().filter(s -> s.getStage().equals(c)).findFirst().get();
            Users u = userRestTemplate.getByIdUser(stageHistory.getUserTask());
            mail.setLoginUser(login);
            mail.setNumberRequest(numberRequest);
            mail.setSendDate(LocalDateTime.now());
            mail.setMailFrom(user.getEmail());
            mail.setMailTo(u.getEmail());
            mail.setMailSubject("Workflow backward");
            mail.setMailContent("Operacion se encuentra en su area, solicitud nro: " + numberRequest.toString());
            mailRestTemplate.add(mail);

        }

    }

    public static void sendMailCreateUser(Users users, String plainPassword, String login, String comeFrom){
        Mail mail = new Mail();
        mail.setLoginUser(login);
        mail.setSendDate(LocalDateTime.now());
        mail.setNumberRequest(0);
        mail.setMailFrom(comeFrom);
        mail.setMailTo(users.getEmail());
        mail.setMailSubject("Cuenta usuario PROMOCRED");
        mail.setMailContent("Se creo su cuenta de usuario en el sisema PROMOCRED, su clave temporal es: " + plainPassword);
        mailRestTemplate.add(mail);
    }


}
