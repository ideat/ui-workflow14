package com.mindware.workflow.ui.ui.views.stageHistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.CreditRequestApplicant;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.config.RequestStage;
import com.mindware.workflow.ui.backend.entity.config.States;
import com.mindware.workflow.ui.backend.entity.config.WorkflowProduct;
import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.entity.dto.CreditRequestApplicantDto;
import com.mindware.workflow.ui.backend.entity.patrimonialStatement.PatrimonialStatement;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.rest.creditRequest.CreditRequestRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditRequestApplicantDto.CreditRequestApplicantDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.email.MailRestTemplate;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
import com.mindware.workflow.ui.backend.rest.stageHistory.StageHistoryRestTemplate;
import com.mindware.workflow.ui.backend.rest.workflowProducdt.WorkflowProductRestTemplate;
import com.mindware.workflow.ui.backend.util.PrepareMail;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "stage-history-register", layout = MainLayout.class)
@PageTitle("Registro de etapa de la solicitud")
public class StageHistoryRegister extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {

    private Map<String, List<String>> paramStage;
    private StageHistoryRestTemplate restTemplate;
    private MailRestTemplate mailRestTemplate;
    private BeanValidationBinder<StageHistory> binder;
    private FlexBoxLayout contentStageHistory;
    private List<States> statesList= new ArrayList<>();
    private Rol rol;
    private RolRestTemplate rolRestTemplate = new RolRestTemplate();
    private WorkflowProductRestTemplate workflowProductRestTemplate;
    private WorkflowProduct workflowProduct;

    private String comesFromStage = "";
    private String initState="";


    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        restTemplate = new StageHistoryRestTemplate();
        mailRestTemplate = new MailRestTemplate();
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        paramStage = queryParameters.getParameters();
        StageHistory stageHistory = restTemplate.getById(UUID.fromString(paramStage.get("id-stage-history").get(0)));
        initState = stageHistory.getState();
       comesFromStage = stageHistory.getComesFrom();
//        rol = rolRestTemplate.getRolByName(VaadinSession.getCurrent().getAttribute("rol").toString());
        workflowProductRestTemplate = new WorkflowProductRestTemplate();
//        workflowProduct = workflowProductRestTemplate.getByCode(paramStage.get("code-product").get(0));
        workflowProduct = workflowProductRestTemplate.getByTypeCreditAndObject(paramStage.get("code-type-credit").get(0)
                                                        , paramStage.get("code-object-credit").get(0));
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<RequestStage> requestStageList = mapper.readValue(workflowProduct.getRequestStage(), new TypeReference<List<RequestStage>>() {});
            RequestStage requestStage = requestStageList.stream().filter(rs -> rs.getStage().equals(stageHistory.getStage()))
                    .findFirst().get();
            statesList = mapper.readValue(requestStage.getStates(), new TypeReference<List<States>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        contentStageHistory = (FlexBoxLayout) createContent(createStageHistory(stageHistory));
        setViewContent(contentStageHistory);
        setViewDetailsPosition(Position.BOTTOM);
        binder.readBean(stageHistory);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(paramStage.get("stage").get(0));

    }

    private AppBar initAppBar() {
        MainLayout.get().getAppBar().reset();
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e -> UI.getCurrent().navigate(StageHistoryView.class));
        return appBar;
    }

    private Component createContent(DetailsDrawer component){
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setMargin(Vertical.AUTO, Vertical.RESPONSIVE_L);
//        content.setMaxWidth("1024px");

        return content;
    }

    private DetailsDrawer createStageHistory(StageHistory stageHistory){
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        NumberField numberRequest = new NumberField();
        numberRequest.setWidth("100%");
        numberRequest.setReadOnly(true);

        TextField stage = new TextField();
        stage.setWidth("100%");
        stage.setReadOnly(true);

        TextField startDateTime = new TextField();
        startDateTime.setWidth("100%");
        startDateTime.setReadOnly(true);


        CheckboxGroup<String> stageObservation = new CheckboxGroup<>();
        stageObservation.setItems(Arrays.asList(comesFromStage.split(",")));
        stageObservation.setVisible(false);
        stageObservation.addValueChangeListener(e -> footer.saveState(true));

        ComboBox<String> state = new ComboBox<>();
        stage.setWidthFull();
        state.setItems(getListStates());

        ComboBox<String> nextState = new ComboBox<>();
        nextState.setWidth("100%");
        nextState.setItems(getListNextStates());
        nextState.addValueChangeListener(e -> {
            if(isGoBack(e.getValue())){
                stageObservation.setVisible(false); //TODO: change to true
            }else{
                stageObservation.setVisible(false);
            }
        });

        TextField initDateTime = new TextField();
        initDateTime.setWidth("100%");
        initDateTime.setReadOnly(true);

        TextField userTask = new TextField();
        userTask.setWidth("100%");
        userTask.setReadOnly(true);

        TextField comesFrom = new TextField();
        comesFrom.setWidth("100%");
        comesFrom.setReadOnly(true);

        TextArea observation = new TextArea();
        observation.setWidth("100%");

        TextArea answer = new TextArea();
        answer.setWidth("100%");


        binder = new BeanValidationBinder<>(StageHistory.class);

        binder.forField(numberRequest).withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(StageHistory::getNumberRequest,StageHistory::setNumberRequest);
        binder.forField(stage).bind(StageHistory::getStage,StageHistory::setStage);
        binder.forField(startDateTime).withConverter(new UtilValues.InstantToStringConverter())
                .bind(StageHistory::getStartDateTime,StageHistory::setStartDateTime);
        binder.forField(initDateTime).withConverter(new UtilValues.InstantToStringConverter())
                .bind(StageHistory::getInitDateTime,StageHistory::setInitDateTime);
        binder.forField(state).withValidator(s -> !s.equals(initState),"Seleccione el nuevo estado")
                .asRequired("Nuevo estado es requerido")
                .bind(StageHistory::getState,StageHistory::setState);
        binder.forField(userTask).bind(StageHistory::getUserTask,StageHistory::setUserTask);
        binder.forField(comesFrom).bind(StageHistory::getComesFrom,StageHistory::setComesFrom);
        if(isGoBack(nextState.getValue())==true) {
            binder.forField(observation).asRequired("Tiene que registrar observaciones")
                    .bind(StageHistory::getObservation, StageHistory::setObservation);
        }else{
            binder.forField(observation).bind(StageHistory::getObservation, StageHistory::setObservation);
        }
        binder.forField(answer).bind(StageHistory::getAnswer,StageHistory::setAnswer);
        binder.forField(nextState).asRequired("Dato requerido").bind(StageHistory::getNextState,StageHistory::setNextState);
        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(isValid && hasChanges);
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px",4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.addFormItem(numberRequest,"Nro. solicitud");
        formLayout.addFormItem(stage,"Etapa actual");
        formLayout.addFormItem(startDateTime,"Fecha registro etapa");
        formLayout.addFormItem(initDateTime,"Fecha inicio etapa");
        formLayout.addFormItem(userTask,"Usuario");
        formLayout.addFormItem(comesFrom,"Etapa anterior");
        formLayout.addFormItem(state,"Nuevo estado");
        formLayout.addFormItem(nextState,"Estado de envio");
        formLayout.addFormItem(stageObservation,"Etapas observadas");

        FormLayout.FormItem observationItem = formLayout.addFormItem(observation,"Observaciones");
        UIUtils.setColSpan(4,observationItem);
        FormLayout.FormItem answerItem = formLayout.addFormItem(answer,"Respuesta a observaciones");
        UIUtils.setColSpan(4,answerItem);

        footer.addSaveListener(e ->{
//            if(stageHistory.getInitDateTime()==null && stageHistory.getUserTask()!=null) {
//                stageHistory.setInitDateTime(Instant.now());
//            }
           if(binder.writeBeanIfValid(stageHistory)){
               States states = statesList.stream().filter(s -> s.getState().equals(nextState.getValue())).findFirst().get();
               if(states.isGoForward()){
                   if(initState.equals("OBSERVADO")){
                       createGoForwardStageResponseObservation(stageHistory,comesFromStage);
                   }else {
                       createGoForwardStage(stageHistory, nextState);
                   }
               }else if(states.isGoBackward()){

                    stageHistory.setObservation(observation.getValue());
                    stageHistory.setAnswer(answer.getValue());
                    createGoBackFirstStage(stageHistory,"EVALUACION_CREDITO");
//                    if(stageObservation.getSelectedItems().size()>0 && !observation.isEmpty()) {
//                        createGoBackwardStage(stageHistory, stageObservation.getSelectedItems());
//                    }else{
//                        UIUtils.showNotification("Si realiza una observacion, indique que ETAPA observa e ingrese observaciones");
//                    }
               }else if(states.isFinished()){
                   stageHistory.setFinishedDateTime((Instant.now()));
                   restTemplate.update(stageHistory);
                   UIUtils.showNotification("Workflow concluido");
               }
            }
        });
        footer.addCancelListener(e -> UI.getCurrent().navigate(StageHistoryView.class));
        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidth("100%");
        detailsDrawer.setFooter(footer);
        detailsDrawer.setContent(formLayout);

        return detailsDrawer;
    }

    private void createGoForwardStageResponseObservation(StageHistory stageHistory, String nextStage){
        stageHistory.setFinishedDateTime(Instant.now());
        List<StageHistory> stageHistoryList = restTemplate.getByNumberRequest(stageHistory.getNumberRequest());
        StageHistory aux = stageHistoryList.stream()
                .filter(f -> f.getStage().equals(nextStage))
                .collect(Collectors.toList()).get(0);
        restTemplate.update(stageHistory);
        StageHistory s = createNewStageHistory(stageHistory.getComesFrom(), stageHistory.getNextState());
        s.setNumberRequest(stageHistory.getNumberRequest());
        s.setComesFrom(stageHistory.getStage());
        s.setStage(nextStage);
        s.setUserTask(aux.getUserTask());
        s.setInitDateTime(Instant.now());
        s.setAnswer(stageHistory.getAnswer());
        s.setObservation(stageHistory.getObservation());
        restTemplate.add(s);

        PrepareMail.sendMailWorkflowUser(stageHistory.getUserTask(),stageHistory.getNumberRequest(),aux.getUserTask());
        UIUtils.showNotification("Correo enviado");
        UIUtils.showNotification("Estado concluido");
        UI.getCurrent().navigate(StageHistoryView.class);
    }

    private void createGoForwardStage(StageHistory stageHistory, ComboBox<String> nextState) {

        ObjectMapper mapper = new ObjectMapper();
        CreditRequestRestTemplate creditRequestRestTemplate = new CreditRequestRestTemplate();
        CreditRequest creditRequest = creditRequestRestTemplate.getByNumberRequest(stageHistory.getNumberRequest());

        try {
             List<RequestStage> requestStages = Arrays.asList(mapper.readValue(workflowProduct.getRequestStage(),RequestStage[].class));
             requestStages = requestStages.stream()
                     .filter(r -> r.isActive()==true)
                     .sorted(Comparator.comparing(RequestStage::getPosition))
                     .collect(Collectors.toList());
             stageHistory.setFinishedDateTime(Instant.now());
             restTemplate.update(stageHistory);
             List<StageHistory> stageHistoryList = restTemplate.getByNumberRequest(stageHistory.getNumberRequest());

             if (verifyAllStageFinished(stageHistoryList,stageHistory.getStage(),requestStages)) {
                 List<RequestStage> nextRequestStages = getNextStage(stageHistory, requestStages,0);
                 String comesFrom = getAllStagesForNextStage(stageHistoryList,stageHistory.getStage(),requestStages);
                 for (RequestStage r : nextRequestStages) {
                     if(r.getStage().equals("APROBACION2") || r.getStage().equals("APROBACION3")){
                        if(validStage(r.getStage(),creditRequest)){
                            StageHistory s = createNewStageHistory(r.getStage(), nextState.getValue());
                            s.setNumberRequest(stageHistory.getNumberRequest());
                            s.setComesFrom(comesFrom);
                            restTemplate.add(s);
                            PrepareMail.sendMailWorkflowGoForward(stageHistory.getUserTask(), stageHistory.getNumberRequest(),
                                    r.getRols(), "");
                        }else{ //next stage after APROBACION2 /APROBACION3
                            List<RequestStage> nextRequestStages1 = getNextStage(stageHistory, requestStages,1);
                            for(RequestStage r1:nextRequestStages1) {
                                StageHistory s = createNewStageHistory(r1.getStage(), nextState.getValue());
                                s.setNumberRequest(stageHistory.getNumberRequest());
                                s.setComesFrom(comesFrom);
                                restTemplate.add(s);
                                PrepareMail.sendMailWorkflowGoForward(stageHistory.getUserTask(), stageHistory.getNumberRequest(),
                                        r1.getRols(), "");
                            }
                        }

                     } else {
                         StageHistory s = createNewStageHistory(r.getStage(), nextState.getValue());
                         s.setNumberRequest(stageHistory.getNumberRequest());
                         s.setComesFrom(comesFrom);
                         restTemplate.add(s);
                         PrepareMail.sendMailWorkflowGoForward(stageHistory.getUserTask(), stageHistory.getNumberRequest(),
                                 r.getRols(), "");
                     }
                 }
                 UIUtils.showNotification(stageHistory.getStage() +" concluida, paso a la siguiente Etapa");
             }else{
                 UIUtils.showNotification(stageHistory.getStage() +" concluida, Etapas paralelas estan pendientes");
             }
             UI.getCurrent().navigate(StageHistoryView.class);


        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    private boolean validStage(String stage, CreditRequest creditRequest){
        boolean result =false;
        Double amount = creditRequest.getAmount();
        if(stage.equals("APROBACION2")){
            PatrimonialStatementRestTemplate patrimonialStatementRestTemplate = new PatrimonialStatementRestTemplate();
            CreditRequestApplicantDtoRestTemplate creditRequestApplicantDtoRestTemplate = new CreditRequestApplicantDtoRestTemplate();
            List<CreditRequestApplicantDto> creditRequestApplicantDtoList = creditRequestApplicantDtoRestTemplate.getByNumberRequest(creditRequest.getNumberRequest());
            CreditRequestApplicantDto creditRequestApplicantDto = creditRequestApplicantDtoList.stream()
                    .filter(c->c.getTypeRelation().equals("deudor") && c.getNumberRequest().equals(creditRequest.getNumberRequest().toString()))
                    .findFirst().get();

            List<PatrimonialStatement> patrimonialStatementList = patrimonialStatementRestTemplate
                    .getByIdCreditRequestApplicant(creditRequestApplicantDto.getId());
            patrimonialStatementList = patrimonialStatementList.stream()
                    .filter(p->p.getElementCategory().equals("DOCUMENTOS Y CUENTAS POR PAGAR") && p.getFieldBoolean1().equals("SI"))
                    .collect(Collectors.toList());

            ParameterRestTemplate parameterRestTemplate = new ParameterRestTemplate();
            List<Parameter> parameterList = new ArrayList<>();
            parameterList = parameterRestTemplate.getParametersByCategory("RANGO APROBACION");
            parameterList = parameterList.stream()
                    .filter(p->p.getValue().contains("APROBACION2"))
                    .collect(Collectors.toList());
            Parameter parameterHomeMortage = parameterList.stream()
                    .filter(f -> f.getValue().equals("APROBACION2-VIVIENDA")).findFirst().get();

            if(creditRequest.getCurrency().equals("$us.")) {
                amount = amount * UtilValues.getExchange();
            }

            Map<String,Double> totalByTypeCredit = getTotalDebtsByTypeCredit(patrimonialStatementList
                    , parameterHomeMortage.getExternalCode(),creditRequest.getCurrency());

            if(parameterHomeMortage.getExternalCode().contains(creditRequest.getTypeCredit())){
                if(totalByTypeCredit.get("H")!=null)
                    amount=amount+totalByTypeCredit.get("H").doubleValue();
            }else{
                if(totalByTypeCredit.get("O")!=null)
                    amount=amount+totalByTypeCredit.get("O").doubleValue();
            }

            String[] conditions1 = new String[2];
            String[] conditions2 = new String[2];
            String[] allConditions = new String[2];
            //Get conditions amount
            for(Parameter p:parameterList){
                if(p.getValue().contains(stage)){
                    String[] listTypeCredit = p.getExternalCode().split(",");
                    if(Arrays.stream(listTypeCredit).anyMatch(s -> s.trim().equals(creditRequest.getTypeCredit()))){
                        allConditions = p.getDescription().split("-");

                        conditions1 = allConditions[0].length()>0? allConditions[0].split(";"):new String[0];
                        if(allConditions.length>1) {
                            conditions2 = allConditions[1].length() > 0 ? allConditions[1].split(";") : new String[0];
                        }else{
                            conditions2[0]="";
                        }
                    }
                }
            }

            if(conditions1[0].equals(">")){
                if(conditions2[0].equals("<")){
                    if(amount > Double.parseDouble(conditions1[1]) && amount < Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else if(conditions2[0].equals("<=")){
                    if(amount > Double.parseDouble(conditions1[1]) && amount <= Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else {
                    if (amount > Double.parseDouble(conditions1[1])) result = true;
                }
            } else if(conditions1[0].equals(">=")){
                if(conditions2[0].equals("<")){
                    if(amount >= Double.parseDouble(conditions1[1]) && amount < Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else if(conditions2[0].equals("<=")){
                    if(amount >= Double.parseDouble(conditions1[1]) && amount <= Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else {
                    if (amount >= Double.parseDouble(conditions1[1])) result = true;
                }
            }
        }else if(stage.equals("APROBACION3")){
            PatrimonialStatementRestTemplate patrimonialStatementRestTemplate = new PatrimonialStatementRestTemplate();
            CreditRequestApplicantDtoRestTemplate creditRequestApplicantDtoRestTemplate = new CreditRequestApplicantDtoRestTemplate();
            List<CreditRequestApplicantDto> creditRequestApplicantDtoList = creditRequestApplicantDtoRestTemplate.getByNumberRequest(creditRequest.getNumberRequest());
            CreditRequestApplicantDto creditRequestApplicantDto = creditRequestApplicantDtoList.stream()
                    .filter(c->c.getTypeRelation().equals("deudor") && c.getNumberRequest().equals(creditRequest.getNumberRequest().toString()))
                    .findFirst().get();
            List<PatrimonialStatement> patrimonialStatementList = patrimonialStatementRestTemplate
                    .getByIdCreditRequestApplicant(creditRequestApplicantDto.getId());
            patrimonialStatementList = patrimonialStatementList.stream()
                    .filter(p->p.getElementCategory().equals("DOCUMENTOS Y CUENTAS POR PAGAR") && p.getFieldBoolean1().equals("SI"))
                    .collect(Collectors.toList());
            ParameterRestTemplate parameterRestTemplate = new ParameterRestTemplate();
            List<Parameter> parameterList = new ArrayList<>();
            parameterList = parameterRestTemplate.getParametersByCategory("RANGO APROBACION");
            parameterList = parameterList.stream()
                    .filter(p->p.getValue().contains("APROBACION3"))
                    .collect(Collectors.toList());
            Parameter parameterHomeMortage = parameterList.stream()
                    .filter(f -> f.getValue().equals("APROBACION3-VIVIENDA")).findFirst().get();
            if(creditRequest.getCurrency().equals("$us.")) {
                amount = amount * UtilValues.getExchange();
            }
            Map<String,Double> totalByTypeCredit = getTotalDebtsByTypeCredit(patrimonialStatementList
                    , parameterHomeMortage.getExternalCode(),creditRequest.getCurrency());

            Double aux = totalByTypeCredit.values().stream().reduce(0.0,Double::sum);
            amount=amount+aux;

            String[] conditions1 = new String[2];
            String[] conditions2 = new String[2];
            String[] allConditions = new String[2];
            for(Parameter p:parameterList){
                if(p.getValue().contains(stage)){
                    String[] listTypeCredit = p.getExternalCode().split(",");
                    if(Arrays.stream(listTypeCredit).anyMatch(s -> s.trim().equals(creditRequest.getTypeCredit()))){
                        allConditions = p.getDescription().split("-");

                        conditions1 = allConditions[0].length()>0? allConditions[0].split(";"):new String[0];
                        if(allConditions.length>1) {
                            conditions2 = allConditions[1].length() > 0 ? allConditions[1].split(";") : new String[0];
                        }else{
                            conditions2[0]="";
                        }

                    }
                }
            }

            if(conditions1[0].equals(">")){
                if(conditions2[0].equals("<")){
                    if(amount > Double.parseDouble(conditions1[1]) && amount < Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else if(conditions2[0].equals("<=")){
                    if(amount > Double.parseDouble(conditions1[1]) && amount <= Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else {
                    if (amount > Double.parseDouble(conditions1[1])) result = true;
                }
            } else if(conditions1[0].equals(">=")){
                if(conditions2[0].equals("<")){
                    if(amount >= Double.parseDouble(conditions1[1]) && amount < Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else if(conditions2[0].equals("<=")){
                    if(amount >= Double.parseDouble(conditions1[1]) && amount <= Double.parseDouble(conditions2[1])){
                        result = true;
                    }
                }else {
                    if (amount >= Double.parseDouble(conditions1[1])) result = true;
                }
            }
        }
        return result;
    }



    private String getAllStagesForNextStage(List<StageHistory> stageHistories, String currentStageHistory,
                                            List<RequestStage> requestStageList){
        RequestStage requestStage = requestStageList.stream().filter(r -> r.getStage().equals(currentStageHistory))
                .findFirst().get();
        List<RequestStage> requestStagesSamePosition = requestStageList.stream()
                .filter(r -> r.getPosition().equals(requestStage.getPosition()) && r.isActive()==true)
                .collect(Collectors.toList());

        List<String> comesFrom = new ArrayList<>();
        for(RequestStage r : requestStagesSamePosition){
            comesFrom.add(r.getStage());
        }
        return comesFrom.stream().collect(Collectors.joining(","));
    }

    private List<RequestStage> getNextStage(StageHistory currentStageHistory, List<RequestStage> requestStages, int skip){
        List<RequestStage> currentRequestStage = requestStages.stream().filter(p -> p.getStage().equals(currentStageHistory.getStage()))
                                                .collect(Collectors.toList());
        List<RequestStage> nextStages = requestStages.stream()
                .filter(p -> p.getPosition().equals(currentRequestStage.get(0).getPosition() + 1+skip) && p.isActive()==true)
                .collect(Collectors.toList());
        return nextStages;

    }

    private boolean verifyAllStageFinished(List<StageHistory> stageHistories, String currentStageHistory,
                                           List<RequestStage> requestStageList){

        RequestStage requestStage = requestStageList.stream().filter(r -> r.getStage().equals(currentStageHistory))
                .findFirst().get();
       List<RequestStage> requestStagesSamePosition = requestStageList.stream()
               .filter(r -> r.getPosition().equals(requestStage.getPosition()))
                .collect(Collectors.toList());

        List<StageHistory> statesHistorySamePosition = stageHistories.stream()
                .filter(p -> getStagePositionToString(requestStagesSamePosition).contains(p.getStage()))
                .collect(Collectors.toList());

        boolean finished = true;
        ObjectMapper mapper = new ObjectMapper();
        for(StageHistory s:statesHistorySamePosition){
            RequestStage r = requestStagesSamePosition.stream().filter(rs -> rs.getStage().equals(s.getStage()))
                    .findFirst().get();
            try {
                List<States> statesList = mapper.readValue(r.getStates(), new TypeReference<List<States>>() {});
                States states = statesList.stream().filter(st -> st.getState().equals(s.getState())).findFirst().get();
                if(!states.isFinishState() || states.getState().equals("OBSERVADO")) {
                    finished = false;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
        return finished;
    }

    private String getStagePositionToString(List<RequestStage> stageList){
        List<String> list = new ArrayList<>();
        for(RequestStage r:stageList){
            list.add(r.getStage());
        }
        return list.stream().collect(Collectors.joining());
    }


    private StageHistory createNewStageHistory(String stage, String state){
        StageHistory stageHistory = new StageHistory();
        stageHistory.setState(state);
        stageHistory.setStartDateTime(Instant.now());
        stageHistory.setStage(stage);
        return stageHistory;
    }

    private List<String> getListStates(){
        List<String> list = new ArrayList<>();
        for(States s:statesList){
            if(s.isActive() )
               list.add(s.getState());
        }

        return list;
    }

    private List<String> getListNextStates(){
        List<String> list = new ArrayList<>();
        for(States s:statesList){
            if(s.isInitState() ||  s.isFinished() )
                list.add(s.getState());
        }

        return list;
    }

    private boolean isGoBack(String state){
        if(state!=null && !state.equals("")) {
            States s = statesList.stream().filter(p -> p.getState().equals(state)).findFirst().get();
            return s.isGoBackward();
        }
        return false;
    }

    private boolean isGoForward(String state){
        if(state!=null && !state.equals("")) {
            States s = statesList.stream().filter(p -> p.getState().equals(state)).findFirst().get();
            return s.isGoForward();
        }
        return false;
    }

    private void createGoBackwardStage(StageHistory stageHistory, Set<String> stageToBack)  {

        stageHistory.setFinishedDateTime(Instant.now());

        restTemplate.update(stageHistory);
        List<StageHistory> stageHistoryList = restTemplate.getByNumberRequest(stageHistory.getNumberRequest());

        for(String stage : stageToBack) {
            StageHistory aux = stageHistoryList.stream()
                    .filter(f -> f.getStage().equals(stage))
                    .collect(Collectors.toList()).get(0);
            StageHistory s = createNewStageHistory(stageHistory.getComesFrom(), stageHistory.getNextState());
            s.setNumberRequest(stageHistory.getNumberRequest());
            s.setComesFrom(stageHistory.getStage());
            s.setStage(stage);
            s.setUserTask(aux.getUserTask());
            s.setInitDateTime(Instant.now());
            s.setObservation(stageHistory.getObservation());
            restTemplate.add(s);

        }


        PrepareMail.sendMailWorkflowGoBackward(stageHistoryList,stageHistory.getUserTask(),stageHistory.getNumberRequest(),
                stageHistory.getComesFrom());

        UIUtils.showNotification("Correos enviados");
        UIUtils.showNotification("Estado observado");
        UI.getCurrent().navigate(StageHistoryView.class);
    }

    private void createGoBackFirstStage(StageHistory stageHistory, String stageGoBack){
        stageHistory.setFinishedDateTime(Instant.now());
        restTemplate.update(stageHistory);
        StageHistory aux = restTemplate.getByNumberRequestStageState(stageHistory.getNumberRequest().toString(),
                "CONCLUIDO",stageGoBack).get(0);

        List<StageHistory> stageHistoryList = new ArrayList<>();
        StageHistory s = createNewStageHistory(stageHistory.getComesFrom(),stageHistory.getNextState());
        s.setNumberRequest(stageHistory.getNumberRequest());
        s.setComesFrom(stageHistory.getStage());
        s.setStage(stageGoBack);
        s.setUserTask(aux.getUserTask());
        s.setInitDateTime(Instant.now());
        s.setObservation(stageHistory.getObservation());
        restTemplate.add(s);

        stageHistoryList.add(aux);
        PrepareMail.sendMailWorkflowGoBackward(stageHistoryList,stageHistory.getUserTask(),stageHistory.getNumberRequest(),
                stageGoBack);

        UIUtils.showNotification("Correos enviados");
        UIUtils.showNotification("Estado observado");
        UI.getCurrent().navigate(StageHistoryView.class);
    }

    private Map<String,Double> getTotalDebtsByTypeCredit(List<PatrimonialStatement> patrimonialStatementList
            , String typeCreditsHome, String currency){
        Double totalHomeMortage=0.0;
        Double totalOthers=0.0;
        Double amount =0.0;
        Map<String,Double> totalByTypeCredit = new HashMap<>();
        for(PatrimonialStatement p:patrimonialStatementList){
            if(currency.equals("$us.")){
                amount = p.getFieldDouble1()*UtilValues.getExchange();
            }else{
                amount = p.getFieldDouble1();
            }
            boolean isHomeMortage = false;

            if(typeCreditsHome.contains(p.getFieldSelection1())){
                isHomeMortage=true;
            }

            if(isHomeMortage){
                totalHomeMortage = totalHomeMortage + amount;
                totalByTypeCredit.remove("H");
                totalByTypeCredit.put("H",totalHomeMortage);
            }else{
                totalOthers = totalOthers + amount;
                totalByTypeCredit.remove("O");
                totalByTypeCredit.put("O",totalOthers);
            }
        }

        return totalByTypeCredit;
    }
}
