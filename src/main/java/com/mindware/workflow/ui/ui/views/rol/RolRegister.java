package com.mindware.workflow.ui.ui.views.rol;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.config.States;
import com.mindware.workflow.ui.backend.entity.rol.Option;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "rol-register", layout = MainLayout.class)
@PageTitle("Registro de Rol")
public class RolRegister extends SplitViewFrame implements HasUrlParameter<String> {

    private BeanValidationBinder<Rol> binder;
    private List<Option> optionList = new ArrayList<>();
    private List<States> statesList = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private Rol rol;
    private RolRestTemplate restTemplate;
    private ParameterRestTemplate parameterRestTemplate;
    private ListDataProvider<Option> optionListDataProvider;
    private ListDataProvider<States> statesListDataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private  VerticalLayout layoutOptions;
    private VerticalLayout layoutStates;

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(Optional.ofNullable(rol.getName()).orElse("Nuevo"));

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        restTemplate = new RolRestTemplate();
        parameterRestTemplate = new ParameterRestTemplate();
        binder = new BeanValidationBinder<>(Rol.class);
        if(s.equals("NUEVO")){
            rol = new Rol();
            try {
                File file = ResourceUtils.getFile("classpath:static/menu-json.json");
                optionList = mapper.readValue(file,new TypeReference<List<Option>>() {});
                statesList = new ArrayList<>();
                List<Parameter> parameterList = parameterRestTemplate.getParametersByCategory("ESTADO-WORKFLOW");
                parameterList = parameterList.stream().sorted(Comparator.comparing(Parameter::getValue))
                        .collect(Collectors.toList());
                for(Parameter p : parameterList){
                    States states = new States();
                    states.setState(p.getDescription());
                    states.setActive(false);
                    statesList.add(states);
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            setViewContent(createContent());

        }else{
            rol = restTemplate.getById(UUID.fromString(s));
            if(rol.getOptions()==null || rol.getOptions().equals("")) rol.setOptions("[]");
            try {
                optionList = mapper.readValue(rol.getOptions(), new TypeReference<List<Option>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if(rol.getStates()==null || rol.getStates().equals("")) rol.setStates("[]");
            try {
                statesList = mapper.readValue(rol.getStates(), new TypeReference<List<States>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            setViewContent(createRol(rol));
            setViewDetails(createDetailDrawer());
            setViewDetailsPosition(Position.BOTTOM);
        }
        binder.readBean(rol);
    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.addTab("PERMISOS");
//        appBar.addTab("ESTADOS DEL WORKFLOW");
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(RolView.class);
        });

        layoutStates.setVisible(false);
        appBar.addTabSelectionListener(e ->{
            Tab selectedTab = MainLayout.get().getAppBar().getSelectedTab();
            if (selectedTab != null){
                if(selectedTab.getLabel().equals("PERMISOS")){
                    layoutOptions.setVisible(true);
                    layoutStates.setVisible(false);
                }else{
                    layoutOptions.setVisible(false);
                    layoutStates.setVisible(true);
                }
            }

        });
        appBar.centerTabs();
        return appBar;
    }

    private Component createContent(){

        FlexBoxLayout content = new FlexBoxLayout(createRol(rol));
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

    private DetailsDrawer createRol(Rol rol){
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("90%");
        TextField name = new TextField("Nombre Rol");
        name.setRequired(true);
//        name.setReadOnly(true);

        TextField description = new TextField("Descripcion del Rol");
        description.setWidthFull();
//        description.setReadOnly(true);

        ComboBox<String> scope = new ComboBox<>("Alcance");
        scope.setItems("LOCAL","NACIONAL");
        scope.setWidthFull();

        horizontalLayout.add(name,scope,description);

        layoutOptions = new VerticalLayout();
        layoutOptions.setHeight("100%");


        binder.forField(name).asRequired("Nombre Rol es requerido").bind(Rol::getName,Rol::setName);
        binder.forField(description).bind(Rol::getDescription,Rol::setDescription);
        binder.forField(scope).asRequired("Alcance es requerido").bind(Rol::getScope,Rol::setScope);

        Grid<Option> grid = new Grid<>();
        grid.setWidthFull();
        optionListDataProvider = new ListDataProvider<>(optionList);
        grid.setDataProvider(optionListDataProvider);

        grid.addColumn(Option::getName).setFlexGrow(1).setResizable(true).setHeader("Opcion");
        Grid.Column<Option> assignedColumn= grid.addColumn(new ComponentRenderer<>(this::createAssigned))
                .setHeader("Habilitar").setFlexGrow(1).setResizable(true);
        Grid.Column<Option> readColumn= grid.addColumn(new ComponentRenderer<>(this::createReader))
                .setHeader("Lectura").setFlexGrow(1).setResizable(true);
        Grid.Column<Option> writeColumn= grid.addColumn(new ComponentRenderer<>(this::createWriter))
                .setHeader("Escritura").setFlexGrow(1).setResizable(true);

        Binder<Option> binderOption = new Binder<>(Option.class);
        Editor<Option> editor = grid.getEditor();
        editor.setBinder(binderOption);
        editor.setBuffered(true);

        Checkbox assigned = new Checkbox();
        binderOption.forField(assigned).bind(Option::isAssigned,Option::setAssigned);
        assignedColumn.setEditorComponent(assigned);

        Checkbox read = new Checkbox();
        binderOption.forField(read).bind(Option::isRead,Option::setRead);
        readColumn.setEditorComponent(read);

        Checkbox write = new Checkbox();
        binderOption.forField(write).bind(Option::isWrite,Option::setWrite);
        writeColumn.setEditorComponent(write);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<Option> editorColumn = grid.addComponentColumn(option -> {
            Button edit = new Button("Editar");
            edit.addClickListener(e ->{
               editor.editItem(option);

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

        layoutOptions.add(horizontalLayout,grid);
/////////////
        layoutStates = new VerticalLayout();
        layoutStates.setHeight("100%");

        Grid<States> gridStates = new Grid<>();
        gridStates.setSizeFull();
        statesListDataProvider = new ListDataProvider<>(statesList);
        gridStates.setDataProvider(statesListDataProvider);

        gridStates.addColumn(States::getState).setFlexGrow(1).setResizable(true).setHeader("Estado workflow");
        Grid.Column<States> activedColumn = gridStates.addColumn(new ComponentRenderer<>(this::createActive))
                .setHeader("Etapa Activa").setFlexGrow(1).setResizable(true);
        Grid.Column<States> finishedColumn = gridStates.addColumn(new ComponentRenderer<>(this::createFinished))
                .setHeader("Termina Flujo").setFlexGrow(1).setResizable(true);
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

        layoutStates.add(gridStates);

////////////////

        footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Roles"));
        footer.addSaveListener(e ->{
            if(binder.writeBeanIfValid(rol)){
                try {
                    String options = mapper.writeValueAsString(optionList);
                    String states = mapper.writeValueAsString(statesList);
                    rol.setOptions(options);
                    rol.setStates(states);
                    if(rol.getId()==null) {
                        restTemplate.add(rol);
                    }else{
                        restTemplate.update(rol);
                    }
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
                UIUtils.showNotification("Rol Registrado");
                UI.getCurrent().navigate(RolView.class);
            }
        });

        footer.addCancelListener(e ->{
            UI.getCurrent().navigate(RolView.class);
        });

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("100%");
        detailsDrawer.setContent(layoutOptions,layoutStates);
        detailsDrawer.setFooter(footer);
        detailsDrawer.show();

        return detailsDrawer;
    }

    private Component createAssigned(Option option){
        Icon icon;
        if(option.isAssigned()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createReader(Option option){
        Icon icon;
        if(option.isRead()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createWriter(Option option){
        Icon icon;
        if(option.isWrite()){
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

    private Component createFinished(States states){
        Icon icon;
        if(states.isFinished()){
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
}
