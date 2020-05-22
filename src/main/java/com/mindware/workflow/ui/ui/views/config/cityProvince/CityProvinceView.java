package com.mindware.workflow.ui.ui.views.config.cityProvince;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowingcode.vaadin.addons.errorwindow.ErrorWindow;
import com.mindware.workflow.ui.backend.entity.config.CityProvince;
import com.mindware.workflow.ui.backend.entity.config.ExchangeRate;
import com.mindware.workflow.ui.backend.entity.config.Province;
import com.mindware.workflow.ui.backend.rest.cityProvince.CityProvinceRestTemplate;
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
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "cityProvince", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Ciudades")
public class CityProvinceView extends SplitViewFrame implements RouterLayout {
    private Grid<CityProvince> grid;
    private ListDataProvider<CityProvince> dataProvider;

    private Button btnNew;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private CityProvinceRestTemplate restTemplate;
    private Binder<CityProvince> binder;

    private List<CityProvince> cityProvinceList;
    private CityProvince current;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        restTemplate = new CityProvinceRestTemplate();
        getListCityProvince();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());

    }

    private void getListCityProvince() {
        cityProvinceList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new ListDataProvider<>(cityProvinceList);
    }

    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nueva Ciudad");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.setEnabled(GrantOptions.grantedOption("Ciudad-Provincias"));
        btnNew.addClickListener(e -> {
            showDetails(new CityProvince());
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
        FlexBoxLayout content = new FlexBoxLayout(createGridCityProvince());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGridCityProvince() {
        grid = new Grid<>();
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));

        grid.setDataProvider(dataProvider);
        grid.setHeight("100%");
        grid.addColumn(CityProvince::getExternalCode).setFlexGrow(1)
                .setHeader("Codigo externo").setSortable(true)
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(CityProvince::getCity).setFlexGrow(1)
                .setHeader("Ciudad").setSortable(true)
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createButtonProvince))
                .setFlexGrow(1).setTextAlign(ColumnTextAlign.START);

        return grid;
    }

    private Component createButtonProvince(CityProvince cityProvince){
        Button btn = new Button("Provincias");
        btn.addClickListener(e ->{
            ObjectMapper mapper = new ObjectMapper();
            Map<String,List<String>> param = new HashMap<>();
            List<String> provinces = new ArrayList<>();
            List<String> city = new ArrayList<>();
            try {
                String jsonCityProvince = mapper.writeValueAsString(cityProvince);
                provinces.add(jsonCityProvince);
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
            city.add(cityProvince.getCity());
            param.put("city-provinces",provinces);
            param.put("city",city);

            QueryParameters qp = new QueryParameters(param);

            UI.getCurrent().navigate("province",qp);
        });

        return btn;
    }

    private void showDetails(CityProvince cityProvince) {
        current = cityProvince;
        detailsDrawerHeader.setTitle(cityProvince.getCity());
        detailsDrawer.setContent(createDetails(current));
        detailsDrawer.show();
        binder.readBean(current);
    }

    private FormLayout createDetails(CityProvince cityProvince) {
        NumberField externalCode = new NumberField();
        externalCode.setWidth("100%");
        externalCode.setRequiredIndicatorVisible(true);

        TextField city = new TextField();
        city.setWidth("100%");
        city.setRequiredIndicatorVisible(true);
        city.setRequired(true);

        binder = new Binder<>(CityProvince.class);
        binder.forField(city).asRequired("Ciudad es requerida").bind(CityProvince::getCity,CityProvince::setCity);
        binder.forField(externalCode).asRequired("Codigo externo es requerido")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(CityProvince::getExternalCode,CityProvince::setExternalCode);
        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(isValid && hasChanges && GrantOptions.grantedOption("Ciudad-Provincias"));
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        form.addFormItem(externalCode,"Codigo externo");
        form.addFormItem(city,"Ciduad");
        UIUtils.setColSpan(2,externalCode);
        UIUtils.setColSpan(2,city);

        return form;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(current !=null && binder.writeBeanIfValid(current)){
                if(current.getId()==null){
                    current.setProvinces("[]");
                    try {
                        CityProvince result = (CityProvince) restTemplate.add(current);
                        cityProvinceList.add(result);
                        grid.getDataProvider().refreshAll();
                        UIUtils.showNotification("Datos de la Ciudad Registrados");
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                        ErrorWindow w = new ErrorWindow(jsonProcessingException,"Error al guardar,  Show Error Details detalla el Error");
                        w.open();
                    }
                }else{
                    try {
                        CityProvince result = (CityProvince) restTemplate.add(current);
                        grid.getDataProvider().refreshItem(current);
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                        ErrorWindow w = new ErrorWindow(jsonProcessingException,"Error al guardar,  Show Error Details detalla el Error");
                        w.open();
                    }
                }
                detailsDrawer.hide();
            }else{
                UIUtils.showNotification("Datos incorrectos, verifique");
            }
        });

        footer.addCancelListener(e -> {
            footer.saveState(false);
            detailsDrawer.hide();
        });
        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }

}
