package com.mindware.workflow.ui.backend.util;

import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.users.Users;
import com.mindware.workflow.ui.backend.entity.email.Mail;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.entity.users.UsersOfficeDto;
import com.mindware.workflow.ui.backend.rest.email.MailRestTemplate;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrepareMail {
    private static UserRestTemplate userRestTemplate = new UserRestTemplate();
    private static MailRestTemplate mailRestTemplate = new MailRestTemplate();
    private static RolRestTemplate rolRestTemplate = new RolRestTemplate();
    private static OfficeRestTemplate officeRestTemplate = new OfficeRestTemplate();

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

        Rol rol = rolRestTemplate.getRolByName(rolNextStage);
        List<UsersOfficeDto> usersNextStage = new ArrayList<>();
        Office office = officeRestTemplate.getByCode(user.getCodeOffice());

        if(rol.getScope().equals("LOCAL")){
            usersNextStage = userRestTemplate.getByUserOfficeByCityAndRol(office.getCity(),rolNextStage);
        }else {

           usersNextStage = userRestTemplate.getByUserOfficeByRol(rolNextStage);
        }
        usersNextStage = usersNextStage.stream()
                .filter(u -> u.getStateUser().equals("ACTIVO"))
                .collect(Collectors.toList());
        for(UsersOfficeDto u:usersNextStage){
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

    public static void sendMailNewCreditRequestForward(String rol,  Integer numberRequest, String stage, Integer idOffice){
        List<Users> usersList = userRestTemplate.getByRol(rol);
        Rol rolUser = rolRestTemplate.getRolByName(rol);
        if(rolUser.getScope().equals("LOCAL")){
            usersList = usersList.stream()
                    .filter(u -> u.getCodeOffice().equals(idOffice) && u.getState().equals("ACTIVO"))
                    .collect(Collectors.toList());
        }

        for(Users users:usersList){
            Mail mail = new Mail();
            mail.setLoginUser(users.getLogin());
            mail.setSendDate(LocalDateTime.now());
            mail.setNumberRequest(numberRequest);
            mail.setMailFrom(users.getEmail());
            mail.setMailTo(users.getEmail());
            mail.setMailSubject("Workflow");
            mail.setMailContent("Operacion se encuentra en su area, solicitud nro: " + numberRequest.toString());
            mailRestTemplate.add(mail);
        }
    }

    public static void sendMailWorkflowGoBackward(List<StageHistory> stageHistoryList, String login, Integer numberRequest, String comesFrom){

        Users user = userRestTemplate.getByIdUser(login);
//        String[] comeFromList = comesFrom.split(",");

        Mail mail = new Mail();
        StageHistory stageHistory = stageHistoryList.stream().filter(s -> s.getStage().equals(comesFrom)).findFirst().get();
        Users u = userRestTemplate.getByIdUser(stageHistory.getUserTask());
        mail.setLoginUser(login);
        mail.setNumberRequest(numberRequest);
        mail.setSendDate(LocalDateTime.now());
        mail.setMailFrom(user.getEmail());
        mail.setMailTo(u.getEmail());
        mail.setMailSubject("Workflow backward");
        mail.setMailContent("Operacion se encuentra en su area, solicitud nro: " + numberRequest.toString());
        mailRestTemplate.add(mail);

//        for(String c:comeFromList){
//            Mail mail = new Mail();
//            StageHistory stageHistory = stageHistoryList.stream().filter(s -> s.getStage().equals(c)).findFirst().get();
//            Users u = userRestTemplate.getByIdUser(stageHistory.getUserTask());
//            mail.setLoginUser(login);
//            mail.setNumberRequest(numberRequest);
//            mail.setSendDate(LocalDateTime.now());
//            mail.setMailFrom(user.getEmail());
//            mail.setMailTo(u.getEmail());
//            mail.setMailSubject("Workflow backward");
//            mail.setMailContent("Operacion se encuentra en su area, solicitud nro: " + numberRequest.toString());
//            mailRestTemplate.add(mail);
//
//        }



    }

    public static void sendMailCreateUser(Users users, String plainPassword, String login, String comeFrom){
        Mail mail = new Mail();
        mail.setLoginUser(login);
        mail.setSendDate(LocalDateTime.now());
        mail.setNumberRequest(0);
        mail.setMailFrom(comeFrom);
        mail.setMailTo(users.getEmail());
        mail.setMailSubject("Cuenta usuario SEC");
        mail.setMailContent("Se creo su cuenta de usuario en el sistema SEC, su clave temporal es: " + plainPassword);
        mailRestTemplate.add(mail);
    }


}
