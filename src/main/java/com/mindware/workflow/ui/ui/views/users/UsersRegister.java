package com.mindware.workflow.ui.ui.views.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
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
import com.mindware.workflow.ui.ui.util.LumoStyles;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.rol.RolView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Route(value = "user-register", layout = MainLayout.class)
@PageTitle("Registro de Usuario")
public class UsersRegister extends SplitViewFrame implements HasUrlParameter<String> {

    private BeanValidationBinder<Users> binder;
    private ObjectMapper mapper = new ObjectMapper();
    private Users users;
    private UserRestTemplate restTemplate;
    private RolRestTemplate restTemplateRol;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private List<String> rolListNames = new ArrayList<>();

    private NumberField codeOffice;
    private TextField nameFilter;
    private TextField cityFilter;
    private TextField provinceFilter;
    private TextField codeFilter;
    private TextField login;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        restTemplateRol = new RolRestTemplate();
        restTemplate = new UserRestTemplate();

        List<Rol> rolList = restTemplateRol.getAllRols();
        for(Rol rol:rolList){
            String r = rol.getName();
            rolListNames.add(r);
        }
        if(s.contains("NUEVO")){
            users = new Users();
            users.setPassword("password");
            setViewContent(createContent());

        }else{
            users = restTemplate.getById(UUID.fromString(s));
            setViewContent(createContent());
            login.setEnabled(false);

        }
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        binder.readBean(users);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(users.getFullName().equals("null null")?"Nuevo":users.getFullName());

    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(UsersView.class);
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

        FlexBoxLayout content = new FlexBoxLayout(createUser(users));
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Horizontal.RESPONSIVE_L);

        return content;
    }

    private DetailsDrawer createUser(Users users){

        login = new TextField();
        login.setWidth("100%");
        login.setRequired(true);
        login.setRequiredIndicatorVisible(true);

        TextField names = new TextField();
        names.setWidth("100%");
        names.setRequired(true);
        names.setRequiredIndicatorVisible(true);

        TextField lastNames = new TextField();
        lastNames.setWidth("100%");
        lastNames.setRequired(true);
        lastNames.setRequiredIndicatorVisible(true);

        RadioButtonGroup<String> state = new RadioButtonGroup<>();
        state.setItems("ACTIVO","BAJA");
        state.setValue(Optional.ofNullable(users.getState()).orElse("").equals("ACTIVO") ? "ACTIVO":"BAJA");

        codeOffice = new NumberField();
        codeOffice.setWidth("80%");
        codeOffice.setEnabled(false);
        codeOffice.setRequiredIndicatorVisible(true);
        codeOffice.addValueChangeListener(event ->{
           if(!event.getHasValue().getValue().equals(event.getOldValue())){
               footer.saveState(true);
           }
        });

        Button btnSearch = new Button();
        btnSearch.setWidth("10%");
        btnSearch.setIcon(VaadinIcon.SEARCH_PLUS.create());
        btnSearch.addClickListener(e ->{
            showSearch();
        });

        FlexBoxLayout layoutSearch =  new FlexBoxLayout(codeOffice,btnSearch);
        layoutSearch.setFlexGrow(1,btnSearch);
        layoutSearch.setSpacing(Right.S);

        ComboBox<String> rols = new ComboBox<>();
        rols.setItems(rolListNames);
        rols.setRequired(true);
        rols.setRequiredIndicatorVisible(true);

        NumberField numDaysValidity = new NumberField();
        numDaysValidity.setWidth("100%");
        numDaysValidity.setRequiredIndicatorVisible(true);

        TextField email = new TextField();
        email.setWidth("100%");
        email.setRequired(true);
        email.setRequiredIndicatorVisible(true);

        Button btnReset = new Button("Reset Password");
        btnReset.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        btnReset.setWidth("100%");
        btnReset.setEnabled(GrantOptions.grantedOption("Usuarios"));

        binder = new BeanValidationBinder<>(Users.class);
        binder.forField(login).asRequired("Login es requerido").bind(Users::getLogin,Users::setLogin);
        binder.forField(names).asRequired("Nombre es requerido").bind(Users::getNames,Users::setNames);
        binder.forField(lastNames).asRequired("Apellidos son requeridos").bind(Users::getLastNames,Users::setLastNames);
        binder.forField(state).asRequired("Estado es requerido").bind(Users::getState,Users::setState);
        binder.forField(codeOffice).asRequired("Oficina es requerida")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(Users::getCodeOffice,Users::setCodeOffice);
        binder.forField(rols).asRequired("Rol es requerido").bind(Users::getRol,Users::setRol);
        binder.forField(numDaysValidity).asRequired("Dias de Validez es requerido")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(Users::getNumDaysValidity,Users::setNumDaysValidity);
        binder.forField(email).asRequired("Email es requerido")
                .withValidator(new EmailValidator("Correo invalido"))
                .bind(Users::getEmail,Users::setEmail);

        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges() ;
            footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Usuarios"));
        });
        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeUndefined();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.addFormItem(login,"Login");
        formLayout.addFormItem(names,"Nombres");
        formLayout.addFormItem(lastNames,"Apellidos");
        formLayout.addFormItem(state,"Estado Usuario");
        formLayout.addFormItem(layoutSearch,"Oficina");
        formLayout.addFormItem(rols,"Rol");
        formLayout.addFormItem(numDaysValidity,"Dias validez password");
        formLayout.addFormItem(email,"Correo electronico");
        formLayout.addFormItem(btnReset,"");

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(binder.writeBeanIfValid(users)){
                restTemplate.add(users);
                UIUtils.showNotification("Usuario Registrado");
                UI.getCurrent().navigate(UsersView.class);
            }
        });
        footer.addCancelListener(e ->{

        });

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidth("100%");
        detailsDrawer.setContent(formLayout);
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private void showSearch(){

        detailsDrawerHeader.setTitle("Seleccionar Oficina");
        detailsDrawer.setContent(searchOffice());
        detailsDrawer.show();
    }

    private Grid searchOffice(){
        OfficeRestTemplate rest = new OfficeRestTemplate();
        List<Office> officeList = new ArrayList<>(rest.getAllOffice());
        officeList.removeIf(e -> e.getInternalCode() == (users.getCodeOffice()==null?0:users.getCodeOffice()));

        ListDataProvider<Office> data = new ListDataProvider<>(officeList);

        Grid<Office> gridOffice = new Grid<>();
        gridOffice.setWidthFull();
        gridOffice.setDataProvider(data);

        gridOffice.addColumn(Office::getName).setHeader("Oficina")
                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("name");
        gridOffice.addColumn(Office::getInternalCode).setHeader("Codigo")
                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("code");
        gridOffice.addColumn(Office::getCity).setHeader("Ciudad")
                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("city");
        gridOffice.addColumn(Office::getProvince).setHeader("Provincia")
                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("province");
        gridOffice.addColumn(new ComponentRenderer<>(this::createSelectOffice)).setFlexGrow(1);

        HeaderRow hr = gridOffice.appendHeaderRow();
        nameFilter = new TextField();
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.setWidth("100%");
        nameFilter.addValueChangeListener(e->applyFilter(data));
        hr.getCell(gridOffice.getColumnByKey("name")).setComponent(nameFilter);

        codeFilter = new TextField();
        codeFilter.setValueChangeMode(ValueChangeMode.EAGER);
        codeFilter.setWidth("100%");
        codeFilter.addValueChangeListener(e -> applyFilter(data));
        hr.getCell(gridOffice.getColumnByKey("code")).setComponent(codeFilter);

        cityFilter = new TextField();
        cityFilter.setValueChangeMode(ValueChangeMode.EAGER);
        cityFilter.setWidth("100%");
        cityFilter.addValueChangeListener(e -> applyFilter(data));
        hr.getCell(gridOffice.getColumnByKey("city")).setComponent(cityFilter);

        provinceFilter = new TextField();
        provinceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        provinceFilter.setWidth("100%");
        provinceFilter.addValueChangeListener(e -> applyFilter(data));
        hr.getCell(gridOffice.getColumnByKey("province")).setComponent(provinceFilter);

        return gridOffice;
    }

    private Component createSelectOffice(Office office){
        Button btnSelect = new Button();
        btnSelect.setIcon(VaadinIcon.CHEVRON_CIRCLE_UP.create());
        btnSelect.addClickListener(e ->{
            codeOffice.setValue((double) office.getInternalCode());
            detailsDrawer.hide();
        });
        return btnSelect;
    }

    private void applyFilter(ListDataProvider<Office> dataProvider){
        dataProvider.clearFilters();
        if(!nameFilter.getValue().trim().equals("")) {
            dataProvider.addFilter(office ->  StringUtils.containsIgnoreCase(office.getName(),nameFilter.getValue()));
        }
        if(!codeFilter.getValue().trim().equals("")){
            dataProvider.addFilter(office -> Objects.equals(codeFilter.getValue().trim(),office.getInternalCode().toString()));
        }
        if(!cityFilter.getValue().trim().equals("")){
            dataProvider.addFilter(office -> StringUtils.containsIgnoreCase(office.getCity(),cityFilter.getValue()));
        }
        if(!provinceFilter.getValue().trim().equals("")){
            dataProvider.addFilter(office -> StringUtils.containsIgnoreCase(office.getProvince(),provinceFilter.getValue()));
        }
    }
}
