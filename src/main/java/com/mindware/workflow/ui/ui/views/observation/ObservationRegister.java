package com.mindware.workflow.ui.ui.views.observation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.observation.Observation;
import com.mindware.workflow.ui.backend.entity.templateObservation.TemplateObservation;
import com.mindware.workflow.ui.backend.rest.observation.ObservationRestTemplate;
import com.mindware.workflow.ui.backend.rest.templateObservation.TemplateObservationRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "register-observation", layout = MainLayout.class)
@PageTitle("Observaciones")
public class ObservationRegister extends SplitViewFrame implements HasUrlParameter<String> {
    private ListDataProvider<TemplateObservation> templateObservationListDataProvider;
    private List<TemplateObservation> templateObservationList = new ArrayList<>();
    private Map<String,List<String>> param = new HashMap<>();
    private Observation observation;
    private BeanValidationBinder<TemplateObservation> binderTemplateObservation;
    private BeanValidationBinder<Observation> binderObservation;
    private ObservationRestTemplate observationRestTemplate;
    private DetailsDrawer detailsDrawerGlobal;
    private DetailsDrawerHeader detailsDrawerHeaderGlobal;
    private FlexBoxLayout contentObservationDetail;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        TemplateObservationRestTemplate templateObservationRestTemplate = new TemplateObservationRestTemplate();
        observationRestTemplate = new ObservationRestTemplate();
        Location location = beforeEvent.getLocation();
        QueryParameters qp =  location.getQueryParameters();
        param = qp.getParameters();

        Integer numberRequest = Integer.parseInt(param.get("number-request").get(0));
        observation = new Observation();
        observation = observationRestTemplate.getByNumberRequestApplicantTask(numberRequest,param.get("task").get(0));
        if(observation.getObservations()==null || observation.getObservations().equals("[]") || observation.getObservations().equals("")){
            templateObservationList =  templateObservationRestTemplate.getByTask(param.get("task").get(0));
        }else{
            ObjectMapper mapper = new ObjectMapper();
            String observationsString = observation.getObservations();
            try {
                templateObservationList = mapper.readValue(observationsString, new TypeReference<List<TemplateObservation>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);

        setViewContent(createObservationByTask());
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
    }


    private DetailsDrawer createDetailDrawer(){
        detailsDrawerGlobal = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeaderGlobal = new DetailsDrawerHeader("");
        detailsDrawerHeaderGlobal.addCloseListener(buttonClickEvent -> detailsDrawerGlobal.hide());
        detailsDrawerGlobal.setHeader(detailsDrawerHeaderGlobal);

        return detailsDrawerGlobal;
    }

    private DetailsDrawer createObservationByTask(){
        HorizontalLayout topBar = new HorizontalLayout();
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(e ->{
            if (observation.getId()!=null) {
                Map<String, List<String>> paramObs = new HashMap<>();
                List<String> origin = new ArrayList<>();
                origin.add("Observation Analisis Crediticio");
                List<String> path = new ArrayList<>();
                path.add("register-observation");

                List<String> task = new ArrayList<>();
                List<String> titleReport = new ArrayList<>();
                titleReport.add("Observaciones ");
                paramObs.put("number-applicant", param.get("number-applicant"));
                paramObs.put("number-request", param.get("number-request"));
                paramObs.put("task", param.get("task"));
                paramObs.put("title", titleReport);
                paramObs.put("origin", origin);
                paramObs.put("path", path);

                QueryParameters qp = new QueryParameters(paramObs);
                UI.getCurrent().navigate("report-preview", qp);
            }else{
                UIUtils.showNotification("Registre Observaciones");
            }
        });
        topBar.add(btnPrint);
        Accordion accordion = new Accordion();
        accordion.setSizeFull();

        templateObservationListDataProvider  = new ListDataProvider<>(templateObservationList);

        Grid<TemplateObservation> gridConditionGeneral = createGridObservation("INFORME ANALISTA","I. CONDICIONES GENERALES (Cumplimiento normativa y coherencia análisis)");
        gridConditionGeneral.setHeight("300px");
        Grid<TemplateObservation> gridPaymentCapacity = createGridObservation("INFORME ANALISTA","II. CAPACIDAD DE PAGO (Cumplimiento normativa y coherencia análisis)");
        gridPaymentCapacity.setHeight("300px");
        Grid<TemplateObservation> gridPatrimony = createGridObservation("INFORME ANALISTA","III. PATRIMONIO (Cumplimiento normativa y coherencia análisis)");
        gridPatrimony.setHeight("300px");

        gridConditionGeneral.setDataProvider(templateObservationListDataProvider);
        gridPaymentCapacity.setDataProvider(templateObservationListDataProvider);
        gridPatrimony.setDataProvider(templateObservationListDataProvider);

        gridConditionGeneral.addSelectionListener(e ->{
           if(e.getFirstSelectedItem().isPresent()==true) {
               e.getFirstSelectedItem().ifPresent(this::showTemplateObservation);
               binderTemplateObservation.readBean(e.getFirstSelectedItem().get());
           }
        });

        gridPaymentCapacity.addSelectionListener(e ->{
            if(e.getFirstSelectedItem().isPresent()==true) {
                e.getFirstSelectedItem().ifPresent(this::showTemplateObservation);
                binderTemplateObservation.readBean(e.getFirstSelectedItem().get());
            }
        });

        gridPatrimony.addSelectionListener(e ->{
            if(e.getFirstSelectedItem().isPresent()==true) {
                e.getFirstSelectedItem().ifPresent(this::showTemplateObservation);
                binderTemplateObservation.readBean(e.getFirstSelectedItem().get());
            }
        });

        accordion.addOpenedChangeListener(e -> {
           if(e.getOpenedIndex().isPresent()==true) {
               if (e.getOpenedIndex().getAsInt() == 0) {
                   filter("I. CONDICIONES GENERALES (Cumplimiento normativa y coherencia análisis)");
               } else if (e.getOpenedIndex().getAsInt() == 1) {
                   filter("II. CAPACIDAD DE PAGO (Cumplimiento normativa y coherencia análisis)");
               } else if (e.getOpenedIndex().getAsInt() == 2) {
                   filter("III. PATRIMONIO (Cumplimiento normativa y coherencia análisis)");
               }
           }
        });
        accordion.add("Condiciones Generales",gridConditionGeneral);
        accordion.add("Capacidad de Pago",gridPaymentCapacity);
        accordion.add("Patrimonio",gridPatrimony);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        layout.add(topBar,accordion);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;
    }

    private void filter(String filter){
        if(templateObservationListDataProvider.getItems().size()>0){
            templateObservationListDataProvider.setFilterByValue(TemplateObservation::getCategory,filter);
        }
    }

    private Grid createGridObservation(String task, String category){

        List<TemplateObservation> list = getFilterAndSort(templateObservationList,task,category);
        Grid<TemplateObservation> grid = new Grid<>();
        grid.addColumn(TemplateObservation::getCondition).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Condicion").setSortable(true);
        grid.addColumn(TemplateObservation::getObservation).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Observacion");
        grid.addColumn(TemplateObservation::getAnswer).setHeader("Respuesta")
                .setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);

        return grid;

    }

    private List<TemplateObservation> getFilterAndSort(List<TemplateObservation> list, String task, String category){
        List<TemplateObservation> filter = list.stream()
                .filter(f -> f.getTask().equals(task) && f.getCategory().contains(category))
                .sorted(Comparator.comparing(TemplateObservation::getCondition))
                .collect(Collectors.toList());
        return filter;
    }

    private DetailsDrawer createObservationDetail(TemplateObservation templateObservation){
        TextField condition = new TextField();
        condition.setWidth("100%");

        TextArea observationText = new TextArea();
        observationText.setWidth("100%");
        observationText.setRequired(true);
        observationText.setRequiredIndicatorVisible(true);

        ComboBox<String> state = new ComboBox<>();
        state.setWidth("100%");
        state.setItems("ACTIVA","SUBSANADA","PENDIENTE");
        state.setAllowCustomValue(false);
        state.setRequired(true);
        state.setRequiredIndicatorVisible(true);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderTemplateObservation = new BeanValidationBinder<>(TemplateObservation.class);
        binderTemplateObservation.forField(condition).bind(TemplateObservation::getCondition, TemplateObservation::setCondition);
        binderTemplateObservation.forField(observationText).asRequired("Observacion es requerida")
                .bind(TemplateObservation::getObservation,TemplateObservation::setObservation);
        binderTemplateObservation.forField(state).asRequired("Estado es requerido")
                .bind(TemplateObservation::getState,TemplateObservation::setState);
        FormLayout.FormItem conditionItem = formLayout.addFormItem(condition,"Condicion");
        UIUtils.setColSpan(2,conditionItem);
        formLayout.addFormItem(state,"Estado");
        FormLayout.FormItem observationItem = formLayout.addFormItem(observationText,"Observacion");
        UIUtils.setColSpan(3,observationItem);

        binderTemplateObservation.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderTemplateObservation.hasChanges();
           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Formulario Observaciones"));
        });

        footer.addSaveListener(e -> {
           if(binderTemplateObservation.writeBeanIfValid(templateObservation)) {
               if (templateObservation.getRegisterDate()==null){
                   templateObservation.setRegisterDate(LocalDate.now());
                   templateObservation.setAnswer("");
               }
               templateObservationList.removeIf(value -> value.getId().equals(templateObservation.getId()));
               templateObservationList.add(templateObservation);
               templateObservationListDataProvider.refreshAll();

               ObjectMapper mapper = new ObjectMapper();
               try {
                   String jsonTemmplateObservation = mapper.writeValueAsString(templateObservationList);
                   observation.setObservations(jsonTemmplateObservation);
               } catch (JsonProcessingException ex) {
                   ex.printStackTrace();
               }

               observation.setNumberRequest(Integer.parseInt(param.get("number-request").get(0)));
               observation.setTask(param.get("task").get(0));
               observation= observationRestTemplate.add(observation);

               detailsDrawerGlobal.hide();
           }
        });

        footer.addCancelListener(e -> detailsDrawerGlobal.hide());
        detailsDrawerForm.setContent(formLayout);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }

    private void showTemplateObservation(TemplateObservation templateObservation){
        detailsDrawerHeaderGlobal.setTitle(templateObservation.getCondition());
        detailsDrawerGlobal.setContent(createObservationDetail(templateObservation));
//        binderTemplateObservation.readBean(templateObservation);
        detailsDrawerGlobal.show();
    }

    private Component createContent(DetailsDrawer component) {
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setMargin(Vertical.AUTO,Vertical.RESPONSIVE_L);
        content.setSizeFull();
        return content;
    }
}
