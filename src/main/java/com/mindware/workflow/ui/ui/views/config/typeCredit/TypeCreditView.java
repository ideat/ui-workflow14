package com.mindware.workflow.ui.ui.views.config.typeCredit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowingcode.vaadin.addons.errorwindow.ErrorWindow;
import com.mindware.workflow.ui.backend.entity.config.TypeCredit;
import com.mindware.workflow.ui.backend.rest.typeCredit.TypeCreditRestTemplate;
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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;

import java.util.*;

@Route(value = "typeCredit", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Tipo de credito")
public class TypeCreditView extends SplitViewFrame implements RouterLayout {
    private Grid<TypeCredit> grid;
    private TypeCreditDataProvider dataProvider;
    List<TypeCredit> typeCreditList;

    private TypeCreditRestTemplate restTemplate;

    private TextField filterText;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private Binder<TypeCredit> binder;
    private TypeCredit current;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        restTemplate = new TypeCreditRestTemplate();
        getTypeCreditList();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());

    }

    private void getTypeCreditList(){
        typeCreditList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new TypeCreditDataProvider(typeCreditList);
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Codigo, Descripcion");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.setWidth("100%");
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setEnabled(GrantOptions.grantedOption("Tipo de Credito")); //TODO : CREATE GRANT OPTION FOR Tipo de Credito
        btnNew.addClickListener(e -> showDetails(new TypeCredit()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.add(btnNew);
        topLayout.setSpacing(true);

        return topLayout;
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridTypeCredit());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGridTypeCredit() {
        grid = new Grid<>();
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));

        grid.setDataProvider(dataProvider);
        grid.setHeight("100%");
        grid.addColumn(TypeCredit::getExternalCode)
                .setFlexGrow(1)
                .setHeader("Codigo")
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(TypeCredit::getDescription)
                .setFlexGrow(1)
                .setHeader("Descripcion")
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(this::createButtonProductCredit))
                .setFlexGrow(1)
                .setTextAlign(ColumnTextAlign.START);
        grid.addColumn(new ComponentRenderer<>(this::createButtonObjectCredit))
                .setFlexGrow(1)
                .setTextAlign(ColumnTextAlign.START);

        return grid;
    }

    private Component createButtonProductCredit(TypeCredit typeCredit){
        Button btn = new Button("Productos Credito");
        btn.addClickListener(e ->{

            Map<String,List<String>> param = new HashMap<>();
            List<String> externalCode = new ArrayList<>();

            externalCode.add(typeCredit.getExternalCode());
            param.put("external-code",externalCode);

            QueryParameters qp = new QueryParameters(param);
            UI.getCurrent().navigate("productCredit",qp);

        });

        return btn;
    }

    private Component createButtonObjectCredit(TypeCredit typeCredit){
        Button btn = new Button("Objeto Credito");
        btn.addClickListener(e ->{
            Map<String,List<String>> param = new HashMap<>();
            List<String> externalCode = new ArrayList<>();

            externalCode.add(typeCredit.getExternalCode());
            param.put("external-code",externalCode);

            QueryParameters qp = new QueryParameters(param);
            UI.getCurrent().navigate("objectCredit",qp);

        });

        return btn;
    }

    private void showDetails(TypeCredit typeCredit) {
        current = typeCredit;
        detailsDrawerHeader.setTitle(typeCredit.getDescription());
        detailsDrawer.setContent(createDetails(current));
        detailsDrawer.show();
        binder.readBean(current);
    }

    private FormLayout createDetails(TypeCredit typeCredit) {
        TextField externalCode = new TextField();
        externalCode.setWidth("100%");
        externalCode.setRequiredIndicatorVisible(true);
        externalCode.setRequired(true);

        TextField description = new TextField();
        description.setWidth("100%");
        description.setRequired(true);
        description.setRequiredIndicatorVisible(true);

        binder = new BeanValidationBinder<>(TypeCredit.class);
        binder.forField(externalCode).asRequired("Codigo tipo de credito es requerido")
                .bind(TypeCredit::getExternalCode,TypeCredit::setExternalCode);
        binder.forField(description).asRequired("Descripcion tipo de credito es requerida")
                .bind(TypeCredit::getDescription,TypeCredit::setDescription);
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Tipo de Credito"));
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        form.addFormItem(externalCode,"Codigo");
        form.addFormItem(description,"Descripcion");
        UIUtils.setColSpan(2,externalCode);
        UIUtils.setColSpan(2,description);

        return form;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e -> {
            if(current!=null && binder.writeBeanIfValid(current)){
                if(current.getId()==null){
                    current.setProductTypeCredit("[]");
                    current.setObjectCredit("[]");
                    try {
                        TypeCredit result = (TypeCredit) restTemplate.add(current);
                        typeCreditList.add(result);
                        grid.getDataProvider().refreshAll();
                        UIUtils.showNotification("Tipo de credito registrado");
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                        ErrorWindow w = new ErrorWindow(jsonProcessingException,"Error al guardar,  Show Error Details detalla el Error");
                        w.open();
                    }
                }else{
                    try {
                        TypeCredit result = (TypeCredit) restTemplate.add(current);
                        grid.getDataProvider().refreshItem(current);
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                        ErrorWindow w = new ErrorWindow(jsonProcessingException,"Error al guardar,  Show Error Details detalla el Error");
                        w.open();
                    }
                }
                detailsDrawer.hide();
            }else{
                UIUtils.showNotification("Datos incorrectos, verifique");
            }
        });

        footer.addCancelListener(e ->{
            footer.saveState(false);
            detailsDrawer.hide();
        });
        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }
}
