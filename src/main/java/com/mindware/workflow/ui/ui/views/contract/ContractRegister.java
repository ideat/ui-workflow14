package com.mindware.workflow.ui.ui.views.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindware.workflow.ui.backend.entity.contract.Contract;
import com.mindware.workflow.ui.backend.entity.contract.ContractVariable;
import com.mindware.workflow.ui.backend.entity.contract.TemplateContract;
import com.mindware.workflow.ui.backend.rest.contract.ContractRestTemplate;
import com.mindware.workflow.ui.backend.rest.contract.ContractVariableRestTemplate;
import com.mindware.workflow.ui.backend.rest.contract.TemplateContractRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Right;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.creditResolution.CreditResolutionCreditRequestDtoView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Route(value = "contract-register", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Registro de contrato a una solicitud")
public class ContractRegister extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {

    private Map<String, List<String>> param;
    private ContractRestTemplate restTemplate;
    private TemplateContractRestTemplate templateContractRestTemplate;
    private ContractVariableRestTemplate contractVariableRestTemplate;

    private Contract contract;
    private TemplateContract templateContract;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private ListDataProvider<TemplateContract> templateContractListDataProvider;

    private BeanValidationBinder<Contract> binder;

    private FlexBoxLayout contentContract;

    private TextField nameContractFilter;
    private TextField descriptionFilter;
    private TextField fileTemplateContract;
    private Integer numberRequest;
    private boolean isActiveCreditRequest;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        templateContractRestTemplate = new TemplateContractRestTemplate();
        restTemplate = new ContractRestTemplate();

        contractVariableRestTemplate = new ContractVariableRestTemplate();

        Location location = beforeEvent.getLocation();
        QueryParameters qp =  location.getQueryParameters();
        param = qp.getParameters();
        numberRequest = Integer.parseInt(param.get("number-request").get(0));
        isActiveCreditRequest = UtilValues.isActiveCreditRequest(numberRequest);

        contract = restTemplate.getByNumberRequest(numberRequest);
//        if(contract.getId()==null){
//            setViewContent(createContent());
//        }else {

            setViewContent(createContent());
            setViewDetails(createDetailDrawer());
            setViewDetailsPosition(Position.BOTTOM);
            setViewFooter(footer);
//        }
        binder.readBean(contract);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initBar();
        appBar.setTitle("Solicitud credito: " + param.get("number-request").get(0));

        UI.getCurrent().getPage().setTitle(contract.getFileName()==null?"Nuevo contrato":contract.getFileName());

    }

    private AppBar initBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(ContractCreditRequestDtoView.class);
        });

        return appBar;
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(createContract(contract));
        content.setFlexDirection(FlexDirection.ROW);
        content.setMargin(Vertical.AUTO,Vertical.RESPONSIVE_L);
        content.setSizeFull();
        return content;
    }

    private DetailsDrawer createDetailDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);
//        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private DetailsDrawer createContract(Contract contract){
        DatePicker dateContract = new DatePicker();
        dateContract.setWidth("100%");
        dateContract.setRequired(true);
        dateContract.setRequiredIndicatorVisible(true);

        ComboBox<String> denominationDebtor = new ComboBox<>();
        denominationDebtor.setWidthFull();
        ContractVariable contractVariable = contractVariableRestTemplate.getByName("@denominacionDeudor");
        String[] contractVariableList = contractVariable.getVariable().split(",");
        denominationDebtor.setItems(contractVariableList);

        ComboBox<String> denominationGuarantor = new ComboBox<>();
        denominationGuarantor.setWidth("100%");
        contractVariable = contractVariableRestTemplate.getByName("@denominacionGarante");
        contractVariableList = contractVariable.getVariable().split(",");
        denominationGuarantor.setItems(contractVariableList);

        ComboBox<String> denominationCreditor = new ComboBox<>();
        denominationCreditor.setWidthFull();
        contractVariable = contractVariableRestTemplate.getByName("@denominacionAcreedor");
        contractVariableList = contractVariable.getVariable().split(",");
        denominationCreditor.setItems(contractVariableList);

        fileTemplateContract = new TextField();
        fileTemplateContract.setRequired(true);
        fileTemplateContract.setRequiredIndicatorVisible(true);
        fileTemplateContract.setReadOnly(true);

        TextArea description = new TextArea();
        description.setWidth("100%");


        Button btnSearch = new Button();
        btnSearch.addThemeVariants(ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_PRIMARY);
        btnSearch.setIcon(VaadinIcon.SEARCH.create());
        btnSearch.addClickListener(event -> showSearch());

        FlexBoxLayout searchForm = new FlexBoxLayout(fileTemplateContract,btnSearch);
        searchForm.setFlexGrow(1,fileTemplateContract);
        searchForm.setSpacing(Right.S);

        binder = new BeanValidationBinder<>(Contract.class);
        binder.forField(dateContract).asRequired("Fecha de contrato es requerida")
                .bind(Contract::getDateContract,Contract::setDateContract);
        binder.forField(fileTemplateContract).asRequired("Plantilla contrato es requerida")
                .bind(Contract::getFileName,Contract::setFileName);
        binder.forField(denominationCreditor).asRequired("Denominacion acreedor es requerida")
                .bind(Contract::getDenominationCreditor,Contract::setDenominationCreditor);
        binder.forField(denominationDebtor).asRequired("Denominacion deudor es requerida")
                .bind(Contract::getDenominationDebtor,Contract::setDenominationDebtor);
        binder.forField(denominationGuarantor).bind(Contract::getDenominationGuarantor,Contract::setDenominationGuarantor);
        binder.forField(description).bind(Contract::getDescription,Contract::setDescription);

        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Contratos") && isActiveCreditRequest);
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        formLayout.addFormItem(dateContract,"Fecha contrato");
        formLayout.addFormItem(denominationCreditor,"Denominacion acreedor(s)");
        formLayout.addFormItem(denominationDebtor,"Denominacion deudor(s)");
        formLayout.addFormItem(denominationGuarantor,"Denominacion garante(s)");
        FormLayout.FormItem searchFormItem = formLayout.addFormItem(searchForm,"Plantilla contrato");
        UIUtils.setColSpan(4,searchFormItem);


        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
           if(binder.writeBeanIfValid(contract)){
                contract.setNumberRequest(numberRequest);
                UI.getCurrent().navigate(ContractCreditRequestDtoView.class);
               try {
                   restTemplate.add(contract);
               } catch (JsonProcessingException ex) {
                   ex.printStackTrace();
               }
           }
        });

        footer.addCancelListener(e ->{
            UI.getCurrent().navigate(ContractCreditRequestDtoView.class);
        });

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(formLayout);
        detailsDrawer.setHeight("100%");
        detailsDrawer.setAlignItems(FlexComponent.Alignment.START);

        return detailsDrawer;
    }

    private void showSearch(){

        detailsDrawerHeader.setTitle("Seleccionar Contrato");
        detailsDrawer.setContent(searchTemplateContract());
        detailsDrawer.show();
    }

    private Grid searchTemplateContract(){
        List<TemplateContract> templateContractList = templateContractRestTemplate.getAllActive("SI");
        templateContractListDataProvider = new ListDataProvider<>(templateContractList);
        Grid<TemplateContract> gridTemplateContract = new Grid<>();
        gridTemplateContract.setSizeFull();
        gridTemplateContract.setDataProvider(templateContractListDataProvider);

        gridTemplateContract.addColumn(TemplateContract::getFileName).setFlexGrow(1)
                .setHeader("Contrato").setAutoWidth(true).setKey("name");
        gridTemplateContract.addColumn(TemplateContract::getDetail).setFlexGrow(1)
                .setHeader("Descripcion").setAutoWidth(true).setKey("description");
        gridTemplateContract.addColumn(new ComponentRenderer<>(this::createSelectionTemplate)).setAutoWidth(true);

        HeaderRow hr = gridTemplateContract.appendHeaderRow();

        nameContractFilter = new TextField();
        nameContractFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameContractFilter.setWidth("100%");
        nameContractFilter.addValueChangeListener(e -> applyFilter(templateContractListDataProvider));
        hr.getCell(gridTemplateContract.getColumnByKey("name")).setComponent(nameContractFilter);

        descriptionFilter = new TextField();
        descriptionFilter.setValueChangeMode(ValueChangeMode.EAGER);
        descriptionFilter.setWidth("100%");
        descriptionFilter.addValueChangeListener(e -> applyFilter(templateContractListDataProvider));
        hr.getCell(gridTemplateContract.getColumnByKey("description")).setComponent(descriptionFilter);

        return gridTemplateContract;
    }

    private Component createSelectionTemplate(TemplateContract templateContract){
        Button btnSelect = new Button();
        btnSelect.setIcon(VaadinIcon.CHEVRON_CIRCLE_UP.create());
        btnSelect.addClickListener(e->{

            contract.setFileName(templateContract.getFileName());
            contract.setPathTemplate(templateContract.getPathContract());
            fileTemplateContract.setReadOnly(false);
            fileTemplateContract.setValue(templateContract.getFileName());
            fileTemplateContract.setReadOnly(true);
            detailsDrawer.hide();
        });
        return btnSelect;
    }

    private void applyFilter(ListDataProvider<TemplateContract> dataProvider){
        dataProvider.clearFilters();
        if(!nameContractFilter.getValue().trim().equals("")){
            dataProvider.addFilter(templateContract1 -> StringUtils.containsIgnoreCase(templateContract1.getFileName(),nameContractFilter.getValue()));
        }
        if(!descriptionFilter.getValue().trim().equals("")){
            dataProvider.addFilter(templateContract1 -> StringUtils.containsIgnoreCase(templateContract1.getDetail(),descriptionFilter.getValue()));
        }
    }
}
