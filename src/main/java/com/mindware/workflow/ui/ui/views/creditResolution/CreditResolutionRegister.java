package com.mindware.workflow.ui.ui.views.creditResolution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.creditResolution.CreditResolution;
import com.mindware.workflow.ui.backend.entity.creditResolution.DirectIndirectDebts;
import com.mindware.workflow.ui.backend.entity.creditResolution.Disbursements;
import com.mindware.workflow.ui.backend.entity.creditResolution.Exceptions;
import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequest;
import com.mindware.workflow.ui.backend.rest.creditResolution.CreditResolutionRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsCreditRequestRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsRestTemplate;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.router.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Route(value = "credit-resolution-register", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Registro Resolucion de Credito")
public class CreditResolutionRegister extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {

    private Map<String, List<String>> param;
    private CreditResolutionRestTemplate creditResolutionRestTemplate;
    private PatrimonialStatementRestTemplate patrimonialStatementRestTemplate;
    private ParameterRestTemplate parameterRestTemplate;
//    private BeanValidationBinder<CreditResolution> binder;

    private FlexBoxLayout contentResolution;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;

    private BeanValidationBinder<Disbursements> binderDisbursements;
    private BeanValidationBinder<DirectIndirectDebts> binderDirectIndirectDebts;
    private BeanValidationBinder<CreditResolution> binderCreditResolution;
    private BeanValidationBinder<Exceptions> binderExceptions;
    private ListDataProvider<DirectIndirectDebts> directIndirectDebtsListDataProvider;
    private ListDataProvider<Exceptions> exceptionsListDataProvider;
    private ListDataProvider<Disbursements> disbursementsListDataProvider;

    private List<Disbursements> disbursementsList;
    private List<DirectIndirectDebts> directIndirectDebtsList;
    private List<Exceptions> exceptionsList;
    private CreditResolution creditResolution;

    private  TextField sector;
    private  TextField item;
    private ComboBox<String> creditObject;
//    private TextArea conclusion;
    private TextArea relevantInformation;
    private  DatePicker creationDate;
    private TextField amortizationDescription;
    private ComboBox<String> typeResolution;
    private NumberField reciprocity;
    private ComboBox<String> applicantRating;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        creditResolutionRestTemplate = new CreditResolutionRestTemplate();
        patrimonialStatementRestTemplate = new PatrimonialStatementRestTemplate();
        parameterRestTemplate = new ParameterRestTemplate();

        Location location = beforeEvent.getLocation();
        QueryParameters qp =  location.getQueryParameters();
        param = qp.getParameters();
        int numberRequest = Integer.parseInt(param.get("number-request").get(0));
        creditResolution = creditResolutionRestTemplate.getByNumberRequest(numberRequest);
        String directIndirectDebtsDebts = creditResolution.getDirectIndirectDebts();
        String exceptions = creditResolution.getExceptions();
        String disbursement = creditResolution.getNumberDisbursements();

        ObjectMapper mapper = new ObjectMapper();

        if(directIndirectDebtsDebts==null || directIndirectDebtsDebts.equals("") || directIndirectDebtsDebts.equals("[]")){
            directIndirectDebtsList = new ArrayList<>();
        }else {
            try {
                directIndirectDebtsList = mapper.readValue(directIndirectDebtsDebts, new TypeReference<List<DirectIndirectDebts>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(disbursement==null || disbursement.equals("") || disbursement.equals("[]")){
            disbursementsList = new ArrayList<>();
        }else{
            try {
                disbursementsList = mapper.readValue(disbursement, new TypeReference<List<Disbursements>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

//        if(exceptions==null || exceptions.equals("") || exceptions.equals("[]")){
//            exceptionsList = new ArrayList<>();
//
//
//        }else{
//            try {
//                exceptionsList = mapper.readValue(exceptions,new TypeReference<List<Exceptions>>(){});
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        exceptionsList = getExceptionsCreditRequest(numberRequest);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initBar();
        appBar.setTitle("Resolucion Credito - "+ param.get("full-name").get(0));

        contentResolution = (FlexBoxLayout) createContent(createCreditResolution());

        setViewContent(contentResolution);
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        binderCreditResolution.readBean(creditResolution);
    }

    private List<Exceptions> getExceptionsCreditRequest(Integer numberRequest){
        ExceptionsCreditRequestRestTemplate exceptionsCreditRequestRestTemplate = new ExceptionsCreditRequestRestTemplate();
        List<ExceptionsCreditRequest> exceptionsCreditRequestList = exceptionsCreditRequestRestTemplate.getByNumberRequest(numberRequest);
        ExceptionsRestTemplate exceptionsRestTemplate = new ExceptionsRestTemplate();
        List<Exceptions> exceptionsList = new ArrayList<>();
        for(ExceptionsCreditRequest e : exceptionsCreditRequestList){
            com.mindware.workflow.ui.backend.entity.exceptions.Exceptions ex = exceptionsRestTemplate.getByInternalCode(e.getCodeException());
            if(ex.getTypeException().equals("PERMANENTE")) {
                Exceptions exceptions = new Exceptions();
                exceptions.setPoliticalNumber(e.getCodeException());
                exceptions.setDescription(ex.getDescription());
                exceptions.setId(UUID.randomUUID());
                exceptionsList.add(exceptions);
            }
        }
        return exceptionsList;
    }

    private AppBar initBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(CreditResolutionCreditRequestDtoView.class);
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

    private Component createContent(DetailsDrawer component) {
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setMargin(Vertical.AUTO,Vertical.RESPONSIVE_L);
        content.setSizeFull();
        return content;
    }

    private DetailsDrawer createCreditResolution(){
        HorizontalLayout topBar = new HorizontalLayout();
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(click -> {
            List<String> origin = new ArrayList<>();
            List<String> title = new ArrayList<>();
            List<String> path = new ArrayList<>();
            origin.add("credit-resolution");
            title.add("Resolucion de Creditos");
            path.add("credit-resolution-register");
            Map<String,List<String>> paramCreditResolution = new HashMap<>();
            paramCreditResolution.put("number-request",param.get("number-request"));
            paramCreditResolution.put("number-applicant",param.get("number-applicant"));
            paramCreditResolution.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramCreditResolution.put("full-name",param.get("full-name"));
            paramCreditResolution.put("origin",origin);
            paramCreditResolution.put("path",path);
            paramCreditResolution.put("title",title);
            paramCreditResolution.put("amount",param.get("amount"));

            QueryParameters qp = new QueryParameters(paramCreditResolution);
            UI.getCurrent().navigate("report-preview",qp);

        });

        Button btnSave = new Button("Guardar");
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSave.setEnabled(GrantOptions.grantedOption("Resolucion de Credito"));
        btnSave.addClickListener(click ->{
            if(validateDisbursement()) {
                if (binderCreditResolution.writeBeanIfValid(creditResolution)) {
                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        String jsonException = mapper.writeValueAsString(exceptionsList);
                        String jsonDirectIndirectDebts = mapper.writeValueAsString(directIndirectDebtsList);
                        String jsonDisbursement = mapper.writeValueAsString(disbursementsList);
                        creditResolution.setExceptions(jsonException);
                        creditResolution.setDirectIndirectDebts(jsonDirectIndirectDebts);
                        creditResolution.setNumberRequest(Integer.parseInt(param.get("number-request").get(0)));
                        creditResolution.setNumberDisbursements(jsonDisbursement);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    creditResolutionRestTemplate.add(creditResolution);
                    UIUtils.showNotification("Datos resolucion registrados");
                }
            }
        });

        topBar.setSpacing(true);
        topBar.setPadding(true);
        topBar.add(btnPrint,btnSave);

        Accordion accordion = new Accordion();
        accordion.setWidthFull();

        directIndirectDebtsListDataProvider = new ListDataProvider<>(directIndirectDebtsList);
        exceptionsListDataProvider = new ListDataProvider<>(exceptionsList);
        disbursementsListDataProvider = new ListDataProvider<>(disbursementsList);

        Grid<DirectIndirectDebts> gridIndirectDebts = createGridDebts();
        gridIndirectDebts.setHeight("200px");
        Grid<DirectIndirectDebts> gridDirectDebts = createGridDebts();
        gridDirectDebts.setHeight("200px");
        Grid<Exceptions> gridExceptions = createGridExceptions();
        gridExceptions.setHeight("200px");
        Grid<Disbursements> gridDisbursements = createGridDisbursements();
        gridDisbursements.setHeight("200px");

        gridDirectDebts.addSelectionListener(e -> {
            if(e.getFirstSelectedItem().isPresent()) {
                e.getFirstSelectedItem().ifPresent(this::showDirectIndirectDebts);
                binderDirectIndirectDebts.readBean(e.getFirstSelectedItem().get());
            }
        });

        gridIndirectDebts.addSelectionListener(e -> {
            if(e.getFirstSelectedItem().isPresent()) {
                e.getFirstSelectedItem().ifPresent(this::showDirectIndirectDebts);
                binderDirectIndirectDebts.readBean(e.getFirstSelectedItem().get());
            }
        });

        gridExceptions.addSelectionListener(e ->{
            if(e.getFirstSelectedItem().isPresent()) {
                e.getFirstSelectedItem().ifPresent(this::showExceptions);
                binderExceptions.readBean(e.getFirstSelectedItem().get());
            }
        });

        gridDisbursements.addSelectionListener(e ->{
           if(e.getFirstSelectedItem().isPresent()){
               e.getFirstSelectedItem().ifPresent(this::showDisbursements);
               binderDisbursements.readBean(e.getFirstSelectedItem().get());
           }
        });

        gridDirectDebts.setDataProvider(directIndirectDebtsListDataProvider);
        gridIndirectDebts.setDataProvider(directIndirectDebtsListDataProvider);
        gridExceptions.setDataProvider(exceptionsListDataProvider);
        gridDisbursements.setDataProvider(disbursementsListDataProvider);

        VerticalLayout layoutDirectDebts = gridLayoutDebts(gridDirectDebts,"direct");
        VerticalLayout layoutIndirectDebts = gridLayoutDebts(gridIndirectDebts,"indirect");
        VerticalLayout layoutExceptions = gridLayoutExceptions(gridExceptions);
        VerticalLayout layoutDisbursements = gridLayoutDisbursements(gridDisbursements);

        FormLayout formLayout = formCreditDestination();

        accordion.add("Deudas directas",layoutDirectDebts);
        accordion.add("Deudas indirectas",layoutIndirectDebts);
        accordion.add("Excepciones",layoutExceptions);
        accordion.add("Desembolsos",layoutDisbursements);


        accordion.addOpenedChangeListener(e -> {
            if(e.getOpenedIndex().isPresent()) {
                if (e.getOpenedIndex().getAsInt() == 0) {
                    filterDirectIndirectDebts("direct");
                } else if (e.getOpenedIndex().getAsInt() == 1) {
                    filterDirectIndirectDebts("indirect");
                } else if (e.getOpenedIndex().getAsInt() == 2) {

                }
            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        layout.add(topBar,formLayout,accordion);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeightFull();
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;
    }

    private boolean validateDisbursement(){
        if(disbursementsList.isEmpty()){
            UIUtils.showNotification("Registre los desembolsos a realizarse");
            return false;
        }else{
            Double amount = Double.parseDouble(param.get("amount").get(0));
            Double totalDisbursement = disbursementsList.stream()
                    .mapToDouble(Disbursements::getAmount).sum();
            boolean result = totalDisbursement.equals(amount);
            if(!result){
                UIUtils.showNotification("Monto de la solicitud no coincide con los desembolsos, verifique");
            }
            return result;
        }
    }

    private Grid<Exceptions> createGridExceptions(){
        Grid<Exceptions> grid = new Grid<>();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);

        grid.addColumn(Exceptions::getPoliticalNumber).setFlexGrow(0)
                .setHeader("Cod. Excepcion").setWidth(UIUtils.COLUMN_WIDTH_M)
                .setResizable(true);
        grid.addColumn(Exceptions::getDescription).setFlexGrow(0)
                .setHeader("Excepcion").setWidth(UIUtils.COLUMN_WIDTH_XL)
                .setResizable(true);

        return grid;
    }

    private Grid createGridDebts(){
        Grid<DirectIndirectDebts> grid = new Grid<>();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);

        grid.addColumn(DirectIndirectDebts::getEntity).setFlexGrow(0).setHeader("Entidad")
                .setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createAmount))
                .setFlexGrow(0).setHeader("Monto")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(DirectIndirectDebts::getState).setFlexGrow(0).setHeader("Estado")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(DirectIndirectDebts::getRating).setFlexGrow(0).setHeader("Calif")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(DirectIndirectDebts::getGuarantee).setFlexGrow(0).setHeader("Garantia")
                .setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true);
        grid.addColumn(DirectIndirectDebts::getRate).setFlexGrow(0).setHeader("Tasa")
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setResizable(true);
        grid.addColumn(DirectIndirectDebts::getFinalExpiration).setFlexGrow(0).setHeader("Vcto. final")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createButtonDelete)).setFlexGrow(1);

        return grid;
    }

    private Component createButtonDelete(DirectIndirectDebts directIndirectDebts){
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_ERROR);
        button.setIcon(VaadinIcon.TRASH.create());
        button.setEnabled(GrantOptions.grantedOption("Resolucion de Credito"));
        button.addClickListener(e ->{
            directIndirectDebtsList.removeIf(item -> item.getId().equals(directIndirectDebts.getId()));
            directIndirectDebtsListDataProvider.refreshAll();
            UIUtils.showNotification("Registro marcado para borrar, Guarde los Cambios");
        });

        return button;
    }

    private VerticalLayout gridLayoutDebts(Grid<DirectIndirectDebts> grid, String typeDebt){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btnNew.setEnabled(GrantOptions.grantedOption("Resolucion de Credito"));
        btnNew.addClickListener(click -> {
           DirectIndirectDebts directIndirectDebts = new DirectIndirectDebts();
           directIndirectDebts.setEntity("");
           directIndirectDebts.setAmount(0.0);
           directIndirectDebts.setState("");
           directIndirectDebts.setRating("");
           directIndirectDebts.setGuarantee("");
           directIndirectDebts.setRate(0.0);
           directIndirectDebts.setFinalExpiration(LocalDate.now());
           directIndirectDebts.setTypeDebts(typeDebt);
           showDirectIndirectDebts(directIndirectDebts);
        });
        layout.add(btnNew,grid);
        return layout;
    }

    private VerticalLayout gridLayoutDisbursements(Grid<Disbursements> grid){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btnNew.setEnabled(GrantOptions.grantedOption("Resolucion de Credito"));
        btnNew.addClickListener(event ->{
           Disbursements disbursements = new Disbursements();
           showDisbursements(disbursements);
        });

        layout.add(btnNew,grid);

        return layout;
    }

    private VerticalLayout gridLayoutExceptions(Grid<Exceptions> grid){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
//        Button btnNew = new Button("Nuevo");
//        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
//        btnNew.setEnabled(GrantOptions.grantedOption("Resolucion de Credito"));
//        btnNew.addClickListener(click -> {
//           Exceptions exceptions = new Exceptions();
//           exceptions.setPoliticalNumber("");
//           exceptions.setDescription("");
//           showExceptions(exceptions);
//        });
        layout.add(grid);

        return layout;
    }

    private List<String> getListObjectCredit(){
        List<Parameter> parameterList = parameterRestTemplate.getParametersByCategory("OBJETO DEL CREDITO");
        List<String> objectCreditList = new ArrayList<>();
        for(Parameter p:parameterList){
            objectCreditList.add(p.getDescription());
        }
        return objectCreditList;
    }

    private FormLayout formCreditDestination(){
        FormLayout formLayout = new FormLayout();
        sector = new TextField();
        sector.setWidth("100%");
        sector.setRequired(true);
        sector.setRequiredIndicatorVisible(true);

        item = new TextField();
        item.setWidth("100%");
        item.setRequired(true);
        item.setRequiredIndicatorVisible(true);

        creditObject = new ComboBox<>();
        creditObject.setWidth("100%");
        creditObject.setItems(getListObjectCredit());
        creditObject.setRequired(true);
        creditObject.setRequiredIndicatorVisible(true);
        creditObject.setAllowCustomValue(false);

//        conclusion = new TextArea();
//        conclusion.setWidth("100%");
//        conclusion.setRequired(true);
//        conclusion.setRequiredIndicatorVisible(true);

        relevantInformation = new TextArea();
        relevantInformation.setWidthFull();
        relevantInformation.setRequired(true);
        relevantInformation.setRequiredIndicatorVisible(true);

        creationDate = new DatePicker();
        creationDate.setWidth("100%");
        creationDate.setRequired(true);
        creationDate.setRequiredIndicatorVisible(true);

        amortizationDescription = new TextField();
        amortizationDescription.setWidth("100%");
        amortizationDescription.setRequiredIndicatorVisible(true);
        amortizationDescription.setRequired(true);

        typeResolution = new ComboBox<>();
        typeResolution.setItems(getValueParameter("RESOLUCION"));
        typeResolution.setItems("REGIONAL","NIVEL1","NIVEL2","NIVEL3");
        typeResolution.setRequired(true);
        typeResolution.setRequiredIndicatorVisible(true);
        typeResolution.setWidth("100%");

        reciprocity = new NumberField();
        reciprocity.setWidth("100%");
        reciprocity.setRequiredIndicatorVisible(true);

        applicantRating = new ComboBox<>();
        applicantRating.setWidth("100%");
        applicantRating.setItems("A","B","C","D","E","F");
        applicantRating.setRequired(true);
        applicantRating.setRequiredIndicatorVisible(true);

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

        binderCreditResolution = new BeanValidationBinder<>(CreditResolution.class);
        binderCreditResolution.forField(typeResolution).asRequired("Tipo Resolucion es requerida")
                .bind(CreditResolution::getTypeResolution,CreditResolution::setTypeResolution);
        binderCreditResolution.forField(creationDate).asRequired("Fecha Resolucion es requerida")
                .bind(CreditResolution::getCreationDate,CreditResolution::setCreationDate);
        binderCreditResolution.forField(amortizationDescription).asRequired("Descripcion de la amortizacion es requerida")
                .bind(CreditResolution::getAmortizationDescription,CreditResolution::setAmortizationDescription);
        binderCreditResolution.forField(sector).asRequired("Sector es requerido")
                .bind(CreditResolution::getSector,CreditResolution::setSector);
        binderCreditResolution.forField(item).asRequired("Rubro es requerido")
                .bind(CreditResolution::getItem,CreditResolution::setItem);
        binderCreditResolution.forField(creditObject).asRequired("Objeto del credito es requerido")
                .bind(CreditResolution::getCreditObject,CreditResolution::setCreditObject);
//        binderCreditResolution.forField(conclusion).asRequired("Conclusion es requerida")
//                .bind(CreditResolution::getConclusion,CreditResolution::setConclusion);
        binderCreditResolution.forField(relevantInformation).asRequired("Informacion requerida")
                .bind(CreditResolution::getCreditRequestRelevantInformation,CreditResolution::setCreditRequestRelevantInformation);
        binderCreditResolution.forField(reciprocity).asRequired("Reciprocidad es requerida")
                .bind(CreditResolution::getReciprocity,CreditResolution::setReciprocity);
        binderCreditResolution.forField(applicantRating).asRequired("Calificacion es requerida")
                .bind(CreditResolution::getApplicantRating,CreditResolution::setApplicantRating);

        formLayout.addFormItem(typeResolution,"Tipo Resolucion");
        formLayout.addFormItem(creationDate,"Fecha Resolucion");
        formLayout.addFormItem(amortizationDescription,"Descripcion de la amortizacion");
        formLayout.addFormItem(applicantRating,"Calificacion");
        formLayout.addFormItem(sector,"Sector");
        formLayout.addFormItem(item,"Rubro");
        formLayout.addFormItem(creditObject,"Objeto de la operacion");
        formLayout.addFormItem(reciprocity,"Monto Reciprocidad");
//        FormLayout.FormItem conclusionItem = formLayout.addFormItem(conclusion,"Conclusion");
        FormLayout.FormItem relavantInformationItem = formLayout.addFormItem(relevantInformation,"Informacion relevante del credito");
//        UIUtils.setColSpan(2,conclusionItem);
        UIUtils.setColSpan(3,relavantInformationItem);


        return formLayout;
    }

    private List<String> getValueParameter(String category){
        List<Parameter> parameterList = parameterRestTemplate.getParametersByCategory("category");
        List<String> valueList = new ArrayList<>();
        for(Parameter p : parameterList){
            String value = p.getValue();
            valueList.add(value);
        }
        return valueList;
    }

    private Component createAmount(DirectIndirectDebts directIndirectDebts){
        Double amount = directIndirectDebts.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private Component createAmountDisbursement(Disbursements disbursements){
        Double amount = disbursements.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private Grid createGridDisbursements(){
        Grid<Disbursements> grid = new Grid<>();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);

        grid.addColumn(Disbursements::getDescription)
                .setFlexGrow(0)
                .setHeader("Descripcion")
                .setAutoWidth(true)
                .setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createAmountDisbursement))
                .setFlexGrow(0)
                .setHeader("Monto desembolso")
                .setAutoWidth(true)
                .setResizable(true);
        grid.addColumn(Disbursements::getConditions)
                .setFlexGrow(0)
                .setHeader("Condiciones")
                .setAutoWidth(true)
                .setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createButtonDeleteDisbursement))
                .setFlexGrow(0);
        return grid;
    }

    private Component createButtonDeleteDisbursement(Disbursements disbursements){
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_ERROR);
        button.setIcon(VaadinIcon.TRASH.create());
        button.setEnabled(GrantOptions.grantedOption("Resolucion de Credito"));
        button.addClickListener(e ->{
            disbursementsList.removeIf(item -> item.getId().equals(disbursements.getId()));
            disbursementsListDataProvider.refreshAll();
            UIUtils.showNotification("Registro marcado para borrar, guarde los cambios");
        });
        return button;

    }

    private void showDisbursements(Disbursements disbursements){
        detailsDrawerHeader.setTitle(disbursements.getDescription());
        detailsDrawer.setContent(createDisbursements(disbursements));
        binderDisbursements.readBean(disbursements);
        detailsDrawer.show();
    }

    private DetailsDrawer createDisbursements(Disbursements disbursements){
        TextField description = new TextField();
        description.setWidthFull();
        description.setRequired(true);
        description.setRequiredIndicatorVisible(true);

        NumberField amount = new NumberField();
        amount.setWidthFull();
        amount.setRequiredIndicatorVisible(true);

        TextArea conditions = new TextArea();
        conditions.setWidthFull();
        conditions.setRequired(true);
        conditions.setRequiredIndicatorVisible(true);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderDisbursements = new BeanValidationBinder<>(Disbursements.class);
        binderDisbursements.forField(description).asRequired("Descripcion del desembolso es requerido")
                .bind(Disbursements::getDescription,Disbursements::setDescription);
        binderDisbursements.forField(amount).asRequired("Monto del desembolso es requerido")
                .bind(Disbursements::getAmount,Disbursements::setAmount);
        binderDisbursements.forField(conditions).asRequired("Condiciones para el desembolso son requeridas")
                .bind(Disbursements::getConditions,Disbursements::setConditions);

        binderDisbursements.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderDisbursements.hasChanges();
           footer.saveState(isValid && hasChanges);
        });

        formLayout.addFormItem(description,"Descripcion");
        formLayout.addFormItem(amount,"Monto desembolso");
        FormLayout.FormItem conditionItem = formLayout.addFormItem(conditions,"Condiciones desembolso");
        UIUtils.setColSpan(3, conditionItem);

        footer.addSaveListener(event -> {
           if(binderDisbursements.writeBeanIfValid(disbursements)){
               if(disbursements.getId()==null){
                   disbursements.setId(UUID.randomUUID());
               }
               disbursementsList.removeIf(value -> value.getId().equals(disbursements.getId()));
               disbursementsList.add(disbursements);
               disbursementsListDataProvider.refreshAll();
               ObjectMapper mapper = new ObjectMapper();
               try{
                   String jsonDisbursements = mapper.writeValueAsString(disbursementsList);
                   creditResolution.setNumberDisbursements(jsonDisbursements);
               }catch (JsonProcessingException e){
                   e.printStackTrace();
               }
               detailsDrawer.hide();
           }
        });

        footer.addCancelListener(e ->  detailsDrawer.hide());
        detailsDrawerForm.setContent(formLayout);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }

    private void showDirectIndirectDebts(DirectIndirectDebts directIndirectDebts){
        detailsDrawerHeader.setTitle(directIndirectDebts.getEntity());
        detailsDrawer.setContent(createDirectIndirectDebts(directIndirectDebts));
        binderDirectIndirectDebts.readBean(directIndirectDebts);
        detailsDrawer.show();
    }

    private DetailsDrawer createDirectIndirectDebts(DirectIndirectDebts directIndirectDebts){
        TextField entity = new TextField();
        entity.setWidth("100%");
        entity.setRequired(true);
        entity.setRequiredIndicatorVisible(true);

        NumberField amount = new NumberField();
        amount.setWidth("100%");
        amount.setRequiredIndicatorVisible(true);

        TextField state = new TextField();
        state.setWidth("100%");
        state.setRequired(true);
        state.setRequiredIndicatorVisible(true);

        ComboBox<String> rating = new ComboBox<>();
        rating.setWidth("100%");
        rating.setItems("A","B","C","D","E","F");
        rating.setRequiredIndicatorVisible(true);
        rating.setRequired(true);

        TextField guarantee = new TextField();
        guarantee.setWidth("100%");
        guarantee.setRequiredIndicatorVisible(true);
        guarantee.setRequired(true);

        NumberField rate = new NumberField();
        rate.setWidth("100%");
        rate.setRequiredIndicatorVisible(true);

        DatePicker finalExpiration = new DatePicker();
        finalExpiration.setWidth("100%");
        finalExpiration.setRequiredIndicatorVisible(true);
        finalExpiration.setRequired(true);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderDirectIndirectDebts = new BeanValidationBinder<>(DirectIndirectDebts.class);
        binderDirectIndirectDebts.forField(entity).asRequired("Entidad es requerida")
                .bind(DirectIndirectDebts::getEntity,DirectIndirectDebts::setEntity);
        binderDirectIndirectDebts.forField(amount).asRequired("Monto es requerido")
                .withValidator(value -> value.doubleValue()>0,"Monto debe ser mayor a cero")
                .bind(DirectIndirectDebts::getAmount,DirectIndirectDebts::setAmount);
        binderDirectIndirectDebts.forField(state).asRequired("Estado es requerido")
                .bind(DirectIndirectDebts::getState,DirectIndirectDebts::setState);
        binderDirectIndirectDebts.forField(rating).asRequired("Calificacion es requerida")
                .bind(DirectIndirectDebts::getRating,DirectIndirectDebts::setRating);
        binderDirectIndirectDebts.forField(guarantee).asRequired("Garantia  es requerida")
                .bind(DirectIndirectDebts::getGuarantee,DirectIndirectDebts::setGuarantee);
        binderDirectIndirectDebts.forField(rate).asRequired("Tasa es requerida")
                .withValidator(value -> value.doubleValue()>0,"Tasa debe ser mayor a cero")
                .bind(DirectIndirectDebts::getRate,DirectIndirectDebts::setRate);
        binderDirectIndirectDebts.forField(finalExpiration).asRequired("Fecha vencimiento es requerida")
                .bind(DirectIndirectDebts::getFinalExpiration,DirectIndirectDebts::setFinalExpiration);

        binderDirectIndirectDebts.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderDirectIndirectDebts.hasChanges();
           footer.saveState(isValid && hasChanges);
        });

        formLayout.addFormItem(entity,"Entidad");
        formLayout.addFormItem(amount,"Monto $us");
        formLayout.addFormItem(state,"Estado");
        formLayout.addFormItem(rating,"Calificacion");
        formLayout.addFormItem(guarantee,"Garantia");
        formLayout.addFormItem(rate,"Tasa interes");
        formLayout.addFormItem(finalExpiration,"Fecha vencimiento");

        footer.addSaveListener(event ->{
            if(binderDirectIndirectDebts.writeBeanIfValid(directIndirectDebts)){
                if(directIndirectDebts.getId()==null){
                    directIndirectDebts.setId(UUID.randomUUID());
                }
                directIndirectDebtsList.removeIf(value -> value.getId().equals(directIndirectDebts.getId()));
                directIndirectDebtsList.add(directIndirectDebts);
                directIndirectDebtsListDataProvider.refreshAll();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String jsonDirectIndirectDebts = mapper.writeValueAsString(directIndirectDebtsList);
                    creditResolution.setDirectIndirectDebts(jsonDirectIndirectDebts);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                detailsDrawer.hide();
            }

        });

       footer.addCancelListener(e -> detailsDrawer.hide());
       detailsDrawerForm.setContent(formLayout);
       detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }

    private void filterDirectIndirectDebts(String filter){
        if(directIndirectDebtsListDataProvider.getItems().size()>0){
            directIndirectDebtsListDataProvider.setFilterByValue(DirectIndirectDebts::getTypeDebts,filter);
        }
    }

    private DetailsDrawer createExceptions(Exceptions exceptions){
        TextField politicalNumber = new TextField();
        politicalNumber.setWidth("100%");

        TextArea description = new TextArea();
        description.setWidth("100%");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderExceptions = new BeanValidationBinder<>(Exceptions.class);
        binderExceptions.forField(politicalNumber).asRequired("Cod. Excepcion es requerido")
                .bind(Exceptions::getPoliticalNumber,Exceptions::setPoliticalNumber);
        binderExceptions.forField(description).asRequired("Excepcion es requerida")
                .bind(Exceptions::getDescription,Exceptions::setDescription);

        binderExceptions.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderExceptions.hasChanges();
           footer.saveState(isValid && hasChanges);
        });

        formLayout.addFormItem(politicalNumber,"Cod. Excepcion");
        formLayout.addFormItem(description,"Excepcion");

        footer.addSaveListener(event -> {
           if(binderExceptions.writeBeanIfValid(exceptions)){
               if(exceptions.getId()==null){
                   exceptions.setId(UUID.randomUUID());
               }
               exceptionsList.removeIf(value -> value.getId().equals(exceptions.getId()));
               exceptionsList.add(exceptions);
               exceptionsListDataProvider.refreshAll();
               ObjectMapper mapper = new ObjectMapper();
               try {
                   String jsonExceptions = mapper.writeValueAsString(exceptionsList);
                   creditResolution.setExceptions(jsonExceptions);
               } catch (JsonProcessingException e) {
                   e.printStackTrace();
               }
           }
           detailsDrawer.hide();
        });

        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawerForm.setContent(formLayout);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }

    private void showExceptions(Exceptions exceptions){
        detailsDrawerHeader.setTitle("Politica de excepcion:"+ exceptions.getPoliticalNumber());
        detailsDrawer.setContent(createExceptions(exceptions));
        binderExceptions.readBean(exceptions);
        detailsDrawer.show();
    }

}
