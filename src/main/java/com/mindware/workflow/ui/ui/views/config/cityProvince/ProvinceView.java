package com.mindware.workflow.ui.ui.views.config.cityProvince;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.CityProvince;
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
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Route(value = "province", layout = MainLayout.class)
@PageTitle("Provincias")
public class ProvinceView extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {
    private CityProvinceRestTemplate restTemplate;

    private ObjectMapper mapper;
    private Map<String, List<String>> param;
    private List<Province> provinceList;
    private ListDataProvider<Province> dataProvider;
    private Button btnNew;
    private Grid<Province> grid;
    private Province current;
    private Province initial;
    private CityProvince cityProvince;
    private Binder<Province> binder;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;


    @SneakyThrows
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        mapper = new ObjectMapper();
        restTemplate  = new CityProvinceRestTemplate();
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        param = queryParameters.getParameters();

        String jsonCityProvince = param.get("city-provinces").get(0);

        cityProvince = mapper.readValue(jsonCityProvince,CityProvince.class);
        String jsonProvince = cityProvince.getProvinces();
        provinceList = mapper.readValue(jsonProvince, new TypeReference<List<Province>>() {});

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        dataProvider = new ListDataProvider<>(provinceList);

        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
    }


    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nueva Provincia");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.setEnabled(GrantOptions.grantedOption("Ciudad-Provincias"));
        btnNew.addClickListener(e -> {
            showDetails(new Province());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private void showDetails(Province province) {
        current = province;
        initial = province;
        detailsDrawerHeader.setTitle(province.getName()==null?"Nueva":province.getName());
        detailsDrawer.setContent(createDetails(current));
        detailsDrawer.show();
        binder.readBean(current);
    }

    private FormLayout createDetails(Province province) {
        NumberField externalCode = new NumberField();
        externalCode.setWidth("100%");
        externalCode.setRequiredIndicatorVisible(true);

        TextField provinceName = new TextField();
        provinceName.setWidth("100%");
        provinceName.setRequiredIndicatorVisible(true);
        provinceName.setRequired(true);

        binder = new Binder<>(Province.class);
        binder.forField(externalCode).asRequired("Codigo externo es requerido")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(Province::getExternalCode,Province::setExternalCode);
        binder.forField(provinceName).asRequired("Provincia es requerida")
                .bind(Province::getName,Province::setName);
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
        FormLayout.FormItem externalCodeItem = form.addFormItem(externalCode,"Codigo externo");
        FormLayout.FormItem provinceNameItem = form.addFormItem(provinceName,"Provincia");
        UIUtils.setColSpan(2,externalCodeItem);
        UIUtils.setColSpan(2,provinceNameItem);

        return form;
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridProvince());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGridProvince() {
        grid = new Grid<>();
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
        grid.setDataProvider(dataProvider);
        grid.setHeight("100%");

        grid.addColumn(Province::getExternalCode).setFlexGrow(1)
                .setHeader("Codigo Externo").setSortable(true)
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(Province::getName).setFlexGrow(1)
                .setHeader("Provincia").setSortable(true)
                .setResizable(true).setAutoWidth(true);

        return grid;
    }

    private DetailsDrawer createDetailsDrawer() {
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(binder.writeBeanIfValid(current)){
                if(current.getId()==null){
                   current.setId(UUID.randomUUID());
                }else{
                   provinceList.remove(initial);
                }
                try {
                    provinceList.add(current);
                    String jsonProvince = mapper.writeValueAsString(provinceList);
                    cityProvince.setProvinces(jsonProvince);
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }
                detailsDrawer.hide();
                dataProvider.refreshAll();
                try {
                    restTemplate.add(cityProvince);
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }

            }
        });

        footer.addCancelListener(event ->{
           footer.saveState(false);
           detailsDrawer.hide();
        });
        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }
}
