package com.mindware.workflow.ui.ui.views.legal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.legal.*;
import com.mindware.workflow.ui.backend.rest.legal.LegalInformationRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDate;
import java.util.*;

@Route(value = "register-legal-information", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Registro Informe Legal")
public class LegalInformationRegister extends SplitViewFrame implements HasUrlParameter<String> {
    private Map<String, List<String>> param;
    private LegalInformationRestTemplate legalInformationRestTemplate;

//    private FlexBoxLayout contentLegalInformation;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;

    private LegalInformation legalInformation;

    private BeanValidationBinder<LegalInformation> binderLegalInformation;
    private BeanValidationBinder<DocumentSubmitted> binderDocumentSubmitted;
    private BeanValidationBinder<DataDocument> binderDataDocument;
    private BeanValidationBinder<Owners> binderOwners;
    private BeanValidationBinder<Seat> binderSeat;
//    private BeanValidationBinder<GenericItem> binderObservation;
//    private BeanValidationBinder<GenericItem> binderMissingDocumentation;
//    private BeanValidationBinder<GenericItem> binderContractRequiriment;
//    private BeanValidationBinder<GenericItem> binderClarification;
    private BeanValidationBinder<GenericItem> binderGenericItem;

    private FlexBoxLayout contentIdentificationProperty;
    private FlexBoxLayout contentDocumentSubmitted;
    private FlexBoxLayout contentGenericItems;

    private ListDataProvider<Seat> seatListDataProvider;
    private ListDataProvider<Owners> ownersListDataProvider;
    private ListDataProvider<DataDocument> dataDocumentListDataProvider;
    private ListDataProvider<GenericItem> genericItemListDataProvider;


    private List<Seat> seatList;
    private List<Owners> ownersList;
    private List<GenericItem> genericItemList;
    private List<DocumentSubmitted> documentSubmittedList;
    private List<DataDocument> dataDocumentList;
    private List<String> listTabs;
    private Integer numberRequest;
    private DocumentSubmitted documentSubmitted;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        legalInformationRestTemplate = new LegalInformationRestTemplate();

        Location location = beforeEvent.getLocation();
        QueryParameters qp =  location.getQueryParameters();
        param = qp.getParameters();
        numberRequest = Integer.parseInt(param.get("number-request").get(0));
        legalInformation = legalInformationRestTemplate.getByNumberRequest(numberRequest);
        binderLegalInformation = new BeanValidationBinder<>(LegalInformation.class);

        try {
            readListValues();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private void readListValues() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String registrationSeat = legalInformation.getRegistrationSeat();
        String registrationOwners = legalInformation.getOwners();
        String documentSubmittedStr = legalInformation.getDocumentsSubmitted();
        String genericItems = legalInformation.getDetails();
        String dataDocument = legalInformation.getDataDocument();

        if(registrationSeat==null || registrationSeat.equals("") || registrationSeat.equals("[]")){
            seatList = new ArrayList<>();
        }else {
            seatList = mapper.readValue(registrationSeat, new TypeReference<List<Seat>>() {});
        }

        if(registrationOwners==null || registrationOwners.equals("") || registrationOwners.equals("[]")){
            ownersList = new ArrayList<>();
        }else{
            ownersList = mapper.readValue(registrationOwners, new TypeReference<List<Owners>>() {});
        }

        if(documentSubmittedStr==null || documentSubmittedStr.equals("") || documentSubmittedStr.equals("[]")){
            documentSubmittedList = new ArrayList<>();
            documentSubmitted = new DocumentSubmitted();
        }else{
            documentSubmittedList = mapper.readValue(documentSubmittedStr, new TypeReference<List<DocumentSubmitted>>() {});
            documentSubmitted = documentSubmittedList.get(0);
        }
        if(dataDocument==null || dataDocument.equals("") || dataDocument.equals("[]")){
            dataDocumentList = new ArrayList<>();
        }else{
            dataDocumentList = mapper.readValue(dataDocument,new TypeReference<List<DataDocument>>(){});
        }

        if(genericItems==null || genericItems.equals("") || genericItems.equals("[]")){
            genericItemList = new ArrayList<>();
        }else{
            genericItemList = mapper.readValue(genericItems, new TypeReference<List<GenericItem>>() {});
        }

        ownersListDataProvider = new ListDataProvider<>(ownersList);
        seatListDataProvider = new ListDataProvider<>(seatList);
        dataDocumentListDataProvider = new ListDataProvider<>(dataDocumentList);
        genericItemListDataProvider = new ListDataProvider<>(genericItemList);

    }

//    @SneakyThrows
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initBar();
        appBar.setTitle("Informe Legal - "+ param.get("full-name").get(0));

        binderOwners = new BeanValidationBinder<>(Owners.class);

        contentIdentificationProperty = (FlexBoxLayout) createContent(createLayoutIdentificationProperty());
        contentDocumentSubmitted = (FlexBoxLayout) createContent(createDocumentsSubmitted());
        contentGenericItems = (FlexBoxLayout) createContent(createGenericItems());

        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Informe Legal"));
        footer.addSaveListener(e -> {
            ObjectMapper mapper = new ObjectMapper();
            if(binderDocumentSubmitted.writeBeanIfValid(documentSubmitted)) {
                if (documentSubmittedList.size()>0)    documentSubmittedList.remove(0);
                documentSubmittedList.add(documentSubmitted);
                String jsonDocumentSubmitted = null;
                try {
                    jsonDocumentSubmitted = mapper.writeValueAsString(documentSubmittedList);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
                legalInformation.setDocumentsSubmitted(jsonDocumentSubmitted);
                if(binderLegalInformation.writeBeanIfValid(legalInformation)){
                    if(legalInformation.getNumberRequest()==null){
                        legalInformation.setNumberRequest(numberRequest);
                        legalInformation.setCreationDate(LocalDate.now());
                        legalInformation.setCreatedBy(VaadinSession.getCurrent().getAttribute("login").toString());
                    }

                    legalInformationRestTemplate.add(legalInformation);
                    UIUtils.showNotification("Informe Legal Registrado");
                    UI.getCurrent().navigate(LegalInformationView.class);
                }
            }

        });
        setViewHeader(topBar());
        setViewContent(contentIdentificationProperty,contentDocumentSubmitted,contentGenericItems);
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        setViewFooter(footer);
        binderLegalInformation.readBean(legalInformation);
    }

    private AppBar initBar(){
        MainLayout.get().getAppBar().reset();
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);

        appBar.addTab("Datos Bien Inmueble/Derecho Propietario");
        appBar.addTab("Documentos presentados");
        appBar.addTab("Observaciones/Conclusiones/Aclaraciones");

        listTabs = new ArrayList<>();
        listTabs.add("Datos Bien Inmueble/Derecho Propietario");
        listTabs.add("Documentos presentados");
        listTabs.add("Observaciones/Conclusiones/Aclaraciones");

        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(LegalInformationView.class);
        });

        appBar.addTabSelectionListener(e ->{
            if(e.getSelectedTab() != null){
                setComponentVisible(e.getSelectedTab());
            }
        });
        return appBar;
    }

    private  HorizontalLayout topBar(){
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");

        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(e ->{
            Map<String,List<String>> paramL = new HashMap<>();
            List<String> path = new ArrayList<>();
            List<String> origin = new ArrayList<>();
            List<String> createdBy = new ArrayList<>();

            path.add("register-legal-information");
            origin.add("legal-information-report");
            createdBy.add(legalInformation.getCreatedBy());

            paramL.put("origin",origin);
            paramL.put("path",path);
            paramL.put("number-request",param.get("number-request"));
            paramL.put("created-by",createdBy);
            paramL.put("number-applicant",param.get("number-applicant"));
            paramL.put("task",param.get("task"));
            paramL.put("full-name",param.get("full-name"));

            QueryParameters qp = new QueryParameters(paramL);
            UI.getCurrent().navigate("report-preview",qp);
        });
        layout.add(btnPrint);
        return layout;
    }

    private void setComponentVisible(Tab selectdTab){
        if(listTabs.contains(selectdTab.getLabel())){
            if(selectdTab.getLabel().equals("Datos Bien Inmueble/Derecho Propietario")){
                contentIdentificationProperty.setVisible(true);
                contentDocumentSubmitted.setVisible(false);
                contentGenericItems.setVisible(false);
            }else if(selectdTab.getLabel().equals("Documentos presentados")){
                contentIdentificationProperty.setVisible(false);
                contentDocumentSubmitted.setVisible(true);
                contentGenericItems.setVisible(false);
            }else if(selectdTab.getLabel().equals("Observaciones/Conclusiones/Aclaraciones")){
                contentIdentificationProperty.setVisible(false);
                contentDocumentSubmitted.setVisible(false);
                contentGenericItems.setVisible(true);
            }
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

    private Component createContent(DetailsDrawer component) {
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setMargin(Vertical.AUTO,Vertical.RESPONSIVE_L);
        content.setSizeFull();
        return content;
    }
    
// INIT REGISTRATION SEAT
    private DetailsDrawer createLayoutIdentificationProperty(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Accordion accordion = new Accordion();
        accordion.setSizeFull();

        accordion.add("Asientos",layoutSeat());
        accordion.add("Datos inmueble",formIdentificationLeglInformation());
        accordion.add("Datos de los propietarios",createGridOwners());

        accordion.addOpenedChangeListener(e -> {
           if(e.getOpenedIndex().isPresent()){

           }
        });

        layout.add(accordion);


        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeightFull();
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;
    }


    private VerticalLayout layoutSeat(){
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("400px");
        layout.setSpacing(true);

        TextField registration = new TextField("Matricula");
        registration.setWidth("100%");
        registration.setRequired(true);
        registration.setRequiredIndicatorVisible(true);
        registration.setPlaceholder("0.00.0.00.0000000");

        binderLegalInformation.forField(registration).asRequired("Numero matricula es requerida")
                .bind(LegalInformation::getRegistration,LegalInformation::setRegistration);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setHeight("300px");

        Grid<Seat> grid = new Grid<>();
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setDataProvider(seatListDataProvider);
        grid.addColumn(Seat::getSeatNumber).setHeader("Asiento").setWidth(UIUtils.COLUMN_WIDTH_L).setFlexGrow(0);
        grid.addColumn(Seat::getSeatDate).setHeader("Fecha").setWidth(UIUtils.COLUMN_WIDTH_L).setFlexGrow(0);
        grid.addSelectionListener(e ->{
           if(e.getFirstSelectedItem().isPresent()){
               e.getFirstSelectedItem().ifPresent(this::showSeatDetail);
               binderSeat.readBean(e.getFirstSelectedItem().get());
           }
        });

        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setEnabled(GrantOptions.grantedOption("Informe Legal"));
        btnNew.addClickListener(event -> {
            showSeatDetail(new Seat());
        });

        horizontalLayout.add(grid,btnNew);
        layout.add(registration,horizontalLayout);

        return layout;
    }

    private void showSeatDetail(Seat seat){
        detailsDrawerHeader.setTitle("Registro Asiento");
        detailsDrawer.setContent(createSeat(seat));
        detailsDrawer.show();
    }

    private DetailsDrawer createSeat(Seat seat){
        TextField seatNumber = new TextField();
        seatNumber.setWidth("100%");
        seatNumber.setRequired(true);
        seatNumber.setRequiredIndicatorVisible(true);

        DatePicker seatDate = new DatePicker();
        seatDate.setWidth("100%");
        seatDate.setRequired(true);
        seatDate.setRequiredIndicatorVisible(true);

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

        binderSeat = new BeanValidationBinder<>(Seat.class);
        binderSeat.forField(seatNumber).asRequired("Numero Asiento es requerido").bind(Seat::getSeatNumber,Seat::setSeatNumber);
        binderSeat.forField(seatDate).asRequired("Fecha de asiento es requerida").bind(Seat::getSeatDate,Seat::setSeatDate);

        formLayout.addFormItem(seatNumber,"Numero Asiento");
        formLayout.addFormItem(seatDate,"Fecha Asiento");

        binderSeat.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderSeat.hasChanges();
           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Informe Legal"));
        });

        footer.addSaveListener(e ->{
           if(binderSeat.writeBeanIfValid(seat)) {
               if(seat.getId()==null){
                   seat.setId(UUID.randomUUID());
               }
           }
           seatList.removeIf(value -> value.getId().equals(seat.getId()));
           seatList.add(seat);
           seatListDataProvider.refreshAll();
           ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonRegistrationSeat = mapper.writeValueAsString(seatList);
                legalInformation.setRegistrationSeat(jsonRegistrationSeat);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            detailsDrawer.hide();

        });
        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawerForm.setContent(formLayout);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }
//    END SEAT REGISTRATION
    private FormLayout formIdentificationLeglInformation(){
        FormLayout formLayout = new FormLayout();

        TextField reportNumber = new TextField();
        reportNumber.setWidth("100%");
        reportNumber.setRequired(true);
        reportNumber.setRequiredIndicatorVisible(true);
        reportNumber.setPlaceholder("No. 00/0000");

        TextField propertyType = new TextField();
        propertyType.setWidth("100%");
        propertyType.setRequiredIndicatorVisible(true);
        propertyType.setRequired(true);
        propertyType.setPlaceholder("LOTE DE TERRENO / DEPARTAMENTO / PARQUEO (CONFORME FOLIO REAL)");

        TextField location = new TextField();
        location.setWidth("100%");
        location.setRequired(true);
        location.setRequiredIndicatorVisible(true);
        location.setPlaceholder("CONFORME FOLIO");

        NumberField surface = new NumberField();
        surface.setWidth("100%");
        surface.setRequiredIndicatorVisible(true);
        surface.setPlaceholder("000.00 mts2");

        TextField north = new TextField();
        north.setWidth("100%");
        north.setRequired(true);
        north.setRequiredIndicatorVisible(true);
        north.setPlaceholder("CONFORME FOLIO REAL");

        TextField south = new TextField();
        south.setWidth("100%");
        south.setRequired(true);
        south.setRequiredIndicatorVisible(true);
        south.setPlaceholder("CONFORME FOLIO REAL");

        TextField east = new TextField();
        east.setWidth("100%");
        east.setRequired(true);
        east.setRequiredIndicatorVisible(true);
        east.setPlaceholder("CONFORME FOLIO REAL");

        TextField west = new TextField();
        west.setWidth("100%");
        west.setRequired(true);
        west.setRequiredIndicatorVisible(true);
        west.setPlaceholder("CONFORME FOLIO REAL");

        TextField typeTitle = new TextField();
        typeTitle.setWidth("100%");
        typeTitle.setRequiredIndicatorVisible(true);
        typeTitle.setRequired(true);
        typeTitle.setPlaceholder("COMPRA VENTA / DECLARATORIA DE HEREDEROS / ETC.");

        TextField publicDeed = new TextField();
        publicDeed.setWidth("100%");
        publicDeed.setRequired(true);
        publicDeed.setRequiredIndicatorVisible(true);
        publicDeed.setPlaceholder("N° 000");

        DatePicker dateDeed = new DatePicker();
        dateDeed.setWidth("100%");
        dateDeed.setRequiredIndicatorVisible(true);
        dateDeed.setRequired(true);

        TextArea givenBy = new TextArea();
        givenBy.setWidth("100%");
        givenBy.setRequiredIndicatorVisible(true);
        givenBy.setRequired(true);
        givenBy.setPlaceholder("Notaria de Fe Pública Nª 000, del Distrito Judicial de XXXXX, a cargo del Dr. XXXXXXXXXX.");

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

        formLayout.addFormItem(reportNumber,"Nro. Informe");
        FormLayout.FormItem propertyTypeItem = formLayout.addFormItem(propertyType,"Tipo de Inmueble");
        UIUtils.setColSpan(2,propertyTypeItem);
        formLayout.addFormItem(location,"Ubicacion");
        formLayout.addFormItem(surface,"Superficie (mts2)");
        formLayout.addFormItem(north,"Lindero Norte");
        formLayout.addFormItem(south,"Lindero Sud");
        formLayout.addFormItem(east,"Lindero Este");
        formLayout.addFormItem(west,"Lindero Oeste");
        formLayout.addFormItem(typeTitle,"Tipo Titulo Propiedad");
        formLayout.addFormItem(publicDeed,"Escritura publica");
        formLayout.addFormItem(dateDeed,"Fecha escritura");
        FormLayout.FormItem givenByItem = formLayout.addFormItem(givenBy,"Otorgado por");
        UIUtils.setColSpan(4,givenByItem);

        binderLegalInformation.forField(propertyType).asRequired("Tipo Inmueble es requerido")
                .bind(LegalInformation::getPropertyType,LegalInformation::setPropertyType);
        binderLegalInformation.forField(location).asRequired("Ubicacion es requerida")
                .bind(LegalInformation::getLocation,LegalInformation::setLocation);
        binderLegalInformation.forField(surface).asRequired("Superfici es requerida")
                .bind(LegalInformation::getSurface,LegalInformation::setSurface);
        binderLegalInformation.forField(north).asRequired("Lindero Norte es requerido")
                .bind(LegalInformation::getNorth,LegalInformation::setNorth);
        binderLegalInformation.forField(south).asRequired("Lindero Sud es requerido")
                .bind(LegalInformation::getSouth,LegalInformation::setSouth);
        binderLegalInformation.forField(east).asRequired("Lindero Este es requerido")
                .bind(LegalInformation::getEast,LegalInformation::setEast);
        binderLegalInformation.forField(west).asRequired("Lindero Oeste es requerido")
                .bind(LegalInformation::getWest,LegalInformation::setWest);
        binderLegalInformation.forField(typeTitle).asRequired("Tipo Titulo es requerido")
                .bind(LegalInformation::getTypeTitle,LegalInformation::setTypeTitle);
        binderLegalInformation.forField(publicDeed).asRequired("Escritura publica es requerida")
                .bind(LegalInformation::getPublicDeed,LegalInformation::setPublicDeed);
        binderLegalInformation.forField(dateDeed).asRequired("Fecha escritura es requerida")
                .bind(LegalInformation::getDateDeed, LegalInformation::setDateDeed);
        binderLegalInformation.forField(givenBy).asRequired("A quien se otorga es requerido")
                .bind(LegalInformation::getGivenBy,LegalInformation::setGivenBy);


        return formLayout;
    }

    private VerticalLayout createGridOwners(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setHeight("500px");
        Grid<Owners> grid = new Grid<>();
        grid.setDataProvider(ownersListDataProvider);
        grid.setWidthFull();
        grid.setHeight("400px");

        grid.addColumn(Owners::getFullName).setFlexGrow(0).setHeader("Propietario").setWidth(UIUtils.COLUMN_WIDTH_L);
        grid.addColumn(Owners::getIdCardComplete).setFlexGrow(0).setHeader("C.I.").setWidth(UIUtils.COLUMN_WIDTH_S);
        grid.addColumn(Owners::getMaritalStatus).setFlexGrow(0).setHeader("Estado Civil").setWidth(UIUtils.COLUMN_WIDTH_M);

        grid.addSelectionListener(e -> {
           if(e.getFirstSelectedItem().isPresent()) {
               e.getFirstSelectedItem().ifPresent(this::showOwners);
               binderOwners.readBean(e.getFirstSelectedItem().get());
           }
        });

        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);
        btnNew.setEnabled(GrantOptions.grantedOption("Informe Legal"));
        btnNew.addClickListener(event ->{
           showOwners(new Owners());

        });

        layout.add(btnNew,grid);

        return layout;
    }

    private void showOwners(Owners owners){
        detailsDrawerHeader.setTitle("Registro Propietario");
        detailsDrawer.setContent(createOwner(owners));
        detailsDrawer.show();
    }
    private DetailsDrawer createOwner(Owners owners){
        TextField fullName = new TextField();
        fullName.setWidth("100%");
        fullName.setRequiredIndicatorVisible(true);
        fullName.setRequired(true);
        fullName.setPlaceholder("Nombres, Apellido Paterno, Materno");

        TextField idCardComplete = new TextField();
        idCardComplete.setWidth("100%");
        idCardComplete.setRequired(true);
        idCardComplete.setRequiredIndicatorVisible(true);
        idCardComplete.setPlaceholder("99999999EXT");

        TextField maritalStatus = new TextField();
        maritalStatus.setWidth("100%");
        maritalStatus.setRequiredIndicatorVisible(true);
        maritalStatus.setRequired(true);
        maritalStatus.setPlaceholder("Segun Carnet");

        binderOwners.forField(fullName).asRequired("Nombre completo es requerido")
                .bind(Owners::getFullName,Owners::setFullName);
        binderOwners.forField(idCardComplete).asRequired("Identificacion es requerida")
                .bind(Owners::getIdCardComplete,Owners::setIdCardComplete);
        binderOwners.forField(maritalStatus).asRequired("Estado civil requerido")
                .bind(Owners::getMaritalStatus,Owners::setMaritalStatus);

        FormLayout formOwners = new FormLayout();
        formOwners.setSizeFull();
        formOwners.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px",3,FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formOwners.addFormItem(fullName,"Nombre completo");
        formOwners.addFormItem(idCardComplete,"Carnet compelto");
        formOwners.addFormItem(maritalStatus,"Estado civil");

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();

        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderOwners.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderOwners.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Informe Legal"));
        });

        footer.addSaveListener(e -> {
           if(binderOwners.writeBeanIfValid(owners)){
               if(owners.getId()==null){
                   owners.setId(UUID.randomUUID());
               }
               ownersList.removeIf(c -> c.getId().equals(owners.getId()));
               ownersList.add(owners);
               ownersListDataProvider.refreshAll();
               UIUtils.showNotification("Propietario registrado");
               detailsDrawer.hide();
               ObjectMapper mapper = new ObjectMapper();
               try {
                   String jsonOwners = mapper.writeValueAsString(ownersList);
                   legalInformation.setOwners(jsonOwners);
               } catch (JsonProcessingException ex) {
                   ex.printStackTrace();
               }
               detailsDrawer.hide();
           }
        });

        footer.addCancelListener(e -> detailsDrawer.hide());

        detailsDrawerForm.setContent(formOwners);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }

    private void showDataDocumennt(DataDocument dataDocument){
        detailsDrawerHeader.setTitle(dataDocument.getDocument());
        detailsDrawer.setContent(createDataDocument(dataDocument));
        detailsDrawer.show();
    }

    private DetailsDrawer createDocumentsSubmitted() {

        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setEnabled(GrantOptions.grantedOption("Informe Legal"));
        btnNew.addClickListener(event -> {
            DataDocument dataDocument = new DataDocument();
            dataDocument.setDocument("Nuevo");
            showDataDocumennt(dataDocument);
        });

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
//        layout.setSpacing(true);

        Grid<DataDocument> grid = new Grid<>();


        grid.setDataProvider(dataDocumentListDataProvider);
        grid.addColumn(DataDocument::getDocument).setFlexGrow(1).setResizable(true)
                .setHeader("Documento");
        grid.addColumn(TemplateRenderer.<DataDocument> of("[[item.dateDocument]]")
                .withProperty("dateDocument",
                        dataDocument -> UIUtils.formatDate(dataDocument.getDateDocument())))
                .setHeader("Fecha documento").setComparator(DataDocument::getDateDocument)
                .setFlexGrow(1).setResizable(true);
        grid.addColumn(DataDocument::getFs).setFlexGrow(1)
                .setHeader("FS").setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createOriginalPhotocopy)).setResizable(true)
                .setFlexGrow(1).setHeader("Original/Fotoc.");

        grid.addSelectionListener(e ->{
           if(e.getFirstSelectedItem().isPresent()){
               e.getFirstSelectedItem().ifPresent(this::showDataDocumennt);
               binderDataDocument.readBean(e.getFirstSelectedItem().get());
           }
        });

        FormLayout formLayout = new FormLayout();

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        NumberField firstYear = new NumberField();
        firstYear.setWidth("100%");
        firstYear.setRequiredIndicatorVisible(true);
        firstYear.setPlaceholder("AAAA");

        RadioButtonGroup<String> originalPhotocopyFirstYear = new RadioButtonGroup<>();
        originalPhotocopyFirstYear.setRequiredIndicatorVisible(true);
        originalPhotocopyFirstYear.setRequired(true);
        originalPhotocopyFirstYear.setItems("Original","Fotocopia");
//        originalPhotocopyFirstYear.getElement().getStyle().set("display", "flex");
//        originalPhotocopyFirstYear.getElement().getStyle().set("flexDirection", "row");

        NumberField secondYear = new NumberField();
        secondYear.setWidth("100%");
        secondYear.setRequiredIndicatorVisible(true);
        secondYear.setPlaceholder("AAAA");

        RadioButtonGroup<String> originalPhotocopySecondYear = new RadioButtonGroup<>();
        originalPhotocopySecondYear.setRequired(true);
        originalPhotocopySecondYear.setRequiredIndicatorVisible(true);
        originalPhotocopySecondYear.setItems("Original","Fotocopia");

        NumberField thirdYear = new NumberField();
        thirdYear.setWidth("100%");
        thirdYear.setRequiredIndicatorVisible(true);
        thirdYear.setPlaceholder("AAAA");

        RadioButtonGroup<String> originalPhotocopyThirdYear = new RadioButtonGroup<>();
        originalPhotocopyThirdYear.setRequiredIndicatorVisible(true);
        originalPhotocopyThirdYear.setRequired(true);
        originalPhotocopyThirdYear.setItems("Original","Fotocopia");

        VerticalLayout layoutFirst = new VerticalLayout(firstYear,originalPhotocopyFirstYear);
//        layoutFirst.setSpacing(Right.S);
        VerticalLayout layoutSecond = new VerticalLayout(secondYear,originalPhotocopySecondYear);
//        layoutSecond.setSpacing(Right.S);
        VerticalLayout layoutThird = new VerticalLayout(thirdYear,originalPhotocopyThirdYear);
//        layoutThird.setSpacing(Right.S);

        formLayout.addFormItem(layoutFirst," Doc. 1ra Gestion");
        formLayout.addFormItem(layoutSecond," Doc. 2da Gestion");
        formLayout.addFormItem(layoutThird," Doc. 3ra Gestion");

        binderDocumentSubmitted = new BeanValidationBinder<>(DocumentSubmitted.class);

        binderDocumentSubmitted.forField(firstYear).withConverter(new UtilValues.DoubleToIntegerConverter())
                .asRequired("Gestión es requerida").bind(DocumentSubmitted::getFirstYear,DocumentSubmitted::setFirstYear);
        binderDocumentSubmitted.forField(originalPhotocopyFirstYear).asRequired("Seleccionar si es Original o Copia")
                .bind(DocumentSubmitted::getOriginalPhotocopyFirstYear,DocumentSubmitted::setOriginalPhotocopyFirstYear);
        binderDocumentSubmitted.forField(secondYear).withConverter(new UtilValues.DoubleToIntegerConverter())
                .asRequired("Gestión es requerida").bind(DocumentSubmitted::getSecondYear,DocumentSubmitted::setSecondYear);
        binderDocumentSubmitted.forField(originalPhotocopySecondYear).asRequired("Seleccionar si es Original o Copia")
                .bind(DocumentSubmitted::getOriginalPhotocopySecondYear,DocumentSubmitted::setOriginalPhotocopySecondYear);
        binderDocumentSubmitted.forField(thirdYear).withConverter(new UtilValues.DoubleToIntegerConverter())
                .asRequired("Gestión es requerida").bind(DocumentSubmitted::getThirdYear,DocumentSubmitted::setThirdYear);
        binderDocumentSubmitted.forField(originalPhotocopyThirdYear).asRequired("Seleccionar si es Original o Copia")
                .bind(DocumentSubmitted::getOriginalPhotocopyThirdYear,DocumentSubmitted::setOriginalPhotocopyThirdYear);

        Div label = new Div();
        label.getElement().setProperty("innerHTML","<strong>Formulario de impuestos de las 3 ultimas gestiones </strong>");
        label.setWidthFull();
        layout.add(btnNew,grid,label, formLayout);



        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("95%");
        detailsDrawerForm.setWidthFull();

        detailsDrawerForm.setContent(layout);

        binderDocumentSubmitted.readBean(documentSubmitted);

        return detailsDrawerForm;
    }

    private Component createOriginalPhotocopy(DataDocument dataDocument){
        Icon icon;
        if(dataDocument.getOriginalPhotocopy().equals("Original")){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private DetailsDrawer createDataDocument(DataDocument dataDocument){

        TextField document = new TextField();
        document.setWidth("100%");
        document.setRequiredIndicatorVisible(true);
        document.setRequired(true);

        DatePicker dateDocument = new DatePicker();
        dateDocument.setWidth("100%");
        dateDocument.setRequired(true);
        dateDocument.setRequiredIndicatorVisible(true);

        TextField fs = new TextField();
        fs.setWidth("100%");
        fs.setRequired(true);
        fs.setRequiredIndicatorVisible(true);

        RadioButtonGroup<String> originalPhotocopy = new RadioButtonGroup<>();
        originalPhotocopy.setItems("Original","Fotocopia");
        originalPhotocopy.setRequired(true);
        originalPhotocopy.setRequiredIndicatorVisible(true);

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderDataDocument = new BeanValidationBinder<>(DataDocument.class);
        binderDataDocument.forField(document).asRequired("Documento es requerido")
                .bind(DataDocument::getDocument,DataDocument::setDocument);
        binderDataDocument.forField(dateDocument).asRequired("Fecha documento es requerida")
                .bind(DataDocument::getDateDocument,DataDocument::setDateDocument);
        binderDataDocument.forField(fs).asRequired("FS es requerido")
                .bind(DataDocument::getFs,DataDocument::setFs);
        binderDataDocument.forField(originalPhotocopy).asRequired("Indicar si es Original o Copia es requerido")
                .bind(DataDocument::getOriginalPhotocopy,DataDocument::setOriginalPhotocopy);

        binderDataDocument.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderDataDocument.hasChanges();
           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Informe Legal"));


        });

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        FormLayout.FormItem documentItem = formLayout.addFormItem(document,"Documento");
        UIUtils.setColSpan(2,documentItem);
        formLayout.addFormItem(dateDocument,"Fecha documento");
        formLayout.addFormItem(fs,"FS");
        formLayout.addFormItem(originalPhotocopy,"");

        footer.addSaveListener(event -> {
           if(binderDataDocument.writeBeanIfValid(dataDocument)){
               if(dataDocument.getId()==null) dataDocument.setId(UUID.randomUUID());
               dataDocumentList.removeIf(value -> value.getId().equals(dataDocument.getId()));
               dataDocumentList.add(dataDocument);
               ObjectMapper mapper = new ObjectMapper();
               try {
                   String jsonDataDocument = mapper.writeValueAsString(dataDocumentList);
//                   documentSubmitted.setDataDocument(jsonDataDocument);

                   String jsonDocumentSubmitted = mapper.writeValueAsString(documentSubmittedList);
                   legalInformation.setDocumentsSubmitted(jsonDocumentSubmitted);
                   legalInformation.setDataDocument(jsonDataDocument);
                   dataDocumentListDataProvider.refreshAll();
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

    private DetailsDrawer createGenericItems(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Accordion accordion = new Accordion();
        accordion.setSizeFull();

        accordion.add("OBSERVACIONES",layoutDetails("Observacion"));
        accordion.add("DOCUMENTACION FALTANTE",layoutDetails("Documentacion Faltante"));
        accordion.add("REQUISITOS PARA LA ELABORACION DEL CONTRATO",layoutDetails("Requisito"));
        accordion.add("CONCLUSIONES",layoutDetails("Conclusion"));
        accordion.add("ACLARACIONES",layoutDetails("Aclaracion"));

        accordion.addOpenedChangeListener(e -> {
            if(e.getOpenedIndex().isPresent()){
                if(e.getOpenedIndex().getAsInt()==0){
                    filterGenericItems("Observacion");
                }else if(e.getOpenedIndex().getAsInt()==1){
                    filterGenericItems("Documentacion Faltante");
                }else if(e.getOpenedIndex().getAsInt()==2){
                    filterGenericItems("Requisito");
                }else if(e.getOpenedIndex().getAsInt()==3){
                    filterGenericItems("Conclusion");
                }else if(e.getOpenedIndex().getAsInt()==4){
                    filterGenericItems("Aclaracion");
                }
            }
        });

        layout.add(accordion);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("95%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;
    }

    private void filterGenericItems(String filter){
        if(genericItemListDataProvider.getItems().size()>0){
            genericItemListDataProvider.setFilterByValue(GenericItem::getTypeItem,filter);
        }
    }

    private VerticalLayout layoutDetails(String labelHeader){
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeight("400px");
        layout.setSpacing(true);

        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);
        btnNew.setEnabled(GrantOptions.grantedOption("Informe Legal"));
        btnNew.addClickListener(event -> {
            GenericItem genericItem = new GenericItem();
            genericItem.setTypeItem(labelHeader);
            showDetails(genericItem);
        });

        Grid<GenericItem> grid = new Grid<>();
        grid.setDataProvider(genericItemListDataProvider);
        grid.setSizeFull();

        grid.addColumn(new ComponentRenderer<>(this::createTextArea)).setFlexGrow(1).setHeader(labelHeader);

        grid.addSelectionListener(e ->{
           if(e.getFirstSelectedItem().isPresent()){
               e.getFirstSelectedItem().ifPresent(this::showDetails);
               binderGenericItem.readBean(e.getFirstSelectedItem().get());
           }
        });

        layout.add(btnNew,grid);

        return layout;
    }

    private TextArea createTextArea(GenericItem item){
        TextArea textArea = new TextArea();
        textArea.setWidth("90%");
        textArea.setValue(item.getDescription());
        textArea.setEnabled(false);
        return textArea;
    }

    private void showDetails(GenericItem genericItem){
        detailsDrawerHeader.setTitle("Registro "+ genericItem.getTypeItem());
        detailsDrawer.setContent(createGenericItem(genericItem));
        detailsDrawer.show();
    }

    private DetailsDrawer createGenericItem(GenericItem genericItem){
        TextArea textArea = new TextArea(genericItem.getTypeItem());
        textArea.setSizeFull();
        textArea.setRequiredIndicatorVisible(true);
        textArea.setRequired(true);

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderGenericItem = new BeanValidationBinder<>(GenericItem.class);
        binderGenericItem.forField(textArea).asRequired("Dato es querido").bind(GenericItem::getDescription,GenericItem::setDescription);

        binderGenericItem.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binderGenericItem.hasChanges();
            footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Informe Legal"));
        });

        footer.addSaveListener(e ->{
            if(binderGenericItem.writeBeanIfValid(genericItem)) {
                if (genericItem.getId() == null) {
                    genericItem.setId(UUID.randomUUID());
                }
            }
            genericItemList.removeIf(value -> value.getId().equals(genericItem.getId()));
            genericItemList.add(genericItem);
            genericItemListDataProvider.refreshAll();
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonGenericItems = mapper.writeValueAsString(genericItemList);
                legalInformation.setDetails(jsonGenericItems);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            detailsDrawer.hide();
        });

        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawerForm.setContent(textArea);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }
}
