package com.mindware.workflow.ui.ui.views.authorizerExceptions;

import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "authorizer-exceptions-creditrequest", layout = MainLayout.class)
@PageTitle("Lista excepciones de la solicitud")
public class AuthorizerExceptionsCreditRequestView extends SplitViewFrame implements HasUrlParameter<String> {


    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {

    }
}
