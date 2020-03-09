package com.mindware.workflow.ui.ui.views.cashFlow;

import com.mindware.workflow.ui.backend.entity.cashFlow.CashFlowCreditRequestApplicantDto;
import com.mindware.workflow.ui.backend.entity.legal.dto.LegalInformationCreditRequestDto;
import com.mindware.workflow.ui.backend.rest.cashFlow.CashFlowCreditRequestApplicantDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.cashFlow.CashFlowRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "cashFlowCreditRequestApplicantView", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista de solicitudes relacionadas a flujos de caja")
public class CashFlowView extends SplitViewFrame  {
    private Grid<CashFlowCreditRequestApplicantDto> grid;
    private CashFlowCreditRequestApplicantDtoDataProvider dataProvider;
    private List<CashFlowCreditRequestApplicantDto> cashFlowCreditRequestApplicantDtoList;
    private CashFlowCreditRequestApplicantDtoRestTemplate restTemplate = new CashFlowCreditRequestApplicantDtoRestTemplate();

    private TextField filterText;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getCashFlowCreditRequestApplicantDtoList();
        setViewHeader(createTopBar());
        setViewContent(createContent());
    }

    private void getCashFlowCreditRequestApplicantDtoList(){
        //TODO USAR EL ROL DEL USUARIO PARA SABER QUE DATOS RECUPERAR, TODOS, DEL OPERACIONES DEL USUARIO O CIUDAD
        cashFlowCreditRequestApplicantDtoList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new CashFlowCreditRequestApplicantDtoDataProvider(cashFlowCreditRequestApplicantDtoList);
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por: Nombre, Nro solicitud, Ciudad u Oficial ");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.setSpacing(true);
        return topLayout;

    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGrid() {
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(event -> {
            if(event.getFirstSelectedItem().get().isHasPatrimonialStatement() &&
            event.getFirstSelectedItem().get().isHasPaymentPlan()) {
                viewRegister(event.getFirstSelectedItem().get());
            }else{
                UIUtils.showNotification("Complete el registro de la declaracion patrimoniol o Plan de Pagos");
            }
        });

        grid.addColumn(CashFlowCreditRequestApplicantDto::getNumberRequest)
                .setWidth(UIUtils.COLUMN_WIDTH_S)
                .setFlexGrow(0).setSortable(true).setResizable(true)
                .setHeader("# Solicitud");
        grid.addColumn(new ComponentRenderer<>(this::createNameInfo))
                .setWidth(UIUtils.COLUMN_WIDTH_XL)
                .setFlexGrow(0).setSortable(true).setResizable(true)
                .setHeader("Solicitante");
        grid.addColumn(CashFlowCreditRequestApplicantDto::getCurrency)
                .setWidth(UIUtils.COLUMN_WIDTH_S)
                .setFlexGrow(0).setSortable(true).setResizable(true)
                .setHeader("Moneda");
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Monto")
                .setSortable(true).setFlexGrow(0).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_S);
        grid.addColumn(CashFlowCreditRequestApplicantDto::getCity).setHeader("Ciudad")
                .setSortable(true).setFlexGrow(0).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M);
        grid.addColumn(CashFlowCreditRequestApplicantDto::getOfficial).setHeader("Oficial")
                .setSortable(true).setFlexGrow(0).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M);
        grid.addColumn(new ComponentRenderer<>(this::createHasPaymentPLan))
                .setFlexGrow(0).setHeader("Plan de Pagos?").setWidth(UIUtils.COLUMN_WIDTH_S);
        grid.addColumn(new ComponentRenderer<>(this::createHasPatrimonialStatement))
                .setFlexGrow(0).setHeader("Declaracion Patrimonial?").setWidth(UIUtils.COLUMN_WIDTH_S);

        return grid;
    }

    private Component createNameInfo(CashFlowCreditRequestApplicantDto cashFlowCreditRequestApplicantDto){
        ListItem item = new ListItem(
                UIUtils.createInitials(cashFlowCreditRequestApplicantDto.getInitials()),cashFlowCreditRequestApplicantDto.getFullName()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createAmount(CashFlowCreditRequestApplicantDto cashFlowCreditRequestApplicantDto){
        Double amount = cashFlowCreditRequestApplicantDto.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private Component createHasPaymentPLan(CashFlowCreditRequestApplicantDto cashFlowCreditRequestApplicantDto){
        Icon icon;
        if(cashFlowCreditRequestApplicantDto.isHasPaymentPlan()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createHasPatrimonialStatement(CashFlowCreditRequestApplicantDto cashFlowCreditRequestApplicantDto){
        Icon icon;
        if(cashFlowCreditRequestApplicantDto.isHasPatrimonialStatement()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private void viewRegister(CashFlowCreditRequestApplicantDto cashFlowCreditRequestApplicantDto){
        Map<String,List<String>> param = new HashMap<>();
        List<String> numberRequest = new ArrayList<>();
        List<String> idCreditRequestApplicant = new ArrayList<>();
        List<String> fullName = new ArrayList<>();

        numberRequest.add(cashFlowCreditRequestApplicantDto.getNumberRequest().toString());
        idCreditRequestApplicant.add(cashFlowCreditRequestApplicantDto.getIdCreditRequestApplicant().toString());
        fullName.add(cashFlowCreditRequestApplicantDto.getFullName());

        param.put("number-request",numberRequest);
        param.put("id-credit-request-applicant",idCreditRequestApplicant);
        param.put("full-name",fullName);

        QueryParameters qp = new QueryParameters(param);
        UI.getCurrent().navigate("register-cashflow",qp);
    }

}
