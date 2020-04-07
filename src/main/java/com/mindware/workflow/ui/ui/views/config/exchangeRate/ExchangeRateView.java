package com.mindware.workflow.ui.ui.views.config.exchangeRate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindware.workflow.ui.backend.entity.config.ExchangeRate;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.rest.exchangeRate.ExchangeRateRestTemplate;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Route(value = "exchange-rate", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Tipo de cambio")
public class ExchangeRateView extends SplitViewFrame {

    private Grid<ExchangeRate> grid;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private DetailsDrawerFooter footer;

    private Button btnNew;
    private ExchangeRateRestTemplate restTemplate;
    private ParameterRestTemplate parameterRestTemplate;
    private Binder<ExchangeRate> binder;
    private ListDataProvider<ExchangeRate> dataProvider;
    private ExchangeRate current;

    private List<ExchangeRate> exchangeRateList;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getListExchangeRate();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());

    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGridExchangeRate());
        content.addClassName("grid-view");
        content.setHeightFull();
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private void getListExchangeRate(){
        restTemplate = new ExchangeRateRestTemplate();
        exchangeRateList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new ListDataProvider<>(exchangeRateList);
    }

    private HorizontalLayout createTopBar(){
        btnNew = new Button("Nuevo Tipo de Cambio");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.setEnabled(GrantOptions.grantedOption("Parametros"));
        btnNew.addClickListener(e -> {
            showDetails(new ExchangeRate());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END,btnNew);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }

    private Grid createGridExchangeRate(){
        grid = new Grid<>();
        grid.setMultiSort(true);
        grid.setHeightFull();
        grid.setWidthFull();

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));

        grid.setDataProvider(dataProvider);

        grid.addColumn(ExchangeRate::getCurrency).setFlexGrow(1).setHeader("Moneda")
                .setSortable(true).setResizable(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(ExchangeRate::getExchange).setFlexGrow(1).setHeader("Tipo cambio")
                .setSortable(true).setResizable(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(new LocalDateRenderer<>(ExchangeRate::getStartValidity, DateTimeFormatter.ofPattern("MMM dd, YYYY")))
                .setComparator(ExchangeRate::getStartValidity).setFlexGrow(1).setHeader("Inicio vigencia")
                .setResizable(true).setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(new LocalDateRenderer<>(ExchangeRate::getEndValidity, DateTimeFormatter.ofPattern("MMM dd, YYYY")))
                .setComparator(ExchangeRate::getEndValidity).setFlexGrow(1).setHeader("Fin vigencia")
                .setResizable(true).setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(new ComponentRenderer<>(this::createState)).setFlexGrow(1).setHeader("Estado");

        return grid;
    }

    private Component createState(ExchangeRate exchangeRate){
        Icon icon;
        if(exchangeRate.getState().equals("ACTIVO")){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private void showDetails(ExchangeRate exchangeRate){
        current = exchangeRate;
        detailsDrawerHeader.setTitle("Tipo cambio: " + exchangeRate.getExchange());
        detailsDrawer.setContent(createDetails(current));
        detailsDrawer.show();
        binder.readBean(current);
    }

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawer.setHeader(detailsDrawerHeader);

        footer = new DetailsDrawerFooter();
        footer.addSaveListener(e ->{
           if(current !=null && binder.writeBeanIfValid(current)){
               try {
                   ExchangeRate verif = restTemplate.getActiveExchangeRateByCurrency(current.getCurrency());
                   if(current.getId()==null){
                       if(verif.getId()!=null && current.getState().equals("ACTIVO")){
                           UIUtils.showNotification("Existe Tipo de cambio Activo en "+current.getCurrency() +
                                   " primero de de baja y posteriormente registre el nuevo tipo de cambio");
                       }else {
                           ExchangeRate result = (ExchangeRate) restTemplate.add(current);
                           exchangeRateList.add(result);
                           grid.getDataProvider().refreshAll();
                       }
                   }else{
                       if(current.getState().equals("ACTIVO") && verif.getId()!=null ){
                           UIUtils.showNotification("Existe Tipo de cambio Activo en "+current.getCurrency() +
                                   " primero de de baja y posteriormente registre el nuevo tipo de cambio");
                       }else {
                           ExchangeRate result = (ExchangeRate) restTemplate.add(current);
                           grid.getDataProvider().refreshItem(current);
                       }
                   }
                   detailsDrawer.hide();
               } catch (HttpClientErrorException ex) {
                   ex.printStackTrace();
                   UIUtils.showNotification(ex.getMessage());
               } catch (JsonProcessingException ex) {
                   ex.printStackTrace();
                   ex.printStackTrace();
                   UIUtils.showNotification(ex.getMessage());
               }
           }else{
               UIUtils.showNotification("Datos incorrectos, verifique nuevamente");
           }
        });

        footer.addCancelListener(e ->{
           footer.saveState(false);
           detailsDrawer.hide();
        });
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private FormLayout createDetails(ExchangeRate exchangeRate){

        ComboBox<String> currency = new ComboBox<>();
        currency.setWidth("100%");
        currency.setRequiredIndicatorVisible(true);
        currency.setItems("SUS","UFV");

        NumberField exchange = new NumberField();
        exchange.setWidth("100%");
        exchange.setRequiredIndicatorVisible(true);

        DatePicker startValidity = new DatePicker();
        startValidity.setWidth("100%");
        startValidity.setRequired(true);
        startValidity.setRequiredIndicatorVisible(true);

        DatePicker endValidity = new DatePicker();
        endValidity.setWidth("100%");
        endValidity.setRequired(true);
        endValidity.setRequiredIndicatorVisible(true);

        RadioButtonGroup<String> state = new RadioButtonGroup<>();
        state.setItems("ACTIVO","BAJA");
        state.setRequired(true);
        state.setRequiredIndicatorVisible(true);
        state.setValue(Optional.ofNullable(exchangeRate.getState()).orElse("").equals("ACTIVO") ? "ACTIVO":"BAJA");

        binder = new BeanValidationBinder<>(ExchangeRate.class);
        binder.forField(currency).asRequired("Moneda es requerida").bind(ExchangeRate::getCurrency,ExchangeRate::setCurrency);
        binder.forField(exchange).asRequired("Tipo de cambio es requerido").bind(ExchangeRate::getExchange,ExchangeRate::setExchange);
        binder.forField(startValidity).asRequired("Fecha inicio vigencia es requerida")
                .bind(ExchangeRate::getStartValidity,ExchangeRate::setStartValidity);
        binder.forField(endValidity).bind(ExchangeRate::getEndValidity,ExchangeRate::setEndValidity);
        binder.forField(state).asRequired("Estado es requerido").bind(ExchangeRate::getState,ExchangeRate::setState);
        binder.addStatusChangeListener(e ->{
            boolean isValid = !e.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            footer.saveState(hasChanges && isValid && GrantOptions.grantedOption("Tipo de Cambio"));
        });

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.S, LumoStyles.Padding.Top.S);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        formLayout.addFormItem(currency,"Moneda");
        formLayout.addFormItem(exchange,"Tipo Cambio Bs.");
        formLayout.addFormItem(startValidity,"Inicio vigencia");
        formLayout.addFormItem(endValidity,"Fin vigencia");
        formLayout.addFormItem(state,"Estado");
        return formLayout;

    }
}
