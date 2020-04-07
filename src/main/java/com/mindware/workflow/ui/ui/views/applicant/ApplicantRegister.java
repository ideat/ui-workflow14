package com.mindware.workflow.ui.ui.views.applicant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.Applicant;
import com.mindware.workflow.ui.backend.entity.CompanyData;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.rest.applicant.ApplicantRestTemplate;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
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
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;

@Route(value = "applicant-register", layout = MainLayout.class)
@PageTitle("Registro soliciante")
public class ApplicantRegister extends SplitViewFrame implements HasUrlParameter<String> {

    private BeanValidationBinder<Applicant> binder;
    private Applicant applicant;
    private FormLayout formPersonal;
    private FormLayout formWork;
    private FormLayout formCompany;
    private DetailsDrawerFooter footer;
    private ApplicantRestTemplate restTemplate;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footerRelations;
    private Grid<Applicant> gridApplicant;

    private TextField txtNameFilter;
    private TextField txtNumberApplicantFilter;
    private TextField txtIdCardFilter;
    private NumberField numberApplicantSpouse;

    private TextField addressCompany;
    private TextField building;
    private TextField office;
    private ComboBox<String> cityCompany;
    private TextField provinceCompany;
    private TextField blockCompany;
    private TextField phonesCompany;
    private TextField emailCompany;
    private TextField comercialNumber;
    private TextField initials;
    private NumberField antiquityArea;
    private NumberField numberEmployees;
    private TextField webPage;
    private DatePicker constitutionDate;
    private ComboBox<String> societyType;

    private List<CompanyData> companyDataList = new ArrayList<>();

    private ObjectMapper mapper = new ObjectMapper();
    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(Optional.ofNullable(applicant.getFullName()).orElse("NUEVO"));
        UI.getCurrent().getPage().setTitle(applicant.getFullName());

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String uuid) {

        restTemplate = new ApplicantRestTemplate();

        if (uuid.contains("NUEVO")){
            applicant = new Applicant();
            String[] typePerson = uuid.split("-");
            applicant.setTypePerson(typePerson[1]);
            setViewContent(createContent());
        }else {
            applicant = restTemplate.getApplicantById(UUID.fromString(uuid));
            try {
                if (applicant.getCompanyData()==null || applicant.getCompanyData().equals(""))
                    applicant.setCompanyData("[]");
                companyDataList = mapper.readValue(applicant.getCompanyData(),new TypeReference<List<CompanyData>>(){});

            } catch (IOException e) {
                e.printStackTrace();
            }
            setViewContent(createApplicant(applicant));
            setViewDetails(createDetailDrawer());
            setViewDetailsPosition(Position.BOTTOM);
        }
        binder.readBean(applicant);
    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.addTab("Datos Personales");
        appBar.addTab("Datos Laborales");
        if (applicant.getTypePerson().equals("juridica")) {
            appBar.addTab("Datos Empresa");
        }
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(
                e -> UI.getCurrent().navigate(ApplicantView.class)
        );

        formPersonal.setVisible(true);
        formWork.setVisible(false);
        formCompany.setVisible(false);
        appBar.addTabSelectionListener(e -> {
            Tab selectedTab = MainLayout.get().getAppBar().getSelectedTab();
            if (selectedTab!=null)
            if (selectedTab.getLabel().equals("Datos Personales")){
                formPersonal.setVisible(true);
                formWork.setVisible(false);
                formCompany.setVisible(false);
            }else if(selectedTab.getLabel().equals("Datos Laborales")){
                formPersonal.setVisible(false);
                formWork.setVisible(true);
                formCompany.setVisible(false);
            }else if(selectedTab.getLabel().equals("Datos Empresa")){
                formPersonal.setVisible(false);
                formWork.setVisible(false);
                formCompany.setVisible(true);
            }
        });
        appBar.centerTabs();

        return appBar;
    }

    private Component createContent(){

        FlexBoxLayout content = new FlexBoxLayout(createApplicant(applicant));
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Horizontal.RESPONSIVE_L);

        return content;
    }

    private DetailsDrawer createDetailDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        return detailsDrawer;
    }

    private DetailsDrawer createApplicant(Applicant applicant){

        NumberField numberApplicant = new NumberField();
        numberApplicant.setWidth("100%");
        numberApplicant.setReadOnly(true);

        TextField firstName = new TextField();
        firstName.setWidth("100%");
        firstName.setRequired(true);

        TextField secondName = new TextField();
        secondName.setWidth("100%");

        TextField marriedLastName = new TextField();
        marriedLastName.setWidth("100%");

        TextField lastName = new TextField();
        lastName.setWidth("100%");

        TextField motherLastName = new TextField();
        motherLastName.setWidth("100%");

        TextField homeaddress = new TextField();
        homeaddress.setWidth("100%");
        homeaddress.setRequired(true);

        TextField idCard = new TextField();
        idCard.setWidth("70%");
        idCard.setRequired(true);


        ComboBox<String> idCardExpedition = new ComboBox<>();
        idCardExpedition.setItems(getValueParameter("EXTENSION CARNET"));
        idCardExpedition.setWidth("30%");
        idCardExpedition.setRequired(true);

        FlexBoxLayout layoutIdCard = new FlexBoxLayout(idCard,idCardExpedition);
        layoutIdCard.setFlexGrow(1,idCardExpedition);
        layoutIdCard.setSpacing(Right.S);

        DatePicker dateExpirationIdCard = new DatePicker();
        dateExpirationIdCard.setWidth("100%");
        dateExpirationIdCard.setRequired(true);

        ComboBox<String> civilStatus = new ComboBox<>();
        civilStatus.setItems(getValueParameter("ESTADO CIVIL"));
        civilStatus.setWidth("100%");
        civilStatus.setRequired(true);

        NumberField dependentNumber = new NumberField();
        dependentNumber.setWidth("100%");

        DatePicker birthdate = new DatePicker();
        birthdate.setWidth("100%");
        birthdate.setRequired(true);

        ComboBox<String> gender = new ComboBox<>();
        gender.setItems(getValueParameter("GENERO"));
        gender.setWidth("100%");
        gender.setRequired(true);

        ComboBox<String> profession = new ComboBox<>();
        profession.setItems(getValueParameter("PROFESIONES"));
        profession.setWidth("100%");
        profession.setRequired(true);

        ComboBox<String> nationality = new ComboBox<>();
        nationality.setItems(getValueParameter("NACIONALIDAD"));
        nationality.setWidth("100%");
        nationality.setRequired(true);

        ComboBox<String> caedec = new ComboBox<>();
        caedec.setItems(UtilValues.getParameterValueDescription("CAEDEC"));
        caedec.setRequired(true);
        caedec.setWidth("100%");
        caedec.addValueChangeListener(event -> {
            if (event.getValue()!=null) {
                String[] s = event.getValue().split("-");
                caedec.setValue(s[0]);
            }
        });


        DatePicker registerDate = new DatePicker();
        registerDate.setWidth("100%");
        registerDate.setRequired(true);

        TextField cellphone = new TextField();
        cellphone.setWidth("100%");
        cellphone.setRequired(true);

        TextField homephone = new TextField();
        homephone.setWidth("100%");
        homephone.setRequired(true);

        TextField workphone = new TextField();
        workphone.setWidth("100%");

        TextField workcellphone = new TextField();
        workcellphone.setWidth("100%");

        TextField workaddress = new TextField();
        workaddress.setWidth("100%");

        TextField nameCompanyWork = new TextField();
        nameCompanyWork.setWidth("100%");

        TextField position = new TextField();
        position.setWidth("100%");

        NumberField workingtime = new NumberField();
        workingtime.setWidth("100%");

        TextField nit = new TextField();
        nit.setWidth("100%");

        ComboBox<String> city = new ComboBox<>();
        city.setWidth("100%");
        city.setItems("ORURO","COCHABAMBA","SANTA CRUZ",
                "POTOSI","TARIJA","CHUQUISACA","PANDO","LA PAZ");
        city.setAllowCustomValue(true);

        TextField province = new TextField();
        province.setWidth("100%");

        TextField block = new TextField();
        block.setWidth("100%");

        ComboBox<String> typeHome = new ComboBox<>();
        typeHome.setWidth("100%");
        typeHome.setItems("Casa","Departamento","Otro");
        typeHome.setAllowCustomValue(true);

        DatePicker customerFrom = new DatePicker();
        customerFrom.setWidth("100%");

        TextField savingAccount = new TextField();
        savingAccount.setWidth("100%");


        numberApplicantSpouse = new NumberField();
        numberApplicantSpouse.setWidth("100%");
        numberApplicantSpouse.setReadOnly(true);

        //Company Data
        societyType = new ComboBox<>();
        societyType.setWidth("100%");
        societyType.setItems("SRL","LTDA","RL","SA");
        societyType.setPlaceholder("Seleccionar");


        addressCompany = new TextField();
        addressCompany.setWidth("100%");

        building = new TextField();
        building.setWidth("100%");

        office = new TextField();
        office.setWidth("100%");

        cityCompany = new ComboBox<>();
        cityCompany.setItems("ORURO","COCHABAMBA","SANTA CRUZ",
                "POTOSI","TARIJA","CHUQUISACA","PANDO","LA PAZ");
        cityCompany.setAllowCustomValue(true);
        cityCompany.setWidth("100%");

        provinceCompany = new TextField();
        provinceCompany.setWidth("100%");

        blockCompany = new TextField();
        blockCompany.setWidth("100%");

        phonesCompany = new TextField();
        phonesCompany.setWidth("100%");

        emailCompany = new TextField();
        emailCompany.setWidth("100%");

        comercialNumber = new TextField();
        comercialNumber.setWidth("100%");

        initials = new TextField();
        initials.setWidth("100%");

        antiquityArea = new NumberField();
        antiquityArea.setWidth("100%");
        antiquityArea.setValue(0.0);

        numberEmployees = new NumberField();
        numberEmployees.setWidth("100%");
        numberEmployees.setValue(0.0);

        webPage = new TextField();
        webPage.setWidth("100%");

        constitutionDate = new DatePicker();
        constitutionDate.setWidth("100%");

        Button btnSeachApplicant = new Button();
        btnSeachApplicant.addThemeVariants(ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_PRIMARY);
        btnSeachApplicant.setIcon(VaadinIcon.SEARCH.create());
        btnSeachApplicant.addClickListener(event -> showSearch());

        FlexBoxLayout searchApplicant = new FlexBoxLayout(numberApplicantSpouse,btnSeachApplicant);
        searchApplicant.setFlexGrow(1, btnSeachApplicant);
        searchApplicant.setSpacing(Right.S);


        binder = new BeanValidationBinder<>(Applicant.class);
        binder.forField(numberApplicant).withConverter(new DoubleToIntegerConverter()).bind(Applicant::getNumberApplicant,Applicant::setNumberApplicant);
        binder.forField(firstName).asRequired("Primer nombre es requerido").bind(Applicant::getFirstName,Applicant::setFirstName);
        binder.forField(secondName).bind(Applicant::getSecondName,Applicant::setSecondName);
        binder.forField(marriedLastName).bind(Applicant::getMarriedLastName,Applicant::setMarriedLastName);
        binder.forField(lastName).bind(Applicant::getLastName, Applicant::setLastName);
        binder.forField(motherLastName).bind(Applicant::getMotherLastName,Applicant::setMotherLastName);
        binder.forField(homeaddress).asRequired("Direccion domicilio es requerido").bind(Applicant::getHomeaddress,Applicant::setHomeaddress);
        binder.forField(idCard).asRequired("Carnet es requerido").bind(Applicant::getIdCard,Applicant::setIdCard);
        binder.forField(idCardExpedition).asRequired("Extension carnet es requerida").bind(Applicant::getIdCardExpedition,Applicant::setIdCardExpedition);
        binder.forField(dateExpirationIdCard).asRequired("Fecha expiracion carnet es requerida")
                .bind(Applicant::getDateExpirationIdCard,Applicant::setDateExpirationIdCard);
        binder.forField(civilStatus).asRequired("Estado civil es requerido").bind(Applicant::getCivilStatus,Applicant::setCivilStatus);
        binder.forField(dependentNumber).withConverter(new DoubleToIntegerConverter()).bind(Applicant::getDependentNumber,Applicant::setDependentNumber);
        binder.forField(birthdate).asRequired("Fecha nacimiento requerida").bind(Applicant::getBirthdate,Applicant::setBirthdate);
        binder.forField(gender).asRequired("Genero es requerido").bind(Applicant::getGender,Applicant::setGender);
        binder.forField(profession).asRequired("Ocupacion es requerida").bind(Applicant::getProfession,Applicant::setProfession);
        binder.forField(nationality).asRequired("Nacionalidad es requerida").bind(Applicant::getNationality,Applicant::setNationality);
        binder.forField(caedec).asRequired("CAEDEC ocupacion es requerido").bind(Applicant::getCaedec,Applicant::setCaedec);
        binder.forField(cellphone).asRequired("Nro celular es requerido").bind(Applicant::getCellphone,Applicant::setCellphone);
        binder.forField(homephone).bind(Applicant::getHomephone,Applicant::setHomephone);
        binder.forField(workcellphone).bind(Applicant::getWorkcellphone,Applicant::setWorkcellphone);
        binder.forField(workaddress).bind(Applicant::getWorkaddress,Applicant::setWorkaddress);
        binder.forField(nameCompanyWork).bind(Applicant::getNameCompanyWork,Applicant::setNameCompanyWork);
        binder.forField(workphone).bind(Applicant::getWorkphone,Applicant::setWorkphone);
        binder.forField(position).bind(Applicant::getPosition,Applicant::setPosition);
        binder.forField(workingtime).withConverter(new DoubleToIntegerConverter()).bind(Applicant::getWorkingtime,Applicant::setWorkingtime);
        binder.forField(nit).bind(Applicant::getNit,Applicant::setNit);
        binder.forField(numberApplicantSpouse).withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(Applicant::getNumberApplicantSpouse,Applicant::setNumberApplicantSpouse);
        binder.forField(city).bind(Applicant::getCity, Applicant::setCity);
        binder.forField(province).bind(Applicant::getProvince,Applicant::setProvince);
        binder.forField(block).bind(Applicant::getBlock, Applicant::setBlock);
        binder.forField(typeHome).bind(Applicant::getTypeHome, Applicant::setTypeHome);
        binder.forField(customerFrom).bind(Applicant::getCustomerFrom,Applicant::setCustomerFrom);
        binder.forField(savingAccount).bind(Applicant::getSavingAccount,Applicant::setSavingAccount);

        binder.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges() ;
//           footer.saveState(isValid && hasChanges);
            footer.saveState(GrantOptions.grantedOption("Solicitantes"));
        });


        formPersonal = new FormLayout();
        formPersonal.setSizeUndefined();
        formPersonal.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        formPersonal.addFormItem(numberApplicant,"Numero aplicante");
        formPersonal.addFormItem(firstName, "Primer nombre");
        formPersonal.addFormItem(secondName,"Segundo nombre");
        formPersonal.addFormItem(marriedLastName,"Apellido casada");
        formPersonal.addFormItem(lastName,"Apellido paterno");
        formPersonal.addFormItem(motherLastName,"Apellido materno");
        formPersonal.addFormItem(city, "Ciudad");
        formPersonal.addFormItem(province,"Provincia");
        formPersonal.addFormItem(block,"Manzaono/UV");
        formPersonal.addFormItem(typeHome,"Tipo vivienda");
        formPersonal.addFormItem(homeaddress,"Dir. domicilio");
//        formPersonal.addFormItem(idCard,"Nro carnet");
//        formPersonal.addFormItem(idCardExpedition,"Extensión");
        FormLayout.FormItem idCardItem = formPersonal.addFormItem(layoutIdCard,"Carnet identidad");
        UIUtils.setColSpan(1,idCardItem);
        formPersonal.addFormItem(dateExpirationIdCard,"Expiración");
        formPersonal.addFormItem(civilStatus,"Estado civil");
        formPersonal.addFormItem(dependentNumber,"Nro. depen");
        formPersonal.addFormItem(birthdate,"Nacimiento");
        formPersonal.addFormItem(gender,"Genero");
        formPersonal.addFormItem(profession,"Ocupación");
        formPersonal.addFormItem(nationality,"Nacionalidad");
        formPersonal.addFormItem(caedec,"CAEDEC");
        formPersonal.addFormItem(cellphone,"Celular");
        formPersonal.addFormItem(homephone,"Telf. dom.");
        formPersonal.addFormItem(customerFrom,"Cliente desde");
        FormLayout.FormItem numberAplicantSpouseItem = formPersonal.addFormItem(searchApplicant,"Seleccionar Conyuge");
        formPersonal.addFormItem(savingAccount,"Caja Ahorro");
        UIUtils.setColSpan(1,numberAplicantSpouseItem);

        formWork = new FormLayout();
        formWork.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formWork.addFormItem(workphone,"Telf. trabajo");
        formWork.addFormItem(workcellphone,"Celular trab.");
        formWork.addFormItem(workaddress,"Direccion trabajo");
        formWork.addFormItem(nameCompanyWork,"Nombre empresa");
        formWork.addFormItem(workingtime,"Antiguedad (meses)");
        formWork.addFormItem(nit,"NIT empresa");

        formCompany = new FormLayout();
        formCompany.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        formCompany.addFormItem(addressCompany,"Direccion");
        formCompany.addFormItem(building,"Edif/Cond");
        formCompany.addFormItem(cityCompany,"Ciudad");
        formCompany.addFormItem(provinceCompany,"Provincia");
        formCompany.addFormItem(blockCompany,"Manzano/UV");
        formCompany.addFormItem(phonesCompany,"Telefonos");
        formCompany.addFormItem(societyType,"Tipo sociedad");
        formCompany.addFormItem(comercialNumber, "Nro. Matricula comercial");
        formCompany.addFormItem(initials,"Siglas");
        formCompany.addFormItem(antiquityArea,"Antiguedad rubro (meses)");
        formCompany.addFormItem(numberEmployees,"Numero empleados");
        formCompany.addFormItem(webPage,"Pagina Web");
        formCompany.addFormItem(constitutionDate,"Fecha constitucion");


        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e -> {
            if(binder.writeBeanIfValid(applicant)){
                applicant.setRegisterDate(LocalDate.now());
                applicant.setLoginUser(VaadinSession.getCurrent().getAttribute("login").toString()); //FIXME  GET FROM LOGIN USER
                applicant.setIdOffice(Integer.valueOf(VaadinSession.getCurrent().getAttribute("idOffice").toString())); //FIXME GET FROM LOGIN USER
                createCompanyDataList();
                try {
                    String com = mapper.writeValueAsString(companyDataList);
                    applicant.setCompanyData(com);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }

                try {
                    Applicant result = (Applicant) restTemplate.addApplicant(applicant);
                    UI.getCurrent().navigate(ApplicantView.class);
                    UIUtils.showNotification("Datos guardados");
                }catch (HttpClientErrorException err){
                    UIUtils.showNotification("Error " + err);
                } catch (IOException ex) {

                    UIUtils.dialogError("Error",ex.getMessage());
                }
            }
        });

        footer.addCancelListener( e -> UI.getCurrent().navigate(ApplicantView.class));
        DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("REGISTRO");

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("100%");
        detailsDrawer.setContent(formPersonal,formWork, formCompany);
        detailsDrawer.setFooter(footer);
        detailsDrawer.show();
        fillCompanyData();
        return detailsDrawer;

    }

    private void createCompanyDataList(){
        LocalDate date;
        CompanyData companyData = new CompanyData();
        companyData.setAddressCompany(addressCompany.getValue());
        companyData.setBuilding(building.getValue());
        companyData.setOffice(office.getValue());
        companyData.setCityCompany(cityCompany.getValue());
        companyData.setProvinceCompany(provinceCompany.getValue());
        companyData.setBlockCompany(blockCompany.getValue());
        companyData.setPhonesCompany(phonesCompany.getValue());
        companyData.setEmailCompany(emailCompany.getValue());
        companyData.setComercialNumber(comercialNumber.getValue());
        companyData.setInitials(initials.getValue());
        companyData.setAntiquityArea(antiquityArea.getValue().intValue());
        companyData.setNumberEmployees(numberEmployees.getValue().intValue());
        companyData.setWebpage(webPage.getValue());
        date = constitutionDate.getValue();
        companyData.setConstitutionDate(date);
        companyData.setSocietyType(societyType.getValue());

        companyDataList.clear();
        companyDataList.add(companyData);
    }

    private void fillCompanyData(){
        for(CompanyData c : companyDataList) {
            addressCompany.setValue(c.getAddressCompany());
            building.setValue(c.getBuilding());
            office.setValue(c.getOffice());
            cityCompany.setValue(c.getCityCompany());
            provinceCompany.setValue(c.getProvinceCompany());
            blockCompany.setValue(c.getBlockCompany());
            phonesCompany.setValue(c.getPhonesCompany());
            emailCompany.setValue(c.getEmailCompany());
            comercialNumber.setValue(c.getComercialNumber());
            initials.setValue(c.getInitials());
            antiquityArea.setValue(c.getAntiquityArea().doubleValue());
            numberEmployees.setValue(c.getNumberEmployees().doubleValue());
            webPage.setValue(c.getWebpage());
            constitutionDate.setValue(c.getConstitutionDate());
            societyType.setValue(c.getSocietyType());
        }
    }

    private void showSearch(){

        detailsDrawerHeader.setTitle("Seleccionar Conyuge");
        detailsDrawer.setContent(searchApplicant());
        detailsDrawer.show();
    }

    private Grid searchApplicant(){
        ApplicantRestTemplate rest = new ApplicantRestTemplate();

        List<Applicant>  applicantList = new ArrayList<>(rest.getAllApplicants());
        applicantList.removeIf(e -> e.getNumberApplicant().equals(applicant.getNumberApplicant()));

        ListDataProvider<Applicant> data = new ListDataProvider<>(applicantList);
        gridApplicant = new Grid<>();
        gridApplicant.setWidthFull();
        gridApplicant.setDataProvider(data);

        gridApplicant.addColumn(Applicant::getNumberApplicant).setHeader("Solicitante")
                .setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_S).setKey("number");
        gridApplicant.addColumn(Applicant::getFullName).setHeader("Nombre")
                .setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_XL).setKey("name");
        gridApplicant.addColumn(Applicant::getIdCardComplet).setHeader("Carnet")
                .setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_M).setKey("idcard");
        gridApplicant.addColumn(new ComponentRenderer<>(this::createSelectApplicant)).setWidth(UIUtils.COLUMN_WIDTH_M);
        HeaderRow hr = gridApplicant.appendHeaderRow();

        txtNumberApplicantFilter = new TextField();
        txtNumberApplicantFilter.setValueChangeMode(ValueChangeMode.EAGER);
        txtNumberApplicantFilter.setWidth("100%");
        txtNumberApplicantFilter.addValueChangeListener(e -> {
            applyFilter(data);
        });
        hr.getCell(gridApplicant.getColumnByKey("number")).setComponent(txtNumberApplicantFilter);

        txtNameFilter = new TextField();
        txtNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        txtNameFilter.setWidth("100%");
        txtNameFilter.addValueChangeListener(e ->{
            applyFilter(data);
        });
        hr.getCell(gridApplicant.getColumnByKey("name")).setComponent(txtNameFilter);

        txtIdCardFilter = new TextField();
        txtIdCardFilter.setValueChangeMode(ValueChangeMode.EAGER);
        txtIdCardFilter.setWidth("100%");
        txtIdCardFilter.addValueChangeListener(e -> {
            applyFilter(data);
        });
        hr.getCell(gridApplicant.getColumnByKey("idcard")).setComponent(txtIdCardFilter);

        return gridApplicant ;
    }

    private Component createSelectApplicant(Applicant applicant){
        Button btnSelect = new Button();
        btnSelect.setIcon(VaadinIcon.CHEVRON_CIRCLE_UP.create());

        btnSelect.addClickListener(e-> {
            numberApplicantSpouse.setValue(Double.parseDouble(applicant.getNumberApplicant().toString()));

            detailsDrawer.hide();

        });
        return btnSelect;
    }

    private void applyFilter(ListDataProvider<Applicant> dataProvider){
        dataProvider.clearFilters();
        if(!txtNumberApplicantFilter.getValue().trim().equals("")){
            dataProvider.addFilter(applicant -> Objects.equals(txtNumberApplicantFilter.getValue().trim(), applicant.getNumberApplicant().toString()));
        }
        if(!txtNameFilter.getValue().trim().equals("")){
            dataProvider.addFilter(applicant -> StringUtils.containsIgnoreCase(applicant.getFullName(),txtNameFilter.getValue()));
        }
        if(!txtIdCardFilter.getValue().trim().equals("")){
            dataProvider.addFilter(applicant -> StringUtils.containsIgnoreCase(applicant.getIdCardComplet(),txtIdCardFilter.getValue()));
        }
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

    public class DoubleToIntegerConverter implements Converter<Double, Integer> {

        private static final long serialVersionUID = 1L;

        @Override
        public Result<Integer> convertToModel(Double presentation, ValueContext valueContext) {
            return Result.ok(presentation.intValue());
        }

        @Override
        public Double convertToPresentation(Integer model, ValueContext valueContext) {
            if (model==null)
                return 0.0;
            return model.doubleValue();
        }

    }
}
