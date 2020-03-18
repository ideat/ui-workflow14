package com.mindware.workflow.ui.ui.views.config.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.exceptions.Exceptions;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(value = "exceptions-register", layout = MainLayout.class)
@PageTitle("Registro Excepciones")
public class ExceptionsRegister extends SplitViewFrame implements HasUrlParameter<String> {
    private Exceptions exceptions;
    private BeanValidationBinder<Exceptions> binder;
    private List<Exceptions> exceptionsList = new ArrayList<>();
    private ObjectMapper mapper;
    private ExceptionsRestTemplate restTemplate;
    private ListDataProvider<Exceptions> dataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        restTemplate = new ExceptionsRestTemplate();

        if(s.equals("NUEVO")){
            exceptions = new Exceptions();
            setViewContent(createContent());
        }else {
            exceptions = restTemplate.getByInternalCode(s);
            setViewContent(createException(exceptions));
            binder.readBean(exceptions);
            setViewDetails(createDetailDrawer());
            setViewDetailsPosition(Position.BOTTOM);
        }

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle("Registro/Edicion de Excepciones");
    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(ExceptionsView.class);
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
        FlexBoxLayout content = new FlexBoxLayout(createException(exceptions));
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO,Horizontal.RESPONSIVE_L);

        return content;
    }

    private DetailsDrawer createException(Exceptions exceptions){
        TextField internalCode = new TextField();
        internalCode.setWidth("100%");
        internalCode.setRequiredIndicatorVisible(true);
        internalCode.setRequired(true);

        ComboBox<String> typeException = new ComboBox();
        typeException.setWidth("100%");
        typeException.setItems("TEMPORAL","PERMANENTE");
        typeException.setRequired(true);
        typeException.setRequiredIndicatorVisible(true);

        NumberField limitTime = new NumberField();
        limitTime.setWidth("100%");
        limitTime.setRequiredIndicatorVisible(true);

        TextArea description = new TextArea();
        description.setWidth("100%");
        description.setRequired(true);
        description.setRequiredIndicatorVisible(true);

        RadioButtonGroup<String> rbState = new RadioButtonGroup<>();
        rbState.setItems("ACTIVO","BAJA");
        rbState.setValue(Optional.ofNullable(exceptions.getState()).orElse("").equals("ACTIVO") ? "ACTIVO":"BAJA");



        binder = new BeanValidationBinder<>(Exceptions.class);
        binder.forField(internalCode).asRequired("Codigo Excepcion es requerido")
                .bind(Exceptions::getInternalCode,Exceptions::setInternalCode);
        binder.forField(typeException).asRequired("Tipo de Excepcion no puede omitirse")
                .bind(Exceptions::getTypeException,Exceptions::setTypeException);
        binder.forField(limitTime).asRequired("Tiempo maximo es requerido")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(Exceptions::getLimitTime,Exceptions::setLimitTime);
        binder.forField(description).asRequired("Detalle Excepcion no puede omitirse")
                .bind(Exceptions::getDescription,Exceptions::setDescription);
        binder.forField(rbState).asRequired("Estado de excepcion no puede omitirse")
                .bind(Exceptions::getState,Exceptions::setState);
        binder.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Excepciones"));
        });

        FormLayout layout = new FormLayout();
        layout.setSizeUndefined();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        layout.addFormItem(internalCode,"Codigo interno");
        layout.addFormItem(typeException,"Tipo Excepcin");
        layout.addFormItem(limitTime,"Tiempo maximo(dias)");
        layout.addFormItem(rbState,"Estado");
        FormLayout.FormItem descriptionItem = layout.addFormItem(description,"Excepcion (descripcion)");
        UIUtils.setColSpan(3,descriptionItem);
        footer = new DetailsDrawerFooter();

        footer.addSaveListener(event -> {
           if(binder.writeBeanIfValid(exceptions)){
               try {
                   restTemplate.add(exceptions);
                   UIUtils.showNotification("Excecion registrada");
                   UI.getCurrent().navigate(ExceptionsView.class);
               } catch (IOException e) {
                   e.printStackTrace();
                   UIUtils.showNotification(e.getMessage());
               }

           }
        });

        footer.addCancelListener(e -> UI.getCurrent().navigate(ExceptionsView.class));

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidth("100%");
        detailsDrawer.setContent(layout);
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

}
