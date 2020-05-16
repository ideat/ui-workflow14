package com.mindware.workflow.ui.ui.views.patrimonialStatement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.patrimonialStatement.*;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "earning", layout = MainLayout.class)
@PageTitle("Ingresos")
public class EarningRegister extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {

    private Grid<SalesHistory> gridSalesHistory;
    private Grid<SalesProjection> gridProjectionSales;
    private Grid<SalesProjection> gridAverageSales;
    private Grid<ProductSalesBuys> gridProductSales;
    private Grid<ProductSalesBuys> gridProductBuys;
    private Grid<OperativeExpenses> gridOperativeExpenses;
    private BeanValidationBinder<ProductSalesBuys> binderProductSales;
    private BeanValidationBinder<CostProduct> binderCostProduct;
    private BeanValidationBinder<ProductionSalesInventory> binderProductSalesInventory;
    private BeanValidationBinder<OperativeExpenses> binderOperativeExpenses;
    private ListDataProvider<SalesHistory> dataProviderSalesHistory;
    private List<PatrimonialStatement> patrimonialStatementList;
    private PatrimonialStatement patrimonialStatementCommerce;
    private List<SalesHistory> salesHistoryList;
    private List<SalesProjection> salesProjectionList;
    private List<SalesProjection> averageList;
    private List<ProductSalesBuys> productSalesBuysList;
    private List<OperativeExpenses> operativeExpensesList;
    private List<CostProduct> costProductList;
    private List<ProductionSalesInventory> productionSalesInventoryList;

    private PatrimonialStatementRestTemplate restTemplate;

    private Map<String,List<String>> param = new HashMap<>();
    private LocalDate dateInit;
    private TextField concept;
    private Tabs pagedTabs;

    private List<String> listTabs;
    private ListDataProvider<SalesProjection> salesProjectionProvider;
    private ListDataProvider<SalesProjection> averageProjectionProvider;
    private ListDataProvider<ProductSalesBuys> productSalesBuysListProvider;
    private ListDataProvider<OperativeExpenses> operativeExpensesListProvider;
    private ListDataProvider<CostProduct> costProductListProvider;
    private ListDataProvider<ProductionSalesInventory> productionSalesInventoryDataProvider;
    private ProductSalesBuys currentProductSalesBuys;
    private OperativeExpenses currentOperativeOperativeExpenses;

    private FlexBoxLayout contentGridSales;
    private FlexBoxLayout contentVaeIndependent;
    private FlexBoxLayout contentAnalysisCostProduct;
    private FlexBoxLayout contentProductionInventory;
    private FlexBoxLayout contentProductionSales;

//    private DetailsDrawerFooter footerSalesVae;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;

    private HorizontalLayout horizontalLayoutEstimatedSales;
    private VerticalLayout verticalLayoutProductSales;
    private HorizontalLayout horizontalLayoutTopBar;

    private String typeFilterProduct = "VENTA";
    private List<String> itemsCostProducts;
    private ComboBox<String> cmbProduct;
    private NumberField txtPriceSaleProduct;

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);

        AppBar appBar = initAppBar();
        appBar.setTitle("VAE-"+param.get("activity").get(0));
        contentGridSales = (FlexBoxLayout) createContent(createLayoutGridSales());
        contentVaeIndependent = (FlexBoxLayout) createContent(createVaeIndependentOptions());
        contentAnalysisCostProduct = (FlexBoxLayout) createContent(createCostProductForm());
        contentProductionInventory = (FlexBoxLayout) createContent(createProductionInventory());
        contentProductionSales = (FlexBoxLayout) createContent(createProductionSalesInventory());

        contentGridSales.setVisible(true);
        contentVaeIndependent.setVisible(false);
        contentAnalysisCostProduct.setVisible(false);
        contentProductionInventory.setVisible(false);
        contentProductionSales.setVisible(false);

        setViewContent(contentGridSales,contentVaeIndependent,contentAnalysisCostProduct,contentProductionInventory,contentProductionSales);
        setViewDetails(createDetailDrawer());
        setViewDetailsPosition(Position.BOTTOM);

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        patrimonialStatementList = new ArrayList<>();
        Location location = beforeEvent.getLocation();
        QueryParameters qp =  location.getQueryParameters();
        param = qp.getParameters();
        restTemplate = new PatrimonialStatementRestTemplate();
        patrimonialStatementList = restTemplate.getByIdCreditRequestApplicantCategory(UUID.fromString(param.get("id-credit-request-applicant").get(0))
                , param.get("category").get(0));

        List<PatrimonialStatement> auxList = patrimonialStatementList.stream()
                .filter(p -> p.getCategory().equals(param.get("category").get(0))
                        && p.getFieldText1().equals(param.get("activity").get(0))
                ).collect(Collectors.toList());
        patrimonialStatementCommerce = auxList.get(0);
        String historySales = patrimonialStatementCommerce.getFieldText2();
        String projectionSales = patrimonialStatementCommerce.getFieldText3();
        String productSalesAndBuys = patrimonialStatementCommerce.getFieldText4();
        String operativeExpenses = patrimonialStatementCommerce.getFieldText5();
        String costProduct = patrimonialStatementCommerce.getFieldText6();
        String productionInventory = patrimonialStatementCommerce.getFieldText7();

        ObjectMapper mapper = new ObjectMapper();
        if(historySales==null ||  historySales.equals("") || historySales.equals("[]")){
            salesHistoryList = new ArrayList<>();
        }else{
            try {
                salesHistoryList = mapper.readValue(historySales, new TypeReference<List<SalesHistory>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(projectionSales==null || projectionSales.equals("") || projectionSales.equals("[]")){
            salesProjectionList = new ArrayList<>();
        }else{
            try {
                salesProjectionList = mapper.readValue(projectionSales,new TypeReference<List<SalesProjection>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        averageList = salesProjectionList;

        if (productSalesAndBuys==null || productSalesAndBuys.equals("") || productSalesAndBuys.equals("[]") ){
            productSalesBuysList = new ArrayList<>();
        }else{
            try {
                productSalesBuysList = mapper.readValue(productSalesAndBuys, new TypeReference<List<ProductSalesBuys>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(operativeExpenses==null || operativeExpenses.equals("") || operativeExpenses.equals("[]")) {
            operativeExpensesList = new ArrayList<>();
        }else {
            try {
                operativeExpensesList = mapper.readValue(operativeExpenses, new TypeReference<List<OperativeExpenses>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(costProduct==null || costProduct.equals("") || costProduct.equals("[]")){
            costProductList = new ArrayList<>();
        }else{
            try {
                costProductList = mapper.readValue(costProduct,new TypeReference<List<CostProduct>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(productionInventory==null || productionInventory.equals("") || productionInventory.equals("[]")){
            productionSalesInventoryList = new ArrayList<>();
        }else {
            try {
                productionSalesInventoryList = mapper.readValue(productionInventory, new TypeReference<List<ProductionSalesInventory>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private AppBar initAppBar(){
        MainLayout.get().getAppBar().reset();
        AppBar appBar = MainLayout.get().getAppBar();

        appBar.addTab("Tabla Ventas").isSelected();
        appBar.addTab("VAE-Independiente");
        appBar.addTab("Costo por Producto");
        appBar.addTab("Inventario Produccion");
        appBar.addTab("Inventario Venta");
//        appBar.addTab("Balance General");
        appBar.centerTabs();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        listTabs = new ArrayList<>();
        listTabs.add("Tabla Ventas");
        listTabs.add("VAE-Independiente");
        listTabs.add("Costo por Producto");
        listTabs.add("Inventario Produccion");
        listTabs.add("Inventario Venta");
//        listTabs.add("Balance General");

        appBar.getContextIcon().addClickListener(e -> {

        });

        appBar.addTabSelectionListener(e -> {
            if (e.getSelectedTab()!=null) {
                setComponentVisible(e.getSelectedTab());
            }
        });
        return appBar;
    }

    private void setComponentVisible(Tab selectedTab){
        if(listTabs.contains(selectedTab.getLabel())){
            if(selectedTab.getLabel().equals("Tabla Ventas")){
                contentGridSales.setVisible(true);
                contentVaeIndependent.setVisible(false);
                contentAnalysisCostProduct.setVisible(false);
                contentProductionInventory.setVisible(false);
                contentProductionSales.setVisible(false);

            }else if (selectedTab.getLabel().equals("VAE-Independiente")){
                List<SalesProjection> listFilter = salesProjectionList.stream()
                        .filter(p -> p.getCategorySale().equals("DIARIA")
                        ).collect(Collectors.toList());
                horizontalLayoutEstimatedSales.setVisible(true);
                horizontalLayoutTopBar.setVisible(false);
                verticalLayoutProductSales.setVisible(false);
                if (listFilter.size()==0) {
                    List<SalesProjection> newList =  createInitDataSales();
                    salesProjectionList = Stream.concat(salesProjectionList.stream(), newList.stream())
                            .collect(Collectors.toList());
                    averageList = salesProjectionList;
                    salesProjectionProvider = new ListDataProvider<>(salesProjectionList);
                    filterSalesProjectionProvider("DIARIA");
                    gridProjectionSales.setDataProvider(salesProjectionProvider);
                    averageProjectionProvider = new ListDataProvider<>(averageList);
                    filterAverageProjectionProvider("DIARIA");
                    gridAverageSales.setDataProvider(averageProjectionProvider);

                }else{
                    filterSalesProjectionProvider("DIARIA");
                    filterAverageProjectionProvider("DIARIA");
                }

                if(productSalesBuysList!=null){
                    productSalesBuysListProvider = new ListDataProvider<>(productSalesBuysList);
                    gridProductSales.setDataProvider(productSalesBuysListProvider);
                    gridProductBuys.setDataProvider(productSalesBuysListProvider);
                    filterProductSalesProvider(typeFilterProduct);
                }
                operativeExpensesListProvider = new ListDataProvider<>(operativeExpensesList);
                gridOperativeExpenses.setDataProvider(operativeExpensesListProvider);

                contentGridSales.setVisible(false);
                contentAnalysisCostProduct.setVisible(false);
                contentVaeIndependent.setVisible(true);
                contentProductionInventory.setVisible(false);
                contentProductionSales.setVisible(false);
            } else if(selectedTab.getLabel().equals("Costo por Producto")){
                contentGridSales.setVisible(false);
                contentVaeIndependent.setVisible(false);
                contentAnalysisCostProduct.setVisible(true);
                contentProductionInventory.setVisible(false);
                contentProductionSales.setVisible(false);
            }else if(selectedTab.getLabel().equals("Inventario Produccion")){
                contentGridSales.setVisible(false);
                contentVaeIndependent.setVisible(false);
                contentAnalysisCostProduct.setVisible(false);
                contentProductionInventory.setVisible(true);
                contentProductionSales.setVisible(false);
            }else if(selectedTab.getLabel().equals("Inventario Venta")){
                contentGridSales.setVisible(false);
                contentVaeIndependent.setVisible(false);
                contentAnalysisCostProduct.setVisible(false);
                contentProductionInventory.setVisible(false);
                contentProductionSales.setVisible(true);
                filterProductSalesInventory("venta");
            }

        }
    }

    private DetailsDrawer createDetailDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        return detailsDrawer;
    }

    //PRODUCTION SALES
    private DetailsDrawer createProductionSalesInventory(){
        HorizontalLayout topBar = new HorizontalLayout();
        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.addClickListener(click -> {
            ProductionSalesInventory productionSalesInventory = new ProductionSalesInventory();
            productionSalesInventory.setPriceSale(0.0);
            productionSalesInventory.setMb(0.0);
            productionSalesInventory.setAdvancePercentage(0.0);
            productionSalesInventory.setInventoryValueMb(0.0);
            productionSalesInventory.setInventoryValueFinished(0.0);
            productionSalesInventory.setFactor(0.0);
            productionSalesInventory.setTypeInventory("venta");

            showProductSalesInventory(productionSalesInventory);
        });
        btnNew.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));

        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(e -> {
            Map<String, List<String>> paramVae = new HashMap<>();
            List<String> origin = new ArrayList<>();
            origin.add("inventorySales");
            List<String> path = new ArrayList<>();
            path.add("earning");
            List<String> idPatrimonialStatement = new ArrayList<>();
            idPatrimonialStatement.add(patrimonialStatementCommerce.getId().toString());
            List<String> titleReport = new ArrayList<>();
            titleReport.add("Hoja Inventario Ventas");
            List<String> product = new ArrayList<>();

            paramVae.put("title", titleReport);
            paramVae.put("origin",origin);
            paramVae.put("path",path);
            paramVae.put("id-patrimonial-statement",idPatrimonialStatement);
            paramVae.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramVae.put("element",param.get("element"));
            paramVae.put("id-applicant",param.get("id-applicant"));
            paramVae.put("category",param.get("category"));
            paramVae.put("activity",param.get("activity"));

            QueryParameters qp = new QueryParameters(paramVae);
            UI.getCurrent().navigate("report-preview",qp);
        });


        topBar.add(btnNew,btnPrint);
        Grid<ProductionSalesInventory> gridSalesInventory = createGridProductionInventory("venta");
        gridSalesInventory.setSizeFull();

        gridSalesInventory.addSelectionListener(e ->{
           e.getFirstSelectedItem().ifPresent(this::showProductSalesInventory);
           binderProductSalesInventory.readBean(e.getFirstSelectedItem().get());
        });

//        VerticalLayout layoutProductionSales = gridLayout(gridSalesInventory,"venta");
//        productionSalesInventoryDataProvider = new ListDataProvider<>(productionSalesInventoryList);
        gridSalesInventory.setDataProvider(productionSalesInventoryDataProvider);

//        filterProductSalesInventory("venta");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.add(topBar,gridSalesInventory);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;

    }

    //PRODUCTION INVENTORY
    private DetailsDrawer createProductionInventory(){
        HorizontalLayout topBar = new HorizontalLayout();
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(e -> {
            Map<String, List<String>> paramVae = new HashMap<>();
            List<String> origin = new ArrayList<>();
            origin.add("inventoryProduction");
            List<String> path = new ArrayList<>();
            path.add("earning");
            List<String> idPatrimonialStatement = new ArrayList<>();
            idPatrimonialStatement.add(patrimonialStatementCommerce.getId().toString());
            List<String> titleReport = new ArrayList<>();
            titleReport.add("Hoja Inventario Produccion");
            List<String> product = new ArrayList<>();

            paramVae.put("title", titleReport);
            paramVae.put("origin",origin);
            paramVae.put("path",path);
            paramVae.put("id-patrimonial-statement",idPatrimonialStatement);
            paramVae.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramVae.put("element",param.get("element"));
            paramVae.put("id-applicant",param.get("id-applicant"));
            paramVae.put("category",param.get("category"));
            paramVae.put("activity",param.get("activity"));

            QueryParameters qp = new QueryParameters(paramVae);
            UI.getCurrent().navigate("report-preview",qp);
        });
        topBar.add(btnPrint);

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.setSizeFull();

        Grid<ProductionSalesInventory> gridRawMaterial = createGridProductionInventory("insumo");
        gridRawMaterial.setHeight("300px");
        Grid<ProductionSalesInventory> gridProcess = createGridProductionInventory("proceso");
        gridProcess.setHeight("300px");
        Grid<ProductionSalesInventory> gridFinished = createGridProductionInventory("terminado");
        gridFinished.setHeight("300px");

        gridRawMaterial.addSelectionListener(e ->{
            if(!e.getFirstSelectedItem().equals(Optional.empty())) {
                e.getFirstSelectedItem().ifPresent(this::showProductSalesInventory);
                binderProductSalesInventory.readBean(e.getFirstSelectedItem().get());
            }

        });

        gridProcess.addSelectionListener(e ->{
           e.getFirstSelectedItem().ifPresent(this::showProductSalesInventory);
           binderProductSalesInventory.readBean(e.getFirstSelectedItem().get());
        });

        gridFinished.addSelectionListener(e -> {
           e.getFirstSelectedItem().ifPresent(this::showProductSalesInventory);
           binderProductSalesInventory.readBean(e.getFirstSelectedItem().get());
        });


        VerticalLayout layoutRawMaterial = gridLayout(gridRawMaterial,"insumo");
        VerticalLayout layoutProcess = gridLayout(gridProcess,"proceso");
        VerticalLayout layoutFinished = gridLayout(gridFinished,"terminado");

        productionSalesInventoryDataProvider = new ListDataProvider<>(productionSalesInventoryList);
        gridRawMaterial.setDataProvider(productionSalesInventoryDataProvider);
        gridProcess.setDataProvider(productionSalesInventoryDataProvider);
        gridFinished.setDataProvider(productionSalesInventoryDataProvider);

        accordion.addOpenedChangeListener(e ->{
           if(e.getOpenedIndex().isPresent()) {
               if (e.getOpenedIndex().getAsInt() == 0) {
                   filterProductSalesInventory("insumo");
               } else if (e.getOpenedIndex().getAsInt() == 1) {
                   filterProductSalesInventory("proceso");
               } else if (e.getOpenedIndex().getAsInt() == 2) {
                   filterProductSalesInventory("terminado");
               }
           }
        });

        accordion.add("Datos Insumos", layoutRawMaterial);
        accordion.add("En Proceso",layoutProcess);
        accordion.add("Terminados",layoutFinished);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        layout.add(topBar,accordion);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;
    }

    private void filterProductSalesInventory(String filter){
        if(productionSalesInventoryDataProvider.getItems().size()>0){
            productionSalesInventoryDataProvider.setFilterByValue(ProductionSalesInventory::getTypeInventory,filter);
        }
    }

    private VerticalLayout gridLayout(Grid<ProductionSalesInventory> grid, String typeProduction){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btnNew.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));
        btnNew.addClickListener(click -> {
           ProductionSalesInventory productionSalesInventory = new ProductionSalesInventory();
           productionSalesInventory.setPriceSale(0.0);
           productionSalesInventory.setMb(0.0);
           productionSalesInventory.setAdvancePercentage(0.0);
           productionSalesInventory.setInventoryValueMb(0.0);
           productionSalesInventory.setInventoryValueFinished(0.0);
           productionSalesInventory.setFactor(0.0);
           if(typeProduction.equals("insumo")){
               productionSalesInventory.setTypeInventory("insumo");
           }else if(typeProduction.equals("proceso")){
               productionSalesInventory.setTypeInventory("proceso");
           }else if (typeProduction.equals("terminado")){
               productionSalesInventory.setTypeInventory("terminado");
           }else if(typeProduction.equals("venta")){
               productionSalesInventory.setTypeInventory("venta");
           }
           showProductSalesInventory(productionSalesInventory);
        });
        if(!typeProduction.equals("venta")) {
            layout.add(btnNew, grid);
        }else{
            layout.add(grid);
        }
        return layout;
    }

    private void showProductSalesInventory(ProductionSalesInventory productionSalesInventory){
        detailsDrawerHeader.setTitle(productionSalesInventory.getRawMaterial());
        detailsDrawer.setContent(createProductionSalesInventory(productionSalesInventory));
        binderProductSalesInventory.readBean(productionSalesInventory);
        detailsDrawer.show();
    }

    private DetailsDrawer createProductionSalesInventory(ProductionSalesInventory productionSalesInventory){
        NumberField txtQuantity = new NumberField();
        txtQuantity.setWidth("100%");
        txtQuantity.setRequiredIndicatorVisible(true);

        TextField txtUnity = new TextField();
        txtUnity.setWidth("100%");
        txtUnity.setRequiredIndicatorVisible(true);
        txtUnity.setRequired(true);

        TextField txtRawMaterial = new TextField();
        txtRawMaterial.setWidth("100%");
        txtRawMaterial.setRequiredIndicatorVisible(true);
        txtRawMaterial.setRequired(true);

        NumberField txtPriceCost = new NumberField();
        txtPriceCost.setWidth("100%");
        txtPriceCost.setRequiredIndicatorVisible(true);

        NumberField txtPriceSale = new NumberField();
        txtPriceSale.setWidth("100%");
        txtPriceSale.setRequiredIndicatorVisible(true);

        NumberField txtAdvancePercentage = new NumberField();
        txtAdvancePercentage.setWidth("100%");
        txtAdvancePercentage.setRequiredIndicatorVisible(true);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderProductSalesInventory = new BeanValidationBinder<>(ProductionSalesInventory.class);

        binderProductSalesInventory.forField(txtQuantity).asRequired("Cantidad es requerida")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .withValidator(value -> value.intValue()>0,"Cantidad debe ser mayor a 0")
                .bind(ProductionSalesInventory::getQuantity,ProductionSalesInventory::setQuantity);
        binderProductSalesInventory.forField(txtUnity).asRequired("Unidad es requerida")
                .bind(ProductionSalesInventory::getUnity,ProductionSalesInventory::setUnity);

        formLayout.addFormItem(txtQuantity,"Cantidad");
        formLayout.addFormItem(txtUnity,"Unidad");

        if(productionSalesInventory.getTypeInventory().equals("insumo")){
            binderProductSalesInventory.forField(txtRawMaterial).asRequired("Materia prima es requerida")
                    .bind(ProductionSalesInventory::getRawMaterial,ProductionSalesInventory::setRawMaterial);
            binderProductSalesInventory.forField(txtPriceCost).asRequired("Precio Compra es requerido")
                    .withValidator(value -> value.doubleValue()>0,"Precio compra debe ser mayor a cero")
                    .bind(ProductionSalesInventory::getPriceCost,ProductionSalesInventory::setPriceCost);
            formLayout.addFormItem(txtRawMaterial,"Materia prima");
            formLayout.addFormItem(txtPriceCost,"Precio compra");
        }else if (productionSalesInventory.getTypeInventory().equals("proceso")){
            binderProductSalesInventory.forField(txtRawMaterial).asRequired("Producto es requerido")
                    .bind(ProductionSalesInventory::getRawMaterial,ProductionSalesInventory::setRawMaterial);
            binderProductSalesInventory.forField(txtPriceCost).asRequired("Precio costo es requerido")
                    .withValidator(value -> value.doubleValue()>0,"Precio costo  debe ser mayor a cero")
                    .bind(ProductionSalesInventory::getPriceCost,ProductionSalesInventory::setPriceCost);
            binderProductSalesInventory.forField(txtAdvancePercentage).asRequired("Porcentaje avance es requerido")
                    .withValidator(value -> value.doubleValue()>0, "Porcentaje avance debe ser mayor a 0")
                    .bind(ProductionSalesInventory::getAdvancePercentage,ProductionSalesInventory::setAdvancePercentage);
            formLayout.addFormItem(txtRawMaterial,"Producto");
            formLayout.addFormItem(txtPriceCost,"Precio costo");
            formLayout.addFormItem(txtAdvancePercentage,"% Avance");
        }else if(productionSalesInventory.getTypeInventory().equals("terminado")){
            binderProductSalesInventory.forField(txtRawMaterial).asRequired("Mercaderia es requerida")
                    .bind(ProductionSalesInventory::getRawMaterial,ProductionSalesInventory::setRawMaterial);
            binderProductSalesInventory.forField(txtPriceCost).asRequired("Precio costo es requerido")
                    .withValidator(value -> value.doubleValue()>0,"Precio costo  debe ser mayor a cero")
                    .bind(ProductionSalesInventory::getPriceCost,ProductionSalesInventory::setPriceCost);
            binderProductSalesInventory.forField(txtPriceSale).asRequired("Precio venta es requerido")
                    .withValidator(value -> value.doubleValue()>0,"Precio venta  debe ser mayor a cero")
                    .bind(ProductionSalesInventory::getPriceSale,ProductionSalesInventory::setPriceSale);
            formLayout.addFormItem(txtRawMaterial,"Mercaderia");
            formLayout.addFormItem(txtPriceCost,"Precio costo");
            formLayout.addFormItem(txtPriceSale,"Precio venta");
        }else if(productionSalesInventory.getTypeInventory().equals("venta")){
            binderProductSalesInventory.forField(txtRawMaterial).asRequired("Mercaderia es requerida")
                    .bind(ProductionSalesInventory::getRawMaterial,ProductionSalesInventory::setRawMaterial);
            binderProductSalesInventory.forField(txtPriceCost).asRequired("Precio costo es requerido")
                    .withValidator(value -> value.doubleValue()>0,"Precio costo  debe ser mayor a cero")
                    .bind(ProductionSalesInventory::getPriceCost,ProductionSalesInventory::setPriceCost);
            binderProductSalesInventory.forField(txtPriceSale).asRequired("Precio venta es requerido")
                    .withValidator(value -> value.doubleValue()>0,"Precio venta  debe ser mayor a cero")
                    .bind(ProductionSalesInventory::getPriceSale,ProductionSalesInventory::setPriceSale);
            formLayout.addFormItem(txtRawMaterial,"Mercaderia");
            formLayout.addFormItem(txtPriceCost,"Precio costo");
            formLayout.addFormItem(txtPriceSale,"Precio venta");
        }
        binderProductSalesInventory.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderProductSalesInventory.hasChanges();
           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Declaracion Patrimonial"));
        });

        footer.addSaveListener(e ->{
           if(binderProductSalesInventory.writeBeanIfValid(productionSalesInventory)) {
               if(productionSalesInventory.getId()==null){
                   productionSalesInventory.setId(UUID.randomUUID());
               }
               Double invMb;
               if(productionSalesInventory.getTypeInventory().equals("insumo")){
                   invMb = productionSalesInventory.getQuantity()*productionSalesInventory.getPriceCost();
                   invMb = Math.round(invMb*10000.0)/10000.0;
                   productionSalesInventory.setInventoryValueMb(invMb);
               }else if(productionSalesInventory.getTypeInventory().equals("proceso")){
                   invMb = productionSalesInventory.getAdvancePercentage()*productionSalesInventory.getPriceCost()/100.0;
                   invMb = Math.round(invMb*10000.0)/10000.0;
                   productionSalesInventory.setInventoryValueMb(invMb);
               }else if(productionSalesInventory.getTypeInventory().equals("terminado")){
                   Double mb = (productionSalesInventory.getPriceSale()-productionSalesInventory.getPriceCost())/productionSalesInventory.getPriceSale();
                   mb = Math.round(mb*10000.0)/10000.0;
                   productionSalesInventory.setMb(mb);
                   Double invFinish = productionSalesInventory.getPriceCost()*productionSalesInventory.getQuantity();
                   invFinish = Math.round(invFinish*100.0)/100.0;
                   productionSalesInventory.setInventoryValueFinished(invFinish);
                   Double factor = productionSalesInventory.getInventoryValueFinished()*mb;
                   factor = Math.round(factor*100.0)/100.0;
                   productionSalesInventory.setFactor(factor);
               }else if(productionSalesInventory.getTypeInventory().equals("venta")){
                   Double mb = (productionSalesInventory.getPriceSale()-productionSalesInventory.getPriceCost())/productionSalesInventory.getPriceSale();
                   mb = Math.round(mb*10000.0)/10000.0;
                   productionSalesInventory.setMb(mb);
                   Double invFinish = productionSalesInventory.getPriceCost()*productionSalesInventory.getQuantity();
                   invFinish = Math.round(invFinish*100.0)/100.0;
                   productionSalesInventory.setInventoryValueFinished(invFinish);
                   Double factor = productionSalesInventory.getInventoryValueFinished()*mb;
                   factor = Math.round(factor*100.0)/100.0;
                   productionSalesInventory.setFactor(factor);
               }
               productionSalesInventoryList.removeIf(value -> value.getId().equals(productionSalesInventory.getId()));
               productionSalesInventoryList.add(productionSalesInventory);
               productionSalesInventoryDataProvider.refreshAll();
               ObjectMapper mapper = new ObjectMapper();
               try {
                   String jsonProductionInventory = mapper.writeValueAsString(productionSalesInventoryList);
                   patrimonialStatementCommerce.setFieldText7(jsonProductionInventory);
               } catch (JsonProcessingException ex) {
                   ex.printStackTrace();
               }


               restTemplate.add(patrimonialStatementCommerce);
               detailsDrawer.hide();
           }
        });

        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawerForm.setContent(formLayout);
        detailsDrawerForm.setFooter(footer);
        return detailsDrawerForm;

    }

    private Grid createGridProductionInventory(String typeInventory){
        Grid<ProductionSalesInventory> gridProductionSalesInventory = new Grid<>();
        gridProductionSalesInventory.addThemeVariants(GridVariant.LUMO_COMPACT);

        gridProductionSalesInventory.addColumn(ProductionSalesInventory::getQuantity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Cantidad").setResizable(true);
        gridProductionSalesInventory.addColumn(ProductionSalesInventory::getUnity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Unidad").setResizable(true);

        if(typeInventory.equals("insumo")) {
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getRawMaterial).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Materia Prima").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getPriceCost).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Precio compra").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getInventoryValueMb).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Valor inventario").setFlexGrow(0);
        }else if(typeInventory.equals("proceso")){
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getRawMaterial).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Producto").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getPriceCost).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Precio costo").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getAdvancePercentage).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("% Avance").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getInventoryValueMb).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Valor inventario MP,PP").setFlexGrow(0);

        }else if(typeInventory.equals("terminado")){
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getRawMaterial).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Mercaderia").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getPriceCost).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Precio costo").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getPriceSale).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Precio venta").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getMb).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("MB").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getInventoryValueFinished).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Valor inventario terminados").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getFactor).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Factor ponderado").setResizable(true);
        }else if(typeInventory.equals("venta")){
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getRawMaterial).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Mercaderia").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getPriceCost).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Precio costo").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getPriceSale).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Precio venta").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getMb).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("MB").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getInventoryValueFinished).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Valor inventario (En Compra)").setResizable(true);
            gridProductionSalesInventory.addColumn(ProductionSalesInventory::getFactor).setFlexGrow(0)
                    .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Factor ponderado").setResizable(true);
        }
        gridProductionSalesInventory.addColumn(new ComponentRenderer<>(this::createButtonDelete))
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S);

        return gridProductionSalesInventory;

    }

    //UI COST PRODUCT
    private DetailsDrawer createCostProductForm(){
        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("95%");
        detailsDrawer.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Declaracion Patrimonial"));
        footer.addSaveListener(e -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                costProductList.forEach(f -> {
                    if(f.getProduct().equals(cmbProduct.getValue())){
                        f.setPriceSale(txtPriceSaleProduct.getValue());
                    }
                });
                String jsonCostProduct = mapper.writeValueAsString(costProductList);
                patrimonialStatementCommerce.setFieldText6(jsonCostProduct);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            restTemplate.add(patrimonialStatementCommerce);
            UIUtils.showNotification("Costos producto "+cmbProduct.getValue() +" registrados");
        });
        footer.addCancelListener(e-> detailsDrawer.hide());

        detailsDrawer.setContent(layoutCostProduct());
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }


    private VerticalLayout layoutCostProduct(){
        VerticalLayout layout = new VerticalLayout();
        costProductListProvider = new ListDataProvider<>(costProductList);
        layout.setSizeFull();
        layout.add(createTopBarCostProduct(),createGridCostProduct());
        return layout;
    }

    private void showCostProduct(CostProduct costProduct){
        detailsDrawerHeader.setTitle(costProduct.getSupplies());
        detailsDrawer.setContent(createCostProductDetailForm(costProduct));
        binderCostProduct.readBean(costProduct);
        detailsDrawer.show();
    }

    private DetailsDrawer createCostProductDetailForm(CostProduct costProduct){
        TextField txtSupplie = new TextField();
        txtSupplie.setWidth("100%");
        NumberField txtQuantity = new NumberField();
        txtQuantity.setWidth("100%");
        TextField txtUnity = new TextField();
        txtUnity.setWidth("100%");
        NumberField txtPriceUnit = new NumberField();
        txtPriceUnit.setWidth("100%");
        NumberField txtFactorConversion = new NumberField();
        txtFactorConversion.setWidth("100%");

        binderCostProduct = new BeanValidationBinder<>(CostProduct.class);


        binderCostProduct.forField(txtSupplie).asRequired("Insumo es requerido")
                .bind(CostProduct::getSupplies,CostProduct::setSupplies);
        binderCostProduct.forField(txtQuantity).asRequired("Cantidad insumo es requerida")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .withValidator(value -> value.intValue()>0,"Cantidad insumo debe ser mayor a 0")
                .bind(CostProduct::getQuantity,CostProduct::setQuantity);
        binderCostProduct.forField(txtUnity).asRequired("Unidad es requerida")
                .bind(CostProduct::getUnity,CostProduct::setUnity);
        binderCostProduct.forField(txtPriceUnit).asRequired("Precio unitario es requerido")
                .withValidator(value -> value.doubleValue()>0,"Precio unitario debe ser mayor a 0")
                .bind(CostProduct::getPriceUnity,CostProduct::setPriceUnity);
        binderCostProduct.forField(txtFactorConversion).asRequired("Factor de conversion es reqruerido")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .withValidator(value -> value.intValue()>0,"Factor conversion tiene que ser mayor a 0")
                .bind(CostProduct::getFactorConversion,CostProduct::setFactorConversion);

        FormLayout formCostProduct = new FormLayout();
        formCostProduct.setSizeFull();
        formCostProduct.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px",3,FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        formCostProduct.addFormItem(txtSupplie,"Insumo");
        formCostProduct.addFormItem(txtQuantity,"Cantidad");
        formCostProduct.addFormItem(txtUnity,"Unidad");
        formCostProduct.addFormItem(txtPriceUnit,"Precio unitario");
        formCostProduct.addFormItem(txtFactorConversion,"Factor de conversion");

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();

        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderCostProduct.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderCostProduct.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Declaracion Patrimonial"));
        });

        footer.addSaveListener(e -> {
           if(binderCostProduct.writeBeanIfValid(costProduct)) {
               if(costProduct.getId()==null){
                   costProduct.setId(UUID.randomUUID());
                   costProduct.setProduct(cmbProduct.getValue());
                   costProduct.setPriceSale(txtPriceSaleProduct.getValue());
               }
               Double quantity = costProduct.getQuantity().doubleValue();
               Double factorConversion = costProduct.getFactorConversion().doubleValue();
               Double endQuantity = quantity/factorConversion;
               endQuantity = Math.round(endQuantity*1000.0)/1000.0;
               costProduct.setEndQuantity(endQuantity);
               Double totalCost = costProduct.getPriceUnity()*endQuantity;
               totalCost = Math.round(totalCost*100.0)/100.0;
               costProduct.setTotalCostUnit(totalCost);

               costProductList.removeIf(c -> c.getId().equals(costProduct.getId()));
               costProductList.add(costProduct);
               costProductListProvider.refreshAll();
               filterCostProductProvider(costProduct.getProduct());

               UIUtils.showNotification("Insumo agregado");
               detailsDrawer.hide();
           }
        });
        footer.addCancelListener(e -> detailsDrawer.hide());

        detailsDrawerForm.setContent(formCostProduct);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }


    private void filterCostProductProvider(String product){
        costProductListProvider.clearFilters();
        costProductListProvider.addFilter(p -> Objects.equals(product,p.getProduct()));

    }

    private HorizontalLayout createTopBarCostProduct(){
        HorizontalLayout topBar = new HorizontalLayout();
        cmbProduct = new ComboBox();
        cmbProduct.setPlaceholder("Producto");
        cmbProduct.setAllowCustomValue(true);
        itemsCostProducts = new ArrayList<>();

        txtPriceSaleProduct = new NumberField();
        txtPriceSaleProduct.setPlaceholder("Precio venta");
        List<CostProduct> distinctProducts = costProductList.stream()
                .filter(UtilValues.distinctByKey(CostProduct::getProduct)).collect(Collectors.toList());

        for(CostProduct c : distinctProducts){
            itemsCostProducts.add(c.getProduct());
        }

        cmbProduct.setItems(itemsCostProducts);
        cmbProduct.addCustomValueSetListener(e ->{
           itemsCostProducts.removeIf(i -> i.equals(e.getDetail()));
           itemsCostProducts.add(e.getDetail());
           cmbProduct.setItems(itemsCostProducts);
           cmbProduct.setValue(e.getDetail());

        });
        cmbProduct.addValueChangeListener(e ->{
           filterCostProductProvider(e.getValue());
           List<CostProduct> list = costProductListProvider.getItems().stream().collect(Collectors.toList());
           String f = cmbProduct.getValue();
           List<CostProduct> list2= list.stream().filter(c -> c.getProduct().equals(f))
                   .collect(Collectors.toList());
           if (list2.size()>0){
               txtPriceSaleProduct.setValue(list2.get(0).getPriceSale());
           }

        });


        Button btnNewSupplie = new Button("Nuevo Insumo");
        btnNewSupplie.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNewSupplie.addClickListener(e -> {
            if(cmbProduct.getValue()!=null && !cmbProduct.getValue().isEmpty() && txtPriceSaleProduct.getValue()>0 ) {
                showCostProduct(new CostProduct());
            }else{
                UIUtils.showNotification("Selecciona o ingresa un producto e ingrese su precio de venta");
                cmbProduct.focus();
            }
        });
        btnNewSupplie.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(e -> {
            if(!cmbProduct.isEmpty()) {
                Map<String, List<String>> paramVae = new HashMap<>();
                List<String> origin = new ArrayList<>();
                origin.add("costProduct");
                List<String> path = new ArrayList<>();
                path.add("earning");
                List<String> idPatrimonialStatement = new ArrayList<>();
                idPatrimonialStatement.add(patrimonialStatementCommerce.getId().toString());
                List<String> titleReport = new ArrayList<>();
                titleReport.add("Costo Producto -" + cmbProduct.getValue());
                List<String> product = new ArrayList<>();
                product.add(cmbProduct.getValue());

                paramVae.put("title", titleReport);
                paramVae.put("origin", origin);
                paramVae.put("path", path);
                paramVae.put("product", product);
                paramVae.put("id-patrimonial-statement", idPatrimonialStatement);
                paramVae.put("id-credit-request-applicant", param.get("id-credit-request-applicant"));
                paramVae.put("element", param.get("element"));
                paramVae.put("id-applicant", param.get("id-applicant"));
                paramVae.put("category", param.get("category"));
                paramVae.put("activity", param.get("activity"));

                QueryParameters qp = new QueryParameters(paramVae);
                UI.getCurrent().navigate("report-preview", qp);
            }else{
                UIUtils.showNotification("Seleccione un producto para imprimir");
                cmbProduct.focus();
            }
        });

        topBar.add(cmbProduct, txtPriceSaleProduct, btnNewSupplie,btnPrint);
        return topBar;
    }

    private Grid createGridCostProduct(){
        Grid<CostProduct> costProductGrid = new Grid<>();
        costProductGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        costProductGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showCostProduct));
        costProductGrid.setDataProvider(costProductListProvider);

        costProductGrid.addColumn(CostProduct::getSupplies).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Insumo").setResizable(true);
        costProductGrid.addColumn(CostProduct::getQuantity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setHeader("Cantidad").setResizable(true);
        costProductGrid.addColumn(CostProduct::getUnity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setHeader("Unidad").setResizable(true);
        costProductGrid.addColumn(CostProduct::getPriceUnity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Precio unitario").setResizable(true);
        costProductGrid.addColumn(CostProduct::getFactorConversion).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Factor conversion").setResizable(true);
        costProductGrid.addColumn(CostProduct::getEndQuantity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Cantidad Final").setResizable(true);
        costProductGrid.addColumn(CostProduct::getTotalCostUnit).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Total costo").setResizable(true);
        costProductGrid.addColumn(new ComponentRenderer<>(this::createButtonDelete))
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S);
        return costProductGrid;

    }

    //UI VAE INDEPENDENT
    private VerticalLayout layoutAnalysisSales(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        horizontalLayoutTopBar = createTopBarProductSales();
        layout.add(horizontalLayoutTopBar, createGridProductSales(), createGridProductBuys(),createGridOperativeExpenses());

        return layout;
    }

    private Grid createGridOperativeExpenses(){
        gridOperativeExpenses = new Grid<>();
        gridOperativeExpenses.setSizeFull();
        gridOperativeExpenses.addThemeVariants(GridVariant.LUMO_COMPACT);
        gridOperativeExpenses.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showOperativeExpenses));

        gridOperativeExpenses.addColumn(OperativeExpenses::getExpense).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XXL).setHeader("Detalle").setResizable(true);
        gridOperativeExpenses.addColumn(OperativeExpenses::getAmount).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Monto");

        gridOperativeExpenses.addColumn(new ComponentRenderer<>(this::createButtonDelete))
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S);

        return gridOperativeExpenses;
    }


    private HorizontalLayout createTopBarProductSales(){
        Button btnNew = new Button("Nuevo");

        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.add(btnNew);
        btnNew.addClickListener(e ->{
            if(typeFilterProduct.equals("VENTA") || typeFilterProduct.equals("COMPRA")) {
                currentProductSalesBuys = new ProductSalesBuys();
                showDetailsProductSales(currentProductSalesBuys);
            }else if(typeFilterProduct.equals("GASTO")){
                currentOperativeOperativeExpenses = new OperativeExpenses();
                showOperativeExpenses(currentOperativeOperativeExpenses);
            }
        });
        btnNew.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));
        return layout;
    }

    private Grid createGridProductBuys(){
        gridProductBuys = new Grid<>();
        gridProductBuys.setSizeFull();
        gridProductBuys.addThemeVariants(GridVariant.LUMO_COMPACT);

        gridProductBuys.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetailsProductSales));

        gridProductBuys.addColumn(ProductSalesBuys::getProduct).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Producto").setResizable(true);
        gridProductBuys.addColumn(ProductSalesBuys::getUnit).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Unidad").setResizable(true);
        gridProductBuys.addColumn(ProductSalesBuys::getQuantity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Cantidad").setFlexGrow(0);
        gridProductBuys.addColumn(ProductSalesBuys::getFrecuency).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Frecuencia").setResizable(true);

        gridProductBuys.addColumn(ProductSalesBuys::getPriceCost).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Monto").setResizable(true);
        gridProductBuys.addColumn(ProductSalesBuys::getTotalBuys).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Total Mes").setResizable(true);

        gridProductBuys.addColumn(new ComponentRenderer<>(this::createButtonDelete))
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S);

        return gridProductBuys;

    }
    
    
    private Grid createGridProductSales(){
        gridProductSales = new Grid<>();
        gridProductSales.setSizeFull();
        gridProductSales.addThemeVariants(GridVariant.LUMO_COMPACT);

        gridProductSales.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetailsProductSales));

        gridProductSales.addColumn(ProductSalesBuys::getProduct).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_L).setHeader("Producto").setResizable(true);
        gridProductSales.addColumn(ProductSalesBuys::getUnit).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setHeader("Unidad").setResizable(true);
        gridProductSales.addColumn(ProductSalesBuys::getQuantity).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setHeader("Cantidad").setFlexGrow(0);
        gridProductSales.addColumn(ProductSalesBuys::getFrecuency).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Frecuencia").setResizable(true);
    
        gridProductSales.addColumn(ProductSalesBuys::getPriceCost).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setHeader("P. Costo").setResizable(true);
        gridProductSales.addColumn(ProductSalesBuys::getPriceSale).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setHeader("P. Venta").setResizable(true);
        gridProductSales.addColumn(ProductSalesBuys::getTotalBuys).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Total Comp").setResizable(true);
        gridProductSales.addColumn(ProductSalesBuys::getTotalSales).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Total Venta").setResizable(true);
      
        gridProductSales.addColumn(new ComponentRenderer<>(this::createButtonDelete))
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S);

        return gridProductSales;

    }

    private Component createButtonDelete(Object p){
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_ERROR);
        button.setIcon(VaadinIcon.TRASH.create());
        button.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));
        button.addClickListener(e ->{
            if(p.getClass().getName().equals("com.mindware.workflow.backend.entity.patrimonialStatement.ProductSalesBuys")) {
                productSalesBuysList.removeIf(productSalesBuys1 -> productSalesBuys1.equals(p));
                productSalesBuysListProvider.refreshAll();
            }else if(p.getClass().getName().equals("com.mindware.workflow.backend.entity.patrimonialStatement.OperativeExpenses")){
                operativeExpensesList.removeIf(operativeExpenses -> operativeExpenses.equals(p));
                operativeExpensesListProvider.refreshAll();
            }else if(p.getClass().getName().equals("com.mindware.workflow.backend.entity.patrimonialStatement.CostProduct")){
                costProductList.removeIf(costProduct -> costProduct.equals(p));
                costProductListProvider.refreshAll();
            }else if(p.getClass().getName().equals("com.mindware.workflow.backend.entity.patrimonialStatement.ProductionSalesInventory")){
                productionSalesInventoryList.removeIf(productionSalesInventory -> productionSalesInventory.equals(p));
                productionSalesInventoryDataProvider.refreshAll();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String jsonProductionInventory = mapper.writeValueAsString(productionSalesInventoryList);
                    patrimonialStatementCommerce.setFieldText7(jsonProductionInventory);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }

                restTemplate.add(patrimonialStatementCommerce);
            }
        });
        return button;

    }

    private DetailsDrawer createOperativeExpensesDetailsForm(OperativeExpenses operativeExpenses){
        TextField txtExpenses = new TextField();
        txtExpenses.setWidth("100%");

        NumberField txtAmount = new NumberField();
        txtAmount.setWidth("100%");

        binderOperativeExpenses = new BeanValidationBinder<>(OperativeExpenses.class);
        binderOperativeExpenses.forField(txtExpenses).asRequired("Detalle gasto es requerido")
                .bind(OperativeExpenses::getExpense, OperativeExpenses::setExpense);
        binderOperativeExpenses.forField(txtAmount).asRequired("Monto es requerido")
                .withValidator(value -> value.doubleValue()>0,"Monto no es valido")
                .bind(OperativeExpenses::getAmount,OperativeExpenses::setAmount);
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        formLayout.addFormItem(txtExpenses,"Detalle");
        formLayout.addFormItem(txtAmount,"Monto");

        DetailsDrawerFooter footer = new DetailsDrawerFooter();

        binderOperativeExpenses.addStatusChangeListener(event -> {
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binderOperativeExpenses.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Declaracion Patrimonial"));
        });

        footer.addSaveListener(e ->{
            if (binderOperativeExpenses.writeBeanIfValid(currentOperativeOperativeExpenses)){
                if(currentOperativeOperativeExpenses.getId()==null){
                    currentOperativeOperativeExpenses.setId(UUID.randomUUID());
                }
                operativeExpensesList.removeIf(o -> o.getId().equals(currentOperativeOperativeExpenses.getId()));
                operativeExpensesList.add(currentOperativeOperativeExpenses);
                operativeExpensesListProvider.refreshAll();
                UIUtils.showNotification("Gasto Operativo agregado");
                detailsDrawer.hide();

            }
        });

        footer.addCancelListener(e -> detailsDrawer.hide());

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidth("100%");
        detailsDrawerForm.setContent(formLayout);
        detailsDrawerForm.setFooter(footer);

        return detailsDrawerForm;
    }

    private DetailsDrawer createProductSalesDetailsForm(ProductSalesBuys productSalesBuys){
        TextField txtProduct = new TextField();
        txtProduct.setWidth("100%");
        TextField txtUnit = new TextField();
        txtUnit.setWidth("100%");
        NumberField txtQuantity = new NumberField();
        txtQuantity.setWidth("100%");
        NumberField txtFrecuency = new NumberField();
        txtFrecuency.setWidth("100%");
        NumberField txtPriceCost = new NumberField();
        txtPriceCost.setWidth("100%");
        NumberField txtPriceSale = new NumberField();
        txtPriceSale.setWidth("100%");
        NumberField txtTotalBuys = new NumberField();
        txtTotalBuys.setWidth("100%");
        if(typeFilterProduct.equals("VENTA")) {
            NumberField txtTotalSales = new NumberField();
            txtTotalSales.setWidth("100%");
        }

        binderProductSales = new BeanValidationBinder<>(ProductSalesBuys.class);

        binderProductSales.forField(txtProduct).asRequired("Producto es requerido")
                .bind(ProductSalesBuys::getProduct, ProductSalesBuys::setProduct);
        binderProductSales.forField(txtUnit).asRequired("Unidad es requerida")
                .bind(ProductSalesBuys::getUnit, ProductSalesBuys::setUnit);
        binderProductSales.forField(txtQuantity).asRequired("Cantidad es requerida")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .withValidator(value -> value.intValue()>0,"Cantidad debe ser mayor a 0")
                .bind(ProductSalesBuys::getQuantity, ProductSalesBuys::setQuantity);
        binderProductSales.forField(txtFrecuency).asRequired("Frecuencia es requerida")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .withValidator(value -> value.intValue()>0, "Frecuencia debe ser mayor a 0")
                .bind(ProductSalesBuys::getFrecuency, ProductSalesBuys::setFrecuency);
        binderProductSales.forField(txtPriceCost).withValidator(value -> value.doubleValue()>0, typeFilterProduct.equals("VENTA")?"Precio de costo de be ser mayor a 0":"Monto debe ser mayor a 0")
                .asRequired(typeFilterProduct.equals("VENTA")?"Precio de costo es requerido":"Monto es requerido")
                .bind(ProductSalesBuys::getPriceCost, ProductSalesBuys::setPriceCost);
        if(typeFilterProduct.equals("VENTA")) {
            binderProductSales.forField(txtPriceSale).withValidator(value -> value.doubleValue() > 0, "Precio de venta debe ser mayor a 0")
                    .asRequired("Precio de venta es requerido")
                    .bind(ProductSalesBuys::getPriceSale, ProductSalesBuys::setPriceSale);
        }
        FormLayout formSalesVae = new FormLayout();
        formSalesVae.setSizeFull();
        formSalesVae.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px",3,FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );
        formSalesVae.addFormItem(txtProduct,"Producto");
        formSalesVae.addFormItem(txtUnit,"Unidad");
        formSalesVae.addFormItem(txtQuantity,"Cantidad");
        formSalesVae.addFormItem(txtFrecuency,"Frecuencia");
        if(typeFilterProduct.equals("VENTA")) {
            formSalesVae.addFormItem(txtPriceCost, "Precio Costo");
            formSalesVae.addFormItem(txtPriceSale, "Precio Venta");
        }else{
            formSalesVae.addFormItem(txtPriceCost, "Monto");
        }
        DetailsDrawerFooter footerSalesProducts = new DetailsDrawerFooter();

//        binderProductSales.bindInstanceFields(this);

        binderProductSales.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binderProductSales.hasChanges();
            footerSalesProducts.saveState(hasChanges && isValid && GrantOptions.grantedOption("Declaracion Patrimonial"));
        });

        footerSalesProducts.addSaveListener(e ->{
            if(binderProductSales.writeBeanIfValid(currentProductSalesBuys)) {
                if(currentProductSalesBuys.getId()==null) {
                    currentProductSalesBuys.setId(UUID.randomUUID());
                }
                currentProductSalesBuys.setTypeRegister(typeFilterProduct);
                if(typeFilterProduct.equals("VENTA")) {
                    Double totalBuys = currentProductSalesBuys.getQuantity() * currentProductSalesBuys.getFrecuency() * currentProductSalesBuys.getPriceCost();
                    currentProductSalesBuys.setTotalBuys(totalBuys);
                    Double totalSales = currentProductSalesBuys.getQuantity() * currentProductSalesBuys.getFrecuency() * currentProductSalesBuys.getPriceSale();
                    currentProductSalesBuys.setTotalSales(totalSales);
                }else{
                    Double totalBuys =  currentProductSalesBuys.getQuantity() * currentProductSalesBuys.getPriceCost()*currentProductSalesBuys.getFrecuency();
                    currentProductSalesBuys.setTotalBuys(totalBuys);

                    currentProductSalesBuys.setTotalSales(0.0);
                    currentProductSalesBuys.setPriceSale(0.0);
                }

                productSalesBuysList.removeIf(p -> p.getId().equals(currentProductSalesBuys.getId()));
                productSalesBuysList.add(currentProductSalesBuys);
                productSalesBuysListProvider.refreshAll();
                filterProductSalesProvider(typeFilterProduct);
                UIUtils.showNotification(typeFilterProduct.equals("VENTA")?"Producto de Venta agregado":"Producto compra agregado");
                detailsDrawer.hide();

            }
        });

        footerSalesProducts.addCancelListener(e ->{
            detailsDrawer.hide();
        });

        DetailsDrawer detailsDrawerForm = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerForm.setHeight("90%");
        detailsDrawerForm.setWidthFull();
        detailsDrawerForm.setContent(formSalesVae);
        detailsDrawerForm.setFooter(footerSalesProducts);

        return detailsDrawerForm;
    }

    private void showOperativeExpenses(OperativeExpenses operativeExpenses){
        currentOperativeOperativeExpenses = operativeExpenses;
        detailsDrawerHeader.setTitle(operativeExpenses.getExpense());
        detailsDrawer.setContent(createOperativeExpensesDetailsForm(currentOperativeOperativeExpenses));
        binderOperativeExpenses.readBean(currentOperativeOperativeExpenses);
        detailsDrawer.show();
    }

    private void showDetailsProductSales(ProductSalesBuys productSalesBuys){
        currentProductSalesBuys = productSalesBuys;
        detailsDrawerHeader.setTitle(productSalesBuys.getProduct());
        detailsDrawer.setContent(createProductSalesDetailsForm(currentProductSalesBuys));
        binderProductSales.readBean(currentProductSalesBuys);
        detailsDrawer.show();
    }

    private void filterAverageProjectionProvider(String element) {
        averageProjectionProvider.clearFilters();
        averageProjectionProvider.addFilter(salesProjection -> (Objects.equals("ALTA", salesProjection.getPeriod()) ||
                Objects.equals("REGULAR", salesProjection.getPeriod()) ||
                Objects.equals("BAJA", salesProjection.getPeriod())) &&
                Objects.equals(element, salesProjection.getCategorySale())  );
    }

    private void filterSalesProjectionProvider(String element) {
        salesProjectionProvider.clearFilters();
        salesProjectionProvider.addFilter(p -> !Objects.equals("ALTA", p.getPeriod()) &&
                !Objects.equals("REGULAR", p.getPeriod()) &&
                !Objects.equals("BAJA", p.getPeriod()) &&
                Objects.equals(element, p.getCategorySale()));
    }

    private void filterProductSalesProvider(String type){
        productSalesBuysListProvider.clearFilters();
        productSalesBuysListProvider.addFilter(p -> Objects.equals(type,p.getTypeRegister()));
    }

    private List<SalesProjection> createInitDataSales() {
        List<SalesProjection> list = new ArrayList<>();
        String[] daysWeek = {"LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO"};
        int i=0;
        while(i<daysWeek.length){
            SalesProjection p = initPatrimonialStatement(daysWeek[i], "DIARIA");
            list.add(p);
            i++;
        }
        String[] weeks = {"SEMANA1","SEMANA2","SEMANA3","SEMANA4"};
        i=0;
        while(i<weeks.length){
            SalesProjection p = initPatrimonialStatement(weeks[i], "SEMANAL");
            list.add(p);
            i++;

        }
        String[] months = {"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO",
                "JULIO","AGOSTO","SEPTIEMRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};
        i=0;
        while(i<months.length){
            SalesProjection p = initPatrimonialStatement(months[i], "MENSUAL");
            list.add(p);
            i++;
        }

        String[] textCategoryAmounts = {"ALTA","REGULAR","BAJA"};
        i =0;
        while (i < textCategoryAmounts.length){
            SalesProjection p = initPatrimonialStatement(textCategoryAmounts[i],"DIARIA");
            list.add(p);
            i++;
        }

        i=0;
        while (i<textCategoryAmounts.length){
            SalesProjection p = initPatrimonialStatement( textCategoryAmounts[i],"SEMANAL");
            list.add(p);
            i++;

        }

        i=0;
        while (i<textCategoryAmounts.length){
            SalesProjection p = initPatrimonialStatement(textCategoryAmounts[i],"MENSUAL");
            list.add(p);
            i++;

        }
        return list;
    }

    private SalesProjection initPatrimonialStatement(String fieldText2, String elementCategory) {
        SalesProjection p = new SalesProjection();
        p.setId(UUID.randomUUID());
        p.setPeriod(fieldText2);
        p.setCategorySale(elementCategory);
        p.setTypeSale("");
        p.setLowSale(0.0);
        p.setHightSale(0.0);
        p.setAverageSale(0.0);

        return p;
    }

    private Component createContent(DetailsDrawer component) {
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private DetailsDrawer createVaeIndependentOptions(){
        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("95%");
        detailsDrawer.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Declaracion Patrimonial"));
        footer.addSaveListener(e ->{

            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonSalesProjection = mapper.writeValueAsString(averageList);
                String jsonProductSales = mapper.writeValueAsString(productSalesBuysList);
                String jsonOperativeExpenses = mapper.writeValueAsString(operativeExpensesList);
                patrimonialStatementCommerce.setFieldText3(jsonSalesProjection);
                patrimonialStatementCommerce.setFieldText4(jsonProductSales);
                patrimonialStatementCommerce.setFieldText5(jsonOperativeExpenses);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }

            restTemplate.add(patrimonialStatementCommerce);
            PatrimonialStatementDtoRestTemplate rest = new PatrimonialStatementDtoRestTemplate();
            Double earning = rest.getOperativeEarningVae(patrimonialStatementCommerce.getId().toString(),
                    param.get("id-applicant").get(0));
            patrimonialStatementCommerce.setFieldDouble1(earning);
            restTemplate.add(patrimonialStatementCommerce);



            UIUtils.showNotification("Datos VAE-Independiente guardado");

        });
        footer.addCancelListener(e ->{
            detailsDrawer.hide();
        });

        detailsDrawer.setContent(layoutVaeIndependent());
        detailsDrawer.setFooter(footer);

        return detailsDrawer;

    }

    private VerticalLayout layoutVaeIndependent(){
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btnPrint.addClickListener(event -> {
            Map<String, List<String>> paramVae = new HashMap<>();
            List<String> origin = new ArrayList<>();
            origin.add("vaeIndependent");
            List<String> path = new ArrayList<>();
            path.add("earning");
            List<String> idPatrimonialStatement = new ArrayList<>();
            idPatrimonialStatement.add(patrimonialStatementCommerce.getId().toString());
            List<String> titleReport = new ArrayList<>();
            titleReport.add("VAE -" + patrimonialStatementCommerce.getFieldText1());

            paramVae.put("title", titleReport);
            paramVae.put("origin",origin);
            paramVae.put("path",path);
            paramVae.put("id-patrimonial-statement",idPatrimonialStatement);
            paramVae.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramVae.put("element",param.get("element"));
            paramVae.put("id-applicant",param.get("id-applicant"));
            paramVae.put("category",param.get("category"));
            paramVae.put("activity",param.get("activity"));

            QueryParameters qp = new QueryParameters(paramVae);
            UI.getCurrent().navigate("report-preview",qp);

        });

        horizontalLayoutEstimatedSales = layoutEstimatedSales();
        verticalLayoutProductSales = layoutAnalysisSales();
        layout.add(btnPrint, createTabsVaeIndependent(), horizontalLayoutEstimatedSales, verticalLayoutProductSales);
        if (salesProjectionList.size()>0){
            salesProjectionProvider = new ListDataProvider<>(salesProjectionList);
            gridProjectionSales.setDataProvider(salesProjectionProvider);
            averageProjectionProvider = new ListDataProvider<>(averageList);
            gridAverageSales.setDataProvider(averageProjectionProvider);
        }
        return layout;
    }

    private Tabs createTabsVaeIndependent(){
        Tabs tabsVae = new Tabs();
        tabsVae.setWidthFull();

        Tab tabDaily = new Tab("DIARIA");
        Tab tabWeekly = new Tab("SEMANAL");
        Tab tabMountly = new Tab("MENSUAL");
        Tab tabSales = new Tab("VENTAS");
        Tab tabBuys = new Tab("COMPRAS");
        Tab tabExpenses = new Tab("GASTOS OPERATIVOS");

        tabsVae.add(tabDaily,tabWeekly,tabMountly,tabSales,tabBuys,tabExpenses);
        tabsVae.setFlexGrowForEnclosedTabs(1);
        tabsVae.addSelectedChangeListener(e -> {
           if(e.getSelectedTab()!=null){
               if(e.getSelectedTab().getLabel().equals("DIARIA")
               || e.getSelectedTab().getLabel().equals("SEMANAL")
               || e.getSelectedTab().getLabel().equals("MENSUAL")) {
                   if (salesProjectionProvider.getItems().size() > 0) {
                       filterSalesProjectionProvider(e.getSelectedTab().getLabel());
                       filterAverageProjectionProvider(e.getSelectedTab().getLabel());
                   }
                   horizontalLayoutEstimatedSales.setVisible(true);
                   horizontalLayoutTopBar.setVisible(false);
                   verticalLayoutProductSales.setVisible(false);

               }else if(e.getSelectedTab().getLabel().equals("VENTAS")){
                   typeFilterProduct = "VENTA";
                   filterProductSalesProvider(typeFilterProduct);
                   horizontalLayoutTopBar.setVisible(true);
                   horizontalLayoutEstimatedSales.setVisible(false);
                   verticalLayoutProductSales.setVisible(true);
                   gridProductBuys.setVisible(false);
                   gridProductSales.setVisible(true);
                   gridOperativeExpenses.setVisible(false);
               }else if (e.getSelectedTab().getLabel().equals("COMPRAS")){
                   horizontalLayoutTopBar.setVisible(true);
                   typeFilterProduct = "COMPRA";
                   filterProductSalesProvider(typeFilterProduct);
                   horizontalLayoutEstimatedSales.setVisible(false);
                   verticalLayoutProductSales.setVisible(true);
                   gridProductBuys.setVisible(true);
                   gridProductSales.setVisible(false);
                   gridOperativeExpenses.setVisible(false);
               }else if(e.getSelectedTab().getLabel().equals("GASTOS OPERATIVOS")){
                   horizontalLayoutTopBar.setVisible(true);
                   typeFilterProduct = "GASTO";
                   horizontalLayoutEstimatedSales.setVisible(false);
                   verticalLayoutProductSales.setVisible(true);

                   gridProductBuys.setVisible(false);
                   gridProductSales.setVisible(false);
                   gridOperativeExpenses.setVisible(true);
               }
           }
        });

        return tabsVae;
    }

    private HorizontalLayout layoutEstimatedSales(){
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSizeFull();
        layout.add(createGridEstimatedSales(), createGridLayoutEstimatedAmountsSales());

        return layout;
    }

    private Grid createGridLayoutEstimatedAmountsSales(){
        gridAverageSales = new Grid<>();
        gridAverageSales.setWidthFull();
        gridAverageSales.addThemeVariants(GridVariant.LUMO_COMPACT);

        Binder<SalesProjection> binder = new BeanValidationBinder<>(SalesProjection.class);
        Editor<SalesProjection> editor = gridAverageSales.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        Collection<Button> editButtons = Collections.newSetFromMap(new HashMap<>());
        NumberField fieldMinText = new NumberField();
        NumberField fieldMaxText = new NumberField();
        NumberField fieldAvgText = new NumberField();

        fieldAvgText.setEnabled(false);

        gridAverageSales.addColumn(SalesProjection::getPeriod).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true).setHeader("Tipo Venta");

        Grid.Column<SalesProjection> fieldMinColumn = gridAverageSales.addColumn(SalesProjection::getLowSale).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true).setHeader("Minimo");
        Grid.Column<SalesProjection> fieldMaxColumn = gridAverageSales.addColumn(SalesProjection::getHightSale).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true).setHeader("Maximo");
        Grid.Column<SalesProjection> fieldAvgColumn = gridAverageSales.addColumn(SalesProjection::getAverageSale).setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true).setHeader("Promedio");

        fieldMinColumn.setEditorComponent(fieldMinText);
        fieldMaxColumn.setEditorComponent(fieldMaxText);
        fieldAvgColumn.setEditorComponent(fieldAvgText);

        binder.forField(fieldMinText).bind(SalesProjection::getLowSale,SalesProjection::setLowSale);
        binder.forField(fieldMaxText).bind(SalesProjection::getHightSale,SalesProjection::setHightSale);
        binder.forField(fieldAvgText).bind(SalesProjection::getAverageSale,SalesProjection::setAverageSale);

        Grid.Column<SalesProjection> editorColumn = gridAverageSales.addComponentColumn(p -> {
           Button edit = new Button("Editar");
           edit.addClickListener(e -> {
              editor.editItem(p);
              fieldMinText.focus();
           });
           edit.setEnabled(!editor.isOpen());
           editButtons.add(edit);
           return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach((button -> button.setEnabled((!editor.isOpen())))));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        Button save = new Button("Guardar", e -> {
            Double d = (fieldMinText.getValue() + fieldMaxText.getValue()) / 2;

            fieldAvgText.setValue(d);
            editor.save();
        });
        Button cancel = new Button("Cancelar", e -> editor.cancel());
        cancel.addClassName("cancel");

        gridAverageSales.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");
        Div buttons = new Div(save,cancel);
        editorColumn.setEditorComponent(buttons);

        return gridAverageSales;
    }

    private Grid createGridEstimatedSales(){
        gridProjectionSales = new Grid<>();
        gridProjectionSales.setWidth("65%");
        gridProjectionSales.setHeightFull();
        gridProjectionSales.addThemeVariants(GridVariant.LUMO_COMPACT);

        Binder<SalesProjection> binder = new BeanValidationBinder<>(SalesProjection.class);
        Editor<SalesProjection> editor = gridProjectionSales.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        ComboBox<String> typeSale = new ComboBox<>();
        Collection<Button> editButtons = Collections.newSetFromMap(new HashMap<>());


        gridProjectionSales.addColumn(SalesProjection::getPeriod).setHeader("Venta En:")
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        Grid.Column<SalesProjection> typeSaleColumn = gridProjectionSales.addColumn(SalesProjection::getTypeSale).setHeader("Tipo Venta")
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);

        typeSale.setItems("ALTA","REGULAR","BAJA");
        typeSaleColumn.setEditorComponent(typeSale);
        binder.forField(typeSale).bind(SalesProjection::getTypeSale,SalesProjection::setTypeSale);

        Grid.Column<SalesProjection> editorColumn = gridProjectionSales.addComponentColumn(p -> {
           Button edit = new Button("Editar");
           edit.addClickListener(e ->{
              editor.editItem(p);
              typeSale.focus();
           });
           edit.setEnabled(!editor.isOpen());
           editButtons.add(edit);
           return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach((button -> button.setEnabled((!editor.isOpen())))));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        Button save = new Button("Guardar", e -> editor.save());
        Button cancel = new Button("Cancelar", e -> editor.cancel());
        cancel.addClassName("cancel");

        gridProjectionSales.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");
        Div buttons = new Div(save,cancel);
        editorColumn.setEditorComponent(buttons);

        return gridProjectionSales;

    }

    //UI Sales
    private DetailsDrawer createLayoutGridSales(){
        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("95%");
        detailsDrawer.setWidthFull();
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Declaracion Patrimonial"));
        footer.addSaveListener(e ->{
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonSalesHistory = mapper.writeValueAsString(salesHistoryList);
                patrimonialStatementCommerce.setFieldText2(jsonSalesHistory);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            restTemplate.add(patrimonialStatementCommerce);
            UIUtils.showNotification("Datos Tabla Ventas guardado");
        });
        footer.addCancelListener(e ->{

        });

        detailsDrawer.setContent(layoutSalesHistory());
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private VerticalLayout layoutSalesHistory(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        tabs();
        layout.add(pagedTabs,createTopBar(), createGridSalesHistory());
        if (salesHistoryList.size()>0) {
            dataProviderSalesHistory = new ListDataProvider<>(salesHistoryList);
            gridSalesHistory.setDataProvider(dataProviderSalesHistory);
            filterSalesHistory();
        }
        return layout;
    }

    private void tabs(){
        pagedTabs = new Tabs();
        pagedTabs.setWidthFull();

        Tab tab1 = new Tab("MES-1");
        Tab tab2 = new Tab("MES-2");
        Tab tab3 = new Tab("MES-3");
        Tab tab4 = new Tab("MES-4");
        Tab tab5 = new Tab("MES-5");
        Tab tab6 = new Tab("MES-6");
        pagedTabs.add(tab1,tab2,tab3,tab4,tab5,tab6);
        pagedTabs.setFlexGrowForEnclosedTabs(1);
        pagedTabs.setSelectedTab(tab1);
        pagedTabs.addSelectedChangeListener(e ->{
            if(e.getSource().getSelectedTab()!=null){
                 filterSalesHistory();
            }
        });
    }

    private HorizontalLayout createTopBar(){
        DatePicker date = new DatePicker();
        date.setPlaceholder("Fecha Inicial");
        concept = new TextField();
        concept.setPlaceholder("Concepto");
        Button btnCreate = new Button("Generar");
        btnCreate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.add(date,concept,btnCreate, btnPrint);

        btnCreate.addClickListener(e -> {
            dateInit = date.getValue();
            salesHistoryList = generateInitialData();

            dataProviderSalesHistory = new ListDataProvider<>(salesHistoryList);
            gridSalesHistory.setDataProvider(dataProviderSalesHistory);
            filterSalesHistory();
        });
        btnCreate.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));

        btnPrint.addClickListener(e ->{
            Map<String,List<String>> paramSales = new HashMap<>();
            List<String> titleReport = new ArrayList<>();
            titleReport.add("Reporte Ventas -" + patrimonialStatementCommerce.getFieldText1());
            List<String> origin = new ArrayList<>();
            origin.add("earning");
            List<String> path = new ArrayList<>();
            path.add("earning");
            List<String> idPatrimonialStatement = new ArrayList<>();
            idPatrimonialStatement.add(patrimonialStatementCommerce.getId().toString());

            paramSales.put("title",titleReport);
            paramSales.put("path",path);
            paramSales.put("origin",origin);
            paramSales.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramSales.put("element",param.get("element"));
            paramSales.put("id-applicant",param.get("id-applicant"));
            paramSales.put("category",param.get("category"));
            paramSales.put("activity",param.get("activity"));
            paramSales.put("id-patrimonial-statement",idPatrimonialStatement);

            QueryParameters qp = new QueryParameters(paramSales);
            UI.getCurrent().navigate("report-preview",qp);

        });


        return layout;
    }

    private Grid createGridSalesHistory(){
        gridSalesHistory = new Grid<>();
        gridSalesHistory.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_WRAP_CELL_CONTENT);
        gridSalesHistory.setSizeFull();
        gridSalesHistory.setHeight("95%");

        gridSalesHistory.addColumn(TemplateRenderer.<SalesHistory> of("[[item.daySale]]")
                    .withProperty("daySale",
                            salesHistory -> UIUtils.formatDate(salesHistory.getDaySale())))
                    .setHeader("Fecha").setWidth(UIUtils.COLUMN_WIDTH_M).setFlexGrow(1).setSortable(true);
        gridSalesHistory.addColumn(SalesHistory::getConcept).setWidth(UIUtils.COLUMN_WIDTH_XL)
                .setHeader("Concepto").setFlexGrow(0).setResizable(true);
        Grid.Column<SalesHistory> totalColumn=  gridSalesHistory.addColumn(SalesHistory::getAmount).setWidth(UIUtils.COLUMN_WIDTH_M)
                .setHeader("Total Bs.").setFlexGrow(0).setResizable(true);


        Div validationStatus = new Div();
        validationStatus.setId("validation");

        Binder<SalesHistory> binder = new BeanValidationBinder<>(SalesHistory.class);
        Editor<SalesHistory> editor = gridSalesHistory.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        NumberField amount = new NumberField();
        amount.setMin(0);
        totalColumn.setEditorComponent(amount);

        binder.forField(amount).bind("amount");

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());
        Grid.Column<SalesHistory> editorColumn = gridSalesHistory.addComponentColumn(fieldsStructure -> {
            Button edit = new Button("Editar");
            edit.addClickListener(e -> {
                editor.editItem(fieldsStructure);
                amount.focus();
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

        gridSalesHistory.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");
        gridSalesHistory.getElement().addEventListener("keyup", event -> editor.save())
                .setFilter("event.key === 'Enter' || event.key === 'Ent'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        return gridSalesHistory;

    }

    private List<SalesHistory> generateInitialData(){
        LocalDate start = dateInit.withDayOfMonth(1);
        LocalDate end = dateInit.withDayOfMonth(dateInit.lengthOfMonth()).plusMonths(5);
        Integer number = 1;
        List<SalesHistory> list = new ArrayList<>();

        while(start.isBefore(end.plusDays(1))){
            SalesHistory salesHistory = new SalesHistory();

            salesHistory.setId(UUID.randomUUID());
            salesHistory.setConcept(concept.getValue());
            salesHistory.setAmount(0.0);
            salesHistory.setDaySale(start);
            salesHistory.setNumberMonth("MES-"+number.toString());
            list.add(salesHistory);
            start = start.plusDays(1);
            if(!start.getMonth().equals(salesHistory.getDaySale().getMonth()))
                number++;
        }
        return list;
    }

    private void filterSalesHistory(){
        if(dataProviderSalesHistory.getItems().size()>0){
            Tab selectedTab = pagedTabs.getSelectedTab();
            if (selectedTab != null) {
                if(dataProviderSalesHistory.getItems().size()>0) {
//                dataProvider.setFilterByValue(PatrimonialStatement::getElementCategory, selectedTab.getLabel());
                    dataProviderSalesHistory.clearFilters();
                    dataProviderSalesHistory.addFilter(p -> Objects.equals(selectedTab.getLabel(), p.getNumberMonth()));


                }
            }
        }
    }


}
