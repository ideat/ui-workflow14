package com.mindware.workflow.ui.ui.views.patrimonialStatement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.patrimonialStatement.PatrimonialStatement;
import com.mindware.workflow.ui.backend.entity.patrimonialStatement.SalaryAnalisys;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
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
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.*;
import lombok.SneakyThrows;


import java.util.*;
import java.util.stream.Collectors;

@Route(value = "salary-analysis",layout = MainLayout.class)
@PageTitle("Analisis Salario")
public class SalaryAnalisysRegister extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {

    private List<SalaryAnalisys> salaryAnalysisList;

    private List<SalaryAnalisys> salaryAnalysisMonth1List;
    private List<SalaryAnalisys> salaryAnalysisMonth2List;
    private List<SalaryAnalisys> salaryAnalysisMonth3List;

    private ListDataProvider<SalaryAnalisys> dataProviderSalaryAnalisys;

    private ListDataProvider<SalaryAnalisys> dataProviderMonth1;
    private ListDataProvider<SalaryAnalisys> dataProviderMonth2;
    private ListDataProvider<SalaryAnalisys> dataProviderMonth3;

    private ParameterRestTemplate parameterRestTemplate;
    private PatrimonialStatementRestTemplate restTemplate;
    private Map<String,List<String>> param = new HashMap<>();
    private List<PatrimonialStatement> patrimonialStatementList;
    private PatrimonialStatement patrimonialStatementSalary;
    private PatrimonialStatementDtoRestTemplate patrimonialStatementDtoRestTemplate;
    private FlexBoxLayout flexBoxLayout;

    private QueryParameters qp;
    private Integer priority;

    private ComboBox<String> typeClient;
    private String idPatrimonialStatement;
    private String idApplicant;

    @SneakyThrows
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        typeClient = new ComboBox<>("Tipo Cliente");
        typeClient.setWidth("300px");
        typeClient.setItems("CLIENTE","CONYUGE","CODEUDOR 1","CODEUDOR 2","CODEUDOR 3","CODEUDOR 4");


        parameterRestTemplate = new ParameterRestTemplate();
        patrimonialStatementDtoRestTemplate = new PatrimonialStatementDtoRestTemplate();
        flexBoxLayout = new FlexBoxLayout();
        flexBoxLayout.setFlexDirection(FlexDirection.ROW);
        Location location = beforeEvent.getLocation();
        qp =  location.getQueryParameters();
        param = qp.getParameters();

        idApplicant = param.get("id-applicant").get(0);
        idPatrimonialStatement = param.get("id-patrimonial-statement").get(0);

        restTemplate = new PatrimonialStatementRestTemplate();
        patrimonialStatementList = restTemplate.getByIdCreditRequestApplicantCategory(UUID.fromString(param.get("id-credit-request-applicant").get(0))
                , param.get("category").get(0));

        List<PatrimonialStatement> auxList = patrimonialStatementList.stream()
                .filter(p -> p.getCategory().equals(param.get("category").get(0))
                        && p.getFieldText1().equals(param.get("activity").get(0))
                ).collect(Collectors.toList());
        patrimonialStatementSalary = auxList.get(0);

        String salaryAnalysisString = patrimonialStatementSalary.getFieldText2();

        ObjectMapper mapper = new ObjectMapper();
        if (salaryAnalysisString==null || salaryAnalysisString.equals("") || salaryAnalysisString.equals("[]")) {
            salaryAnalysisList = new ArrayList<>();
            salaryAnalysisMonth1List = new ArrayList<>();
            salaryAnalysisMonth2List = new ArrayList<>();
            salaryAnalysisMonth3List = new ArrayList<>();

            initListSalary();
        }else {
            salaryAnalysisList = mapper.readValue(salaryAnalysisString, new TypeReference<List<SalaryAnalisys>>() {});
            salaryAnalysisMonth1List = salaryAnalysisList.stream().filter(p -> p.getNumberMonth().equals("Mes 1"))
                    .collect(Collectors.toList());
            salaryAnalysisMonth2List = salaryAnalysisList.stream().filter(p -> p.getNumberMonth().equals("Mes 2"))
                    .collect(Collectors.toList());
            salaryAnalysisMonth3List = salaryAnalysisList.stream().filter(p -> p.getNumberMonth().equals("Mes 3"))
                    .collect(Collectors.toList());
            typeClient.setValue(salaryAnalysisMonth1List.get(0).getTypeClient());
        }


        dataProviderMonth1 = new ListDataProvider<>(salaryAnalysisMonth1List);
        dataProviderMonth2 = new ListDataProvider<>(salaryAnalysisMonth2List);
        dataProviderMonth3 = new ListDataProvider<>(salaryAnalysisMonth3List);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        AppBar appBar = initAppBar();
        appBar.setTitle("ANALISIS SALARIO");

        flexBoxLayout = (FlexBoxLayout) createContent(createLayout());

        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Declaracion Patrimonial"));


        DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.add(typeClient);

        footer.addSaveListener(e -> {
            if(typeClient.getValue()!=null || !typeClient.getValue().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    salaryAnalysisMonth1List.addAll(salaryAnalysisMonth2List);
                    salaryAnalysisMonth1List.addAll(salaryAnalysisMonth3List);
                    for (SalaryAnalisys s : salaryAnalysisMonth1List) {
                        s.setTypeClient(typeClient.getValue());
                    }

                    String items = mapper.writeValueAsString(salaryAnalysisMonth1List);
                    patrimonialStatementSalary.setFieldText2(items);
                    restTemplate.add(patrimonialStatementSalary);

                    Double salary = patrimonialStatementDtoRestTemplate.getSalaryVaeDependent(idApplicant,idPatrimonialStatement,typeClient.getValue());
                    patrimonialStatementSalary.setFieldDouble1(salary);
                    restTemplate.add(patrimonialStatementSalary);


                    UIUtils.showNotification("Registro guardado");

                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }
            }else{
                UIUtils.showNotification("Seleccione el tipo de cliente");
                typeClient.focus();
            }
        });



        setViewContent(flexBoxLayout);
        setViewDetailsPosition(Position.BOTTOM);
        setViewFooter(footer);
        setViewHeader(detailsDrawerHeader);
    }

    private AppBar initAppBar(){
        MainLayout.get().getAppBar().reset();
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);

        return appBar;
    }

    private VerticalLayout gridSalary(String month){

        priority = 0;
        Grid<SalaryAnalisys> grid = new Grid();

//        filter(month);
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        if(month.equals("Mes 1")) {
            grid.setDataProvider(dataProviderMonth1);
        }else if(month.equals("Mes 2")){
            grid.setDataProvider(dataProviderMonth2);
        }else{
            grid.setDataProvider(dataProviderMonth3);
        }

        Binder<SalaryAnalisys> binder = new BeanValidationBinder<>(SalaryAnalisys.class);
        Editor<SalaryAnalisys> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        Collection<Button> editButtons = Collections.newSetFromMap(new HashMap<>());
        NumberField amountField = new NumberField();


        grid.addColumn(SalaryAnalisys::getDescription).setHeader("Detalle").setAutoWidth(true).setKey("detail");
        Grid.Column<SalaryAnalisys> fieldAmount = grid.addColumn(SalaryAnalisys::getAmount).setHeader("Monto").setAutoWidth(true).setKey("amount");

        fieldAmount.setEditorComponent(amountField);
        binder.forField(amountField).bind(SalaryAnalisys::getAmount,SalaryAnalisys::setAmount);

        Grid.Column<SalaryAnalisys> editorColumn = grid.addComponentColumn(s ->{
           Button edit = new Button("Editar");
           edit.addClickListener(e ->{
               if(s.getPriority().equals(1) || s.getPriority().equals(4)){
                   amountField.setEnabled(false);
               }else {
                   editor.editItem(s);
                   amountField.focus();
                   amountField.setEnabled(true);
               }
               priority = s.getPriority();
           });
           edit.setEnabled(!editor.isOpen());
           editButtons.add(edit);
           return edit;
        });
        editor.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Guardar", e-> {
            if(month.equals("Mes 1")){
               if(priority.equals(0)) {
                   salaryAnalysisMonth1List = getDiscountAmount(amountField, salaryAnalysisMonth1List);
               }
               salaryAnalysisMonth1List = getAvailableSalary(salaryAnalysisMonth1List,priority,amountField);
            }else if(month.equals("Mes 2")){
                if(priority.equals(0)) {
                    salaryAnalysisMonth2List = getDiscountAmount(amountField, salaryAnalysisMonth2List);
                }
                salaryAnalysisMonth2List = getAvailableSalary(salaryAnalysisMonth2List,priority,amountField);
            }else if(month.equals("Mes 3")){
                if(priority.equals(0)) {
                    salaryAnalysisMonth3List = getDiscountAmount(amountField, salaryAnalysisMonth3List);
                }
                salaryAnalysisMonth3List = getAvailableSalary(salaryAnalysisMonth3List,priority,amountField);
            }
            editor.save();
            if(month.equals("Mes 1")) {
                dataProviderMonth1.setSortOrder(SalaryAnalisys::getPriority, SortDirection.ASCENDING);
                dataProviderMonth1.refreshAll();
            }else if(month.equals("Mes 2")) {
                dataProviderMonth2.setSortOrder(SalaryAnalisys::getPriority, SortDirection.ASCENDING);
                dataProviderMonth2.refreshAll();
            }else if(month.equals("Mes 3")) {
                dataProviderMonth3.setSortOrder(SalaryAnalisys::getPriority, SortDirection.ASCENDING);
                dataProviderMonth3.refreshAll();
            }


        });
        Button cancel = new Button("Cancelar", e-> editor.cancel());
        cancel.addClassName("cancel");
        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");
        Div buttons = new Div(save,cancel);
        editorColumn.setEditorComponent(buttons);

        VerticalLayout verticalLayout = new VerticalLayout();
        Label label = new Label(month);
        verticalLayout.add(typeClient,label,grid);
        return verticalLayout;
    }

    private List<SalaryAnalisys> getDiscountAmount(NumberField amountField, List<SalaryAnalisys> auxSalaryMonthList) {
        if(amountField.isEmpty()) amountField.setValue(0.0);
        SalaryAnalisys s = auxSalaryMonthList.stream().filter(p -> p.getDescription().equals("(-) Descuentos de Ley"))
                .findFirst().get();
//        SalaryAnalisys sa = auxSalaryMonthList.stream().filter(p -> p.getDescription().equals("Salario Bruto"))
//                .findFirst().get();
        s.setAmount(calculateDescount(amountField.getValue()));
        auxSalaryMonthList.removeIf(p -> p.getPriority().equals(1));
        auxSalaryMonthList.add(s);
        return auxSalaryMonthList;
    }

    private List<SalaryAnalisys> getAvailableSalary(List<SalaryAnalisys> auxSalaryMonthList, Integer priority, NumberField amount){
        Double positive = 0.0;
        Double negative = 0.0;
        if (amount.isEmpty()) amount.setValue(0.0);
        for(SalaryAnalisys s:auxSalaryMonthList){
            if(s.getPriority().equals(0) || s.getPriority().equals(3)){
                if(s.getPriority().equals(priority)){
                    positive+=amount.getValue();
                }else {
                    positive += s.getAmount();
                }
            }else if(s.getPriority().equals(1) || s.getPriority().equals(2)){
                if(s.getPriority().equals(priority)){
                    negative+=amount.getValue();
                }else {
                    negative += s.getAmount();
                }
            }
        }
        SalaryAnalisys salaryAnalisys = auxSalaryMonthList.stream().filter(p -> p.getPriority().equals(4)).findFirst().get();
        salaryAnalisys.setAmount(positive-negative);

        auxSalaryMonthList.removeIf(p->p.getPriority().equals(4));
        auxSalaryMonthList.add(salaryAnalisys);

        return auxSalaryMonthList;
    }

    private Double calculateDescount(Double amount) {
        List<Parameter> parameterList = parameterRestTemplate.getParametersByCategory("DESCUENTO SUELDOS");
        Double descount = 0.0;
        boolean flag = false;
        for(Parameter p:parameterList){
            String[] splitSalary = p.getDescription().split(";");
            Double salary = Double.parseDouble(splitSalary[1]);
            if(splitSalary[0].trim().equals("<=") && !flag){
                if(amount<=salary ){
                    descount = amount * Double.parseDouble(p.getValue());
                    flag = true;
                }
            }else if(splitSalary[0].trim().equals("<") && !flag){
                if(amount<salary ){
                    descount = amount * Double.parseDouble(p.getValue());
                    flag = true;
                }
            }

            if(splitSalary[0].trim().equals(">=") && !flag){
                if(amount>=salary ){
                    descount = amount * Double.parseDouble(p.getValue());
                    flag = true;
                }
            }else  if(splitSalary[0].trim().equals(">")&& !flag){
                if(amount>salary ){
                    descount = amount * Double.parseDouble(p.getValue());
                    flag = true;
                }
            }
        }

        return  Math.round(descount*100.0)/100.0;
    }


    private DetailsDrawer createLayout(){




        VerticalLayout gridMonth1 = gridSalary("Mes 1");
        VerticalLayout gridMonth2 = gridSalary("Mes 2");
        VerticalLayout gridMonth3 = gridSalary("Mes 3");

        VerticalLayout mainLayout = new VerticalLayout();

        HorizontalLayout detailLayout = new HorizontalLayout();
        detailLayout.setWidthFull();
        detailLayout.addAndExpand(gridMonth1,gridMonth2,gridMonth3);

        mainLayout.add(detailLayout);

        FlexBoxLayout layout = new FlexBoxLayout();
        layout.setFlexDirection(FlexDirection.COLUMN);
        layout.add(mainLayout);

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeightFull();
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);

        return detailsDrawer;
    }

//    private void filter(String filter){
//        if(dataProviderSalaryAnalisys.getItems().size()>0){
//            dataProviderSalaryAnalisys.setFilterByValue(SalaryAnalisys::getNumberMonth,filter);
//        }
//    }

    private void initListSalary(){
        String[] details = new String[5];
        details[0]="Salario Bruto";
        details[1]="(-) Descuentos de Ley";
        details[2]="(-) Otros descuentos";
        details[3]="(+)  Otros ingresos";
        details[4]="Liquido disponible";
        for(int i=1;i<=3;i++) {
            for(int j=0;j<=4;j++) {
                SalaryAnalisys salaryAnalisys = new SalaryAnalisys();
                salaryAnalisys.setTypeClient("");
                salaryAnalisys.setDescription(details[j]);
                salaryAnalisys.setAmount(0.0);
                salaryAnalisys.setNumberMonth("Mes " + i);
                salaryAnalisys.setPriority(j);

                if(i==1) {
                    salaryAnalysisMonth1List.add(salaryAnalisys);
                }else if(i==2){
                    salaryAnalysisMonth2List.add(salaryAnalisys);
                }else{
                    salaryAnalysisMonth3List.add(salaryAnalisys);
                }
            }
        }
    }

    private Component createContent(DetailsDrawer component) {
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }


}
