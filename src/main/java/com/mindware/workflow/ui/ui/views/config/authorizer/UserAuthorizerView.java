package com.mindware.workflow.ui.ui.views.config.authorizer;

import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.entity.exceptions.Authorizer;
import com.mindware.workflow.ui.backend.entity.exceptions.UserAuthorizer;
import com.mindware.workflow.ui.backend.rest.exceptions.UserAuthorizerRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.creditRequest.CreditRequestApplicantDataProvider;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "userAuthorizerView", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Usuarios Autorizadores")
public class UserAuthorizerView extends SplitViewFrame implements RouterLayout {

    private Grid<UserAuthorizer> grid;
    private UserAuthorizerDataProvider dataProvider;
    private List<UserAuthorizer> userAuthorizerList;
    private UserAuthorizerRestTemplate restTemplate;

    private Button btnNew;
    private TextField filterText;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        getListCreditRequest();
        setViewHeader(createTopBar());
        setViewContent(createContent());
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private void getListCreditRequest(){
        restTemplate = new UserAuthorizerRestTemplate();
        userAuthorizerList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new UserAuthorizerDataProvider(userAuthorizerList);
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Nro solicitud, Solicitante, Moneda, Fecha solicitud");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        btnNew = new Button("Nueva");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e -> viewRegister("NUEVO"));
        btnNew.setEnabled(GrantOptions.grantedOption("Autorizadores"));

        HorizontalLayout  topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText,btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,btnNew);
        topLayout.setSpacing(true);
        return topLayout;

    }

    private void viewRegister(String login){
//        Map<String,List<String>> param = new HashMap<>();
//        List<String> loginUser = new ArrayList<>();
//        loginUser.add(login);
//        param.put("login",loginUser);
//        QueryParameters qp = new QueryParameters(param);
        UI.getCurrent().navigate(AuthorizerRegister.class,login);
    }

    private Grid createGrid(){
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(event ->{
            viewRegister(event.getFirstSelectedItem().get().getLoginUser());
        });

        grid.addColumn(UserAuthorizer::getLoginUser).setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Login");
        grid.addColumn(UserAuthorizer::getFullName).setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Nombre");
        grid.addColumn(UserAuthorizer::getCity).setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Ciudad");
        grid.addColumn(UserAuthorizer::getScope).setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Alcance");
        grid.addColumn(new ComponentRenderer<>(this::createActiveUserAuthorizer)).setFlexGrow(1)
                .setSortable(true).setResizable(true)
                .setHeader("Estado Autorizador");
        grid.addColumn(new ComponentRenderer<>(this::createActiveUser)).setFlexGrow(1)
                .setSortable(true).setResizable(true)
                .setHeader("Estado Usuario");




        return grid;
    }

    private Component createActiveUserAuthorizer(UserAuthorizer userAuthorizer){
        Icon icon;
        if (userAuthorizer.getStateAuthorizer().equals("ACTIVO")) {
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createActiveUser(UserAuthorizer userAuthorizer){
        Icon icon;
        if (userAuthorizer.getStateUser().equals("ACTIVO")) {
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }
}
