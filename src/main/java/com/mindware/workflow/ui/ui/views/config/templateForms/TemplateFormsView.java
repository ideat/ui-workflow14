package com.mindware.workflow.ui.ui.views.config.templateForms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.FieldsStructure;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.config.TemplateForm;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.templateForm.TemplateFormRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;


@Route(value = "templateForms", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Plantillas de formularios")
public class TemplateFormsView extends SplitViewFrame implements RouterLayout {

    private ParameterRestTemplate parameterRestTemplate;
    private TemplateFormRestTemplate restTemplate;

    private Grid<TemplateForm> grid;

    private ListDataProvider<TemplateForm> dataProviderTemplateForms;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;


    private ListDataProvider<FieldsStructure> structureListDataProvider;
//    private Binder<TemplateForm> binder;
    private List<Parameter> parameterList = new ArrayList<>();
    private TemplateForm current;

    public TemplateFormsView(){
        restTemplate = new TemplateFormRestTemplate();
        parameterRestTemplate = new ParameterRestTemplate();

        getParameters();
        verifyNewParamters();

        setViewContent(createContent());
        setViewHeader(createTopBar());
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(Position.BOTTOM);
    }

    private HorizontalLayout createTopBar(){
        Button btnImport = new Button("Obtener plantillas");
        btnImport.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnImport.setIcon(VaadinIcon.ADD_DOCK.create());
        btnImport.setEnabled(GrantOptions.grantedOption("Plantillas"));
        btnImport.addClickShortcut(Key.KEY_I, KeyModifier.ALT);
        btnImport.addClickListener(e ->{

        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnImport);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnImport);
        topLayout.setPadding(true);

        return topLayout;
    }

    private void getParameters(){
        List<String> categoryList = new ArrayList<>();
        categoryList.add("ACTIVO");
        categoryList.add("PASIVO");
        categoryList.add("INGRESOS");
        categoryList.add("EGRESOS");

        for(String s : categoryList){
            parameterList.addAll(parameterRestTemplate.getParametersByCategory(s));

        }

    }

    private void verifyNewParamters(){
        for(Parameter p : parameterList){
            Optional<TemplateForm> t = restTemplate.getByNameAndCategory(p.getValue(),p.getCategory());
            if (!t.isPresent()){
                TemplateForm templateForm = new TemplateForm();
                templateForm.setName(p.getValue());
                templateForm.setCategory(p.getCategory());
                templateForm.setFieldsStructure("[]");
                restTemplate.add(templateForm);
            }
        }
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridTemplateForm());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGridTemplateForm(){
        grid = new Grid<>();
        grid.setId("templateForm");
        grid.setMultiSort(true);
        grid.setHeightFull();
        grid.setWidth("100%");

        dataProviderTemplateForms = new ListDataProvider<>(restTemplate.getAll());

        grid.setDataProvider(dataProviderTemplateForms);

        grid.addSelectionListener(event -> {
            if(event.getFirstSelectedItem()!=null) {
                current = event.getFirstSelectedItem().get();
                event.getFirstSelectedItem().ifPresent(this::showDetails);
            }
        } );

        grid.addColumn(TemplateForm::getCategory).setFlexGrow(0).setSortable(true)
                .setHeader("Categoria").setWidth(UIUtils.COLUMN_WIDTH_L).setTextAlign(ColumnTextAlign.START);
        grid.addColumn(TemplateForm::getName).setFlexGrow(0).setSortable(true)
                .setHeader("Nombre formulario").setWidth(UIUtils.COLUMN_WIDTH_XXL)
                .setTextAlign(ColumnTextAlign.START).setResizable(true);

        return grid;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Plantillas"));
        footer.addSaveListener(e -> {
            detailsDrawer.hide();
            Collection<FieldsStructure> fieldsStructureList = structureListDataProvider.getItems();
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(fieldsStructureList);
                current.setFieldsStructure(json);
                restTemplate.updateFieldStructure(current);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }


            UIUtils.showNotification("Cambios guardados");
        });
        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private void showDetails(TemplateForm templateForm)  {
        detailsDrawerHeader.setTitle(templateForm.getName());
        detailsDrawer.setContent(createDetails(templateForm));
        detailsDrawer.show();
    }

    private VerticalLayout createDetails(TemplateForm templateForm) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        if(templateForm.getFieldsStructure().equals("[]")) {
            structureListDataProvider = new ListDataProvider<>(initFieldStructure());
        }else{
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<FieldsStructure> structureList = mapper.readValue(templateForm.getFieldsStructure(), new TypeReference<List<FieldsStructure>>(){} );
                structureListDataProvider = new ListDataProvider<>(structureList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Grid gridStructureFields = createGridFieldStructure();

        layout.add(gridStructureFields);

        return layout;

    }

    private Grid createGridFieldStructure()  {

        Grid<FieldsStructure> gridStructureFields = new Grid<>();
        gridStructureFields.setWidth("100%");
        gridStructureFields.setDataProvider(structureListDataProvider);

        gridStructureFields.addColumn(FieldsStructure::getComponentName).setHeader("Nombre campo")
                .setWidth(UIUtils.COLUMN_WIDTH_M).setFlexGrow(0).setResizable(true);
        Grid.Column<FieldsStructure> labelColumn = gridStructureFields.addColumn(FieldsStructure::getComponentLabel).setHeader("Etiqueta")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0).setResizable(true);
        Grid.Column<FieldsStructure> visibleColumn = gridStructureFields.addColumn(FieldsStructure::isVisible).setHeader("Es visible")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0).setResizable(true);
        Grid.Column<FieldsStructure> requiredColumn = gridStructureFields.addColumn(FieldsStructure::isRequired).setHeader("Es requerido")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0).setResizable(true);
        Grid.Column<FieldsStructure> positionColumn =  gridStructureFields.addColumn(FieldsStructure::getPosition).setHeader("Posicion")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0).setResizable(true);
        Grid.Column<FieldsStructure> valueColumn = gridStructureFields.addColumn(FieldsStructure::getValues).setHeader("Valor")
                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0).setResizable(true);


        Div validationStatus = new Div();
        validationStatus.setId("validation");

        Binder<FieldsStructure> binder = new BeanValidationBinder<>(FieldsStructure.class);
        Editor<FieldsStructure> editor = gridStructureFields.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField label = new TextField();
        labelColumn.setEditorComponent(label);

        TextField values = new TextField();
        valueColumn.setEditorComponent(values);

        Checkbox visible = new Checkbox();
        visibleColumn.setEditorComponent(visible);

        Checkbox required = new Checkbox();
        requiredColumn.setEditorComponent(required);

        NumberField position = new NumberField();
        position.setMin(0);
        position.setMax(14);
        positionColumn.setEditorComponent(position);


        binder.forField(label).withValidator(value -> !value.isEmpty(),"Etiqueta no puede estar vacia")
                .withStatusLabel(validationStatus).bind("componentLabel");
        binder.bind(visible,"visible");
        binder.bind(required,"required");
        binder.forField(position).withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind("position");
        binder.bind(values,"values");
        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<FieldsStructure> editorColumn = gridStructureFields.addComponentColumn(fieldsStructure -> {
            Button edit = new Button("Editar");
            edit.addClickListener(e -> {
                editor.editItem(fieldsStructure);
                label.focus();
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


        Button cancel = new Button("Cancelar", e -> editor.cancel());
        cancel.addClassName("cancel");

        // Add a keypress listener that listens for an escape key up event.
        // Note! some browsers return key as Escape and some as Esc
        gridStructureFields.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        return gridStructureFields;

    }

    private List<FieldsStructure> initFieldStructure()  {
        Class clase;
        Field[] fields = {};
        try {
            clase = Class.forName("com.mindware.workflow.backend.entity.patrimonialStatement.PatrimonialStatement");
            fields = clase.getFields();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        List<FieldsStructure> fieldsStructureList = new ArrayList<>();
        for(int i=0; i< fields.length;i++){
            FieldsStructure fieldsStructure = new FieldsStructure();
            fieldsStructure.setComponentName(fields[i].getName());
            fieldsStructure.setComponentLabel("Etiqueta");
            fieldsStructure.setVisible(false);
            fieldsStructure.setRequired(false);
            fieldsStructure.setPosition(0);
            fieldsStructureList.add(fieldsStructure);
        }
        return fieldsStructureList;
    }

    private List<String> getListNameForm(String category){
        List<String> listNameForms = new ArrayList<>();
        List<Parameter> listParameters= parameterRestTemplate.getParametersByCategory(category);
        for(Parameter p:listParameters){
            listNameForms.add(p.getValue());
        }
        return listNameForms;
    }
}
