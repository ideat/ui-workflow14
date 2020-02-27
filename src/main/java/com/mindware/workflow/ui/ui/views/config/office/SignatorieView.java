package com.mindware.workflow.ui.ui.views.config.office;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.Signatories;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
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
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "signatorie", layout = MainLayout.class)
@PageTitle("Responsables Oficinas")
public class SignatorieView extends SplitViewFrame {

    private Grid<Office> grid;
    private Grid<Signatories> gridSignatories;
    private OfficeDataProvider dataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;

    private DetailsDrawer detailsDrawerSign;
    private DetailsDrawerHeader detailsDrawerHeaderSign;
    private List<Office> officeList;
    private Binder<Signatories> binder;
    private Office current;
    private Signatories currentSignatorie;
    private DetailsDrawerFooter footer;

    private OfficeRestTemplate restTemplate = new OfficeRestTemplate();
    private Button btnNew;

    private TextField filterText;

    public SignatorieView(){
        getListOffice();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawerSign());
        setViewDetailsPosition(Position.RIGHT);
    }

    private void getListOffice(){
        officeList = new ArrayList<>(restTemplate.getAllOffice()) ;
        dataProvider = new OfficeDataProvider(officeList);

    }


    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro Nombre, Ciudad, Tipo");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));


        btnNew = new Button("Nuevo Representante");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e -> {
            if (current !=null) {
                setViewDetails(createDetailsDrawer());
                setViewDetailsPosition(Position.BOTTOM);
                showDetails(new Signatories());
            }else{
                UIUtils.showNotification("Primero Seleccione una oficina");
            }
        });
        btnNew.setEnabled(GrantOptions.grantedOption("Responsables"));

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

        grid.setSizeFull();
        grid.setDataProvider(dataProvider);

        grid.addSelectionListener(event -> {
            if(event.getFirstSelectedItem().isPresent()) {
                event.getFirstSelectedItem()
                        .ifPresent(this::showSignatories);
                current = event.getFirstSelectedItem().get();
            }
        });

        grid.addColumn(new ComponentRenderer<>(this::createOfficeInfo))
                .setHeader("Nombre").setWidth(UIUtils.COLUMN_WIDTH_M)
                .setTextAlign(ColumnTextAlign.START).setResizable(true).setSortable(true);
        grid.addColumn(Office::getTypeOffice).setHeader("Tipo oficina").setSortable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.START);
        grid.addColumn(Office::getCity).setHeader("Ciudad").setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.START);



        return grid;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(event -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e -> {
            if (current !=null & binder.writeBeanIfValid(currentSignatorie)) {
                ObjectMapper mapper = new ObjectMapper();
                List<Signatories> signatoriesList = new ArrayList<>();
                try {
                    if (!current.getSignatorie().equals("[]"))
                        signatoriesList = new ArrayList<>(Arrays.asList(mapper.readValue(current.getSignatorie(),Signatories[].class)));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    if (currentSignatorie.getId()==null) {
                        currentSignatorie.setId(UUID.randomUUID());
                        signatoriesList.add(currentSignatorie);
                    }else{
                        List<Signatories> newList = signatoriesList.stream()
                                .filter(f -> !f.getId().equals(currentSignatorie.getId()))
                                .collect(Collectors.toList());
                        newList.add(currentSignatorie);
                        signatoriesList = newList;

                    }
                    String jsonSignatorie = mapper.writeValueAsString(signatoriesList);
                    current.setSignatorie(jsonSignatorie);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }

                restTemplate.updateOfficeSignature(current);
                UIUtils.showNotification("Cambios salvados");
                detailsDrawer.hide();
            }

        });
        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private DetailsDrawer createDetailsDrawerSign(){
        detailsDrawerSign = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

        detailsDrawerHeaderSign = new DetailsDrawerHeader("");
        detailsDrawerHeaderSign.addCloseListener(event -> detailsDrawerSign.hide());
        detailsDrawerSign.setHeader(detailsDrawerHeaderSign);

        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.addCancelListener(e -> detailsDrawerSign.hide());

        return detailsDrawerSign;
    }

    private Component createOfficeInfo(Office office){
        ListItem item = new ListItem(
                UIUtils.createInitials(office.getInitials()),office.getName().trim(),
                office.getAddress().trim());
        item.setHorizontalPadding(false);
        return item;
    }

    private void showDetails(Signatories signatories){
        currentSignatorie = signatories;
        detailsDrawerHeader.setTitle(current.getName() + ": "+ Optional.ofNullable(signatories.getName()).orElse(""));
        detailsDrawer.setContent(createDetails(currentSignatorie));
        detailsDrawer.show();
    }

    private void showSignatories(Office office)  {
        setViewDetails(createDetailsDrawerSign());
        setViewDetailsPosition(Position.RIGHT);
        detailsDrawerHeaderSign.setTitle(office.getName());
        try {
            detailsDrawerSign.setContent(createGridSignatories(office));
        } catch (IOException e) {
            e.printStackTrace();
        }
        detailsDrawerSign.show();
    }

    private FormLayout createDetails(Signatories signatories){
        TextField txtName = new TextField();
        txtName.setValue(Optional.ofNullable(signatories.getName()).orElse(""));
        txtName.setWidth("100%");

        TextField txtIdentiyCard = new TextField();
        txtIdentiyCard.setValue(Optional.ofNullable(signatories.getIdentifyCard()).orElse(""));
        txtIdentiyCard.setWidth("100%");

        TextField txtPosition = new TextField();
        txtPosition.setValue(Optional.ofNullable(signatories.getPosition()).orElse(""));
        txtPosition.setWidth("100%");

        RadioButtonGroup<String> rbState = new RadioButtonGroup<>();
        rbState.setItems("ACTIVO","BAJA");
        rbState.setValue(Optional.ofNullable(signatories.getState()).orElse("").equals("ACTIVO") ? "ACTIVO":"BAJA");

//        TextField txtNumPower = new TextField();
//        txtNumPower.setValue(Optional.ofNullable(signatories.getNumPower()).orElse(""));
//        txtNumPower.setWidth("100%");

        TextField txtNumNotary = new TextField();
        txtNumNotary.setValue(Optional.ofNullable(signatories.getNumNotary()).orElse(""));
        txtNumNotary.setWidth("100%");

        TextField txtNameNotary = new TextField();
        txtNameNotary.setValue(Optional.ofNullable(signatories.getNameNotary()).orElse(""));
        txtNameNotary.setWidth("100%");

        TextField txtJudicialDistrict = new TextField();
        txtJudicialDistrict.setValue(Optional.ofNullable(signatories.getJudicialDistrict()).orElse(""));
        txtJudicialDistrict.setWidth("100%");

        TextField txtNumTestimony = new TextField();
        txtNumTestimony.setValue(Optional.ofNullable(signatories.getNumTestimony()).orElse(""));
        txtNumTestimony.setWidth("100%");

        TextField txtDateTestimony = new TextField();
        txtDateTestimony.setValue(Optional.ofNullable(signatories.getDateTestimony()).orElse(""));
        txtDateTestimony.setWidth("100%");

        ComboBox cmbPriority = new ComboBox();
        cmbPriority.setItems("1","2");
        cmbPriority.setValue(Optional.ofNullable(signatories.getPriority()).orElse(""));
        cmbPriority.setWidth("100%");

        binder = new BeanValidationBinder<>(Signatories.class);
        binder.forField(txtName).asRequired("Nombre es requerido").bind(Signatories::getName,Signatories::setName);
        binder.forField(txtIdentiyCard).asRequired("Carnet es requerido").bind(Signatories::getIdentifyCard,Signatories::setIdentifyCard);
        binder.forField(txtPosition).asRequired("Cargo es requerido").bind(Signatories::getPosition,Signatories::setPosition);
        binder.forField(rbState).bind(Signatories::getState,Signatories::setState);
//        binder.forField(txtNumPower).asRequired("Numero poder es requerido").bind(Signatories::getNumPower,Signatories::setNumPower);
        binder.forField(txtNumNotary).asRequired("Numero notaria es requerido").bind(Signatories::getNumNotary,Signatories::setNumNotary);
        binder.forField(txtNameNotary).asRequired("Nombre notario es requerido").bind(Signatories::getNameNotary,Signatories::setNameNotary);
        binder.forField(txtJudicialDistrict).asRequired("Distrito Judicial es requerido").bind(Signatories::getJudicialDistrict,Signatories::setJudicialDistrict);
        binder.forField(txtNumTestimony).asRequired("Numero testimonio es requerido").bind(Signatories::getNumTestimony,Signatories::setNumTestimony);
        binder.forField(txtDateTestimony).asRequired("Fecha testimonio es requerida").bind(Signatories::getDateTestimony, Signatories::setDateTestimony);
        binder.forField(cmbPriority).asRequired("Prioridad es requerida").bind("priority");
        binder.addStatusChangeListener(event ->{
           boolean isValid = !event.hasValidationErrors();
           boolean hasChanges = binder.hasChanges();
           footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Responsables"));
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1096px", 4,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        form.addFormItem(txtName,"Nombre");
        form.addFormItem(txtIdentiyCard,"Carnet");
        form.addFormItem(txtPosition,"Cargo");
        form.addFormItem(rbState,"Estado");
//        form.addFormItem(txtNumPower,"Nro Poder");
        form.addFormItem(txtNumNotary,"Nro notaria");
        form.addFormItem(txtNameNotary,"Nombre notario");
        form.addFormItem(txtJudicialDistrict,"Nro distrito");
        form.addFormItem(txtNumTestimony,"Nro testimonio");
        form.addFormItem(txtDateTestimony,"Fecha testimonio");
        form.addFormItem(cmbPriority,"Prioridad");

        return form;
    }

    private Grid createGridSignatories(Office office) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String jsonSignatories = office.getSignatorie();
        List<Signatories> signatories = Arrays.asList(mapper.readValue(jsonSignatories,Signatories[].class));
        gridSignatories = new Grid<>();
        gridSignatories.setItems(signatories);
        gridSignatories.setHeight("100%");

//        gridSignatories.addSelectionListener(event -> event.getFirstSelectedItem()
//                .ifPresent(this::showDetails));

        gridSignatories.addColumn(Signatories::getName).setFlexGrow(0).setHeader("Nombre")
                .setWidth(UIUtils.COLUMN_WIDTH_L)
                .setTextAlign(ColumnTextAlign.START);
//        gridSignatories.addColumn(Signatories::getPosition).setFlexGrow(0).setHeader("Cargo")
//                .setWidth(UIUtils.COLUMN_WIDTH_XS);
        gridSignatories.addColumn(new ComponentRenderer<>(this::createActiveSignatorie))
                .setHeader("Estado").setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_XS)
                .setTextAlign(ColumnTextAlign.START);
        gridSignatories.addColumn(new ComponentRenderer<>(this::createButtonSignatorie))
                .setHeader("Editar").setFlexGrow(0)
                .setWidth(UIUtils.COLUMN_WIDTH_S)
                .setTextAlign(ColumnTextAlign.START);

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

    private Component createButtonSignatorie(Signatories signatories){
        Button btn = new Button();
        btn.setIcon(VaadinIcon.EDIT.create());
        btn.addClickListener(e -> {
            setViewDetails(createDetailsDrawer());
            setViewDetailsPosition(Position.BOTTOM);
            showDetails(signatories);
        });

        return btn;
    }
}
