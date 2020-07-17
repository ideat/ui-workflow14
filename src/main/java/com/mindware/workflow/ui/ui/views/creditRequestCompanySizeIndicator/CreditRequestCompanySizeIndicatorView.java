package com.mindware.workflow.ui.ui.views.creditRequestCompanySizeIndicator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.creditRequest.CompanySizeIndicator;
import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.entity.dto.CreditRequestCompanySizeIndicatorDto;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.rest.creditRequest.CreditRequestRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditRequestCompanySizeIndicatorDto.CreditRequestCompanySizeIndicatorDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.LumoStyles;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "creditrequest-company-size-indicator",layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Indicador de empresa")
public class CreditRequestCompanySizeIndicatorView extends SplitViewFrame implements RouterLayout {
    private Grid<CreditRequestCompanySizeIndicatorDto> grid;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private CreditRequestCompanySizeIndicatorDtoRestTemplate restTemplate = new CreditRequestCompanySizeIndicatorDtoRestTemplate();
    private CreditRequestRestTemplate creditRequestRestTemplate;
    private Binder<CompanySizeIndicator> binder;
    private CompanySizeIndicator current;

    private List<CreditRequestCompanySizeIndicatorDto> creditRequestCompanySizeIndicatorDtoList;
    private CreditRequestCompanySizeIndicatorDataProvider dataProvider;
    private TextField filterText;

    private ObjectMapper mapper;
    private ComboBox<String> typeCompany;
    private NumberField annualSale;
    private NumberField numberEmployees;
    private NumberField patrimony;

    private Integer currentNumberRequest;
    private CreditRequestCompanySizeIndicatorDto currentCreditRequestCompanySizeIndicatorDto;
   @Override
   protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        mapper = new ObjectMapper();
        creditRequestRestTemplate = new CreditRequestRestTemplate();
        getCreditRequestCompanySizeIndicator();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());

   }

    public void getCreditRequestCompanySizeIndicator(){
        restTemplate = new CreditRequestCompanySizeIndicatorDtoRestTemplate();
        String rol = VaadinSession.getCurrent().getAttribute("rol").toString();
        String login = VaadinSession.getCurrent().getAttribute("login").toString();
        String city = VaadinSession.getCurrent().getAttribute("city").toString();
        if(rol.equals("OFICIAL")){
            creditRequestCompanySizeIndicatorDtoList = new ArrayList<>(restTemplate.getByUser(login)) ;
        }else{
            RolRestTemplate rolRestTemplate = new RolRestTemplate();
            Rol rol1 = rolRestTemplate.getRolByName(rol);
            if (rol1.getScope().equals("LOCAL")){
                creditRequestCompanySizeIndicatorDtoList = restTemplate.getByCity(city);
            }else{
                creditRequestCompanySizeIndicatorDtoList = restTemplate.getAll();
            }
        }

        dataProvider = new CreditRequestCompanySizeIndicatorDataProvider(creditRequestCompanySizeIndicatorDtoList);
    }

    private HorizontalLayout createTopBar(){
       filterText = new TextField();
       filterText.setPlaceholder("Filtro: #Solicitud, Nombre solicitante, Ciudad, Oficial");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;

    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createCreditRequestCompanySizeIndicator());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createCreditRequestCompanySizeIndicator(){
       grid = new Grid<>();
       grid.addSelectionListener(event ->{
            event.getFirstSelectedItem().ifPresent(this::showDetails);
       });

       grid.setDataProvider(dataProvider);
       grid.setHeightFull();
       grid.addColumn(CreditRequestCompanySizeIndicatorDto::getNumberRequest).setFlexGrow(1)
               .setHeader("# Solicitud").setSortable(true).setResizable(true).setAutoWidth(true);
       grid.addColumn(CreditRequestCompanySizeIndicatorDto::getFullName).setFlexGrow(1)
               .setHeader("Nombre solicitante").setSortable(true).setResizable(true).setAutoWidth(true);
       grid.addColumn(CreditRequestCompanySizeIndicatorDto::getNameOfficer).setFlexGrow(1)
               .setHeader("Ofcial creditos").setSortable(true).setResizable(true).setAutoWidth(true);
       grid.addColumn(CreditRequestCompanySizeIndicatorDto::getCity).setFlexGrow(1)
               .setHeader("Ciudad").setSortable(true).setResizable(true).setAutoWidth(true);
       grid.addColumn(CreditRequestCompanySizeIndicatorDto::getNameOffice).setFlexGrow(1)
               .setHeader("Oficina").setSortable(true).setResizable(true).setAutoWidth(true);
       grid.addColumn(CreditRequestCompanySizeIndicatorDto::getIndicator).setFlexGrow(1)
               .setHeader("Indicador").setSortable(true).setResizable(true).setAutoWidth(true);
       grid.addColumn(CreditRequestCompanySizeIndicatorDto::getIndicatorClassification).setFlexGrow(1)
               .setHeader("Clasificacion").setSortable(true).setResizable(true).setAutoWidth(true);
       grid.addColumn(new ComponentRenderer<>(this::createButtonPrint)).setFlexGrow(1).setResizable(true).setAutoWidth(true);
       return grid;

    }

    private Component createButtonPrint(CreditRequestCompanySizeIndicatorDto creditRequestCompanySizeIndicatorDto){
        Button btn = new Button();
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btn.setIcon(VaadinIcon.PRINT.create());
        if(creditRequestCompanySizeIndicatorDto.getCompanySizeIndicator()==null || creditRequestCompanySizeIndicatorDto.getCompanySizeIndicator().equals("[]")){
            btn.setVisible(false);
        }else{
            btn.setVisible(true);
        }
        btn.addClickListener(e ->{
            Map<String,List<String>> param = new HashMap<>();
            List<String> origin = new ArrayList<>();
            origin.add("creditrequest-company-size-indicator");
            List<String> path = new ArrayList<>();
            path.add("creditrequest-company-size-indicator");
            List<String> numberRequestList = new ArrayList<>();
            numberRequestList.add(creditRequestCompanySizeIndicatorDto.getNumberRequest().toString());

            List<String> titleReport = new ArrayList<>();
            titleReport.add("Autorizacion Excepciones ");
            param.put("path",path);
            param.put("title",titleReport);
            param.put("origin",origin);
            param.put("number-request",numberRequestList);
            QueryParameters qp = new QueryParameters(param);
            UI.getCurrent().navigate("report-preview",qp);
        });

        return btn;
    }

    @SneakyThrows
    private void showDetails(CreditRequestCompanySizeIndicatorDto creditRequestCompanySizeIndicatorDto){
       if( creditRequestCompanySizeIndicatorDto.getCompanySizeIndicator()==null || creditRequestCompanySizeIndicatorDto.getCompanySizeIndicator().equals("[]")){
           current = new CompanySizeIndicator();
       }else {
           current = mapper.readValue(creditRequestCompanySizeIndicatorDto.getCompanySizeIndicator(),CompanySizeIndicator.class);
       }
       currentCreditRequestCompanySizeIndicatorDto = creditRequestCompanySizeIndicatorDto;
       currentNumberRequest = creditRequestCompanySizeIndicatorDto.getNumberRequest();
       detailsDrawerHeader.setTitle("Nro solicitud: "+ creditRequestCompanySizeIndicatorDto.getNumberRequest());
       detailsDrawer.setContent(createDetails(creditRequestCompanySizeIndicatorDto));
       detailsDrawer.show();
       binder.readBean(current);
    }

    private DetailsDrawer createDetailsDrawer(){
       detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
       detailsDrawerHeader = new DetailsDrawerHeader("");
       detailsDrawerHeader.addCloseListener(event -> detailsDrawer.hide());
       detailsDrawer.setHeader(detailsDrawerHeader);

       footer = new DetailsDrawerFooter();
       footer.addSaveListener(e ->{
           if(current !=null && binder.writeBeanIfValid(current)){
               CreditRequest creditRequest = creditRequestRestTemplate.getByNumberRequest(currentNumberRequest);
               try {
                   Double indicator = calculateIndicator();
                   current.setIndicator(indicator);
                   current.setIndicatorClassification(indicatorClassification(indicator));
                   String jsonCompanySizeIndicator = mapper.writeValueAsString(current);
                   creditRequest.setCompanySizeIndicator(jsonCompanySizeIndicator);
                   creditRequestRestTemplate.updateCompanySizeIndicator(creditRequest);
                   currentCreditRequestCompanySizeIndicatorDto.setCompanySizeIndicator(jsonCompanySizeIndicator);
                   currentCreditRequestCompanySizeIndicatorDto.setIndicator(current.getIndicator());
                   currentCreditRequestCompanySizeIndicatorDto.setIndicatorClassification(current.getIndicatorClassification());
                   detailsDrawer.hide();
                   grid.getDataProvider().refreshItem(currentCreditRequestCompanySizeIndicatorDto);
                   UIUtils.showNotification("Datos para calculo de indicador de empresa registrado");
               } catch (JsonProcessingException jsonProcessingException) {
                   jsonProcessingException.printStackTrace();
               }
           }else{
               UIUtils.showNotification("Datos incompletos o incorrectos, verifique");
           }
       });

       footer.addCancelListener(e -> {
           footer.saveState(false);
           detailsDrawer.hide();
       });
       detailsDrawer.setFooter(footer);
        return  detailsDrawer;
    }

    private FormLayout createDetails(CreditRequestCompanySizeIndicatorDto creditRequestCompanySizeIndicatorDto){
        typeCompany = new ComboBox<>();
        typeCompany.setRequired(true);

        typeCompany.setItems("PRODUCCION","COMERCIO","SERVICIO");
        typeCompany.setWidth("100%");

        annualSale = new NumberField();
        annualSale.setRequiredIndicatorVisible(true);
        annualSale.setWidth("100%");

        numberEmployees = new NumberField();
        numberEmployees.setRequiredIndicatorVisible(true);
        numberEmployees.setWidth("100%");

        patrimony = new NumberField();
        patrimony.setRequiredIndicatorVisible(true);
        patrimony.setWidth("100%");

        binder = new BeanValidationBinder<>(CompanySizeIndicator.class);
        binder.forField(typeCompany).asRequired("Tipo empresa es requerido")
                .bind(CompanySizeIndicator::getTypeCompany,CompanySizeIndicator::setTypeCompany);
        binder.forField(annualSale).asRequired("Ventas anuales es requerido")
                .bind(CompanySizeIndicator::getAnnualSale,CompanySizeIndicator::setAnnualSale);
        binder.forField(numberEmployees).asRequired("Numero empleados es requerido")
                .bind(CompanySizeIndicator::getNumberEmployees,CompanySizeIndicator::setNumberEmployees);
        binder.forField(patrimony).asRequired("Patrimonio es requerido")
                .bind(CompanySizeIndicator::getPatrimony,CompanySizeIndicator::setPatrimony);
        binder.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Indicador Tamaño Empresa"));
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));

        form.addFormItem(typeCompany,"Tipo actividad");
        form.addFormItem(annualSale,"Ventas anuales");
        form.addFormItem(numberEmployees,"Nro. empleados");
        form.addFormItem(patrimony,"Patrimonio");

       return form;
    }

    private Double calculateIndicator(){

        Double indicator = 0.0;

       if(typeCompany.getValue().equals("PRODUCCION")){
           indicator = Math.pow((annualSale.getValue()/35000000.0)*(patrimony.getValue()/21000000.0)*(numberEmployees.getValue()/100.0),(1.0/3.0));
       }else if(typeCompany.getValue().equals("COMERCIO")){
           indicator = Math.pow((annualSale.getValue()/35000000.0)*(patrimony.getValue()/21000000.0)*(numberEmployees.getValue()/100.0),(1.0/3.0));
       }else if(typeCompany.getValue().equals("SERVICIO")){
           indicator = Math.pow((Math.round((annualSale.getValue()/28000000.0)*10000.0)/10000.0)
                   *(Math.round((patrimony.getValue()/14000000.0)*10000.0)/10000.0)
                   *(Math.round((numberEmployees.getValue()/50.0)*10000.0)/10000.0),(1.0/3.0));
       }
       return Math.round(indicator*1000.0)/1000.0;
    }

    private String indicatorClassification(Double indicator){
       if(indicator>=0 && indicator < 0.035){
           return "MICROEMPRESA";
       } else if (indicator >=0.035 && indicator < 0.115){
           return "PEQUEÑA EMPRESA";
       } else if (indicator >= 0.015 && indicator < 1){
           return "MEDIANA EMPRESA";
       } else if (indicator > 1){
           return "GRAN EMPRESA";
       }else return "DESCONOCIDO";
    }
}
