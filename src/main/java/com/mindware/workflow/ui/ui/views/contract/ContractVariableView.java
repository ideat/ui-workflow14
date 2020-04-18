package com.mindware.workflow.ui.ui.views.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.contract.ContractVariable;
import com.mindware.workflow.ui.backend.rest.contract.ContractVariableRestTemplate;
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
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Route(value = "contract-variable", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Variables de contratos")
public class ContractVariableView extends SplitViewFrame {
    private Grid<ContractVariable> grid;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;
    private Button btnNew;

    private ContractVariable current;
    private List<ContractVariable> contractVariableList;

    private ContractVariableRestTemplate restTemplate;
    private Binder<ContractVariable> binder;
    private ListDataProvider<ContractVariable> dataProvider;

    private ComboBox<String> typeVariableFilter;
    private TextField nameFilter;
    private TextField variableFilter;
    private static String[] typeVariableConst = {"CONSTANTE","SIMPLE","COMPUESTA"};

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getListContractVariable();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());

    }

    private void getListContractVariable(){
        restTemplate = new ContractVariableRestTemplate();
        contractVariableList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new ListDataProvider<>(contractVariableList);
    }

    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nueva Variable");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.setEnabled(GrantOptions.grantedOption("Variables de Contratos"));
        btnNew.addClickListener(e -> {
            showDetails(new ContractVariable());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGriContractVariable());
        content.addClassName("grid-view");
        content.setHeightFull();
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGriContractVariable(){
        grid = new Grid<>();
        grid.setMultiSort(true);
        grid.setHeightFull();
        grid.setWidthFull();

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));

        grid.setDataProvider(dataProvider);

        grid.addColumn(ContractVariable::getTypeVariable).setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Tipo variable").setKey("typeVariable");
        grid.addColumn(ContractVariable::getName).setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Nombre variable").setKey("name");
        grid.addColumn(ContractVariable::getVariable).setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Contenido de la variable").setKey("variable");

        HeaderRow hr = grid.appendHeaderRow();

        typeVariableFilter = new ComboBox<>();
        typeVariableFilter.setItems(typeVariableConst);
        typeVariableFilter.setWidth("100%");
        typeVariableFilter.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("typeVariable")).setComponent(typeVariableFilter);

        nameFilter = new TextField();
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(e-> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("name")).setComponent(nameFilter);

        variableFilter = new TextField();
        variableFilter.setWidth("100%");
        variableFilter.setValueChangeMode(ValueChangeMode.EAGER);
        variableFilter.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("variable")).setComponent(variableFilter);

        return grid;
    }

    private void showDetails(ContractVariable contractVariable){
        current = contractVariable;
        detailsDrawerHeader.setTitle("Variable:" + contractVariable.getName());
        detailsDrawer.setContent(createDetails(contractVariable));
        detailsDrawer.show();
        binder.readBean(current);
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if (current !=null && binder.writeBeanIfValid(current)){
                try {
                    ContractVariable result = (ContractVariable) restTemplate.add(current);
                    if(current.getId()==null){
                        contractVariableList.add(result);
                        grid.getDataProvider().refreshAll();
                    }else{
                        grid.getDataProvider().refreshItem(current);
                    }
                    detailsDrawer.hide();
                } catch (HttpStatusCodeException ex) {
                    UIUtils.showNotification("Error: " +ex.getMessage());
                    ex.printStackTrace();
                } catch (JsonProcessingException ex) {
                    UIUtils.showNotification("Error: " +ex.getMessage());
                    ex.printStackTrace();
                }
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

    private FormLayout createDetails(ContractVariable contractVariable){
        ComboBox<String> typeVariable = new ComboBox<>();
        typeVariable.setWidth("100%");
        typeVariable.setItems(typeVariableConst);
        typeVariable.setRequiredIndicatorVisible(true);

        TextField name = new TextField();
        name.setWidth("100%");
        name.setRequiredIndicatorVisible(true);
        name.setRequired(true);

        TextArea variable = new TextArea();
        variable.setWidth("100%");
        variable.setRequiredIndicatorVisible(true);
        variable.setRequired(true);

        binder = new BeanValidationBinder<>(ContractVariable.class);
        binder.forField(typeVariable).asRequired("Tipo de variable es requerido")
                .bind(ContractVariable::getTypeVariable,ContractVariable::setTypeVariable);
        binder.forField(name).asRequired("Nombre variable es requerido")
                .bind(ContractVariable::getName,ContractVariable::setName);
        binder.forField(variable).asRequired("Contenido de la variable es requerido")
                .bind(ContractVariable::getVariable,ContractVariable::setVariable);

        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Variables de Contratos"));
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));

        FormLayout.FormItem typeVariableItem =  form.addFormItem(typeVariable,"Tipo variable");
        FormLayout.FormItem nameItem =  form.addFormItem(name,"Nombre variable");
        FormLayout.FormItem variableItem = form.addFormItem(variable,"Contenido variable");
        UIUtils.setColSpan(2,typeVariableItem);
        UIUtils.setColSpan(2,nameItem);
        UIUtils.setColSpan(2,variableItem);
        return form;
    }

    private void applyFilter(ListDataProvider<ContractVariable> dataProvider){
        dataProvider.clearFilters();
        if(typeVariableFilter.getValue()!=null){
            dataProvider.addFilter(contractVariable -> Objects.equals(typeVariableFilter.getValue(),contractVariable.getTypeVariable()));
        }
        if(!nameFilter.getValue().trim().equals("")){
            dataProvider.addFilter(contractVariable -> StringUtils.containsIgnoreCase(contractVariable.getName(),nameFilter.getValue()));
        }
        if(!variableFilter.getValue().trim().equals("")){
            dataProvider.addFilter(contractVariable -> StringUtils.containsIgnoreCase(contractVariable.getVariable(),variableFilter.getValue()));
        }
    }

}
