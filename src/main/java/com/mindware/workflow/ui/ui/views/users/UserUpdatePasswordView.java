package com.mindware.workflow.ui.ui.views.users;

import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;

import java.util.List;
import java.util.Map;

@Route(value = "user-update-password")
@PageTitle("Actualizar clave usuario")
public class UserUpdatePasswordView extends HorizontalLayout implements HasUrlParameter<String> {

    private UserRestTemplate restTemplate;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private PasswordField oldPassword;
    private PasswordField newPassword;
    private PasswordField confirmNewPassword;

    private Users users;



    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> params = queryParameters.getParameters();

        restTemplate = new UserRestTemplate();
        users = getUserByLogin(params.get("login").get(0));
//        getStyle().set("background","url(images/background-login.jpg");
        this.setJustifyContentMode ( FlexComponent.JustifyContentMode.CENTER );
        this.setDefaultVerticalComponentAlignment ( FlexComponent.Alignment.CENTER);
        add(updatePassword());
    }


    private Users getUserByLogin(String login){
        users = restTemplate.getByIdUser(login);

        return users;

    }


    private VerticalLayout updatePassword(){
        oldPassword = new PasswordField("Clave anterior");
        oldPassword.setWidth("100%");
        oldPassword.setRequired(true);
        oldPassword.setRequiredIndicatorVisible(true);

        newPassword = new PasswordField("Nueva clave");
        newPassword.setWidth("100%");
        newPassword.setRequired(true);
        newPassword.setRequiredIndicatorVisible(true);

        confirmNewPassword = new PasswordField("Confirme nueva clave");
        confirmNewPassword.setWidth("100%");
        confirmNewPassword.setRequired(true);
        confirmNewPassword.setRequiredIndicatorVisible(true);

        Button save = new Button("Guardar");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e ->{
//            if(validateOldPasswords()){
                if(validateNewPassword()){
                    users.setPassword(newPassword.getValue());
                    restTemplate.updatePassword(users);
                    UI.getCurrent().navigate(LoginView.class);
                }else{
                    UIUtils.showNotification("Nuevo password no coincide, ingrese nuevamente");
                    newPassword.focus();
                }

//            }else {
//                UIUtils.showNotification("Clave antigua incorrecta, ingrese nuevamente");
//                oldPassword.focus();
//            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("20%");
        layout.setHeight("50%");
        layout.setHorizontalComponentAlignment(Alignment.CENTER,newPassword,confirmNewPassword,save);
//        layout.setAlignItems(Alignment.BASELINE);
        layout.add(newPassword,confirmNewPassword,save);



        return layout;
    }

    private boolean validateOldPasswords(){
        String pass = restTemplate.getPassword(oldPassword.getValue());
        return pass.equals(users.getPassword());

    }
    private boolean validateNewPassword(){
        return newPassword.getValue().equals(confirmNewPassword.getValue());

    }
}
