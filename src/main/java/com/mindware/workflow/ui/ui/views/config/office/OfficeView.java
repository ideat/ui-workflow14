package com.mindware.workflow.ui.ui.views.config.office;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.Signatories;
import com.mindware.workflow.ui.backend.entity.config.CityProvince;
import com.mindware.workflow.ui.backend.entity.config.Province;
import com.mindware.workflow.ui.backend.rest.cityProvince.CityProvinceRestTemplate;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
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
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Route(value="oficinas", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Oficinas")
public class OfficeView extends SplitViewFrame implements RouterLayout {

    private Grid<Office> grid;
    private Grid<Signatories> gridSignatories;

    private ListDataProvider<Signatories> dataProviderSignatories;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;

    private Button btnNew;

    private TextField filterText;

    private OfficeRestTemplate restTemplate = new OfficeRestTemplate();

    private CityProvinceRestTemplate cityProvinceRestTemplate = new CityProvinceRestTemplate();

    private Binder<Office> binder;

    private DetailsDrawerFooter footer;

    private Office current;

    private ComboBox<Office> cmbRoot;

    private ComboBox cmbTypeOffice;

    private List<Office> officeList;

    private OfficeDataProvider dataProvider;

    private List<String> listProvinces;
    private ComboBox<String> cmbProvince;

    public OfficeView(){

//        officeList = restTemplate.getAllOffice();
//        dataProvider = new OfficeDataProvider(officeList);
        getListOffice();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridOffice());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }


    private void getListOffice(){
        listProvinces = new ArrayList<>();
        officeList = new ArrayList<>(restTemplate.getAllOffice()) ;
        dataProvider = new OfficeDataProvider(officeList);
//        return dataProvider;
    }

    private Grid createGridOffice(){
        grid = new Grid<>();
        grid.setId("offices");
        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));

//        dataProvider = DataProvider.ofCollection(restTemplate.getAllOffice());
        grid.setDataProvider(dataProvider);
//        grid.setItems(officeList);
        grid.setHeight("100%");

        grid.addColumn(Office::getInternalCode).setFlexGrow(0)
                .setHeader("Codigo oficina").setSortable(true).setFrozen(true)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createOfficeInfo))
                .setHeader("Nombre oficina").setWidth(UIUtils.COLUMN_WIDTH_XS)
                .setTextAlign(ColumnTextAlign.START).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createOfficeLocation)).setFlexGrow(0)
                .setHeader("Ciudad").setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true)
                .setTextAlign(ColumnTextAlign.START).setFlexGrow(0);
        grid.addColumn(Office::getTypeOffice).setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_XL)
                .setHeader("Tipo oficina").setSortable(true).setResizable(true);


        return grid;
    }


    private Component createOfficeInfo(Office office){
        ListItem item = new ListItem(
                UIUtils.createInitials(office.getInitials()),office.getName().trim(),
                office.getAddress().trim());
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createOfficeLocation(Office office){
        ListItem item = new ListItem(
                UIUtils.createInitials(office.getInitialsLocation()),office.getCity().trim(),
                office.getProvince().trim());
        item.setHorizontalPadding(false);
        return item;
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro Nombre, Ciudad, Tipo, Codigo oficina");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));


        btnNew = new Button("Nueva Oficina");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e -> {
            showDetails(new Office());
        });
        btnNew.setEnabled(GrantOptions.grantedOption("Oficinas"));


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

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

        // Header
        Tab details = new Tab("Datos oficina");
        Tab signatories = new Tab("Representantes");

        Tabs tabs = new Tabs(details,signatories);
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.addSelectedChangeListener(e->{
            Tab selectedTab = tabs.getSelectedTab();
            if (selectedTab.equals(details)){
                detailsDrawer.setContent(createDetails(grid.getSelectionModel().getFirstSelectedItem().get()));
            }else if(selectedTab.equals(signatories)){
                try {

                    detailsDrawer.setContent(createGridSignatories(grid.getSelectionModel().getFirstSelectedItem().get()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        detailsDrawerHeader = new DetailsDrawerHeader("",tabs);
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        // Footer
        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e -> {
            if (current!=null && binder.writeBeanIfValid(current)) {
                if (cmbTypeOffice.getValue().equals("AGENCIA") || cmbTypeOffice.getValue().equals("AGENCIA MOVIL") || cmbTypeOffice.getValue().equals("SUCURSAL")) {
                    try {
                        current.setIdRoot(cmbRoot.getValue().getId());
                    }catch (Exception ex){
                        UIUtils.showNotification("Error: seleccione oficina principal");
                        cmbRoot.focus();
                        return;
                    }
                }
                if (current.getId()==null)
                    current.setSignatorie("[]");
                Office result = restTemplate.addOffice(current);
                if (result !=null) {
                    UIUtils.showNotification("Cambios salvados.");
                    if(current.getId()==null) {
                        officeList.add(result);
                        grid.getDataProvider().refreshAll();
                    }else{
                        grid.getDataProvider().refreshItem(current);
                    }

                    detailsDrawer.hide();

                }else{
                    UIUtils.showNotification("Error al guardar cambios." );
                }

            }else{
              UIUtils.showNotification("Datos incorrecto, verifique nuevamente");
            }

        });
        footer.addCancelListener(e -> {
            footer.saveState(false);
            detailsDrawer.hide();
        });
        detailsDrawer.setFooter(footer);

        return detailsDrawer;

    }


    private void showDetails(Office office){
        current = office;
        detailsDrawerHeader.setTitle(office.getName());
        detailsDrawer.setContent(createDetails(current));
        detailsDrawer.show();
    }

    private List<Office> filterNameSucursal(){

        return dataProvider.getItems().stream()
                .filter(office -> office.getTypeOffice().toUpperCase().equals("SUCURSAL") ||
                        office.getTypeOffice().toUpperCase().equals("CENTRAL"))
                .collect(Collectors.toList());

    }

    private Optional<Office> findOfficeById(){
        Optional<Office> office = Optional.empty();
        if(current.getTypeOffice().equals("SUCURSAL") || current.getTypeOffice().equals("CENTRAL")) {

            office = dataProvider.getItems().stream()
                    .filter(o -> o.getIdRoot().equals(current.getIdRoot()) && o.getId().equals(current.getIdRoot()))
                    .findFirst();
            return office;
        }else{
            office = dataProvider.getItems().stream()
                    .filter(o -> o.getId().equals(current.getIdRoot()) && o.getCity().equals(current.getCity()))
                    .findFirst();
            return office;
        }
    }

    private FormLayout createDetails(Office office){
        if (office.getCity()!=null) {
            listProvinces = getProvinces(office.getCity());
        }
        TextField txtName = new TextField();
        txtName.setValue(Optional.ofNullable(office.getName()).orElse(""));
        txtName.setWidth("100%");

        TextField txtInternalCode = new TextField();
        txtInternalCode.setValue(Optional.ofNullable(String.valueOf(office.getInternalCode())).orElse("0"));
        txtInternalCode.setRequired(true);
        txtInternalCode.setWidth("100%");

        TextField txtAddress = new TextField();
        txtAddress.setValue(Optional.ofNullable(office.getAddress()).orElse(""));
        txtAddress.setRequired(true);
        txtAddress.setWidth("100%");

        ComboBox<String> cmbCity = new ComboBox<>();
        cmbCity.setItems(getAllCities());
        cmbCity.setValue(Optional.ofNullable(office.getCity()).orElse(""));
        cmbCity.setWidth("100%");
        cmbCity.setRequired(true);
        cmbCity.addValueChangeListener(e ->{
            listProvinces = getProvinces(e.getValue());
            cmbProvince.clear();
            cmbProvince.setItems(listProvinces);
        });


        cmbProvince = new ComboBox<>();
        cmbProvince.setItems(listProvinces);
        cmbProvince.setValue(Optional.ofNullable(office.getProvince()).orElse(""));
        cmbProvince.setWidth("100%");
        cmbProvince.setRequired(true);

        TextField txtPhone = new TextField();
        txtPhone.setValue(Optional.ofNullable(office.getPhone()).orElse(""));
        txtPhone.setWidth("100%");
        txtPhone.setRequired(true);


        cmbRoot = new ComboBox<>();
        cmbRoot.setItems(filterNameSucursal());
        cmbRoot.setItemLabelGenerator(Office::getName);
        cmbRoot.addValueChangeListener(event ->{
            if (Optional.ofNullable(event.getValue()).orElse(current).equals(Optional.ofNullable(event.getOldValue()).orElse(current))) {
                footer.saveState(false);
            }else{
                footer.saveState(true && GrantOptions.grantedOption("Oficinas"));
            }
            if(event.getValue()!=null) {
                current.setIdRoot(event.getValue().getId());
            }else{
                UIUtils.showNotification("Error: seleccione la oficina principal");
                cmbRoot.focus();
            }
        });
        cmbRoot.setValue(findOfficeById().orElse(null));


        cmbTypeOffice = new ComboBox();
        cmbTypeOffice.setItems("CENTRAL","SUCURSAL","AGENCIA","AGENCIA MOVIL");
        cmbTypeOffice.setValue(Optional.ofNullable(office.getTypeOffice()).orElse(""));
        cmbTypeOffice.setRequired(true);
        cmbTypeOffice.addValueChangeListener(e ->{

           if (e.getValue().equals("AGENCIA") || e.getValue().equals("AGENCIA MOVIL") || e.getValue().equals("SUCURSAL") ){
               cmbRoot.setEnabled(true);
           }else{
               cmbRoot.clear();
               cmbRoot.setEnabled(false);
           }
        });


        //Binder office
        binder = new BeanValidationBinder<>(Office.class);
        binder.forField(txtName).asRequired("Nombre no puede ser omitido").bind(Office::getName,Office::setName);
        binder.forField(txtInternalCode).asRequired("Codigo Interno no puede ser omitido").withConverter(new StringToIntegerConverter("De ingresar un numero")).bind(Office::getInternalCode,Office::setInternalCode);
        binder.forField(txtAddress).asRequired("Direccion no puede ser omitida").bind(Office::getAddress,Office::setAddress);
        binder.forField(cmbCity).asRequired("Ciudad no puede ser omitida").bind(Office::getCity,Office::setCity);
        binder.forField(cmbProvince).asRequired("Provincia no puede ser omitida").bind(Office::getProvince,Office::setProvince);
        binder.forField(txtPhone).asRequired("Telefono no puede ser omitido").bind(Office::getPhone,Office::setPhone);
//        binder.forMemberField(cmbRoot).bind("idRoot");
        binder.forField(cmbTypeOffice).asRequired("Tipo oficina no puede ser omitdo").bind("typeOffice");
        binder.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Oficinas"));
        });


        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        form.addFormItem(txtName,"Nombre");
        form.addFormItem(txtInternalCode,"Código");
        form.addFormItem(txtAddress,"Dirección");
        form.addFormItem(txtPhone,"Telefono");
        form.addFormItem(cmbCity,"Ciudad");
        form.addFormItem(cmbProvince,"Provincia");
        form.addFormItem(cmbTypeOffice,"Tipo oficina");
        form.addFormItem(cmbRoot,"Oficina principal");

        return form;
    }

    private Grid createGridSignatories(Office office) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String jsonSignatories = office.getSignatorie();
        List<Signatories> signatories = Arrays.asList(mapper.readValue(jsonSignatories,Signatories[].class));
        gridSignatories = new Grid<>();
        dataProviderSignatories = DataProvider.ofCollection(signatories);
        gridSignatories.setDataProvider(dataProviderSignatories);
        gridSignatories.setHeight("100%");

        gridSignatories.addColumn(Signatories::getName).setFlexGrow(0).setHeader("Nombre")
                .setWidth(UIUtils.COLUMN_WIDTH_L);
        gridSignatories.addColumn(Signatories::getPosition).setFlexGrow(0).setHeader("Cargo")
                .setWidth(UIUtils.COLUMN_WIDTH_L);
        gridSignatories.addColumn(new ComponentRenderer<>(this::createActiveSignatorie))
                .setHeader("Estado").setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS)
                .setTextAlign(ColumnTextAlign.END);


//        layout.add(btnNew,gridSignatories);
//        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        return gridSignatories;
    }

    private Component createActiveSignatorie(Signatories signatories){
        Icon icon;
        if (signatories.getState().equals("ACTIVO")){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private List<String> getAllCities(){

        List<CityProvince> cityProvinceList = cityProvinceRestTemplate.getAll();
        List<String> citiesList = new ArrayList<>();
        for(CityProvince cityProvince:cityProvinceList){
            citiesList.add(cityProvince.getCity());
        }
        return citiesList;
    }

    @SneakyThrows
    private List<String> getProvinces(String city){
        List<String> provinceList = new ArrayList<>();
        CityProvince cityProvince = cityProvinceRestTemplate.getByCity(city);
        String provinces = cityProvince.getProvinces();
        ObjectMapper mapper = new ObjectMapper();
        List<Province>listProvinces = mapper.readValue(provinces, new TypeReference<List<Province>>() {});
        provinceList.clear();
        for(Province province:listProvinces){
            provinceList.add(province.getName());
        }

        return provinceList;
    }



}
