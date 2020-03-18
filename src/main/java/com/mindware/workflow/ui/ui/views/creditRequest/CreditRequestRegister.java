package com.mindware.workflow.ui.ui.views.creditRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.Applicant;
import com.mindware.workflow.ui.backend.entity.CreditRequestApplicant;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.creditRequest.Charge;
import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.entity.creditRequest.LinkUp;
import com.mindware.workflow.ui.backend.entity.creditRequest.PaymentPlan;
import com.mindware.workflow.ui.backend.entity.dto.CreditRequestApplicantDto;
import com.mindware.workflow.ui.backend.entity.exceptions.Exceptions;
import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequest;
import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequestDto;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.enums.TypeLinkUp;
import com.mindware.workflow.ui.backend.rest.applicant.ApplicantRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditRequest.CreditRequestRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditRequestApplicant.CreditRequestAplicantRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditRequestApplicantDto.CreditRequestApplicantDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsCreditRequestRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsRestTemplate;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementRestTemplate;
import com.mindware.workflow.ui.backend.rest.paymentPlan.PaymentPlanRestTemplate;
import com.mindware.workflow.ui.backend.rest.stageHistory.StageHistoryRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Right;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "creditrequest-register", layout = MainLayout.class)
@PageTitle("Registro solicitudes de credito")
public class CreditRequestRegister extends SplitViewFrame implements HasUrlParameter<String> {
    private BeanValidationBinder<CreditRequest> binder;
    private CreditRequest current;
    private FormLayout formRequest;
    private CreditRequestRestTemplate restTemplate;
    private CreditRequestApplicantDtoRestTemplate creditRequestApplicantDtoRestTemplate;
    private PatrimonialStatementRestTemplate patrimonialStatementRestTemplate;
    private CreditRequestAplicantRestTemplate creditRequestAplicantRestTemplate;
    private CreditRequestApplicant creditRequestApplicant;
    private PaymentPlanRestTemplate paymentPlanRestTemplate;
    private ExceptionsCreditRequestRestTemplate exceptionsCreditRequestRestTemplate;
    private ExceptionsCreditRequestDtoRestTemplate exceptionsCreditRequestDtoRestTemplate;


    private DetailsDrawerFooter footer;
    private ListDataProvider<LinkUp> dataProviderLinkUp;

    private TextField txtNameFilter;
    private TextField txtNumberApplicantFilter;
    private TextField txtIdCardFilter;
    private Grid<Applicant> gridApplicant;
    private Grid<Charge> gridCharge;
    private TextField numberApplicant;
    private TextField numberRequest;

    private TextField internalCodeException;
    private TextField typeException;
    private TextField descriptionException;
    private TextField daysException;

    private BeanValidationBinder<LinkUp> binderRelations;
    private LinkUp currenRelation;
    private List<LinkUp> linkUpList;
    private List<Charge> chargeList;
    private List<PaymentPlan> paymentPlanList;
    private ListDataProvider<CreditRequestApplicantDto> dataProviderCreditRequestApplicant;
    private List<CreditRequestApplicantDto> creditRequestApplicantDtoList;
    private BeanValidationBinder<ExceptionsCreditRequest> binderExceptionCreditRequest;
    private List<ExceptionsCreditRequestDto> exceptionsCreditRequestDtoList;
    private ListDataProvider<ExceptionsCreditRequestDto> dataProviderExceptionsCreditRequestDto;

    private ListDataProvider<Charge> dataCharge;
    private Grid<CreditRequestApplicantDto> gridCodebtorGuarantor;
    private Grid<ExceptionsCreditRequestDto> gridExceptionsCreditRequestDto;
    private Grid<Exceptions> gridExceptions;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footerRelations;

    private FlexBoxLayout contentCreditRequest;
    private FlexBoxLayout contentRelations;
    private FlexBoxLayout contentPaymentPlan;
    private FlexBoxLayout contentCodebtor;
    private FlexBoxLayout contentExceptionsCreditRequest;

    private List<String> listTabs = new ArrayList<>();

    private Integer paramNumberRequest;
    private Integer paramNumberApplicant;
    private String typeRelation;
    private String jsonCharge;
    private String nameTabSelected;
    private UUID idCreditRequest;
    private Location currentLocation;
    private Map<String,List<String>> paramCredit;



    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        if(paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
            appBar.setTitle(paramCredit.get("numberRequest").get(0));
        }else{
            appBar.setTitle(paramCredit.get("fullName").get(0));
        }
        UI.getCurrent().getPage().setTitle("");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        currentLocation = beforeEvent.getLocation();
        restTemplate = new CreditRequestRestTemplate();
        paymentPlanRestTemplate = new PaymentPlanRestTemplate();
        patrimonialStatementRestTemplate = new PatrimonialStatementRestTemplate();
        creditRequestAplicantRestTemplate = new CreditRequestAplicantRestTemplate();
        exceptionsCreditRequestRestTemplate = new ExceptionsCreditRequestRestTemplate();
        exceptionsCreditRequestDtoRestTemplate = new ExceptionsCreditRequestDtoRestTemplate();
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        paramCredit = queryParameters.getParameters();
        typeRelation = paramCredit.get("typeRelation").get(0);



        if(paramCredit.get("numberRequest").get(0).equals("NUEVO")){
            current = new CreditRequest();
            current.setLoginUser(VaadinSession.getCurrent().getAttribute("login").toString());
            current.setCharge(getCharge());
            contentCreditRequest = (FlexBoxLayout) createContent(createCreditRequest(current));
//            contentPatrimonialStatement = (FlexBoxLayout) createContent(createMenuPatrimonialStatement());
            contentRelations = (FlexBoxLayout) createContent(createRelations());
//            contentPaymentPlan = (FlexBoxLayout) createContent(createPaymentPlan());

            setViewContent(contentCreditRequest,contentRelations);//,contentPatrimonialStatement,contentPaymentPlan);
//            contentPatrimonialStatement.setVisible(false);
            contentRelations.setVisible(false);
//            contentPaymentPlan.setVisible(false);
            binder.readBean(current);
        }else{
            idCreditRequest = UUID.fromString(paramCredit.get("idCreditRequest").get(0));
            current = restTemplate.getCreditRequestById(idCreditRequest);
            paymentPlanList = paymentPlanRestTemplate.add(current);

            paramNumberApplicant = Integer.parseInt(paramCredit.get("numberApplicant").get(0));
            paramNumberRequest = Integer.parseInt(paramCredit.get("numberRequest").get(0));

            creditRequestApplicant = creditRequestAplicantRestTemplate.getCreditRequestApplicant(
                    paramNumberRequest,paramNumberApplicant,typeRelation);
            contentCreditRequest = (FlexBoxLayout) createContent(createCreditRequest(current));
//            contentPatrimonialStatement = (FlexBoxLayout) createContent(createMenuPatrimonialStatement());
            contentRelations = (FlexBoxLayout) createContent(createRelations());
            contentPaymentPlan = (FlexBoxLayout) createContent(createPaymentPlan());
            contentCodebtor = (FlexBoxLayout) createContent(createCodebtorGuarantor());
            contentExceptionsCreditRequest = (FlexBoxLayout) createContent(createExceptions());
            setViewContent(contentCreditRequest,/*contentPatrimonialStatement,*/contentRelations
                    ,contentPaymentPlan,contentCodebtor,contentExceptionsCreditRequest);
//            contentPatrimonialStatement.setVisible(false);
            contentRelations.setVisible(false);
            contentPaymentPlan.setVisible(false);
            contentCodebtor.setVisible(false);
            contentExceptionsCreditRequest.setVisible(false);
            numberApplicant.setValue(paramCredit.get("numberApplicant").get(0));

            binder.readBean(current);

            numberRequest.setReadOnly(false);
            numberRequest.setValue(paramCredit.get("numberRequest").get(0));
            numberRequest.setReadOnly(true);

        }
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
    }

    private String getCharge()  {
        ParameterRestTemplate rest = new ParameterRestTemplate();
        List<Parameter> parameterList = new ArrayList<>(rest.getParametersByCategory("CARGOS FINANCIEROS"));
        ObjectMapper mapper = new ObjectMapper();
        List<Charge> chargeList = new ArrayList<>();
        for(Parameter p : parameterList){
            Charge charge = new Charge();
            charge.setId(UUID.randomUUID());
            charge.setName(p.getDescription());
            charge.setValue(Double.parseDouble(p.getValue()));
            charge.setSelected(false);
            chargeList.add(charge);
        }
        String jsonCharge="[]";
        try {
            jsonCharge = mapper.writeValueAsString(chargeList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonCharge;

    }



    private AppBar initAppBar(){
        MainLayout.get().getAppBar().reset();
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.addTab("Datos solicitud");
        appBar.addTab("Relaciones");

        nameTabSelected = "";
        if(!paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
//            appBar.addTab("Declaracion patrimonial");
            appBar.addTab("Plan de pagos");
            appBar.addTab("Codeudores");
            appBar.addTab("Garantes");
            appBar.addTab("Excepciones");

        }

        appBar.centerTabs();

        listTabs.add("Datos solicitud");
        listTabs.add("Relaciones");
        if(!paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
//            listTabs.add("Declaracion patrimonial");
            listTabs.add("Plan de pagos");
            listTabs.add("Codeudores");
            listTabs.add("Garantes");
            listTabs.add("Excepciones");

        }
        appBar.addTabSelectionListener(e ->{
            if(e.getSource().getSelectedTab()!=null) {
                if(listTabs.contains(e.getSource().getSelectedTab().getLabel())) {
                    Tab selectedTab = appBar.getSelectedTab();
                    nameTabSelected = selectedTab.getLabel();
                    if (selectedTab.getLabel().equals("Datos solicitud")) {
                        contentCreditRequest.setVisible(true);
                        contentRelations.setVisible(false);
                        if(!paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
//                            contentPatrimonialStatement.setVisible(false);
                            contentPaymentPlan.setVisible(false);
                            contentCodebtor.setVisible(false);
                            contentExceptionsCreditRequest.setVisible(false);
                        }
                    } else if(selectedTab.getLabel().equals("Relaciones")) {
                        contentCreditRequest.setVisible(false);
                        contentRelations.setVisible(true);
                        if(!paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
//                            contentPatrimonialStatement.setVisible(false);
                            contentPaymentPlan.setVisible(false);
                            contentCodebtor.setVisible(false);
                            contentExceptionsCreditRequest.setVisible(false);
                        }
                    } else if(selectedTab.getLabel().equals("Plan de pagos")){
                        contentCreditRequest.setVisible(false);
                        contentRelations.setVisible(false);
                        contentCodebtor.setVisible(false);
                        contentExceptionsCreditRequest.setVisible(false);
                        if(!paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
//                            contentPatrimonialStatement.setVisible(false);
                            contentPaymentPlan.setVisible(true);
                            contentCodebtor.setVisible(false);
                        }
                    } else if(selectedTab.getLabel().equals("Codeudores") || (selectedTab.getLabel().equals("Garantes"))){
                        contentCreditRequest.setVisible(false);
                        contentRelations.setVisible(false);
                        if(!paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
//                            contentPatrimonialStatement.setVisible(false);
                            contentPaymentPlan.setVisible(false);
                            contentCodebtor.setVisible(true);
                            contentExceptionsCreditRequest.setVisible(false);
                            if (selectedTab.getLabel().equals("Codeudores")){
                                filterTypeRelation("codeudor");
                            }else if (selectedTab.getLabel().equals("Garantes")){
                                filterTypeRelation("garante");
                            }

                        }
                    } else if(selectedTab.getLabel().equals("Excepciones")){
                        contentCreditRequest.setVisible(false);
                        contentRelations.setVisible(false);
                        contentCodebtor.setVisible(false);
                        contentPaymentPlan.setVisible(false);
                        contentExceptionsCreditRequest.setVisible(true);
                    } else {
                        contentCreditRequest.setVisible(false);
                        contentRelations.setVisible(false);
                        if(!paramCredit.get("numberRequest").get(0).equals("NUEVO")) {
//                            contentPatrimonialStatement.setVisible(true);
                            contentPaymentPlan.setVisible(false);
                            contentCodebtor.setVisible(false);
                            contentExceptionsCreditRequest.setVisible(false);
                        }
                    }
                }
            }
        });

        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener( e -> UI.getCurrent().navigate(CreditRequestView.class));
        return appBar;
    }





    private Component createContent(DetailsDrawer component){
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setMargin(Vertical.AUTO, Vertical.RESPONSIVE_L);
        content.setMaxWidth("1024px");

        return content;
    }

    private DetailsDrawer createExceptions(){
        VerticalLayout topLayout = new VerticalLayout();
        topLayout.setWidthFull();
        gridExceptionsCreditRequestDto = new Grid<>();
        Button btnCreateException = new Button("Agregar");
        btnCreateException.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreateException.setEnabled(GrantOptions.grantedOption("Solicitud"));
        btnCreateException.addClickListener(e -> showCreateException(new ExceptionsCreditRequest()));

        topLayout.add(btnCreateException);
        exceptionsCreditRequestRestTemplate = new ExceptionsCreditRequestRestTemplate();

        gridExceptionsCreditRequestDto.addSelectionListener(event -> {
           if(event.getFirstSelectedItem().isPresent()){
               String codeException = event.getFirstSelectedItem().get().getInternalCode();
               ExceptionsCreditRequest exceptionsCreditRequest = exceptionsCreditRequestRestTemplate.getByCodeExceptionNumberRequest(codeException,paramNumberRequest);
               showCreateException(exceptionsCreditRequest);
           }
        });

        exceptionsCreditRequestDtoList = new ArrayList<>(exceptionsCreditRequestDtoRestTemplate.getByNumberRequest(paramNumberRequest));
        dataProviderExceptionsCreditRequestDto = new ListDataProvider<>(exceptionsCreditRequestDtoList);
        gridExceptionsCreditRequestDto.setDataProvider(dataProviderExceptionsCreditRequestDto);
        gridExceptionsCreditRequestDto.setWidthFull();
        gridExceptionsCreditRequestDto.addColumn(ExceptionsCreditRequestDto::getInternalCode).setHeader("Cod. Excepcion")
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true);
        gridExceptionsCreditRequestDto.addColumn(ExceptionsCreditRequestDto::getExceptionDetail).setHeader("Excepcion")
                .setFlexGrow(1).setResizable(true);
        gridExceptionsCreditRequestDto.addColumn(new ComponentRenderer<>(this::createButtonDeleteException)).setResizable(true);
        topLayout.add(gridExceptionsCreditRequestDto);
//        VerticalLayout contentLayout = new VerticalLayout();
//        contentLayout.setWidthFull();
//        contentLayout.setHeightFull();
//        contentLayout.add(topLayout,gridExceptionsCreditRequestDto);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("100%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(topLayout);

        return detailsDrawer;
    }

    private DetailsDrawer createCodebtorGuarantor(){
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
        gridCodebtorGuarantor = new Grid<>();
        gridCodebtorGuarantor.setWidthFull();
        Button btnCreatGC = new Button("Agregar");
        btnCreatGC.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreatGC.setEnabled(GrantOptions.grantedOption("Solicitud"));
        btnCreatGC.addClickListener(e ->{
            showSearch();
        });

        topLayout.add(btnCreatGC);
        creditRequestApplicantDtoRestTemplate = new CreditRequestApplicantDtoRestTemplate();
        //FIXME admin change by logged user login
        creditRequestApplicantDtoList = new ArrayList<>(creditRequestApplicantDtoRestTemplate.getByLoginNumberRequest(VaadinSession.getCurrent().getAttribute("login").toString(),paramNumberRequest));
        dataProviderCreditRequestApplicant = new ListDataProvider<>(creditRequestApplicantDtoList);

//        gridCodebtorGuarantor.addThemeVariants(GridVariant.LUMO_COMPACT,GridVariant.MATERIAL_COLUMN_DIVIDERS);
        gridCodebtorGuarantor.setDataProvider(dataProviderCreditRequestApplicant);

        gridCodebtorGuarantor.addColumn(CreditRequestApplicantDto::getNumberApplicant).setHeader("Codigo")
                .setFlexGrow(0).setResizable(true);

        gridCodebtorGuarantor.addColumn(new ComponentRenderer<>(this::createNameInfo)).setHeader("Nombre completo")
                .setFlexGrow(0).setResizable(true);

//        gridCodebtorGuarantor.addColumn(new ComponentRenderer<>(this::createButtonPatrimonialStatement))
//                .setFlexGrow(0).setResizable(true);//.setHeader("Declaracion Patrimonial");

        gridCodebtorGuarantor.addColumn(new ComponentRenderer<>(this::createButtonDelete))
                .setFlexGrow(0).setResizable(true);
        gridCodebtorGuarantor.getColumns().forEach(column -> {
//              column.setAutoWidth(true);
            column.setWidth(UIUtils.COLUMN_WIDTH_XL);
//            column.setTextAlign(ColumnTextAlign.CENTER);
        });

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.add(topLayout,gridCodebtorGuarantor);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("100%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(contentLayout);

        return detailsDrawer;
    }

    private void filterTypeRelation(String typeRelation){

        Tab selectedTab = MainLayout.get().getAppBar().getSelectedTab();
        if (selectedTab != null) {
            if (dataProviderCreditRequestApplicant.getItems().size() > 0)
                dataProviderCreditRequestApplicant.setFilterByValue(CreditRequestApplicantDto::getTypeRelation,
                        typeRelation);
        }

    }

//    private Component createButtonPatrimonialStatement(CreditRequestApplicantDto creditRequestApplicantDto){
//        Button btn = UIUtils.createPrimaryButton("Declaracion Patrimonial");
//        btn.addClickListener(e ->{
//
//        });
//
//        return btn;
//    }

    private Component createButtonDelete(CreditRequestApplicantDto creditRequestApplicantDto){
        Button btn =UIUtils.createErrorPrimaryButton(VaadinIcon.TRASH);
        btn.setEnabled(GrantOptions.grantedOption("Solicitud"));
        btn.addClickListener(e -> {
            CreditRequestAplicantRestTemplate rest = new CreditRequestAplicantRestTemplate();
            creditRequestApplicantDtoList.remove(creditRequestApplicantDto);
            rest.delete(creditRequestApplicantDto.getId().toString());

            gridCodebtorGuarantor.getDataProvider().refreshAll();
            UIUtils.showNotification("Relacion Credito y Solicitante borrada");
        });

        return btn;
    }

    private Component createButtonDeleteException(ExceptionsCreditRequestDto exceptionsCreditRequestDto){
        Button btn = UIUtils.createErrorPrimaryButton(VaadinIcon.TRASH);
        btn.setEnabled(GrantOptions.grantedOption("Solicitud"));
        btn.addClickListener(e ->{
            ExceptionsCreditRequestRestTemplate rest = new ExceptionsCreditRequestRestTemplate();
            exceptionsCreditRequestDtoList.remove(exceptionsCreditRequestDto);
            rest.delete(exceptionsCreditRequestDto.getIdExceptions());
            gridExceptionsCreditRequestDto.getDataProvider().refreshAll();
            UIUtils.showNotification("Excepcion borrada");
        });

        return btn;
    }

    private Component createNameInfo(CreditRequestApplicantDto creditRequestApplicantDto){
        ListItem item = new ListItem(
                UIUtils.createInitials(creditRequestApplicantDto.getInitials()), creditRequestApplicantDto.getFullName()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private DetailsDrawer createPaymentPlan(){
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
        Grid<PaymentPlan> gridPaymentPlan = new Grid<>();
        Button btnCreatePP = new Button("Plan de Pagos");
        btnCreatePP.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreatePP.addClickListener(e ->{
            paymentPlanList = paymentPlanRestTemplate.add(current);
            gridPaymentPlan.setItems(paymentPlanList);
        });

        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(e -> {
            Map<String,List<String>> paramPaymentPlan = new HashMap<>();
            List<String> numberRequestList = new ArrayList<>();
            List<String> titleReport = new ArrayList<>();
            List<String> origin = new ArrayList<>();
            List<String> path = new ArrayList<>();
            List<String> typeRelationList = new ArrayList<>();
            List<String> numberApplicantList = new ArrayList<>();
            List<String> idCreditRequestList = new ArrayList<>();
            List<String> fullNameList = new ArrayList<>();

            numberRequestList.add(current.getNumberRequest().toString());
            titleReport.add("Plan de Pagos");
            origin.add("payment-plan");
            path.add("creditrequest-register");
            typeRelationList.add(paramCredit.get("typeRelation").get(0));
            numberApplicantList.add(paramCredit.get("numberApplicant").get(0));
            idCreditRequestList.add(idCreditRequest.toString());
            fullNameList.add(paramCredit.get("fullName").get(0));

            paramPaymentPlan.put("number-request",numberRequestList);
            paramPaymentPlan.put("title", titleReport);
            paramPaymentPlan.put("origin",origin);
            paramPaymentPlan.put("type-relation",typeRelationList);
            paramPaymentPlan.put("number-applicant", numberApplicantList);
            paramPaymentPlan.put("id-credit-request",idCreditRequestList);
            paramPaymentPlan.put("path",path);
            paramPaymentPlan.put("full-name",fullNameList);

            QueryParameters qp = new QueryParameters(paramPaymentPlan);
            UI.getCurrent().navigate("report-preview",qp);
        });

        topLayout.add(btnCreatePP,btnPrint);


        gridPaymentPlan.addThemeVariants(GridVariant.LUMO_COMPACT);
        gridPaymentPlan.setItems(paymentPlanList);
        gridPaymentPlan.setWidthFull();
        gridPaymentPlan.addColumn(PaymentPlan::getQuotaNumber).setHeader("Nro")
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setFlexGrow(0);
        gridPaymentPlan.addColumn(PaymentPlan::getPaymentDate).setHeader("Fecha")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);
        gridPaymentPlan.addColumn(new ComponentRenderer<>(this::createCapital)).setHeader("Amort. Capital")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);
        gridPaymentPlan.addColumn(new ComponentRenderer<>(this::createInterest)).setHeader("Interes")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);
        gridPaymentPlan.addColumn(new ComponentRenderer<>(this::createSecure)).setHeader("Seguro")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);
        gridPaymentPlan.addColumn(new ComponentRenderer<>(this::createOtherCharge)).setHeader("Otros cargos")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);
        gridPaymentPlan.addColumn(new ComponentRenderer<>(this::createFee)).setHeader("Cuota")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);
        gridPaymentPlan.addColumn(new ComponentRenderer<>(this::createResidue)).setHeader("Saldo")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeightFull();

        layout.add(topLayout,gridPaymentPlan);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("100%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;

    }

    private Component createCapital(PaymentPlan paymentPlan){
        Double capital = paymentPlan.getCapital();
        return UIUtils.createAmountLabel(capital);
    }

    private Component createInterest(PaymentPlan paymentPlan){
        Double interest = paymentPlan.getInterest();
        return UIUtils.createAmountLabel(interest);
    }

    private Component createSecure(PaymentPlan paymentPlan){
        Double secure = paymentPlan.getSecureCharge();
        return UIUtils.createAmountLabel(secure);
    }

    private Component createOtherCharge(PaymentPlan paymentPlan){
        Double otherCharge = paymentPlan.getOtherCharge();
        return UIUtils.createAmountLabel(otherCharge);
    }

    private Component createFee(PaymentPlan paymentPlan){
        Double fee = paymentPlan.getFee();
        return UIUtils.createAmountLabel(fee);
    }

    private Component createResidue(PaymentPlan paymentPlan){
        Double residue= paymentPlan.getResidue();
        return UIUtils.createAmountLabel(residue);
    }


    private DetailsDrawer createRelations(){
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.setHeightFull();
        ObjectMapper mapper = new ObjectMapper();
        linkUpList = new ArrayList<>();
        if(current.getLinkup()!=null && !current.getLinkup().equals("[]") ){
            try {
                linkUpList = new ArrayList<>(Arrays.asList(mapper.readValue(current.getLinkup(),LinkUp[].class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Grid<LinkUp> gridRP = createGridLinkUp("REFERENCIA_PERSONAL");
        gridRP.setHeight("300px");
        Grid<LinkUp> gridVE = createGridLinkUp("VINCULACION_ECONOMICA");
        gridVE.setHeight("300px");
        Grid<LinkUp> gridPE = createGridLinkUp("PARTICIPACION_ECONOMICA");
        gridPE.setHeight("300px");
        Grid<LinkUp> gridVG = createGridLinkUp("VINCULACION_POR_GARANTIA");
        gridVG.setHeight("300px");

        gridRP.addSelectionListener(e -> {
            e.getFirstSelectedItem().ifPresent(this::showFormRelation);
            binderRelations.readBean(currenRelation);
        });

        gridVE.addSelectionListener(e ->{
           e.getFirstSelectedItem().ifPresent(this::showFormRelation);
           binderRelations.readBean(currenRelation);
        });

        gridPE.addSelectionListener(e ->{
           e.getFirstSelectedItem().ifPresent(this::showFormRelation);
           binderRelations.readBean(currenRelation);
        });

        gridVG.addSelectionListener(e ->{
           e.getFirstSelectedItem().ifPresent(this::showFormRelation);
           binderRelations.readBean(currenRelation);
        });


        VerticalLayout layoutRP = gridLayout(gridRP,"REFERENCIA_PERSONAL");
        VerticalLayout layoutVE = gridLayout(gridVE,"VINCULACION_ECONOMICA");
        VerticalLayout layoutPE = gridLayout(gridPE,"PARTICIPACION_ECONOMICA");
        VerticalLayout layoutVG = gridLayout(gridVG,"VINCULACION_POR_GARANTIA");

        dataProviderLinkUp = new ListDataProvider<>(linkUpList);

        gridRP.setDataProvider(dataProviderLinkUp);
        gridVE.setDataProvider(dataProviderLinkUp);
        gridPE.setDataProvider(dataProviderLinkUp);
        gridVG.setDataProvider(dataProviderLinkUp);

        accordion.addOpenedChangeListener(e ->{
            if(e.getOpenedIndex().isPresent()) {
                if (e.getOpenedIndex().getAsInt() == 0) {
                    filter("REFERENCIA_PERSONAL");
                } else if (e.getOpenedIndex().getAsInt() == 1) {
                    filter("VINCULACION_ECONOMICA");

                } else if (e.getOpenedIndex().getAsInt() == 2) {
                    filter("PARTICIPACION_ECONOMICA");

                } else if (e.getOpenedIndex().getAsInt() == 3) {
                    filter("VINCULACION_POR_GARANTIA");

                }
            }
        });

        accordion.add("Referencia Personales",layoutRP);
        accordion.add("Vinculacion Economica", layoutVE);
        accordion.add("Participacion Economica en Alguna Empresa",layoutPE);
        accordion.add("Vinculacion por Garantia o Destino de Credito con otros Prestatarios de la Entidad", layoutVG);


        VerticalLayout layoutRelations = new VerticalLayout();
        layoutRelations.setWidthFull();
        layoutRelations.setHeightFull();
        layoutRelations.add(accordion);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layoutRelations);

        return detailsDrawer;

    }

    private VerticalLayout gridLayout(Grid<LinkUp> grid,String relation) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        Button btnNew = new Button("NUEVO");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btnNew.setEnabled(GrantOptions.grantedOption("Solicitud"));
        btnNew.addClickListener(click -> {
            LinkUp linkUp = new LinkUp();
            if (relation.equals("REFERENCIA_PERSONAL")){
                linkUp.setTypeLinkUp(TypeLinkUp.REFERENCIA_PERSONAL);
            }else if(relation.equals("VINCULACION_ECONOMICA")){
                linkUp.setTypeLinkUp(TypeLinkUp.VINCULACION_ECONOMICA);
            }else if(relation.equals("PARTICIPACION_ECONOMICA")){
                linkUp.setTypeLinkUp(TypeLinkUp.PARTICIPACION_ECONOMICA);
            }else if(relation.equals("VINCULACION_POR_GARANTIA")){
                linkUp.setTypeLinkUp(TypeLinkUp.VINCULACION_POR_GARANTIA);
            }
            showFormRelation(linkUp);
        });

        layout.add(btnNew, grid);
        return layout;
    }

    private Grid createGridLinkUp(String relation){


        Grid<LinkUp> gridLinkUp = new Grid<>();
        gridLinkUp.setWidthFull();
        gridLinkUp.addThemeVariants(GridVariant.LUMO_COMPACT,GridVariant.MATERIAL_COLUMN_DIVIDERS);
        if (relation.equals("REFERENCIA_PERSONAL")) {

            gridLinkUp.addColumn(LinkUp::getFirstField).setHeader("Nombres y apellidos")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getSeconfField).setHeader("Que Relacion Tiene")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getThirdField).setHeader("Direccion")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getFouthField).setHeader("Telefono")
                    .setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true);
        }else if(relation.equals("VINCULACION_ECONOMICA")){
            gridLinkUp.addColumn(LinkUp::getFirstField).setHeader("Nombre del Ejecutivo")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getSeconfField).setHeader("Numero C.I.")
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getThirdField).setHeader("Actividad")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getFouthField).setHeader("Relacion")
                    .setWidth(UIUtils.COLUMN_WIDTH_L);
        }else if(relation.equals("PARTICIPACION_ECONOMICA")){
            gridLinkUp.addColumn(LinkUp::getFirstField).setHeader("Nombre de la empresa")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getSeconfField).setHeader("Actividad de la Empresa")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getThirdField).setHeader("Relacion que tiene con UD.")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);

        }else if(relation.equals("VINCULACION_POR_GARANTIA")){
            gridLinkUp.addColumn(LinkUp::getFirstField).setHeader("Nombre del prestatario")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getSeconfField).setHeader("Numero de credito")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getThirdField).setHeader("Relacion que tiene con UD.")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getFouthField).setHeader("Tipo vinculacion")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
            gridLinkUp.addColumn(LinkUp::getFifthField).setHeader("Explicacion vinculacion")
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);

        }

        return gridLinkUp;
    }

    private DetailsDrawer createCreditRequest(CreditRequest creditRequest){

        numberRequest = new TextField();
        numberRequest.setWidth("100%");
        numberRequest.setReadOnly(true);


        numberApplicant = new TextField();
        numberApplicant.setValue("");
        numberApplicant.setWidth("80%");
        numberApplicant.setEnabled(false);
        numberApplicant.addValueChangeListener(event -> {
           if(!event.getHasValue().getValue().equals(paramNumberApplicant)) {
               footer.saveState(true && GrantOptions.grantedOption("Solicitud"));
           }
        });
        Button btnSearch = new Button();
        btnSearch.setWidth("10%");
        btnSearch.setIcon(VaadinIcon.SEARCH_PLUS.create());
        btnSearch.addClickListener(e -> {
            showSearch();
        });
        if(creditRequest.getLoginUser()!=null) {
            btnSearch.setEnabled((creditRequest.getLoginUser().equals(VaadinSession.getCurrent().getAttribute("login").toString())
                    || creditRequest.getId() == null));
        }else{
            btnSearch.setEnabled(true);
        }
        FlexBoxLayout layoutSearch = new FlexBoxLayout(numberApplicant,btnSearch);
        layoutSearch.setFlexGrow(1,btnSearch);
        layoutSearch.setSpacing(Right.S);

        Button btnCharge = new Button("Seleccionar");
        btnCharge.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SUCCESS);
        btnCharge.setWidth("100%");
        btnCharge.setIcon(VaadinIcon.BOOK_PERCENT.create());

        btnCharge.addClickListener(e -> showCharge());


        ComboBox<String> typeCredit = new ComboBox<>();
        typeCredit.setItems(UtilValues.getParameterValueDescription("TIPO CREDITO"));
        typeCredit.setRequired(true);
        typeCredit.setWidth("100%");
        typeCredit.addValueChangeListener(e->{
           String[] s = e.getValue().split("-");
           typeCredit.setValue(s[0]);
        });

        ComboBox<String> productCredit = new ComboBox<>();
        productCredit.setItems(UtilValues.getParameterValueDescription("PRODUCTOS"));
        productCredit.setRequired(true);
        productCredit.setWidth("100%");
        productCredit.addValueChangeListener(e ->{
            String[] s = e.getValue().split("-");
            productCredit.setValue(s[0]);
        });

        ComboBox<String> idOffice = new ComboBox<>();
        idOffice.setItems(UtilValues.getListOfficeCodeName());
        idOffice.setRequired(true);
        idOffice.setWidth("100%");
        idOffice.addValueChangeListener(e ->{
           String[] s = e.getValue().split("-");
           idOffice.setValue(s[0]);
        });
        idOffice.clear();

        NumberField amount = new NumberField();
        amount.setWidth("100%");
        amount.setPattern("^[0-9]+([.][0-9]{1,2})?$");
        amount.setRequiredIndicatorVisible(true);

        NumberField rateInterest = new NumberField();
        rateInterest.setWidth("100%");
        rateInterest.setPattern("^[0-9]+([.][0-9]{1,2})?$");
        rateInterest.setRequiredIndicatorVisible(true);
        rateInterest.setErrorMessage("Formato incorrecto");

        ComboBox<String> currency = new ComboBox<>();
        currency.setItems("BS","$US");
        currency.setRequired(true);
        currency.setWidth("100%");

        NumberField term = new NumberField();
        term.setPattern("^[0-9]");
        term.setRequiredIndicatorVisible(true);
        term.setWidth("100%");

        ComboBox<String> typeTerm = new ComboBox<>();
        typeTerm.setRequired(true);
        typeTerm.setItems("DIA","MES","ANUAL");
        typeTerm.setWidth("100%");

        ComboBox<Integer> paymentPeriod = new ComboBox<>();
        paymentPeriod.setRequired(true);
        paymentPeriod.setItems(30,60,90,120,150,180,360);
        paymentPeriod.setWidth("100%");

        NumberField fixedDay = new NumberField();
        fixedDay.setValue(0d);
        fixedDay.setMin(0);
        fixedDay.setMax(30);
        fixedDay.setHasControls(true);
        fixedDay.setRequiredIndicatorVisible(true);
        fixedDay.setWidth("100%");

        ComboBox<String> typeFee = new ComboBox<>();
        typeFee.setRequired(true);
        typeFee.setItems("FIJA","VARIABLE","PLAZO FIJO");
        typeFee.setWidth("100%");

        NumberField baseInterestRate = new NumberField();
        baseInterestRate.setPattern("^[0-9]+([.][0-9]{1,2})?$");
        baseInterestRate.setWidth("100%");

        ComboBox<Integer> initPeriodBaseRate= new ComboBox<>();
        initPeriodBaseRate.setWidth("100%");
        initPeriodBaseRate.setItems(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24);
        initPeriodBaseRate.setValue(0);

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


        DatePicker requestDate = new DatePicker();
//        requestDate.setRequired(true);
        requestDate.setLocale(Locale.ENGLISH);
        requestDate.setWidth("100%");
        requestDate.setReadOnly(true);

        DatePicker paymentPlanDate = new DatePicker();
        paymentPlanDate.setRequired(true);
        paymentPlanDate.setWidth("100%");

        TextField numberCredit = new TextField();
        numberCredit.setWidth("100%");

        ComboBox<String> typeGuarantee = new ComboBox<>();
        typeGuarantee.setItems(getTypeGuarantee());
        typeGuarantee.setWidth("100%");

        TextArea destination = new TextArea();
        destination.setRequired(true);
        destination.setWidth("100%");

        binder = new BeanValidationBinder<>(CreditRequest.class);


        binder.forField(amount).withValidator(value -> value.doubleValue()> 0, "Monto debe ser  mayor a 0" ).asRequired("Monto es requerido")
                .bind(CreditRequest::getAmount,CreditRequest::setAmount);
        binder.forField(typeCredit).asRequired("Tipo credito es requerido").bind(CreditRequest::getTypeCredit,CreditRequest::setTypeCredit);
        binder.forField(productCredit).asRequired("Producto credito es requerido").bind(CreditRequest::getProductCredit,CreditRequest::setProductCredit);
        binder.forField(idOffice).asRequired("Oficina es requerida").withConverter(new StringToIntegerConverter(idOffice.getValue()))
                .withValidator(value -> value.intValue()>0,"Seleccione una oficina") .bind(CreditRequest::getIdOffice, CreditRequest::setIdOffice);
        binder.forField(rateInterest).asRequired("Tasa interes es requerida").withValidator(rate -> rate.doubleValue()>0, "Tasa interes debe ser mayor a 0")
                .bind(CreditRequest::getRateInterest,CreditRequest::setRateInterest);
        binder.forField(currency).asRequired("Moneda es requerida").bind(CreditRequest::getCurrency,CreditRequest::setCurrency);
        binder.forField(term).withValidator(value -> value.doubleValue()>0,"Plazo debe ser mayor a 0")
                .asRequired("Plazo es requerido").withConverter(new UtilValues.DoubleToIntegerConverter()).bind(CreditRequest::getTerm, CreditRequest::setTerm);
        binder.forField(typeTerm).asRequired("Tipo de plazo es requerido").bind(CreditRequest::getTypeTerm, CreditRequest::setTypeTerm);
        binder.forField(paymentPeriod).asRequired("Periodo de pago es requerido")
                .bind(CreditRequest::getPaymentPeriod,CreditRequest::setPaymentPeriod);
        binder.forField(fixedDay).withValidator(value -> value.intValue()>=0 && value.intValue()<=30, "Dia fijo debe estar entre 0 a 30")
                .withConverter(new UtilValues.DoubleToIntegerConverter()).bind(CreditRequest::getFixedDay,CreditRequest::setFixedDay);
        binder.forField(typeFee).asRequired("Tipo de cuota es requerido").bind(CreditRequest::getTypeFee,CreditRequest::setTypeFee);
        binder.forField(baseInterestRate).bind(CreditRequest::getBaseInterestRate,CreditRequest::setBaseInterestRate);
        binder.forField(initPeriodBaseRate).bind(CreditRequest::getInitPeriodBaseRate,CreditRequest::setInitPeriodBaseRate);
        binder.forField(caedec).asRequired("Caedec destino es requerido").bind(CreditRequest::getCaedec,CreditRequest::setCaedec);
        binder.forField(requestDate).bind(CreditRequest::getRequestDate,CreditRequest::setRequestDate);
        binder.forField(paymentPlanDate).asRequired("Fecha de inicio plan de pagos es requerida").bind(CreditRequest::getPaymentPlanDate,CreditRequest::setPaymentPlanDate);
        binder.forField(destination).asRequired("Destino del credito es requerido").bind(CreditRequest::getDestination,CreditRequest::setDestination);
        binder.forField(numberCredit)
                .withConverter(Integer::valueOf,String::valueOf,"Ingrese un numero, 0 valor por defeccto")
//                .withConverter(new StringToIntegerConverter("Se admiten solo numeros"))
                .withValidator(value -> value.intValue()>=0, "Numero credito no valido, 0 valor por defecto")
                .bind(CreditRequest::getNumberCredit,CreditRequest::setNumberCredit);
        binder.forField(typeGuarantee).bind(CreditRequest::getTypeGuarantee,CreditRequest::setTypeGuarantee);

        binder.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();

           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Solicitud")
                   && (creditRequest.getLoginUser().equals(VaadinSession.getCurrent().getAttribute("login").toString())
                   || creditRequest.getId()==null));
        });

        formRequest = new FormLayout();
        formRequest.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px",4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formRequest.addFormItem(numberRequest,"Nro solicitud");
        formRequest.addFormItem(layoutSearch,"Solicitante");
        formRequest.addFormItem(typeCredit, "Tipo de credito");
        formRequest.addFormItem(productCredit, "Producto de credito");
        formRequest.addFormItem(idOffice,"Oficina");
        formRequest.addFormItem(amount,"Monto");
        formRequest.addFormItem(currency,"Moneda");
        formRequest.addFormItem(rateInterest,"Tasa int.");
        formRequest.addFormItem(btnCharge,"Cargos financieros");
        formRequest.addFormItem(term,"Plazo");
        formRequest.addFormItem(typeTerm,"Tipo plazo");
//        formRequest.addFormItem(termFlex,"Plazo - Tipo plazo");
        formRequest.addFormItem(paymentPeriod,"Periodo");
        formRequest.addFormItem(fixedDay,"Dia fijo");
        formRequest.addFormItem(typeFee,"Tipo cuota");
        formRequest.addFormItem(baseInterestRate,"Tasa base");
        formRequest.addFormItem(initPeriodBaseRate,"Periodo inicio tasa base");
        formRequest.addFormItem(caedec,"CAEDEC");
        formRequest.addFormItem(requestDate,"Fecha solicitud");
        formRequest.addFormItem(paymentPlanDate, "Fecha plan pagos");
        formRequest.addFormItem(typeGuarantee,"Garantia princial");
        formRequest.addFormItem(numberCredit,"Numero credito");
//        formRequest.addFormItem(destination,"Destino del credito");
        FormLayout.FormItem destinationItem = formRequest.addFormItem(destination,"Destino del credito");
        UIUtils.setColSpan(4, destinationItem);
        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(binder.writeBeanIfValid(creditRequest)) {
               if(creditRequest.getState()==null || creditRequest.getState().isEmpty()) {
                   creditRequest.setState("ANALISIS_PREVIO");
               }
               creditRequest.setLoginUser(VaadinSession.getCurrent().getAttribute("login").toString());
               CreditRequest result = restTemplate.addCreditRequest(creditRequest);
               if(creditRequest.getId()==null){
                   StageHistoryRestTemplate stageHistoryRestTemplate = new StageHistoryRestTemplate();
                   StageHistory stageHistory = new StageHistory();
                   stageHistory.setNumberRequest(result.getNumberRequest());
                   stageHistory.setStage("EVALUACION_CREDITO");
                   stageHistory.setStartDateTime(Instant.now());
                   stageHistory.setState("pendiente");
                   stageHistory.setInitDateTime(Instant.now());
                   stageHistory.setUserTask(result.getLoginUser());
                   stageHistory.setComesFrom("EVALUACION_CREDITO");
                   stageHistoryRestTemplate.add(stageHistory);
               }
               current = result;

               if(creditRequest.getId()==null) {
                   CreditRequestAplicantRestTemplate rest = new CreditRequestAplicantRestTemplate();
                   CreditRequestApplicant creditRequestApplicant = new CreditRequestApplicant();
                   creditRequestApplicant.setNumberApplicant(paramNumberApplicant);
                   creditRequestApplicant.setNumberRequest(result.getNumberRequest());
                   creditRequestApplicant.setTypeRelation("deudor");
                   rest.add(creditRequestApplicant);
               }else if(!paramCredit.get("numberApplicant").get(0).equals(numberApplicant.getValue())){
                   CreditRequestAplicantRestTemplate rest = new CreditRequestAplicantRestTemplate();
                   CreditRequestApplicant creditRequestApplicant = rest.getCreditRequestApplicant(result.getNumberRequest(),
                           Integer.parseInt(paramCredit.get("numberApplicant").get(0)),"deudor");
                   creditRequestApplicant.setNumberApplicant(Integer.parseInt(numberApplicant.getValue()));
                   rest.update(creditRequestApplicant);
               }
               UIUtils.showNotification("Datos guardados");
               UI.getCurrent().navigate(CreditRequestView.class);
            }
        });

        footer.addCancelListener(e -> UI.getCurrent().navigate(CreditRequestView.class));

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidth("100%");
        detailsDrawer.setContent(formRequest);
        detailsDrawer.setFooter(footer);

        return detailsDrawer;

    }

    private DetailsDrawer createExceptionCreditRequest(ExceptionsCreditRequest exceptionsCreditRequest){
        FormLayout formLayout = new FormLayout();

        ComboBox<String> exceptions = new ComboBox<>();
        exceptions.setItems(getListExceptions());
        exceptions.setWidth("100%");
        exceptions.setRequiredIndicatorVisible(true);
        exceptions.setRequired(true);
        exceptions.addValueChangeListener(e -> {
            if(e.getValue()!=null) {
                String[] values = e.getValue().split("-");
                exceptions.setValue(values[0]);
            }
        });

        RadioButtonGroup<String> state = new RadioButtonGroup<>();
        state.setItems("ACEPTADA","RECHAZADA");
        state.setValue(Optional.ofNullable(exceptionsCreditRequest.getState()).orElse("").equals("ACEPTADA") ? "ACEPTADA":"RECHAZADA");
        state.setRequired(true);

        TextArea justification = new TextArea();
        justification.setWidth("100%");
        justification.setRequiredIndicatorVisible(true);
        justification.setRequired(true);
        justification.setPlaceholder("Ingrese la justificacion a la excepcion solicitada");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        DetailsDrawer detailsDrawerException = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerException.setContent(formLayout);
        DetailsDrawerFooter footerException = new DetailsDrawerFooter();



        FormLayout.FormItem exceptionsItem = formLayout.addFormItem(exceptions,"Seleccione la excepcion");
        UIUtils.setColSpan(3,exceptionsItem);
        FormLayout.FormItem justificationItem = formLayout.addFormItem(justification,"Justificaciones");
        UIUtils.setColSpan(3,justificationItem);
        formLayout.addFormItem(state,"Estado Excepcion");

        binderExceptionCreditRequest = new BeanValidationBinder<>(ExceptionsCreditRequest.class);
        binderExceptionCreditRequest.forField(exceptions).asRequired("Excepcion es requerida")
                .bind(ExceptionsCreditRequest::getCodeException,ExceptionsCreditRequest::setCodeException);
        binderExceptionCreditRequest.forField(justification).asRequired("Justificcion es requerida")
                .bind(ExceptionsCreditRequest::getJustification,ExceptionsCreditRequest::setJustification);
        binderExceptionCreditRequest.forField(state).asRequired("Estado excepcion es requerida")
                .bind(ExceptionsCreditRequest::getState,ExceptionsCreditRequest::setState);
        binderExceptionCreditRequest.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderExceptionCreditRequest.hasChanges();
           footerException.saveState(isValid && hasChanges && GrantOptions.grantedOption("Solicitud"));
        });

        footerException.addSaveListener(e ->{
           if(binderExceptionCreditRequest.writeBeanIfValid(exceptionsCreditRequest)){
               if(exceptionsCreditRequest.getId()==null){
                   exceptionsCreditRequest.setRegister(LocalDate.now());
                   exceptionsCreditRequest.setNumberRequest(paramNumberRequest);

               }
               try {
                   exceptionsCreditRequestRestTemplate.add(exceptionsCreditRequest);
                   UIUtils.showNotification("Excepcion registrada");
                   exceptionsCreditRequestDtoList = new ArrayList<>(exceptionsCreditRequestDtoRestTemplate.getByNumberRequest(paramNumberRequest));
                   dataProviderExceptionsCreditRequestDto = new ListDataProvider<>(exceptionsCreditRequestDtoList);
                   gridExceptionsCreditRequestDto.setDataProvider(dataProviderExceptionsCreditRequestDto);
                   detailsDrawer.hide();

               } catch (IOException ex) {
                   ex.printStackTrace();
               }
           }
        });
        footerException.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawerException.setFooter(footerException);


        return detailsDrawerException;
    }

    private List<String> getListExceptions(){
        ExceptionsRestTemplate exceptionsRestTemplate = new ExceptionsRestTemplate();
        List<String> list = new ArrayList<>();
        List<Exceptions> exceptionsList = exceptionsRestTemplate.getAll();
        for(Exceptions exceptions : exceptionsList){
            if(exceptions.getState().equals("ACTIVO")) {
                list.add(exceptions.getInternalCode() + "-" + exceptions.getDescription());
            }
        }
        return list;
    }

//    private Grid searchExceptions(){
//        ExceptionsRestTemplate rest = new ExceptionsRestTemplate();
//        List<Exceptions> exceptionsList = new ArrayList<>(rest.getAll());
//
//        ListDataProvider<Exceptions> data = new ListDataProvider<>(exceptionsList);
//        gridExceptions = new Grid<>();
//        gridExceptions.setWidthFull();
//        gridExceptions.setDataProvider(data);
//
//        gridExceptions.addColumn(Exceptions::getInternalCode).setHeader("Codigo")
//                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("code");
//        gridExceptions.addColumn(Exceptions::getTypeException).setHeader("Tipo excepcion")
//                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("typeException");
//        gridExceptions.addColumn(Exceptions::getDescription).setHeader("Excepcion")
//                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("exception");
//        gridExceptions.addColumn(Exceptions::getLimitTime).setHeader("Dias")
//                .setSortable(true).setResizable(true).setFlexGrow(1).setKey("days");
//        HeaderRow hr = gridExceptions.appendHeaderRow();
//
////        gridExceptions.addColumn(new ComponentRenderer<>(this::createSelectExcepcion));
//
//        internalCodeException = new TextField();
//        internalCodeException.setValueChangeMode(ValueChangeMode.EAGER);
//        internalCodeException.setWidth("100%");
//        internalCodeException.addValueChangeListener(e -> applyFilterException(data));
//        hr.getCell(gridExceptions.getColumnByKey("code")).setComponent(internalCodeException);
//
//        typeException = new TextField();
//        typeException.setValueChangeMode(ValueChangeMode.EAGER);
//        typeException.setWidth("100%");
//        typeException.addValueChangeListener(e-> applyFilterException(data));
//        hr.getCell(gridExceptions.getColumnByKey("typeExeption")).setComponent(typeException);
//
//        descriptionException = new TextField();
//        descriptionException.setValueChangeMode(ValueChangeMode.EAGER);
//        descriptionException.setWidth("100%");
//        descriptionException.addValueChangeListener(e-> applyFilterException(data));
//        hr.getCell(gridExceptions.getColumnByKey("exception")).setComponent(descriptionException);
//
////        daysException = new TextField();
////        daysException.setValueChangeMode(ValueChangeMode.EAGER);
////        daysException.setWidth("100%");
////        daysException.addValueChangeListener(e-> applyFilterException(data));
////        hr.getCell(gridExceptions.getColumnByKey("days")).setComponent(daysException);
//        return gridExceptions;
//}

    private  Grid searchApplicant(){
        ApplicantRestTemplate rest = new ApplicantRestTemplate();

        List<Applicant>  applicantList = new ArrayList<>(rest.getAllApplicants());
        applicantList.removeIf(e -> e.getNumberApplicant().equals(paramNumberApplicant));

        ListDataProvider<Applicant> data = new ListDataProvider<>(applicantList);
        gridApplicant = new Grid<>();
        gridApplicant.setWidthFull();
        gridApplicant.setDataProvider(data);
//        gridApplicant.addSelectionListener(event -> {
//           event.getFirstSelectedItem();
//        });
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

    private Grid searchCharge(){
        chargeList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            chargeList = new ArrayList<>(Arrays.asList(mapper.readValue(current.getCharge(),Charge[].class)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataCharge = new ListDataProvider<>(chargeList);

        gridCharge = new Grid<>();
        gridCharge.setWidthFull();
        gridCharge.setDataProvider(dataCharge);

        gridCharge.addColumn(Charge::getName).setHeader("Cargo financiero").setSortable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_XL).setFlexGrow(0).setResizable(true);
        Grid.Column<Charge> valueColumn = gridCharge.addColumn(Charge::getValue).setHeader("Valor").setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_M)
                    .setFlexGrow(0).setResizable(true);
        Grid.Column<Charge> selectedColumn = gridCharge.addColumn(new ComponentRenderer<>(this::createActiveCharge))
                .setHeader("Agregar cargo") .setWidth(UIUtils.COLUMN_WIDTH_L).setFlexGrow(0).setResizable(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        Binder<Charge> binder = new BeanValidationBinder<>(Charge.class);
        Editor<Charge> editor = gridCharge.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Checkbox selected = new Checkbox();
        selectedColumn.setEditorComponent(selected);

        NumberField value = new NumberField();
        valueColumn.setEditorComponent(value);

        binder.bind(selected,"selected");
        binder.bind(value,"value");

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());

        Grid.Column<Charge> editorColumn = gridCharge.addComponentColumn(charge -> {
           Button edit = new Button("Editar");
           edit.addClickListener(e -> {
               if(!editor.isOpen()) {
                   editor.editItem(charge);
                   selected.focus();
               }
           });
           edit.setEnabled(!editor.isOpen());
           return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Guardar", e -> {
            editor.save();
            try {
                current.setCharge(mapper.writeValueAsString(chargeList));
                footer.saveState(true && GrantOptions.grantedOption("Solicitud"));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        });
        Button cancel = new Button("Cancelar", e -> editor.cancel());

        gridCharge.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);


        return gridCharge;

    }

    private Component createSelectApplicant(Applicant applicant){
        Button btnSelect = new Button();
        btnSelect.setIcon(VaadinIcon.CHEVRON_CIRCLE_UP.create());

        btnSelect.addClickListener(e-> {
            if(!(nameTabSelected.equals("Codeudores") || nameTabSelected.equals("Garantes"))) {
                numberApplicant.setValue(applicant.getNumberApplicant().toString());
                paramNumberApplicant = applicant.getNumberApplicant();
            }else{
                CreditRequestAplicantRestTemplate rest = new CreditRequestAplicantRestTemplate();
                CreditRequestApplicant creditRequestApplicant = new CreditRequestApplicant();
                creditRequestApplicant.setNumberApplicant(applicant.getNumberApplicant());
                creditRequestApplicant.setNumberRequest(current.getNumberRequest());
                if(nameTabSelected.equals("Codeudores")){
                    creditRequestApplicant.setTypeRelation("codeudor");
                    rest.add(creditRequestApplicant);
                    creditRequestApplicantDtoList = new ArrayList<>(creditRequestApplicantDtoRestTemplate.getByLoginNumberRequest(VaadinSession.getCurrent().getAttribute("login").toString(),paramNumberRequest));
                    dataProviderCreditRequestApplicant = new ListDataProvider<>(creditRequestApplicantDtoList);
                    gridCodebtorGuarantor.setDataProvider(dataProviderCreditRequestApplicant);
                    gridCodebtorGuarantor.getDataProvider().refreshAll();
                    filterTypeRelation("codeudor");
                }else if(nameTabSelected.equals("Garantes")){
                    creditRequestApplicant.setTypeRelation("garante");
                    rest.add(creditRequestApplicant);
                    creditRequestApplicantDtoList = new ArrayList<>(creditRequestApplicantDtoRestTemplate.getByLoginNumberRequest(VaadinSession.getCurrent().getAttribute("login").toString(),paramNumberRequest));
                    dataProviderCreditRequestApplicant = new ListDataProvider<>(creditRequestApplicantDtoList);
                    gridCodebtorGuarantor.setDataProvider(dataProviderCreditRequestApplicant);
                    gridCodebtorGuarantor.getDataProvider().refreshAll();
                    filterTypeRelation("garante");
                }
            }
            detailsDrawer.hide();

        });
        return btnSelect;
    }

    private Component createSelectException(Exceptions exceptions){
        Button btnSelect = new Button();
        btnSelect.setIcon(VaadinIcon.CHEVRON_CIRCLE_UP.create());

        btnSelect.addClickListener(e ->{
            ExceptionsCreditRequest exceptionsCreditRequest = new ExceptionsCreditRequest();

        });

        return btnSelect;
    }

    private Component createSelectParameter(Parameter parameter){
        Button btnSelect = new Button();
        btnSelect.setIcon(VaadinIcon.CHEVRON_CIRCLE_UP.create());
        btnSelect.addClickListener(e ->{

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

    private void applyFilterException(ListDataProvider<Exceptions> dataProvider){
        dataProvider.clearFilters();
        if(!internalCodeException.getValue().trim().equals("")){
            dataProvider.addFilter(exceptions ->StringUtils.containsIgnoreCase(exceptions.getInternalCode(),internalCodeException.getValue()));
        }
        if(!typeException.getValue().trim().equals("")){
            dataProvider.addFilter(exceptions -> StringUtils.containsIgnoreCase(exceptions.getTypeException(),typeException.getValue()));
        }
        if(!descriptionException.getValue().trim().equals("")){
            dataProvider.addFilter(exceptions -> StringUtils.containsIgnoreCase(exceptions.getDescription(),descriptionException.getValue()));
        }

    }

    private DetailsDrawer createDetailDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        return detailsDrawer;
    }

    private void showSearch(){

        detailsDrawerHeader.setTitle("Seleccionar solicitante");
        detailsDrawer.setContent(searchApplicant());
        detailsDrawer.show();
    }

    private void showCreateException(ExceptionsCreditRequest exceptionsCreditRequest){
        detailsDrawerHeader.setTitle("Seleccionar Excepcion");
        detailsDrawer.setContent(createExceptionCreditRequest(exceptionsCreditRequest));
        binderExceptionCreditRequest.readBean(exceptionsCreditRequest);
        detailsDrawer.show();

    }

    private void filter(String filter){
        if(dataProviderLinkUp.getItems().size()>0){
            dataProviderLinkUp.setFilterByValue(LinkUp::getTypeLinkUp, TypeLinkUp.valueOf(filter));
        }
    }

    private FormLayout createFormRelation(LinkUp linkUp){

        TextField field1 = new TextField();
        field1.setWidth("100%");
        field1.setRequired(true);

        TextField field2 = new TextField();
        field2.setWidth("100%");
        field2.setRequired(true);

        TextField field3 = new TextField();
        field3.setWidth("100%");
        field3.setRequired(true);

        TextField field4 = new TextField();
        field4.setWidth("100%");
        field4.setRequired(true);

        TextArea textArea = new TextArea();
        textArea.setWidth("100%");
        textArea.setRequired(true);

        ComboBox<String> comboBox1 = new ComboBox();
        comboBox1.setWidth("100%");
        comboBox1.setRequired(true);
        comboBox1.setItems("PADRE","MADRE","HIJO(A)","TIO(A)","ABUELO(A)","NIETO(A)","HERMANO(A)");

        ComboBox<String> comboBox2 = new ComboBox();
        comboBox2.setWidth("100%");
        comboBox2.setRequired(true);
        comboBox2.setItems("POR DESTINO","POR GARANTIA");

        FormLayout formRelation = new FormLayout();
        formRelation.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        binderRelations = new BeanValidationBinder<>(LinkUp.class);

        if(linkUp.getTypeLinkUp().toString().equals("REFERENCIA_PERSONAL")){
            binderRelations.forField(field1).asRequired("Nombres y Apellidos, son requeridos").bind(LinkUp::getFirstField,LinkUp::setFirstField);
            binderRelations.forField(comboBox1).asRequired("Relacion es requerida").bind(LinkUp::getSeconfField,LinkUp::setSeconfField);
            binderRelations.forField(field2).asRequired("Direccion es requerida").bind(LinkUp::getThirdField,LinkUp::setThirdField);
            binderRelations.forField(field3).asRequired("Telefono/Celular es requerido").bind(LinkUp::getFouthField,LinkUp::setFouthField);

            formRelation.addFormItem(field1,"Nombres y Apellidos");
            formRelation.addFormItem(comboBox1,"Relacion");
            formRelation.addFormItem(field2,"Direccion");
            formRelation.addFormItem(field3,"Telefono/Celular");
        }else if (linkUp.getTypeLinkUp().toString().equals("VINCULACION_ECONOMICA")){
            binderRelations.forField(field1).asRequired("Nombres Ejecutivo es requerido").bind(LinkUp::getFirstField,LinkUp::setFirstField);
            binderRelations.forField(comboBox1).asRequired("Relacion es requerida").bind(LinkUp::getSeconfField,LinkUp::setSeconfField);
            binderRelations.forField(field2).asRequired("Numero C.I. es requerido").bind(LinkUp::getThirdField,LinkUp::setThirdField);
            binderRelations.forField(field3).asRequired("Actividad").bind(LinkUp::getFouthField,LinkUp::setFouthField);

            formRelation.addFormItem(field1,"Nombre del ejecutivo");
            formRelation.addFormItem(comboBox1,"Relacion");
            formRelation.addFormItem(field2,"Numero C.I.");
            formRelation.addFormItem(field3,"Actividad");
        }else if (linkUp.getTypeLinkUp().toString().equals("PARTICIPACION_ECONOMICA")){
            binderRelations.forField(field1).asRequired("Nombre de empresa es requerida").bind(LinkUp::getFirstField,LinkUp::setFirstField);
            binderRelations.forField(field2).asRequired("Actividad de la empresa es requerida").bind(LinkUp::getSeconfField,LinkUp::setSeconfField);
            binderRelations.forField(field3).asRequired("Numero de NIT es requerido").bind(LinkUp::getThirdField,LinkUp::setThirdField);
            binderRelations.forField(field4).asRequired("Participacion es requerida").bind(LinkUp::getFouthField,LinkUp::setFouthField);

            formRelation.addFormItem(field1,"Nombre de la empresa");
            formRelation.addFormItem(field2,"Actividad de la empresa");
            formRelation.addFormItem(field3,"Numero de NIT");
            formRelation.addFormItem(field4,"Participacion");
        }else{
            binderRelations.forField(field1).asRequired("Nombre del prestatario es requerido").bind(LinkUp::getFirstField,LinkUp::setFirstField);
            binderRelations.forField(comboBox1).asRequired("Relacion es requerida").bind(LinkUp::getSeconfField,LinkUp::setSeconfField);
            binderRelations.forField(field2).asRequired("Numero de credito").bind(LinkUp::getThirdField,LinkUp::setThirdField);
            binderRelations.forField(comboBox2).asRequired("Tipo de vinculacion es querida").bind(LinkUp::getFouthField,LinkUp::setFouthField);
            binderRelations.forField(textArea).asRequired("Explicacion de la vinculacion es requerida").bind(LinkUp::getFifthField,LinkUp::setFifthField);

            formRelation.addFormItem(field1,"Nombre del prestatario");
            formRelation.addFormItem(field2,"Numero de credito");
            formRelation.addFormItem(comboBox1,"Relacion");
            formRelation.addFormItem(comboBox2,"Tipo de vinculacion");
            FormLayout.FormItem formItem = formRelation.addFormItem(textArea,"Explicacion sobre vinculacion economica");
            UIUtils.setColSpan(3,formItem);
        }
        footerRelations = new DetailsDrawerFooter();

        binderRelations.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderRelations.hasChanges();
           footerRelations.saveState(hasChanges && isValid && GrantOptions.grantedOption("Solicitud"));
        });

        footerRelations.addSaveListener(e ->{
            if(binderRelations.writeBeanIfValid(currenRelation)){
                ObjectMapper mapper = new ObjectMapper();

//                if(current.getLinkup().equals("[]")) {
//                    try {
//                        linkUpList = new ArrayList<>(Arrays.asList(mapper.readValue(current.getLinkup() ,LinkUp[].class)));
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
                if (currenRelation.getId()==null){
                    currenRelation.setId(UUID.randomUUID());
                    linkUpList.add(currenRelation);
                }else{
                    List<LinkUp> newList = linkUpList.stream()
                            .filter(f-> !f.getId().equals(currenRelation.getId()))
                            .collect(Collectors.toList());
                    newList.add(currenRelation);
                    linkUpList = newList;
                }
                try {
                    String jsonLinkUp = mapper.writeValueAsString(linkUpList);
                    current.setLinkup(jsonLinkUp);

                    UIUtils.showNotification("Datos relacion creados, para terminar de guardar guarde en la solicitud");
                    detailsDrawer.hide();
                    footer.saveState(true && GrantOptions.grantedOption("Solicitud"));
                    dataProviderLinkUp.refreshAll();
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        footerRelations.addCancelListener(e ->{
           detailsDrawer.hide();

        });

       return formRelation;

    }

    private void showCharge(){
        detailsDrawerHeader.setTitle("Cargos financieros");
        detailsDrawer.setContent(searchCharge());
        detailsDrawer.show();
    }

    private void showFormRelation(LinkUp linkUp){
        currenRelation = linkUp;

        detailsDrawerHeader.setTitle(currenRelation.getTypeLinkUp().toString());
        detailsDrawer.setContent(createFormRelation(currenRelation));
        detailsDrawer.setFooter(footerRelations);
        detailsDrawer.show();

    }

    private Component createActiveCharge(Charge charge){
        Icon icon;
        if(charge.isSelected()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;

    }

    private List<String> getTypeGuarantee(){
        ParameterRestTemplate rest = new ParameterRestTemplate();
        List<Parameter> parameters = rest.getParametersByCategory("TIPO GARANTIA");
        List<String> listGuarantee = new ArrayList<>();
        for(Parameter p:parameters){
            listGuarantee.add(p.getValue());
        }
        return listGuarantee;
    }
}
