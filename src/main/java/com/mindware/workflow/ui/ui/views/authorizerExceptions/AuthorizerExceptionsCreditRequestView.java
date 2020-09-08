package com.mindware.workflow.ui.ui.views.authorizerExceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.exceptions.Authorizer;
import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequest;
import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequestDto;
import com.mindware.workflow.ui.backend.entity.exceptions.StatusReview;
import com.mindware.workflow.ui.backend.rest.exceptions.AuthorizerRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsCreditRequestRestTemplate;
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
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "authorizer-exceptions-creditrequest", layout = MainLayout.class)
@PageTitle("Lista excepciones de la solicitud")
public class AuthorizerExceptionsCreditRequestView extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {

    private Map<String, List<String>> params;

    private ExceptionsCreditRequestRestTemplate restTemplate;
    private ExceptionsCreditRequestDtoRestTemplate exceptionsCreditRequestDtoRestTemplate;
    private List<ExceptionsCreditRequestDto> exceptionsCreditRequestDtoList;
    private ListDataProvider<ExceptionsCreditRequestDto> dataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;
    private Optional<StatusReview> statusReview;
    private String currency;
    private Double amount;
    private boolean isActiveCreditRequest;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        restTemplate = new ExceptionsCreditRequestRestTemplate();
        exceptionsCreditRequestDtoRestTemplate = new ExceptionsCreditRequestDtoRestTemplate();
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        params = queryParameters.getParameters();


        exceptionsCreditRequestDtoList = new LinkedList<>(exceptionsCreditRequestDtoRestTemplate.getByNumberRequest(Integer.parseInt(params.get("number-request").get(0))));
        dataProvider = new ListDataProvider<>(exceptionsCreditRequestDtoList);
        isActiveCreditRequest = UtilValues.isActiveCreditRequest(Integer.parseInt(params.get("number-request").get(0)));
        setViewContent(createGrid());
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(params.get("full-name").get(0));
    }

    private AppBar initAppBar(){
        MainLayout.get().getAppBar().reset();
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e -> UI.getCurrent().navigate(AuthorizerExceptionsCreditRequestDtoView.class));
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

    private Component createContent(DetailsDrawer component){
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Vertical.AUTO, Vertical.RESPONSIVE_L);
        return content;
    }

    private Grid createGrid(){
        Grid<ExceptionsCreditRequestDto> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.addColumn(ExceptionsCreditRequestDto::getInternalCode).setHeader("Cod. Excep").setFlexGrow(1)
                .setAutoWidth(true);
        grid.addColumn(ExceptionsCreditRequestDto::getTypeException).setHeader("Tipo excepcion").setFlexGrow(1)
                .setAutoWidth(true);
        grid.addColumn(ExceptionsCreditRequestDto::getStateException).setHeader("Estado").setFlexGrow(1)
                .setAutoWidth(true);
        grid.addColumn(ExceptionsCreditRequestDto::getExceptionDetail).setHeader("Excepcion").setFlexGrow(1)
                .setAutoWidth(true);

        grid.addSelectionListener(e -> {
           if(e.getFirstSelectedItem().isPresent()){
               if(GrantOptions.grantedOption("Autorizar Excepciones")) {
                   ExceptionsCreditRequest exceptionsCreditRequest = restTemplate.getByCodeExceptionNumberRequest(e.getFirstSelectedItem().get().getInternalCode(),
                           e.getFirstSelectedItem().get().getNumberRequest());

                   try {
                       showException(exceptionsCreditRequest);
                   } catch (JsonProcessingException ex) {
                       ex.printStackTrace();
                   }
               }else{
                   UIUtils.showNotification("Usuario no autorizado a editar las autorizaciones");
               }
           }
        });

        return grid;
    }

    private void showException(ExceptionsCreditRequest exceptionsCreditRequest) throws JsonProcessingException {
        detailsDrawerHeader.setTitle("Estados de la Etapa");
        detailsDrawer.setContent(createFormExceptionCreditRequest(exceptionsCreditRequest));
        detailsDrawer.show();
    }

    private DetailsDrawer createFormExceptionCreditRequest(ExceptionsCreditRequest exceptionsCreditRequest) throws JsonProcessingException {
        FormLayout formLayout = new FormLayout();

        ObjectMapper mapper = new ObjectMapper();
        List<StatusReview> statusReviewList = mapper.readValue(exceptionsCreditRequest.getStatusReview(), new TypeReference<List<StatusReview>>() {});
        boolean validUser=isAuthorizedUser(VaadinSession.getCurrent()
                .getAttribute("login").toString(), exceptionsCreditRequest.getRiskType());
        if(statusReviewList.size()>0) {
            statusReview = statusReviewList.stream().filter(f -> f.getLoginUser().equals(VaadinSession.getCurrent()
                    .getAttribute("login").toString()))
                    .findFirst();
            if(!statusReview.isPresent()){
                statusReview =  statusReviewList.stream()
                        .findFirst();
                UIUtils.showNotification(String.format("Excepcion '%s' por el usuario '%s', no puede ser modificada con su cuenta",
                        statusReview.get().getState(),statusReview.get().getLoginUser()));
            }
            if(!validUser){
                UIUtils.showNotification(String.format("Usuario no esta habilitado para Aprobar/Rechazar excepciones de riesgo '%s'",exceptionsCreditRequest.getRiskType()));
            }
        }else{
            statusReview = Optional.ofNullable(new StatusReview());
            statusReview.get().setState("PROPUESTA");
            statusReview.get().setLoginUser(VaadinSession.getCurrent().getAttribute("login").toString());
        }


        TextField codeException = new TextField();
        codeException.setWidth("100%");
        codeException.setValue(exceptionsCreditRequest.getCodeException());
        codeException.setReadOnly(true);

        TextField numberRequest = new TextField();
        numberRequest.setWidth("100%");
        numberRequest.setValue(exceptionsCreditRequest.getNumberRequest().toString());
        numberRequest.setReadOnly(true);

        RadioButtonGroup<String> state = new RadioButtonGroup<>();
        state.setItems("PROPUESTA","APROBADA","RECHAZADA");
        state.setValue(statusReview.get().getState());
        state.setItemEnabledProvider(item -> !"PROPUESTA".equals(item));

        DatePicker register = new DatePicker();
        register.setWidth("100%");
        register.setValue(exceptionsCreditRequest.getRegister());
        register.setReadOnly(true);

        TextArea justification = new TextArea();
        justification.setWidth("100%");
        justification.setValue(exceptionsCreditRequest.getJustification());
//        justification.setReadOnly(true);

        formLayout.addFormItem(codeException,"Cod. Excepcion");
        formLayout.addFormItem(numberRequest,"Nro solicitud");
        formLayout.addFormItem(register,"Fecha registro");
        FormLayout.FormItem justificationItem = formLayout.addFormItem(justification,"Justificacion");
        UIUtils.setColSpan(4,justificationItem);
        formLayout.addFormItem(state,"Estado Excepcion");

        DetailsDrawer detailsDrawerException =  new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(GrantOptions.grantedOption("Autorizar Excepciones")
                && VaadinSession.getCurrent().getAttribute("login").toString().equals(statusReview.get().getLoginUser())
                && validUser && isActiveCreditRequest);

        footer.addSaveListener(e ->{
           exceptionsCreditRequest.setState(state.getValue());
            try {

                statusReviewList.removeIf(s -> s.getLoginUser().equals(statusReview.get().getLoginUser()));

                if(!statusReview.get().getState().equals(state.getValue())) {
                    statusReview.get().setState(state.getValue());
                    statusReview.get().setRegisterDate(LocalDate.now());
                }

                statusReviewList.add(statusReview.get());
                exceptionsCreditRequest.setState(verifyStatus(statusReviewList));
                String jsonStatusReview = mapper.writeValueAsString(statusReviewList);
                exceptionsCreditRequest.setStatusReview(jsonStatusReview);
                restTemplate.add(exceptionsCreditRequest);
                UIUtils.showNotification("Datos excepcion guardados");
                ExceptionsCreditRequestDto ex =  exceptionsCreditRequestDtoList.stream()
                        .filter(excep -> excep.getInternalCode().equals(exceptionsCreditRequest.getCodeException()))
                        .findAny()
                        .orElse(null);
                ex.setStateException(state.getValue());
                exceptionsCreditRequestDtoList.removeIf(e1 -> e1.getInternalCode().equals(exceptionsCreditRequest.getCodeException()));
                exceptionsCreditRequestDtoList.add(ex);
                dataProvider.refreshAll();
                detailsDrawer.hide();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawerException.setContent(formLayout);
        detailsDrawerException.setFooter(footer);

        return detailsDrawerException;
    }

    private String verifyStatus(List<StatusReview> statusReviewList){
        String status ="APROBADA";
        for(StatusReview s:statusReviewList){
            if(!s.getState().equals("APROBADA")){
                status = s.getState();
            }
        }
        return status;
    }

    private boolean isAuthorizedUser(String loginUser, String typeRisk){
        AuthorizerRestTemplate authorizerRestTemplate = new AuthorizerRestTemplate();
        Authorizer authorizer = authorizerRestTemplate.getByLoginUser(loginUser);
        return  authorizer.getRiskType().contains(typeRisk);
    }
}
