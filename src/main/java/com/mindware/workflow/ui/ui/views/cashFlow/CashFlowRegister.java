package com.mindware.workflow.ui.ui.views.cashFlow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.cashFlow.CashFlow;
import com.mindware.workflow.ui.backend.entity.cashFlow.FlowItem;
import com.mindware.workflow.ui.backend.entity.creditRequest.PaymentPlan;
import com.mindware.workflow.ui.backend.entity.patrimonialStatement.PatrimonialStatement;
import com.mindware.workflow.ui.backend.entity.rol.Option;
import com.mindware.workflow.ui.backend.rest.cashFlow.CashFlowRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementRestTemplate;
import com.mindware.workflow.ui.backend.rest.paymentPlan.PaymentPlanRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;

import java.util.*;

@Route(value = "register-cashflow",layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Registro Flujo de Caja")
public class CashFlowRegister extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {
    private Map<String, List<String>> param;
    private PaymentPlanRestTemplate paymentPlanRestTemplate = new PaymentPlanRestTemplate();
    private PatrimonialStatementRestTemplate patrimonialStatementRestTemplate = new PatrimonialStatementRestTemplate();
    private CashFlowRestTemplate cashFlowRestTemplate = new CashFlowRestTemplate();
    private List<PaymentPlan> paymentPlanList;
    private List<PatrimonialStatement> patrimonialStatementList;
    private BeanValidationBinder<CashFlow> binder;
    private CashFlow cashFlow;

    private Integer numberRequest;
    private UUID idCreditRequestApplicant;


    private  List<FlowItem> cashFlowItems;
    private Grid<String[]> grid;
    private TextArea description;

    private FlexBoxLayout contentGrid;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter detailsDrawerFooter;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {

        Location location = beforeEvent.getLocation();
        QueryParameters qp =  location.getQueryParameters();
        param = qp.getParameters();
        numberRequest = Integer.parseInt(param.get("number-request").get(0));
        idCreditRequestApplicant = UUID.fromString(param.get("id-credit-request-applicant").get(0));

        paymentPlanList = paymentPlanRestTemplate.getByNumberRequest(numberRequest);
        patrimonialStatementList = patrimonialStatementRestTemplate.getByIdCreditRequestApplicant(idCreditRequestApplicant);
        cashFlow = cashFlowRestTemplate.getByNumberRequest(numberRequest);
        if(cashFlow == null) {
            cashFlow = new CashFlow();

        }else{
            ObjectMapper mapper = new ObjectMapper();
            try {
                cashFlowItems = Arrays.asList(mapper.readValue(cashFlow.getItems(),FlowItem[].class));

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        cashFlowItems = cashFlowRestTemplate.getGenerateCashFlow(numberRequest,idCreditRequestApplicant);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle("Flujo de Caja- "+ param.get("full-name").get(0));
        contentGrid = (FlexBoxLayout) createContent(createLayoutCashFlow());


        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Flujo de Caja"));
        footer.addSaveListener(e ->{
            ObjectMapper mapper = new ObjectMapper();
            try {
                String items = mapper.writeValueAsString(cashFlowItems);
                cashFlow.setItems(items);
                cashFlow.setDescription(description.getValue());
                cashFlow.setNumberRequest(numberRequest);
                binder.writeBeanIfValid(cashFlow);
                cashFlowRestTemplate.add(cashFlow);
                UIUtils.showNotification("Flujo guardado");

            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        });

        footer.addCancelListener(event -> UI.getCurrent().navigate("cashFlowCreditRequestApplicantView"));
        setViewHeader(topBar());
        setViewContent(contentGrid);
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        setViewFooter(footer);
        binder.readBean(cashFlow);
    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e -> UI.getCurrent().navigate(CashFlowView.class));



        return appBar;
    }

    private HorizontalLayout topBar(){
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setWidth("100%");
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(e -> {
            Optional<CashFlow> cashFlow = Optional.ofNullable(cashFlowRestTemplate.getByNumberRequest(Integer.parseInt(param.get("number-request").get(0))));
            if(!cashFlow.isPresent()){
                UIUtils.showNotification("Guarde el flujo antes de poder imprimirlo");
            }else {

                Map<String, List<String>> paramCashFlow = new HashMap<>();
                List<String> origin = new ArrayList<>();
                origin.add("cashFlow");
                List<String> path = new ArrayList<>();
                path.add("register-cashflow");

                List<String> titleReport = new ArrayList<>();
                titleReport.add("Flujo de Caja Mensual ");
                paramCashFlow.put("path", path);
                paramCashFlow.put("title", titleReport);
                paramCashFlow.put("origin", origin);
                paramCashFlow.put("number-request", param.get("number-request"));
                paramCashFlow.put("full-name", param.get("full-name"));
                paramCashFlow.put("id-credit-request-applicant", param.get("id-credit-request-applicant"));

                QueryParameters qp = new QueryParameters(paramCashFlow);
                UI.getCurrent().navigate("report-preview", qp);
            }

        });

        Button btnCreate = new Button("Actualizar");
        btnCreate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreate.addClickListener(event -> {
            cashFlowItems = cashFlowRestTemplate.getGenerateCashFlow(numberRequest,idCreditRequestApplicant);
            createGrid();
        });
        layout.add(btnPrint);

        return layout;
    }

    private Component createContent(DetailsDrawer component) {
        FlexBoxLayout content = new FlexBoxLayout(component);
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

        return detailsDrawer;
    }

    private void createGrid(){

        grid = new Grid<>();
        grid.removeAllColumns();
        grid.setHeightFull();
        grid.setWidthFull();

        FlowItem flowItem = cashFlowItems.get(0);
        String[] header = flowItem.getItem();
        for(int i=0;i<header.length;i++){
            int index = i;
            grid.addColumn(lineData -> {
                return lineData[index];
            }).setHeader(header[index]).setResizable(true).setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        }
        List<String[]> records = new ArrayList<>();
        cashFlowItems.sort(Comparator.comparing(FlowItem::getOrder));
        for(FlowItem f:cashFlowItems){
            if(!f.getOrder().equals(0))
                records.add(f.getItem());
        }
        grid.setItems(records);
    }

    private DetailsDrawer createLayoutCashFlow() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        binder = new BeanValidationBinder<>(CashFlow.class);

        createGrid();

        description = new TextArea("Supuestos Flujo de Caja");
        description.setWidthFull();
        description.setHeight("20%");
        binder.forField(description).bind(CashFlow::getDescription,CashFlow::setDescription);


        layout.add(grid,description);
        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeightFull();
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;
    }


}
