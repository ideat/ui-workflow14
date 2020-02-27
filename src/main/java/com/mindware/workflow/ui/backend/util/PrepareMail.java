package com.mindware.workflow.ui.backend.util;

import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.entity.email.Mail;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.rest.email.MailRestTemplate;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class PrepareMail {
    private static UserRestTemplate userRestTemplate = new UserRestTemplate();
    private static MailRestTemplate mailRestTemplate = new MailRestTemplate();


    public PrepareMail(){

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

}
