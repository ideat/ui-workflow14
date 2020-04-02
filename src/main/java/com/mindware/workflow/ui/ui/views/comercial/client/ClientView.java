package com.mindware.workflow.ui.ui.views.comercial.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.entity.comercial.client.Client;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.rest.comercial.client.ClientRestTemplate;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Right;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.LumoStyles;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Route(value = "client", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Clientes para mercadeo")
public class ClientView extends SplitViewFrame {

    private Grid<Client> grid;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;
    private Binder<Client> binder;
    private Button btnNew;
    private ClientRestTemplate restTemplate;
    private  List<Client> clientList;
    private ListDataProvider<Client> dataProvider;
    private Client current;

    private ComboBox<String> typePerson;
    private ComboBox<String> typeClient;
    private TextField fullName;
    private TextField idCardComplete;
    private TextField loginUser;
    private TextField cellPhone;
    private TextField email;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getClientList();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
    }

    private void getClientList(){
        restTemplate = new ClientRestTemplate();
        UserRestTemplate userRestTemplate = new UserRestTemplate();
        String login = VaadinSession.getCurrent().getAttribute("login").toString();
        Users users = userRestTemplate.getByIdUser(login);
        if(users.getPosition().equals("OFICIAL DE CREDITOS")){
            clientList = new ArrayList<>(restTemplate.getByUser(login));
        }else{
            clientList = new ArrayList<>(restTemplate.getAll());
        }
        dataProvider = new ListDataProvider<>(clientList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridClient());
        content.addClassName("grid-view");
        content.setHeightFull();
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nuevo Cliente");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.setEnabled(GrantOptions.grantedOption("Clientes"));
        btnNew.addClickListener(e -> {
            showDetails(new Client());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private Grid createGridClient(){
        grid = new Grid<>();
        grid.setHeightFull();
        grid.setWidthFull();
        grid.addSelectionListener(e ->{
            e.getFirstSelectedItem().ifPresent(this::showDetails);
        }
        );
        grid.setDataProvider(dataProvider);

        grid.addColumn(new ComponentRenderer<>(this::createNameInfo)).setFlexGrow(1).setKey("fullName")
                .setSortable(true).setAutoWidth(true).setResizable(true).setHeader("Nombre cliente");
        grid.addColumn(Client::getIdCardComplete).setFlexGrow(1).setSortable(true).setKey("idCardComplete")
                .setAutoWidth(true).setResizable(true).setHeader("Carnet");
        grid.addColumn(Client::getTypePerson).setFlexGrow(1).setSortable(true).setKey("typePerson")
                .setAutoWidth(true).setResizable(true).setHeader("Tipo persona");
        grid.addColumn(Client::getTypeClient).setFlexGrow(1).setResizable(true).setKey("typeClient")
                .setSortable(true).setAutoWidth(true).setHeader("Tipo cliente");
        grid.addColumn(Client::getCellPhone).setFlexGrow(1).setSortable(true).setKey("cellPhone")
                .setSortable(true).setResizable(true).setAutoWidth(true).setHeader("Celular");
        grid.addColumn(Client::getEmail).setFlexGrow(1).setSortable(true).setResizable(true)
                .setAutoWidth(true).setHeader("Email").setKey("email");
        grid.addColumn(Client::getLoginUser).setFlexGrow(1).setSortable(true).setKey("loginUser")
                .setAutoWidth(true).setResizable(true).setHeader("Usuario");

        HeaderRow hr = grid.appendHeaderRow();
        fullName = new TextField();
        fullName.setWidth("100%");
        fullName.addValueChangeListener(e ->{
            applyFilter(dataProvider);
        });
        hr.getCell(grid.getColumnByKey("fullName")).setComponent(fullName);

        idCardComplete=new TextField();
        idCardComplete.setWidth("100%");
        idCardComplete.addValueChangeListener(e->applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("idCardComplete")).setComponent(idCardComplete);

        typePerson = new ComboBox<>();
        typePerson.setWidth("100%");
        typePerson.setItems("DEPENDIENTE","INDEPENDIENTE","JURIDICA");
        typePerson.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("typePerson")).setComponent(typePerson);

        typeClient = new ComboBox<>();
        typeClient.setWidth("100%");
        typeClient.setItems("NUEVO","ANTIGUO");
        typeClient.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("typeClient")).setComponent(typeClient);

        cellPhone = new TextField();
        cellPhone.setWidth("100%");
        cellPhone.addValueChangeListener(e->applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("cellPhone")).setComponent(cellPhone);

        email = new TextField();
        email.setWidth("100%");
        email.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("email")).setComponent(email);

        loginUser = new TextField();
        loginUser.setWidth("100%");
        loginUser.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("loginUser")).setComponent(loginUser);

        return grid;
    }

    private Component createNameInfo(Client client){
        ListItem item = new ListItem(UIUtils.createInitials(client.getInitials()),client.getFullName());
        item.setHorizontalPadding(false);
        return item;
    }

    private void applyFilter(ListDataProvider<Client> dataProvider){
        dataProvider.clearFilters();
        if(fullName.getValue()!=null){
            dataProvider.addFilter(client -> StringUtils.containsIgnoreCase(fullName.getValue(),client.getFullName()));
        }
        if(idCardComplete.getValue()!=null){
            dataProvider.addFilter(client -> StringUtils.containsIgnoreCase(idCardComplete.getValue(),client.getIdCardComplete()));
        }
        if(typePerson.getValue()!=null){
            dataProvider.addFilter(client -> Objects.equals(typePerson.getValue(),client.getTypePerson()));
        }
        if(typeClient.getValue()!=null){
            dataProvider.addFilter(client -> Objects.equals(typeClient.getValue(),client.getTypeClient()));
        }
        if(cellPhone.getValue()!=null){
            dataProvider.addFilter(client ->  StringUtils.containsIgnoreCase(cellPhone.getValue(),client.getCellPhone()));
        }
        if(email.getValue()!=null){
            dataProvider.addFilter(client -> StringUtils.containsIgnoreCase(email.getValue(),client.getEmail()));
        }
        if(loginUser.getValue()!=null){
            dataProvider.addFilter(client -> StringUtils.containsIgnoreCase(loginUser.getValue(),client.getLoginUser()));
        }
    }

    private DetailsDrawer createDetailDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);
        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(current !=null && binder.writeBeanIfValid(current)){
                current.setRegisterDate(LocalDate.now());
                current.setLoginUser(VaadinSession.getCurrent().getAttribute("login").toString());
                try {
                    Client result = (Client) restTemplate.add(current);
                    if(current.getId()==null){
                        clientList.add(result);
                        grid.getDataProvider().refreshAll();
                    }else{
                        grid.getDataProvider().refreshItem(current);
                    }
                    detailsDrawer.hide();
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        footer.addCancelListener(e ->{
            footer.saveState(false);
            detailsDrawer.hide();
        });

        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }

    private void showDetails(Client client){
        current = client;
        detailsDrawerHeader.setTitle("Cliente: "+ client.getFullName());
        detailsDrawer.setContent(createDetails(client));
        detailsDrawer.show();
        binder.readBean(current);
    }

    private FormLayout createDetails(Client client){
        TextField names = new TextField();
        names.setWidth("100%");
        names.setRequired(true);
        names.setRequiredIndicatorVisible(true);

        TextField lastNames = new TextField();
        lastNames.setWidth("100%");
        lastNames.setRequired(true);
        lastNames.setRequiredIndicatorVisible(true);

        TextField idCard = new TextField();
        idCard.setWidth("70%");

        ComboBox<String> extension = new ComboBox<>();
        extension.setItems(getValueParameter("EXTENSION CARNET"));
        extension.setWidth("30%");

        FlexBoxLayout layoutIdCard = new FlexBoxLayout(idCard,extension);
        layoutIdCard.setFlexGrow(1,extension);
        layoutIdCard.setSpacing(Right.S);

        ComboBox<String> typePerson = new ComboBox<>();
        typePerson.setItems("DEPENDIENTE","INDEPENDIENTE","JURIDICA");
        typePerson.setWidth("100%");
        typePerson.setRequired(true);
        typePerson.setRequiredIndicatorVisible(true);

        ComboBox<String> typeClient = new ComboBox<>();
        typeClient.setItems("NUEVO","ANTIGUO");
        typeClient.setWidth("100%");
        typeClient.setRequired(true);
        typeClient.setRequiredIndicatorVisible(true);

        ComboBox<String> size = new ComboBox<>();
        size.setItems("MICRO EMPRESA","PEQUEÑA EMPRESA","MEDIANA EMPRESA","GRANDE EMPRESA");
        size.setWidth("100%");
        size.setRequired(true);
        size.setRequiredIndicatorVisible(true);

        ComboBox<String> caedec = new ComboBox<>();
        caedec.setItems(UtilValues.getParameterValueDescription("CAEDEC"));
        caedec.setRequired(true);
        caedec.setRequiredIndicatorVisible(true);
        caedec.setWidth("100%");
        caedec.addValueChangeListener(event -> {
            if (event.getValue()!=null) {
                String[] s = event.getValue().split("-");
                caedec.setValue(s[0]);
            }
        });

        TextField cellPhone = new TextField();
        cellPhone.setWidth("100%");

        TextField homePhone = new TextField();
        homePhone.setWidth("100%");

        TextField email = new TextField();
        email.setWidth("100%");

        TextField addressHome = new TextField();
        addressHome.setWidth("100%");

        TextField addressWork = new TextField();
        addressWork.setWidth("100%");

        binder = new BeanValidationBinder<>(Client.class);
        binder.forField(names).asRequired("Nombres del cliente es requerido").bind(Client::getNames,Client::setNames);
        binder.forField(lastNames).asRequired("Apellidos del cliente es requerido").bind(Client::getLastNames,Client::setLastNames);
        binder.forField(idCard).bind(Client::getIdCard,Client::setIdCard);
        binder.forField(extension).bind(Client::getExtension,Client::setExtension);
        binder.forField(typePerson).asRequired("Tipo de Persona es requerido").bind(Client::getTypePerson,Client::setTypePerson);
        binder.forField(typeClient).asRequired("Tipo cliente es requerido").bind(Client::getTypeClient,Client::setTypeClient);
        binder.forField(size).asRequired("Tamaño cliente es requerido").bind(Client::getSize,Client::setSize);
        binder.forField(caedec).asRequired("Caedec cliente es requerido").bind(Client::getCaedec,Client::setCaedec);
        binder.forField(cellPhone).bind(Client::getCellPhone,Client::setCellPhone);
        binder.forField(homePhone).bind(Client::getHomePhone,Client::setHomePhone);
        binder.forField(email).bind(Client::getEmail,Client::setEmail);
        binder.forField(addressHome).bind(Client::getAddressHome,Client::setAddressHome);
        binder.forField(addressWork).bind(Client::getAddressWork,Client::setAddressWork);

        binder.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Clientes"));
        });

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.addFormItem(names,"Nombres:");
        formLayout.addFormItem(lastNames,"Apellidos");
        FormLayout.FormItem idCardItem = formLayout.addFormItem(layoutIdCard,"Carnet");
        UIUtils.setColSpan(1,idCardItem);
        formLayout.addFormItem(typePerson,"Tipo Persona");
        formLayout.addFormItem(typeClient,"Tipo cliente");
        formLayout.addFormItem(size,"Tamaño cliente");
        formLayout.addFormItem(caedec,"CAEDEC ocupacion");
        formLayout.addFormItem(cellPhone,"Celular");
        formLayout.addFormItem(homePhone,"Telf. domicilio");
        formLayout.addFormItem(email,"Correo electronico");
        formLayout.addFormItem(addressHome,"Direc. Domicilio");
        formLayout.addFormItem(addressWork,"Direc. Trabajo");


        return formLayout;

    }

    private List<String> getValueParameter(String category){
        ParameterRestTemplate rest = new ParameterRestTemplate();
        List<String> values = new ArrayList<>();
        List<Parameter> parameters = rest.getParametersByCategory(category);
        for (Parameter p : parameters){
            values.add(p.getValue());
        }
        return values;
    }
}
