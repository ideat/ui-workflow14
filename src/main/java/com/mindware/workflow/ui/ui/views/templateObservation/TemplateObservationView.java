package com.mindware.workflow.ui.ui.views.templateObservation;

import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.templateObservation.TemplateObservation;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.templateObservation.TemplateObservationRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Route(value = "templateObservation", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Plantillas de Observaciones")
public class TemplateObservationView extends SplitViewFrame implements RouterLayout {
    private Grid<TemplateObservation> grid;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private TemplateObservationRestTemplate restTemplate = new TemplateObservationRestTemplate();
    private Binder<TemplateObservation> binder;
    private TemplateObservation current;

    private List<TemplateObservation> templateObservationList;
    private TemplateObservationDataProvider dataProvider;
    private TextField filterText;
    private Button btnNew;

    public TemplateObservationView(){

        getTemplateObservations();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(Position.BOTTOM);
    }

    private void getTemplateObservations(){
        templateObservationList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new TemplateObservationDataProvider(templateObservationList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridTemplateObservation());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }



    private Grid createGridTemplateObservation(){
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));

        grid.addColumn(TemplateObservation::getTask)
                .setFlexGrow(1).setHeader("Informe").setSortable(true)
                .setAutoWidth(true).setResizable(true);
        grid.addColumn(TemplateObservation::getCategory)
                .setFlexGrow(1).setHeader("Categoria").setSortable(true)
                .setAutoWidth(true).setResizable(true);
        grid.addColumn(TemplateObservation::getCondition).setHeader("Condicion")
                .setAutoWidth(true).setResizable(true);
        grid.addColumn(TemplateObservation::getSequence)
                .setFlexGrow(1).setHeader("Orden").setSortable(true)
                .setAutoWidth(true).setResizable(true);
//        grid.addColumn(TemplateRenderer.<TemplateObservation> of("[[item.requestDate]]")
//                .withProperty("registerDate",
//                        templateObservation -> UIUtils.formatDate(templateObservation.getRegisterDate())))
//                .setHeader("Registro").setComparator(TemplateObservation::getRegisterDate)
//                .setWidth(UIUtils.COLUMN_WIDTH_M).setFlexGrow(0).setSortable(true);

        return grid;
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro Categoria");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));


        btnNew = new Button("Nueva Plantilla");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.setEnabled(GrantOptions.grantedOption("Plantilla Observaciones"));
        btnNew.addClickListener(e -> {
            showDetails(new TemplateObservation());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private void showDetails(TemplateObservation templateObservation){
        current = templateObservation;
        detailsDrawerHeader.setTitle("Plantilla: " +  templateObservation.getTask());
        detailsDrawer.setContent(createDetails(templateObservation));
        binder.readBean(templateObservation);
        detailsDrawer.show();
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
//        detailsDrawer.setSizeFull();
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(event -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(current !=null && binder.writeBeanIfValid(current)){
                TemplateObservation result = restTemplate.add(current);
                if(current.getId()==null){
                    templateObservationList.add(result);
                    grid.getDataProvider().refreshAll();
                }else{
                    grid.getDataProvider().refreshItem(current);
                }
                detailsDrawer.hide();
            }else{
                UIUtils.showNotification("Datos incorrectos, verifique nuevamente");
            }
        });

        footer.addCancelListener(e ->{
            footer.saveState(false);
            detailsDrawer.hide();
        });

        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }

    private FormLayout createDetails(TemplateObservation templateObservation){
        FormLayout form = new FormLayout();
        form.setHeightFull();
        form.setWidthFull();
        ComboBox<String> task = new ComboBox<>();
        task.setItems(getTaskFromParameter());
        task.setWidth("100%");
        task.setRequired(true);

        TextField category = new TextField();
        category.setWidth("100%");
        category.setRequired(true);

        TextField condition = new TextField();
        condition.setWidth("100%");
        condition.setRequired(true);

        NumberField sequence = new NumberField();
        sequence.setWidth("100%");
        sequence.setHasControls(true);
        sequence.setMin(1);



        binder = new BeanValidationBinder<>(TemplateObservation.class);
        binder.forField(task).asRequired("Tarea no puede ser omitida")
                .bind(TemplateObservation::getTask,TemplateObservation::setTask);
        binder.forField(category).asRequired("Categoria no puede ser omitida")
                .bind(TemplateObservation::getCategory,TemplateObservation::setCategory);
        binder.forField(condition).asRequired("Condicion no puede ser omitida")
                .bind(TemplateObservation::getCondition,TemplateObservation::setCondition);
        binder.forField(sequence).withConverter(new UtilValues.DoubleToIntegerConverter())
                .withValidator(p -> p.intValue()>0,"Valor minimo es 1")
                .asRequired("Secuencia no puede ser omitida")
                .bind(TemplateObservation::getSequence,TemplateObservation::setSequence);
        binder.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Plantilla Observaciones"));
        });

        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        form.addFormItem(task,"Tarea");
        form.addFormItem(sequence,"Secuencia");
        FormLayout.FormItem categoryItem = form.addFormItem(category,"Categoria");
        FormLayout.FormItem conditionItem = form.addFormItem(condition,"Condicion");

        UIUtils.setColSpan(2,categoryItem);
        UIUtils.setColSpan(2,conditionItem);

        return form;
    }

    private List<String> getTaskFromParameter(){
        ParameterRestTemplate parameterRestTemplate = new ParameterRestTemplate();
        List<Parameter> parameters = parameterRestTemplate.getParametersByCategory("OBSERVACIONES");
        List<String> list = new ArrayList<>();
        for(Parameter p : parameters){
            String s = p.getValue();
            list.add(s);
        }
        return list;
    }
}
