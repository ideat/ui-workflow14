package com.mindware.workflow.ui.ui.views.patrimonialStatement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.CreditRequestApplicant;
import com.mindware.workflow.ui.backend.entity.patrimonialStatement.PatrimonialStatement;
import com.mindware.workflow.ui.backend.entity.config.FieldsStructure;
import com.mindware.workflow.ui.backend.entity.config.TemplateForm;
import com.mindware.workflow.ui.backend.rest.creditRequestApplicant.CreditRequestAplicantRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementRestTemplate;
import com.mindware.workflow.ui.backend.rest.templateForm.TemplateFormRestTemplate;
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
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.*;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "patrimonial-statement", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Declaracion Patrimonial")
public class PatrimonialStatementView extends SplitViewFrame implements HasUrlParameter<String> {

    private TemplateFormRestTemplate templateFormRestTemplate;

    private CreditRequestAplicantRestTemplate creditRequestAplicantRestTemplate;

    private PatrimonialStatementRestTemplate restTemplate;

    private ArrayList<FieldsStructure> fieldsAvailableList = new ArrayList<>();

    private Grid<PatrimonialStatement> grid;

    private ListDataProvider<PatrimonialStatement> dataProvider;

    private FormLayout formData;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;
    private PatrimonialStatement current;
    private List<PatrimonialStatement> patrimonialStatementList;
    private CreditRequestApplicant creditRequestApplicant;

    private String category;
    private String element;
    private  Map<String,List<String>> param;
    private BeanValidationBinder<PatrimonialStatement> binder;


    @PropertyId("fieldText1")
    private TextField fieldText1;
    @PropertyId("fieldText2")
    private TextField fieldText2;
    @PropertyId("fieldText3")
    private TextField fieldText3;
    @PropertyId("fieldText4")
    private TextField fieldText4;
    @PropertyId("fieldText5")
    private TextField fieldText5;
    @PropertyId("fieldText6")
    private TextField fieldText6;
    @PropertyId("fieldText7")
    private TextField fieldText7;
    @PropertyId("fieldText8")
    private TextField fieldText8;
    @PropertyId("fieldText9")
    private TextField fieldText9;
    @PropertyId("fieldText10")
    private TextField fieldText10;
    @PropertyId("fieldText11")
    private TextField fieldText11;
    @PropertyId("fieldText12")
    private TextField fieldText12;
    @PropertyId("fieldText13")
    private TextField fieldText13;
    @PropertyId("fieldText14")
    private TextField fieldText14;
    @PropertyId("fieldText15")
    private TextField fieldText15;
    @PropertyId("fieldText16")
    private TextField fieldText16;

    @PropertyId("fieldInteger1")
    private NumberField fieldInteger1;
    @PropertyId("fieldInteger2")
    private NumberField fieldInteger2;
    @PropertyId("fieldBoolean1")
    private  RadioButtonGroup<String> fieldBoolean1;
    @PropertyId("fieldBoolean2")
    private  RadioButtonGroup<String> fieldBoolean2;
    @PropertyId("fieldBoolean3")
    private  RadioButtonGroup<String> fieldBoolean3;
    @PropertyId("fieldDouble1")
    private NumberField fieldDouble1;
    @PropertyId("fieldDouble2")
    private NumberField fieldDouble2;
    @PropertyId("fieldDouble3")
    private NumberField fieldDouble3;
    @PropertyId("fieldDouble4")
    private NumberField fieldDouble4;
    @PropertyId("fieldDouble5")
    private NumberField fieldDouble5;
    @PropertyId("fieldDate1")
    private DatePicker fieldDate1;
    @PropertyId("fieldDate2")
    private DatePicker fieldDate2;
    @PropertyId("fieldDate3")
    private DatePicker fieldDate3;
    @PropertyId("fieldDate4")
    private DatePicker fieldDate4;
    @PropertyId("fieldSelection1")
    private ComboBox<String> fieldSelection1;
    @PropertyId("fieldSelection2")
    private ComboBox<String> fieldSelection2;
    @PropertyId("fieldSelection3")
    private ComboBox<String> fieldSelection3;

    @Override
    protected void onAttach(AttachEvent attachment){
        super.onAttach(attachment);

        AppBar appBar = initBar();
        appBar.setTitle(category + "-" + element);
        UI.getCurrent().getPage().setTitle(category + "-" + element);

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        templateFormRestTemplate = new TemplateFormRestTemplate();
        restTemplate =  new PatrimonialStatementRestTemplate();
        creditRequestAplicantRestTemplate = new CreditRequestAplicantRestTemplate();

        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        param = queryParameters.getParameters();

        category = param.get("category").get(0);
        element = param.get("element").get(0);
        Optional<TemplateForm> templateForm = templateFormRestTemplate.getByNameAndCategory(param.get("element").get(0),param.get("category").get(0));
        creditRequestApplicant = creditRequestAplicantRestTemplate.getCreditRequestApplicant(
                Integer.parseInt(param.get("number-request").get(0)), Integer.parseInt(param.get("number-applicant").get(0)), param.get("type-relation").get(0));
        patrimonialStatementList = new ArrayList<>(restTemplate.getByIdCreditRequestApplicantCategoryElement(creditRequestApplicant.getId(),
                category,element));

        dataProvider = new ListDataProvider<>(patrimonialStatementList);
        if(templateForm.isPresent()) {
            String fieldStructure = templateForm.get().getFieldsStructure();
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<FieldsStructure> fieldsStructureList = mapper.readValue(fieldStructure,new TypeReference<List<FieldsStructure>>(){} );
                fieldsAvailableList = fieldsStructureList.stream().filter(f -> f.getPosition()>0 && f.isVisible()==true)
                                            .collect(Collectors.toCollection(ArrayList::new));



            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        setViewContent(createContent());
        setViewHeader(createTopBar());
    }

    private AppBar initBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(
                e -> {
                    Map<String,List<String>> parameter = new HashMap<>();
                    List<String> numberRequestList = new ArrayList<>();
                    numberRequestList.add(param.get("number-request").get(0));
                    parameter.put("number-request",numberRequestList);
                    List<String> numberApplicantList = new ArrayList<>();
                    numberApplicantList.add(param.get("number-applicant").get(0));
                    parameter.put("number-applicant",numberApplicantList);
                    List<String> typeRelationList = new ArrayList<>();
                    typeRelationList.add(param.get("type-relation").get(0));
                    parameter.put("type-relation",typeRelationList);
                    List<String> idCreditRequestList = new ArrayList<>();
                    idCreditRequestList.add(param.get("id-credit-request").get(0));
                    parameter.put("id-credit-request",idCreditRequestList);
                    parameter.put("full-name",param.get("full-name"));
                    parameter.put("id-applicant",param.get("id-applicant"));
                    parameter.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
                    QueryParameters qp = new QueryParameters(parameter);

                    UI.getCurrent().navigate("patrimonial-statement-options",qp);
                }
        );
        return appBar;
    }

    private HorizontalLayout createTopBar(){
        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e ->{
           setViewDetails(createDetailsDrawer());
           setViewDetailsPosition(Position.BOTTOM);
           showDetails(new PatrimonialStatement());

        });
        btnNew.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,btnNew);
        topLayout.setPadding(true);
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

    private Grid createGrid(){
        grid = new Grid<>();
        grid.setWidth("100%");
        grid.setHeightFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(event ->{

           event.getFirstSelectedItem().ifPresent(this::showDetails);
        });

        if (category.equals("ACTIVO") || category.equals("PASIVO") || category.equals("INGRESOS") || category.equals("EGRESOS") ){

                for (FieldsStructure f : fieldsAvailableList) {
                    if (f.getComponentName().equals("fieldText1")) {
                        grid.addColumn(TemplateRenderer.<PatrimonialStatement> of(
                                "<div style='white-space:normal'> <small> [[item.getFieldText1]] </small> </div>")
                                .withProperty("getFieldText1", PatrimonialStatement::getFieldText1)).setHeader(f.getComponentLabel())
                                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);

                    } else if (f.getComponentName().equals("fieldText2")) {
                        grid.addColumn(TemplateRenderer.<PatrimonialStatement> of(
                                "<div style='white-space:normal'> <small> [[item.getFieldText2]] </small> </div>")
                                .withProperty("getFieldText2", PatrimonialStatement::getFieldText2)).setHeader(f.getComponentLabel())
                                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
                    } else if (f.getComponentName().equals("fieldBoolean1")) {
                        grid.addColumn(new ComponentRenderer<>(this::createActiveBoolean1))
                                .setFlexGrow(0).setHeader(f.getComponentLabel()).setWidth(UIUtils.COLUMN_WIDTH_M);
                    } else if (f.getComponentName().equals("fieldDouble1")) {
                        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Valor")
                                .setSortable(true).setFlexGrow(0).setResizable(true).setWidth(UIUtils.COLUMN_WIDTH_L);
                    }
                }

        }

        if(category.equals("INGRESOS") && element.contains("UTILIDAD")){
            grid.addColumn(new ComponentRenderer<>(this::createButtonDetailEarning))
                    .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Detalle");
        }

        grid.addColumn(new ComponentRenderer<>(this::createButtonDelete))
                .setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_S);

        return grid;

    }

    private Component createButtonDelete(PatrimonialStatement patrimonialStatement){
        Button btn = new Button();
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR);
        btn.setIcon((VaadinIcon.TRASH.create()));
        btn.addClickListener(e ->{
            restTemplate.delete(patrimonialStatement.getId().toString());
            patrimonialStatementList.remove(patrimonialStatement);
            dataProvider.refreshAll();
            UIUtils.showNotification("Registro borrado");

        });
        btn.setEnabled(GrantOptions.grantedOption("Declaracion Patrimonial"));
        return btn;
    }

    private Component createButtonDetailEarning(PatrimonialStatement patrimonialStatement){
        Button btn = new Button();
        btn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        btn.setIcon(VaadinIcon.CALC_BOOK.create());
        btn.addClickListener(e ->{
            Map<String,List<String>> p = new HashMap<>();
            List<String> idCreditRequestApplicantList = new ArrayList<>();
            idCreditRequestApplicantList.add(creditRequestApplicant.getId().toString());
            List<String> activity = new ArrayList<>();
            activity.add(patrimonialStatement.getFieldText1());
            List<String> idPatrimonialStatement = new ArrayList<>();
            idPatrimonialStatement.add(patrimonialStatement.getId().toString());

            p.put(("id-patrimonial-statement"),idPatrimonialStatement);
            p.put("id-credit-request-applicant",idCreditRequestApplicantList);
            p.put("element",param.get("element"));
            p.put("id-applicant",param.get("id-applicant"));
            p.put("category",param.get("category"));
            p.put("activity", activity);

            QueryParameters qp = new QueryParameters(p);

           UI.getCurrent().navigate("earning",qp);
        });

        return btn;
    }


    private Component createAmount(PatrimonialStatement patrimonialStatement){
        if(patrimonialStatement.getFieldDouble1()==null){
            return UIUtils.createAmountLabel(0.0);
        }
        Double amount = patrimonialStatement.getFieldDouble1();
        return UIUtils.createAmountLabel(amount);
    }

    private Component createActiveBoolean1(PatrimonialStatement patrimonialStatement){
        Icon icon;
        if(patrimonialStatement.getFieldBoolean1().equals("SI")){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.saveState(true && GrantOptions.grantedOption("Declaracion Patrimonial"));
        footer.addSaveListener(e -> {
            if(current!=null && binder.writeBeanIfValid(current)){
                current.setCategory(category);
                current.setElementCategory(element);
                current.setIdCreditRequestApplicant(creditRequestApplicant.getId());
                if (current.getFieldDouble1()==null)
                    current.setFieldDouble1(0.0);

                PatrimonialStatement result = restTemplate.add(current);
                if (result !=null){
                    UIUtils.showNotification("Cambios salvados");
                    if(current.getId()==null){
                        patrimonialStatementList.add(result);
                        grid.getDataProvider().refreshAll();
                    }else{
                        grid.getDataProvider().refreshItem(current);
                    }
                    detailsDrawer.hide();
                }else{
                    UIUtils.showNotification("Error al guardar cambios");
                }
            }else{
                UIUtils.showNotification("Datos incorrectos, verifique y vuelva a guardar");
            }



        });
        footer.addCancelListener(e -> detailsDrawer.hide());

        detailsDrawer.setFooter(footer);

        return detailsDrawer;


    }

    private void showDetails(PatrimonialStatement patrimonialStatement){
        current = patrimonialStatement;
        detailsDrawerHeader.setTitle("");
        detailsDrawer.setContent(createDetails(current));
        binder.readBean(current);
        detailsDrawer.show();
    }

    private Optional<PatrimonialStatement> findPatrimonialStatementById(){
        return dataProvider.getItems().stream()
                .filter(patrimonialStatement -> patrimonialStatement.getId().equals(current.getId()))
                .findFirst();
    }

    private FormLayout createDetails(PatrimonialStatement patrimonialStatement){


        fieldText1 = new TextField();
        fieldText1.setWidth("100%");

        fieldText2 = new TextField();
        fieldText2.setWidth("100%");

        fieldText3 = new TextField();
        fieldText3.setWidth("100%");

        fieldText4 = new TextField();
        fieldText4.setWidth("100%");

        fieldText5 = new TextField();
        fieldText5.setWidth("100%");

        fieldText6 = new TextField();
        fieldText6.setWidth("100%");

        fieldText7 = new TextField();
        fieldText7.setWidth("100%");

        fieldText8 = new TextField();
        fieldText8.setWidth("100%");

        fieldText9 = new TextField();
        fieldText9.setWidth("100%");

        fieldText10 = new TextField();
        fieldText10.setWidth("100%");

        fieldText11 = new TextField();
        fieldText11.setWidth("100%");

        fieldText12 = new TextField();
        fieldText12.setWidth("100%");

        fieldText13 = new TextField();
        fieldText13.setWidth("100%");

        fieldText14 = new TextField();
        fieldText14.setWidth("100%");

        fieldText15 = new TextField();
        fieldText15.setWidth("100%");

        fieldText16 = new TextField();
        fieldText16.setWidth("100%");

        fieldInteger1 = new NumberField();
        fieldInteger1.setWidth("100%");

        fieldInteger2 = new NumberField();
        fieldInteger2.setWidth("100%");

        fieldBoolean1 = new RadioButtonGroup<>();
        fieldBoolean1.setItems("SI","NO");

        fieldBoolean2 = new RadioButtonGroup<>();
        fieldBoolean2.setItems("SI","NO");

        fieldBoolean3 = new RadioButtonGroup<>();
        fieldBoolean3.setItems("SI","NO");

        fieldDouble1 = new NumberField();
        fieldDouble1.setWidth("100%");

        fieldDouble2 = new NumberField();
        fieldDouble2.setWidth("100%");

        fieldDouble3 = new NumberField();
        fieldDouble3.setWidth("100%");

        fieldDouble4 = new NumberField();
        fieldDouble4.setWidth("100%");

        fieldDouble5 = new NumberField();
        fieldDouble5.setWidth("100%");

        fieldDate1 = new DatePicker();
        fieldDate1.setLocale(Locale.ENGLISH);

        fieldDate2 = new DatePicker();
        fieldDate2.setLocale(Locale.ENGLISH);

        fieldDate3 = new DatePicker();
        fieldDate3.setLocale(Locale.ENGLISH);

        fieldDate4 = new DatePicker();
        fieldDate4.setLocale(Locale.ENGLISH);

        fieldSelection1 = new ComboBox<>();
        fieldSelection1.setWidth("100%");

        fieldSelection2 = new ComboBox<>();
        fieldSelection2.setWidth("100%");

        fieldSelection3 = new ComboBox<>();
        fieldSelection3.setWidth("100%");

        formData = new FormLayout();
        formData.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px",2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("810px",3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px",4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        List<FieldsStructure> fieldsFilter= fieldsAvailableList.stream().filter(p -> p.getPosition()>0 && p.isVisible())
                .collect(Collectors.toList());

        Comparator<FieldsStructure> comparatorByPosition = (FieldsStructure f1, FieldsStructure f2) ->
                f1.getPosition().compareTo(f2.getPosition());
        Collections.sort(fieldsFilter,comparatorByPosition);

        binder = new BeanValidationBinder<>(PatrimonialStatement.class);

        for(FieldsStructure f : fieldsFilter){
           if (f.getComponentName().equals("fieldText1")){
               if(f.isRequired()){
                   fieldText1.setRequired(true);
                   fieldText1.setRequiredIndicatorVisible(true);
                   binder.forField(fieldText1).asRequired("Dato es requerido")
                           .bind(PatrimonialStatement::getFieldText1,PatrimonialStatement::setFieldText1);
               }else{
                   binder.forField(fieldText1).bind(PatrimonialStatement::getFieldText1,PatrimonialStatement::setFieldText1);
               }

               formData.addFormItem(fieldText1,f.getComponentLabel());
           }else
            if (f.getComponentName().equals("fieldText2")){
                if(f.isRequired()){
                    fieldText2.setRequired(true);
                    fieldText2.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText2).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText2,PatrimonialStatement::setFieldText2);
                }else{
                    binder.forField(fieldText2).bind(PatrimonialStatement::getFieldText2,PatrimonialStatement::setFieldText2);
                }
                formData.addFormItem(fieldText2,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText3")){
                if(f.isRequired()){
                    fieldText3.setRequired(true);
                    fieldText3.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText3).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText3,PatrimonialStatement::setFieldText3);
                }else{
                    binder.forField(fieldText3).bind(PatrimonialStatement::getFieldText3,PatrimonialStatement::setFieldText3);
                }
                formData.addFormItem(fieldText3,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText4")){
                if(f.isRequired()){
                    fieldText4.setRequired(true);
                    fieldText4.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText4).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText4,PatrimonialStatement::setFieldText4);
                }else{
                    binder.forField(fieldText4).bind(PatrimonialStatement::getFieldText4,PatrimonialStatement::setFieldText4);
                }
                formData.addFormItem(fieldText4,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText5")){
                if(f.isRequired()){
                    fieldText5.setRequired(true);
                    fieldText5.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText5).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText5,PatrimonialStatement::setFieldText5);
                }else{
                    binder.forField(fieldText5).bind(PatrimonialStatement::getFieldText5,PatrimonialStatement::setFieldText5);
                }
                formData.addFormItem(fieldText5,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText6")){
                if(f.isRequired()){
                    fieldText6.setRequired(true);
                    fieldText6.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText6).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText6,PatrimonialStatement::setFieldText6);
                }else{
                    binder.forField(fieldText6).bind(PatrimonialStatement::getFieldText6,PatrimonialStatement::setFieldText6);
                }
                formData.addFormItem(fieldText6,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText7")){
                if(f.isRequired()){
                    fieldText7.setRequired(true);
                    fieldText7.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText7).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText7,PatrimonialStatement::setFieldText7);
                }else{
                    binder.forField(fieldText7).bind(PatrimonialStatement::getFieldText7,PatrimonialStatement::setFieldText7);
                }
                formData.addFormItem(fieldText7,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText8")){
                if(f.isRequired()){
                    fieldText8.setRequired(true);
                    fieldText8.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText8).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText8,PatrimonialStatement::setFieldText8);
                }else{
                    binder.forField(fieldText8).bind(PatrimonialStatement::getFieldText8,PatrimonialStatement::setFieldText8);
                }
                formData.addFormItem(fieldText8,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText9")){
                if(f.isRequired()){
                    fieldText9.setRequired(true);
                    fieldText9.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText9).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText9,PatrimonialStatement::setFieldText9);
                }else{
                    binder.forField(fieldText9).bind(PatrimonialStatement::getFieldText9,PatrimonialStatement::setFieldText9);
                }
                formData.addFormItem(fieldText9,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText10")){
                if(f.isRequired()){
                    fieldText10.setRequired(true);
                    fieldText10.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText10).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText10,PatrimonialStatement::setFieldText10);
                }else{
                    binder.forField(fieldText10).bind(PatrimonialStatement::getFieldText10,PatrimonialStatement::setFieldText10);
                }
                formData.addFormItem(fieldText10,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText11")){
                if(f.isRequired()){
                    fieldText11.setRequired(true);
                    fieldText11.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText11).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText11,PatrimonialStatement::setFieldText11);
                }else{
                    binder.forField(fieldText11).bind(PatrimonialStatement::getFieldText11,PatrimonialStatement::setFieldText11);
                }
                formData.addFormItem(fieldText11,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText12")){
                if(f.isRequired()){
                    fieldText12.setRequired(true);
                    fieldText12.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText12).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText12,PatrimonialStatement::setFieldText12);
                }else{
                    binder.forField(fieldText12).bind(PatrimonialStatement::getFieldText12,PatrimonialStatement::setFieldText12);
                }
                formData.addFormItem(fieldText12,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText13")){
                if(f.isRequired()){
                    fieldText13.setRequired(true);
                    fieldText13.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText13).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText13,PatrimonialStatement::setFieldText13);
                }else{
                    binder.forField(fieldText13).bind(PatrimonialStatement::getFieldText13,PatrimonialStatement::setFieldText13);
                }
                formData.addFormItem(fieldText13,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText14")){
                if(f.isRequired()){
                    fieldText14.setRequired(true);
                    fieldText14.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText14).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText14,PatrimonialStatement::setFieldText14);
                }else{
                    binder.forField(fieldText14).bind(PatrimonialStatement::getFieldText14,PatrimonialStatement::setFieldText14);
                }
                formData.addFormItem(fieldText14,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText15")){
                if(f.isRequired()){
                    fieldText15.setRequired(true);
                    fieldText15.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText15).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText15,PatrimonialStatement::setFieldText15);
                }else{
                    binder.forField(fieldText15).bind(PatrimonialStatement::getFieldText15,PatrimonialStatement::setFieldText15);
                }
                formData.addFormItem(fieldText15,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldText16")){
                if(f.isRequired()){
                    fieldText16.setRequired(true);
                    fieldText16.setRequiredIndicatorVisible(true);
                    binder.forField(fieldText16).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldText16,PatrimonialStatement::setFieldText16);
                }else{
                    binder.forField(fieldText16).bind(PatrimonialStatement::getFieldText16,PatrimonialStatement::setFieldText16);
                }
                formData.addFormItem(fieldText16,f.getComponentLabel());
            }else

            if (f.getComponentName().equals("fieldSelection1")){
                if(f.isRequired()){
                    fieldSelection1.setRequired(true);
                    fieldSelection1.setRequiredIndicatorVisible(true);
                    binder.forField(fieldSelection1).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldSelection1,PatrimonialStatement::setFieldSelection1);
                }else{
                    binder.forField(fieldSelection1).bind(PatrimonialStatement::getFieldSelection1,PatrimonialStatement::setFieldSelection1);
                }
                String[] item = f.getValues().split(",");
                fieldSelection1.setItems(item);
                formData.addFormItem(fieldSelection1,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldSelection2")){
                if(f.isRequired()){
                    fieldSelection2.setRequired(true);
                    fieldSelection2.setRequiredIndicatorVisible(true);
                    binder.forField(fieldSelection2).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldSelection2,PatrimonialStatement::setFieldSelection2);
                }else{
                    binder.forField(fieldSelection2).bind(PatrimonialStatement::getFieldSelection2,PatrimonialStatement::setFieldSelection2);
                }
                String[] item = f.getValues().split(",");
                fieldSelection2.setItems(item);
                formData.addFormItem(fieldSelection2,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldSelection3")){
                if(f.isRequired()){
                    fieldSelection3.setRequired(true);
                    fieldSelection3.setRequiredIndicatorVisible(true);
                    binder.forField(fieldSelection3).asRequired("Dato es requerido")
                            .bind(PatrimonialStatement::getFieldSelection3,PatrimonialStatement::setFieldSelection3);
                }else{
                    binder.forField(fieldSelection3).bind(PatrimonialStatement::getFieldSelection3,PatrimonialStatement::setFieldSelection3);
                }
                String[] item = f.getValues().split(",");
                fieldSelection3.setItems(item);
                formData.addFormItem(fieldSelection3,f.getComponentLabel());
            }else    
            if (f.getComponentName().equals("fieldInteger1")){
                if(f.isRequired()){
                    fieldInteger1.setRequiredIndicatorVisible(true);
                    binder.forField(fieldInteger1).asRequired("Valor es requerido")
                            .withValidator(value -> value.intValue()>0,"Valor debe ser mayor a 0")
                            .withConverter(new UtilValues.DoubleToIntegerConverter())
                            .bind(PatrimonialStatement::getFieldInteger1,PatrimonialStatement::setFieldInteger1);
                }else{
                    binder.forField(fieldInteger1)
                            .withConverter(new UtilValues.DoubleToIntegerConverter())
                            .bind(PatrimonialStatement::getFieldInteger1,PatrimonialStatement::setFieldInteger1);
                }
                formData.addFormItem(fieldInteger1,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldInteger2")){
                if(f.isRequired()){
                    fieldInteger2.setRequiredIndicatorVisible(true);
                    binder.forField(fieldInteger2).asRequired("Valor es requerido")
                            .withConverter(new UtilValues.DoubleToIntegerConverter())
                            .withValidator(value -> value.intValue()>0,"Valor debe ser mayor a 0")
                            .bind(PatrimonialStatement::getFieldInteger2,PatrimonialStatement::setFieldInteger2);
                }else{
                    binder.forField(fieldInteger2)
                            .withConverter(new UtilValues.DoubleToIntegerConverter())
                            .bind(PatrimonialStatement::getFieldInteger1,PatrimonialStatement::setFieldInteger2);
                }
                formData.addFormItem(fieldInteger2,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldBoolean1")){
                if(f.isRequired()){
                    fieldBoolean1.setRequiredIndicatorVisible(true);
                    binder.forField(fieldBoolean1).asRequired("Marque una opcion")
                            .bind(PatrimonialStatement::getFieldBoolean1,PatrimonialStatement::setFieldBoolean1);
                }else {
                    binder.forField(fieldBoolean1).bind(PatrimonialStatement::getFieldBoolean1, PatrimonialStatement::setFieldBoolean1);
                }
                formData.addFormItem(fieldBoolean1,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldBoolean2")){
                if(f.isRequired()){
                    fieldBoolean2.setRequiredIndicatorVisible(true);
                    binder.forField(fieldBoolean2).asRequired("Marque una opcion")
                            .bind(PatrimonialStatement::getFieldBoolean2,PatrimonialStatement::setFieldBoolean2);
                }else {
                    binder.forField(fieldBoolean2).bind(PatrimonialStatement::getFieldBoolean2, PatrimonialStatement::setFieldBoolean2);
                }
                formData.addFormItem(fieldBoolean2,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldBoolean3")){
                if(f.isRequired()){
                    fieldBoolean3.setRequiredIndicatorVisible(true);
                    binder.forField(fieldBoolean3).asRequired("Marque una opcion")
                            .bind(PatrimonialStatement::getFieldBoolean3,PatrimonialStatement::setFieldBoolean3);
                }else {
                    binder.forField(fieldBoolean3).bind(PatrimonialStatement::getFieldBoolean3, PatrimonialStatement::setFieldBoolean3);
                }
                formData.addFormItem(fieldBoolean3,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDouble1")){
                if(f.isRequired()){
                    fieldDouble1.setRequiredIndicatorVisible(true);
                    if(element.contains("UTILIDAD")) {
                        fieldDouble1.setValue(0.0);
                        fieldDouble1.setReadOnly(true);
                        binder.forField(fieldDouble1).withNullRepresentation(0.0)
                                .bind(PatrimonialStatement::getFieldDouble1, PatrimonialStatement::setFieldDouble1);
                    }else {
                        binder.forField(fieldDouble1).withValidator(value -> value.doubleValue() >= 0.0, "Monto tiene que ser mayor a 0")
                                .withNullRepresentation(0.0)
                                .asRequired("Valor es requerido").bind(PatrimonialStatement::getFieldDouble1, PatrimonialStatement::setFieldDouble1);
                    }

                }
                formData.addFormItem(fieldDouble1,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDouble2")){
                if(f.isRequired()){
                    fieldDouble2.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDouble2).withValidator(value -> value.doubleValue()>0,"Valor tiene que ser mayor a 0")
                            .withNullRepresentation(0.0)
                            .asRequired("Valor es requerido")
                            .bind(PatrimonialStatement::getFieldDouble2,PatrimonialStatement::setFieldDouble2);
                }else{
                    binder.forField(fieldDouble2).withNullRepresentation(0.0)
                            .bind(PatrimonialStatement::getFieldDouble2,PatrimonialStatement::setFieldDouble2);
                }
                formData.addFormItem(fieldDouble2,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDouble3")){
                if(f.isRequired()){
                    fieldDouble3.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDouble3).withValidator(value -> value.doubleValue()>0,"Valor tiene que ser mayor a 0")
                            .withNullRepresentation(0.0)
                            .asRequired("Valor es requerido")
                            .bind(PatrimonialStatement::getFieldDouble3,PatrimonialStatement::setFieldDouble3);
                }else{
                    binder.forField(fieldDouble3).withNullRepresentation(0.0)
                            .bind(PatrimonialStatement::getFieldDouble3,PatrimonialStatement::setFieldDouble3);
                }
                formData.addFormItem(fieldDouble3,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDouble4")){
                if(f.isRequired()){
                    fieldDouble4.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDouble4).withValidator(value -> value.doubleValue()>0,"Valor tiene que ser mayor a 0")
                            .withNullRepresentation(0.0)
                            .asRequired("Valor es requerido")
                            .bind(PatrimonialStatement::getFieldDouble4,PatrimonialStatement::setFieldDouble4);
                }else{
                    binder.forField(fieldDouble4).withNullRepresentation(0.0)
                            .bind(PatrimonialStatement::getFieldDouble4,PatrimonialStatement::setFieldDouble4);
                }
                formData.addFormItem(fieldDouble4,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDouble5")){
                if(f.isRequired()){
                    fieldDouble5.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDouble5).withValidator(value -> value.doubleValue()>0,"Valor tiene que ser mayor a 0")
                            .withNullRepresentation(0.0)
                            .asRequired("Valor es requerido")
                            .bind(PatrimonialStatement::getFieldDouble5,PatrimonialStatement::setFieldDouble5);
                }else{
                    binder.forField(fieldDouble5).withNullRepresentation(0.0)
                            .bind(PatrimonialStatement::getFieldDouble5,PatrimonialStatement::setFieldDouble5);
                }
                formData.addFormItem(fieldDouble5,f.getComponentLabel());
            }else    
            if (f.getComponentName().equals("fieldDate1")){
                if(f.isRequired()){
                    fieldDate1.setRequired(true);
                    fieldDate1.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDate1).asRequired("Fecha es requerida")
                            .bind(PatrimonialStatement::getFieldDate1,PatrimonialStatement::setFieldDate1);
                }else {
                    binder.forField(fieldDate1).bind(PatrimonialStatement::getFieldDate1,PatrimonialStatement::setFieldDate1);
                }
                formData.addFormItem(fieldDate1,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDate2")){
                if(f.isRequired()){
                    fieldDate2.setRequired(true);
                    fieldDate2.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDate2).asRequired("Fecha es requerida")
                            .bind(PatrimonialStatement::getFieldDate2,PatrimonialStatement::setFieldDate2);
                }else {
                    binder.forField(fieldDate2).bind(PatrimonialStatement::getFieldDate2,PatrimonialStatement::setFieldDate2);
                }
                formData.addFormItem(fieldDate2,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDate3")){
                if(f.isRequired()){
                    fieldDate3.setRequired(true);
                    fieldDate3.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDate3).asRequired("Fecha es requerida")
                            .bind(PatrimonialStatement::getFieldDate3,PatrimonialStatement::setFieldDate3);
                }else {
                    binder.forField(fieldDate3).bind(PatrimonialStatement::getFieldDate3,PatrimonialStatement::setFieldDate3);
                }
                formData.addFormItem(fieldDate3,f.getComponentLabel());
            }else
            if (f.getComponentName().equals("fieldDate4")){
                if(f.isRequired()){
                    fieldDate4.setRequired(true);
                    fieldDate4.setRequiredIndicatorVisible(true);
                    binder.forField(fieldDate4).asRequired("Fecha es requerida")
                            .bind(PatrimonialStatement::getFieldDate4,PatrimonialStatement::setFieldDate4);
                }else {
                    binder.forField(fieldDate4).bind(PatrimonialStatement::getFieldDate4,PatrimonialStatement::setFieldDate4);
                }
                formData.addFormItem(fieldDate4,f.getComponentLabel());
            }    
        }
//        binder.bindInstanceFields(this);

        binder.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Declaracion Patrimonial"));
        });
        return formData;
    }

}
