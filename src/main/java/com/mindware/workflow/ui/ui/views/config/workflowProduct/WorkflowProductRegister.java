package com.mindware.workflow.ui.ui.views.config.workflowProduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.config.RequestStage;
import com.mindware.workflow.ui.backend.entity.config.States;
import com.mindware.workflow.ui.backend.entity.config.WorkflowProduct;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
import com.mindware.workflow.ui.backend.rest.workflowProducdt.WorkflowProductRestTemplate;
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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

@Route(value ="workflow-product-register",layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Registro del Flujo por Producto")
public class WorkflowProductRegister extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {
    private WorkflowProductRestTemplate restTemplate;
    private ParameterRestTemplate parameterRestTemplate;
    private RolRestTemplate rolRestTemplate;

    private List<Parameter> parameterList;
    private List<RequestStage> requestStageList;
    private List<Rol> rolList;
    private WorkflowProduct workflowProduct;
    private BeanValidationBinder<WorkflowProduct> binder;
    private ListDataProvider<RequestStage> requestStageListDataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;
    private ObjectMapper mapper = new ObjectMapper();
    private List<String> rolNames = new ArrayList<>();
    private Map<String,List<String>> param;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(param.get("product").get(0));
    }

    @SneakyThrows
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        restTemplate = new WorkflowProductRestTemplate();
        parameterRestTemplate = new ParameterRestTemplate();
        rolRestTemplate = new RolRestTemplate();

        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        param = queryParameters.getParameters();

        workflowProduct = restTemplate.getByCode(param.get("code").get(0));
        requestStageList = new ArrayList<>();
        rolList = rolRestTemplate.getAllRols();

        for(Rol rol:rolList){
            String name = rol.getName();
            rolNames.add(name);
        }

        if(workflowProduct.getId()!=null) {
            String jsonRequestStage = workflowProduct.getRequestStage();
            requestStageList = mapper.readValue(jsonRequestStage, new TypeReference<List<RequestStage>>() {});
//            setViewContent(createContent());
        }else{
            parameterList = parameterRestTemplate.getParametersByCategory("ETAPAS SOLICITUD");
            for(Parameter p:parameterList){
                RequestStage requestStage = new RequestStage();
                requestStage.setId(UUID.randomUUID());
                requestStage.setPosition(0);
                requestStage.setHours(0);
                requestStage.setStage(p.getValue());
                requestStage.setActive(false);
                requestStage.setRols("");
                requestStageList.add(requestStage);
            }
            workflowProduct = new WorkflowProduct();
            workflowProduct.setCodeProductCredit(param.get("code").get(0));
            String jsonRequestStage = mapper.writeValueAsString(requestStageList);
            workflowProduct.setRequestStage(jsonRequestStage);

//            setViewContent(createContent());
//            setViewDetails(createDetailDrawer());
//            setViewDetailsPosition(Position.BOTTOM);
        }
        setViewContent(createContent());
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        binder.readBean(workflowProduct);
    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e->{
            UI.getCurrent().navigate(WorflowProductView.class);
        });

        return appBar;
    }

    private Component createContent(){

        FlexBoxLayout content = new FlexBoxLayout(createRequestStage());
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Horizontal.RESPONSIVE_L);
        content.setHeight("100%");
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

    private DetailsDrawer createRequestStage(){
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("100%");

        binder = new BeanValidationBinder<>(WorkflowProduct.class);

        Grid<RequestStage> grid = new Grid<>();
        grid.setWidthFull();
        requestStageList = requestStageList.stream().sorted(Comparator.comparing(RequestStage::getPosition)).collect(Collectors.toList());
        requestStageListDataProvider = new ListDataProvider<>(requestStageList);
        grid.setDataProvider(requestStageListDataProvider);

        grid.addSelectionListener(e ->{
            e.getFirstSelectedItem().ifPresent(this::showStatesStage);
        });

        grid.addColumn(RequestStage::getStage).setFlexGrow(1).setResizable(true).setHeader("Etapa");
        Grid.Column<RequestStage> activeColumn = grid.addColumn(new ComponentRenderer<>(this::createActive))
                .setHeader("Activo").setFlexGrow(1).setResizable(true);
        Grid.Column<RequestStage> positionColumn=  grid.addColumn(RequestStage::getPosition).setFlexGrow(1)
                .setResizable(true).setSortable(true).setHeader("Posicion");
        Grid.Column<RequestStage> hoursColumn = grid.addColumn(RequestStage::getHours).setFlexGrow(1)
                .setResizable(true).setHeader("Horas");
        Grid.Column<RequestStage> rolsColumn = grid.addColumn(RequestStage::getRols).setFlexGrow(1)
                .setResizable(true).setHeader("Roles Auth");

        Binder<RequestStage> binderRequestStage = new Binder<>(RequestStage.class);
        Editor<RequestStage> editor = grid.getEditor();
        editor.setBinder(binderRequestStage);
        editor.setBuffered(true);

        Checkbox active = new Checkbox();
        binderRequestStage.forField(active).bind(RequestStage::isActive,RequestStage::setActive);
        activeColumn.setEditorComponent(active);

        NumberField position = new NumberField();
        binderRequestStage.forField(position).withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(RequestStage::getPosition,RequestStage::setPosition);
        positionColumn.setEditorComponent(position);

        NumberField hours = new NumberField();
        binderRequestStage.forField(hours).withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(RequestStage::getHours, RequestStage::setHours);
        hoursColumn.setEditorComponent(hours);

        ComboBox<String> rols = new ComboBox<>();
        rols.setWidth("100%");
        rols.setItems(rolNames);
        binderRequestStage.forField(rols).bind(RequestStage::getRols,RequestStage::setRols);
        rolsColumn.setEditorComponent(rols);

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());

        Grid.Column<RequestStage> editorColumn = grid.addComponentColumn(requestStage1 -> {
            Button edit = new Button("Editar");
            edit.addClickListener(e ->{
               editor.editItem(requestStage1);
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
            .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Guardar", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancelar", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        layout.add(grid);

        footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Flujo por Producto"));
        footer.addSaveListener(e -> {
            if(binder.writeBeanIfValid(workflowProduct)){
                try {
                    String jsonRequestStage = mapper.writeValueAsString(requestStageList);
                    Integer totalHours = requestStageList.stream().map(t -> t.getHours()).reduce(0,Integer::sum);
                    workflowProduct.setRequestStage(jsonRequestStage);
                    workflowProduct.setTotalHours(totalHours);
                    restTemplate.add(workflowProduct);

                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }

                UIUtils.showNotification("Flujo de Producto Registrado");
                UI.getCurrent().navigate(WorflowProductView.class);
            }
        });
        footer.addSaveListener(e -> UI.getCurrent().navigate(WorflowProductView.class) );

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("100%");
        detailsDrawer.setContent(layout);
        detailsDrawer.setFooter(footer);
        detailsDrawer.show();

        return detailsDrawer;

    }

    private Component createActive(RequestStage requestStage){
        Icon icon;
        if(requestStage.isActive()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private void showStatesStage(RequestStage requestStage){
        detailsDrawerHeader.setTitle("Estados de la Etapa");
        detailsDrawer.setContent(createGridStates(requestStage));
        detailsDrawer.show();
    }

    private DetailsDrawer createGridStates(RequestStage requestStage){
        ListDataProvider<States> statesListDataProvider;
        List<States> statesList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        if(requestStage.getStates()==null || requestStage.getStates().equals("")|| requestStage.getStates().equals("[]")){
            List<Parameter> parameterList = parameterRestTemplate.getParametersByCategory("ESTADO-WORKFLOW");
            parameterList = parameterList.stream().sorted(Comparator.comparing(Parameter::getValue))
                    .collect(Collectors.toList());
            for(Parameter p : parameterList){
                States states = new States();
                states.setState(p.getDescription());
                states.setActive(false);
                states.setFinished(false);
                states.setGoForward(false);
                states.setGoBackward(false);
                states.setInitState(false);
                states.setFinishState(false);
                statesList.add(states);
            }
        }else {
            try {
                statesList = mapper.readValue(requestStage.getStates(), new TypeReference<List<States>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        statesListDataProvider = new ListDataProvider<>(statesList);

        Grid < States > gridStates = new Grid<>();
        gridStates.setSizeFull();
        gridStates.setDataProvider(statesListDataProvider);

        gridStates.addColumn(States::getState).setFlexGrow(1).setResizable(true).setHeader("Estado workflow");
        Grid.Column<States> activedColumn = gridStates.addColumn(new ComponentRenderer<>(this::createActive))
                .setHeader("Estado de la etapa").setFlexGrow(1).setResizable(true);
        Grid.Column<States> finishedColumn = gridStates.addColumn(new ComponentRenderer<>(this::createFinished))
                .setHeader("Termina Flujo").setFlexGrow(1).setResizable(true);
        Grid.Column<States> finishStateColumn = gridStates.addColumn(new ComponentRenderer<>(this::createFinishState))
                .setHeader("Termina Etapa").setFlexGrow(1).setResizable(true);
        Grid.Column<States> goForwardColumn = gridStates.addColumn(new ComponentRenderer<>(this::createGoForward))
                .setHeader("Avanzar").setFlexGrow(1).setResizable(true);
        Grid.Column<States> goBackwardColumn = gridStates.addColumn(new ComponentRenderer<>(this::createGoBackrward))
                .setHeader("Retroceder").setFlexGrow(1).setResizable(true);
        Grid.Column<States> initStateColumn = gridStates.addColumn(new ComponentRenderer<>(this::createInitState))
                .setHeader("Estado Inicio").setFlexGrow(1).setResizable(true);

        Binder<States> binderStates = new Binder<>(States.class);
        Editor<States> editorStates = gridStates.getEditor();
        editorStates.setBinder(binderStates);
        editorStates.setBuffered(true);

        Checkbox actived = new Checkbox();
        binderStates.forField(actived).bind(States::isActive,States::setActive);
        activedColumn.setEditorComponent(actived);

        Checkbox finished = new Checkbox();
        binderStates.forField(finished).bind(States::isFinished,States::setFinished);
        finishedColumn.setEditorComponent(finished);

        Checkbox finishState = new Checkbox();
        binderStates.forField(finishState).bind(States::isFinishState,States::setFinishState);
        finishStateColumn.setEditorComponent(finishState);

        Checkbox goForward = new Checkbox();
        binderStates.forField(goForward).bind(States::isGoForward,States::setGoForward);
        goForwardColumn.setEditorComponent(goForward);

        Checkbox goBackward = new Checkbox();
        binderStates.forField(goBackward).bind(States::isGoBackward,States::setGoBackward);
        goBackwardColumn.setEditorComponent(goBackward);

        Checkbox initState = new Checkbox();
        binderStates.forField(initState).bind(States::isInitState,States::setInitState);
        initStateColumn.setEditorComponent(initState);

        Collection<Button> editButtonsStates = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<States> editorStatesColumn = gridStates.addComponentColumn(option -> {
            Button edit = new Button("Editar");
            edit.addClickListener(e ->{
                editorStates.editItem(option);

            });
            edit.setEnabled(!editorStates.isOpen());
            editButtonsStates.add(edit);
            return edit;
        });

        editorStates.addOpenListener(e -> editButtonsStates.stream()
                .forEach(button -> button.setEnabled(!editorStates.isOpen())));
        editorStates.addCloseListener(e -> editButtonsStates.stream()
                .forEach(button -> button.setEnabled(!editorStates.isOpen())));

        Button saveStates = new Button("Guardar", e -> editorStates.save());
        saveStates.addClassName("save");

        Button cancelStates = new Button("Cancelar", e -> editorStates.cancel());
        cancelStates.addClassName("cancel");

        gridStates.getElement().addEventListener("keyup", event -> editorStates.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttonsStates = new Div(saveStates, cancelStates);
        editorStatesColumn.setEditorComponent(buttonsStates);

        DetailsDrawer detailsDrawerState =  new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        DetailsDrawerFooter footerState = new DetailsDrawerFooter();
        footerState.saveState(true);

        List<States> finalStatesList = statesList;
        footerState.addSaveListener(e ->{
            try {
                String jsonStates = mapper.writeValueAsString(finalStatesList);
                requestStage.setStates(jsonStates);
                detailsDrawer.hide();
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }

        });

        footerState.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawerState.setContent(gridStates);
        detailsDrawerState.setFooter(footerState);

        return detailsDrawerState;

    }

    private Component createFinished(States states){
        Icon icon;
        if(states.isFinished()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createFinishState(States states){
        Icon icon;
        if(states.isFinishState()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createGoForward(States states){
        Icon icon;
        if(states.isGoForward()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createGoBackrward(States states){
        Icon icon;
        if(states.isGoBackward()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createInitState(States states){
        Icon icon;
        if(states.isInitState()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createActive(States states){
        Icon icon;
        if(states.isActive()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }
}
