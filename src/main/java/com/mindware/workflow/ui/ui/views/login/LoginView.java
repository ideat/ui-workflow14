package com.mindware.workflow.ui.ui.views.login;

import com.mindware.workflow.ui.backend.rest.login.JwtRequest;
import com.mindware.workflow.ui.backend.rest.login.LoginRestTemplate;
import com.mindware.workflow.ui.backend.rest.login.Token;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

@Route("")
@RouteAlias("login")
public class LoginView extends VerticalLayout {
    private final LoginI18n i18n = LoginI18n.createDefault();
    private boolean isHasPass;
    private String login;
    private LoginRestTemplate restTemplate;

    public LoginView(){
        restTemplate = new LoginRestTemplate();
        LoginForm component = new LoginForm();
        component.setI18n(createSpanishI18n());

        component.addLoginListener(e ->{
            JwtRequest jwtRequest = new JwtRequest();
            jwtRequest.setUsername(e.getUsername());
            jwtRequest.setPassword(e.getPassword());
            try {
                Token token = restTemplate.getToken(jwtRequest);
                VaadinSession.getCurrent().setAttribute("jwt", token.getToken());
                VaadinSession.getCurrent().setAttribute("login",e.getUsername());
                UI.getCurrent().navigate("main");
            }catch (Exception ex){
                component.setError(true);
            }
        });

        setSizeFull();
        getStyle().set("background","url(images/background-login.jpg");
        setHorizontalComponentAlignment(Alignment.CENTER,component);
        add(component);
    }

    private LoginI18n createSpanishI18n() {

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setDescription("Workflow - Creditos");
        i18n.getForm().setUsername("Usuario");
        i18n.getForm().setTitle("PROMOCRED");
        i18n.getForm().setSubmit("Entrar");
        i18n.getForm().setPassword("Clave");
        i18n.getForm().setForgotPassword("");
        i18n.getErrorMessage().setTitle("Usuario/clave invalida");
        i18n.getErrorMessage()
                .setMessage("Compruebe su usuario y contraseña y vuelva a intentarlo.");
        i18n.setAdditionalInformation("");
        return i18n;
    }


}
