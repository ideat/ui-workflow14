package com.mindware.workflow.ui.ui.views.config.authorizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindware.workflow.ui.backend.entity.users.Users;
import com.mindware.workflow.ui.backend.entity.exceptions.Authorizer;
import com.mindware.workflow.ui.backend.rest.exceptions.AuthorizerRestTemplate;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Right;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Route(value = "authorizer-register", layout = MainLayout.class)
@PageTitle("Registrar Autorizadores")
public class AuthorizerRegister extends SplitViewFrame implements HasUrlParameter<String> {
    private Authorizer authorizer;
    private BeanValidationBinder<Authorizer> binder;
    private UserRestTemplate userRestTemplate;
    private AuthorizerRestTemplate restTemplate;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private Grid<Users> gridUser;
    private String paramLoginUser;
    private TextField loginUser;

    private TextField loginFilter;
    private TextField namefilter;
    private TextField rolFilter;



    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        restTemplate = new AuthorizerRestTemplate();
        if(s.equals("NUEVO")){
            authorizer = new Authorizer();

        }else{
            authorizer = restTemplate.getByLoginUser(s);
            paramLoginUser = s;

        }

        setViewContent(createContent());
        binder.readBean(authorizer);
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(authorizer.getLoginUser()==null?" Autorizador NUEVO": "Autorizador " +authorizer.getLoginUser());
    }

    private AppBar initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e -> {
            UI.getCurrent().navigate(UserAuthorizerView.class);
        });
        return appBar;
    }

    private DetailsDrawer createDetailDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        return detailsDrawer;
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createAuthorizer(authorizer));
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO,Horizontal.RESPONSIVE_L);

        return content;
    }


    private DetailsDrawer createAuthorizer(Authorizer authorizer){
        loginUser = new TextField();
        loginUser.setWidth("80%");
        loginUser.setEnabled(false);
        loginUser.addValueChangeListener(event ->{

        });

        Button btnSearch = new Button();
        btnSearch.setWidth("10%");
        btnSearch.setIcon(VaadinIcon.SEARCH_PLUS.create());
        btnSearch.addClickListener(e -> {
            showSearch();
        });

        FlexBoxLayout layoutSearch = new FlexBoxLayout(loginUser,btnSearch);
        layoutSearch.setFlexGrow(1,btnSearch);
        layoutSearch.setSpacing(Right.S);

//        NumberField minimumAmountBs = new NumberField();
//        minimumAmountBs.setWidth("100%");
//        minimumAmountBs.setRequiredIndicatorVisible(true);
//
//        NumberField maximumAmountBs = new NumberField();
//        maximumAmountBs.setWidth("100%");
//        maximumAmountBs.setRequiredIndicatorVisible(true);
//
//        NumberField minimumAmountSus = new NumberField();
//        minimumAmountSus.setWidth("100%");
//        minimumAmountSus.setRequiredIndicatorVisible(true);
//
//        NumberField maximumAmountSus = new NumberField();
//        maximumAmountSus.setWidth("100%");
//        maximumAmountSus.setRequiredIndicatorVisible(true);

        ComboBox<String> scope = new ComboBox<>();
        scope.setRequired(true);
        scope.setRequiredIndicatorVisible(true);
        scope.setItems("LOCAL","NACIONAL");

        RadioButtonGroup<String> state = new RadioButtonGroup<>();
        state.setItems("ACTIVO","BAJA");
        state.setValue(Optional.ofNullable(authorizer.getState()).orElse("").equals("ACTIVO") ? "ACTIVO":"BAJA");

        CheckboxGroup riskType = new CheckboxGroup();
        riskType.setItems(UtilValues.getParamterValue("TIPO RIESGO"));
//        riskType.setItems("RIESGO ALTO","RIESGO MEDIO", "RIESGO BAJO");
        riskType.setWidthFull();


        binder = new BeanValidationBinder<>(Authorizer.class);


        binder.forField(loginUser).asRequired("Login usuario es requerido")
                .bind(Authorizer::getLoginUser,Authorizer::setLoginUser);
//        binder.forField(minimumAmountBs).asRequired("Monto minimo en Bs es requerido")
//                .bind(Authorizer::getMinimumAmountBs,Authorizer::setMinimumAmountBs);
//        binder.forField(maximumAmountBs).asRequired("Monto maximo en Bs es requerido")
//                .bind(Authorizer::getMaximumAmountBs,Authorizer::setMaximumAmountBs);
//        binder.forField(minimumAmountSus).asRequired("Monto minimo en $us es requerido")
//                .bind(Authorizer::getMinimumAmountSus,Authorizer::setMinimumAmountSus);
//        binder.forField(maximumAmountSus).asRequired("Monto maximo en $us es requerido")
//                .bind(Authorizer::getMaximumAmountSus,Authorizer::setMaximumAmountSus);
        binder.forField(scope).asRequired("Alcance es requerido")
                .bind(Authorizer::getScope,Authorizer::setScope);
        binder.forField(state).asRequired("Estado es requerido").bind(Authorizer::getState,Authorizer::setState);
        binder.forField(riskType)
                .asRequired("Tipo de riesgo es requerido")
                .withConverter(new UtilValues.SetToStringConverter())
                .bind("riskType");

        binder.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Autorizadores"));
        });

        FormLayout formAuthorizer = new FormLayout();
        formAuthorizer.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formAuthorizer.addFormItem(layoutSearch,"Usuario");
//        formAuthorizer.addFormItem(minimumAmountBs,"Monto Min. Bs");
//        formAuthorizer.addFormItem(maximumAmountBs,"Monto Max. Bs");
//        formAuthorizer.addFormItem(minimumAmountSus,"Monto Min. $us");
//        formAuthorizer.addFormItem(maximumAmountSus, "Monto Max. $us");
        formAuthorizer.addFormItem(scope,"Alcance autorizacion");
        formAuthorizer.addFormItem(state,"Estado autorizador");
        formAuthorizer.addFormItem(riskType,"Tipo de Riesgo");

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
           if(binder.writeBeanIfValid(authorizer)){
               try {
                   restTemplate.add(authorizer);
                   UIUtils.showNotification("Autorizador Creado");
                   UI.getCurrent().navigate(UserAuthorizerView.class);
               } catch (JsonProcessingException ex) {
                   ex.printStackTrace();
               }
           }
        });

        footer.addCancelListener(e ->UI.getCurrent().navigate(UserAuthorizerView.class) );

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidth("100%");
        detailsDrawer.setContent(formAuthorizer);
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private void showSearch(){

        detailsDrawerHeader.setTitle("Seleccionar Usuario");
        detailsDrawer.setContent(searchUser());
        detailsDrawer.show();
    }

    private Grid searchUser(){
        userRestTemplate = new UserRestTemplate();
        List<Users> usersList = new LinkedList<>(userRestTemplate.getAllUsers()) ;

//        usersList.removeIf(e -> e.getLogin().equals(paramLoginUser));

        ListDataProvider<Users> data = new ListDataProvider<>(usersList);
        gridUser = new Grid<>();
        gridUser.setWidthFull();
        gridUser.setDataProvider(data);

        gridUser.addColumn(Users::getLogin).setFlexGrow(1).setSortable(true).setResizable(true)
                .setKey("login").setHeader("Login");
        gridUser.addColumn(Users::getFullName).setFlexGrow(1).setSortable(true).setResizable(true)
                .setKey("fullName").setHeader("Nombre usuario");
        gridUser.addColumn(Users::getRol).setFlexGrow(1).setSortable(true).setResizable(true)
                .setKey("rol").setHeader("Rol");
        gridUser.addColumn(new ComponentRenderer<>(this::createStateUser)).setHeader("Estado");
        gridUser.addColumn(new ComponentRenderer<>(this::createSelectUser)).setHeader("Seleccionar");
        HeaderRow hr = gridUser.appendHeaderRow();

        loginFilter = new TextField();
        loginFilter.setValueChangeMode(ValueChangeMode.EAGER);
        loginFilter.setWidth("100%");
        loginFilter.addValueChangeListener(e-> applyFilter(data));
        hr.getCell(gridUser.getColumnByKey("login")).setComponent(loginFilter);

        namefilter = new TextField();
        namefilter.setValueChangeMode(ValueChangeMode.EAGER);
        namefilter.setWidth("100%");
        namefilter.addValueChangeListener(e-> applyFilter(data));
        hr.getCell(gridUser.getColumnByKey("fullName")).setComponent(namefilter);

        rolFilter = new TextField();
        rolFilter.setValueChangeMode(ValueChangeMode.EAGER);
        rolFilter.setWidth("100%");
        rolFilter.addValueChangeListener(e-> applyFilter(data));
        hr.getCell(gridUser.getColumnByKey("rol")).setComponent(rolFilter);

        return gridUser;
    }

    private Component createStateUser(Users users){
        Icon icon;
        if (users.getState().equals("ACTIVO")) {
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createSelectUser(Users users){
        Button btnSelect = new Button();
        btnSelect.setIcon(VaadinIcon.CHEVRON_CIRCLE_UP.create());
        btnSelect.addClickListener(e ->{
            loginUser.setValue(users.getLogin());
            detailsDrawer.hide();
        });
        return btnSelect;
    }

    private void applyFilter(ListDataProvider<Users> dataUsers){
        dataUsers.clearFilters();
        if(!loginFilter.getValue().trim().equals("")){
            dataUsers.addFilter(users -> StringUtils.containsIgnoreCase(users.getLogin(),loginFilter.getValue().trim()));
        }
        if(!namefilter.getValue().trim().equals("")){
            dataUsers.addFilter(users ->StringUtils.containsIgnoreCase(users.getFullName(),namefilter.getValue().trim()));
        }
        if(!rolFilter.getValue().trim().equals("")){
            dataUsers.addFilter(users -> StringUtils.containsIgnoreCase(users.getRol(),rolFilter.getValue().trim()));
        }

        //StringUtils.containsIgnoreCase(workflowProduct.getTypeCreditDescription(),typeCreditDescriptionFilter.getValue()));
    }



}
