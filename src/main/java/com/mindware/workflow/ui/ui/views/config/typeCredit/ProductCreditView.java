package com.mindware.workflow.ui.ui.views.config.typeCredit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.ProductTypeCredit;
import com.mindware.workflow.ui.backend.entity.config.TypeCredit;
import com.mindware.workflow.ui.backend.rest.typeCredit.TypeCreditRestTemplate;
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
import com.mindware.workflow.ui.ui.util.LumoStyles;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Route(value = "productCredit", layout = MainLayout.class)
@PageTitle("Productos credito")
public class ProductCreditView extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {
    private TypeCreditRestTemplate restTemplate;
    private ObjectMapper mapper;
    private Map<String, List<String>> param;
    private List<ProductTypeCredit>  productTypeCreditList;
    private ProductTypeCredit current;
    private ProductTypeCredit initial;
    private TypeCredit typeCredit;

    private Binder<ProductTypeCredit> binder;
    private ListDataProvider<ProductTypeCredit> dataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    @SneakyThrows
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        mapper = new ObjectMapper();
        restTemplate = new TypeCreditRestTemplate();
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        param = queryParameters.getParameters();

        typeCredit = restTemplate.getByExternalCode(param.get("external-code").get(0));
        String jsonProductTypeCredit = typeCredit.getProductTypeCredit();

        productTypeCreditList = mapper.readValue(jsonProductTypeCredit, new TypeReference<List<ProductTypeCredit>>() {});
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        dataProvider = new ListDataProvider<>(productTypeCreditList);
        AppBar appBar = initBar();
        appBar.setTitle(param.get("external-code").get(0));

        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());

    }

    private AppBar initBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e-> UI.getCurrent().navigate(TypeCreditView.class));

        return appBar;
    }

    private HorizontalLayout createTopBar() {
        Button btnNew = new Button();
        btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.setEnabled(GrantOptions.grantedOption("Tipo de Credito"));
        btnNew.addClickListener(e->showDetails(new ProductTypeCredit()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private void showDetails(ProductTypeCredit productTypeCredit){
        current = productTypeCredit;
        initial = productTypeCredit;
        detailsDrawerHeader.setTitle(productTypeCredit.getDescription()==null?"Nuevo":productTypeCredit.getDescription());
        detailsDrawer.setContent(createDetails(current));
        detailsDrawer.show();
        binder.readBean(current);
    }

    private FormLayout createDetails(ProductTypeCredit productTypeCredit){
        NumberField externalcode = new NumberField();
        externalcode.setWidthFull();
        externalcode.setRequiredIndicatorVisible(true);

        TextField description = new TextField();
        description.setWidthFull();
        description.setRequired(true);
        description.setRequiredIndicatorVisible(true);

        binder = new BeanValidationBinder<>(ProductTypeCredit.class);
        binder.forField(externalcode).asRequired("Codigo externo es requerido")
                .withConverter(new UtilValues.DoubleToIntegerConverter())
                .bind(ProductTypeCredit::getExternalCode,ProductTypeCredit::setExternalCode);
        binder.forField(description).asRequired("Descripcion es requerida")
                .bind(ProductTypeCredit::getDescription,ProductTypeCredit::setDescription);
        binder.addStatusChangeListener(event ->{
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges= binder.hasChanges();
            footer.saveState(isValid && hasChanges & GrantOptions.grantedOption("Tipo de Credito"));
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        FormLayout.FormItem externalCodeItem = form.addFormItem(externalcode,"Codigo externo");
        FormLayout.FormItem descriptionItem = form.addFormItem(description,"Descripcion");
        UIUtils.setColSpan(2,externalCodeItem);
        UIUtils.setColSpan(2,descriptionItem);

        return form;
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridProductTypeCredit());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGridProductTypeCredit(){
        Grid<ProductTypeCredit> grid = new Grid<>();
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
        grid.setDataProvider(dataProvider);
        grid.setHeight("100%");

        grid.addColumn(ProductTypeCredit::getExternalCode)
                .setFlexGrow(1)
                .setHeader("Codigo Externo")
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(ProductTypeCredit::getDescription)
                .setFlexGrow(1)
                .setHeader("Descripcion")
                .setSortable(true)
                .setResizable(true)
                .setAutoWidth(true);
        return grid;
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
            if(binder.writeBeanIfValid(current)){
                if(current.getId()==null){
                    current.setId(UUID.randomUUID());
                }else{
                    productTypeCreditList.remove(initial);
                }

                productTypeCreditList.add(current);
                try {
                    String jsonProductTypeCredit = mapper.writeValueAsString(productTypeCreditList);
                    typeCredit.setProductTypeCredit(jsonProductTypeCredit);
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }
                detailsDrawer.hide();
                dataProvider.refreshAll();

                try {
                    restTemplate.add(typeCredit);
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }
            }
        });

        footer.addCancelListener(e ->{
           footer.saveState(false);
           detailsDrawer.hide();
        });
        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }
}
