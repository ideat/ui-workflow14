package com.mindware.workflow.ui.ui.views.config.exceptions;

import com.mindware.workflow.ui.backend.entity.exceptions.Exceptions;
import com.mindware.workflow.ui.backend.entity.exceptions.UserAuthorizer;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "exceptionsView", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista de solicitudes para registrar excepciones")
public class ExceptionsView extends ViewFrame {
    private ExceptionsDataProvider dataProvider;

    private List<Exceptions> exceptionsList;

    private ExceptionsRestTemplate restTemplate = new ExceptionsRestTemplate();

    private TextField filterText;

    private ExceptionsView(){
        getExceptionsList();
        setViewHeader(createTopBar());
        setViewContent(createContent());
    }

    private void getExceptionsList(){
        exceptionsList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new ExceptionsDataProvider(exceptionsList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout (createLayout());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Codigo, Tipo excepcion, Descripcion");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.setWidth("100%");
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        Button btnNew = new Button("Nueva");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setEnabled(GrantOptions.grantedOption("Excepciones"));
        btnNew.addClickListener(e ->{
            viewRegister(new Exceptions());
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.add(btnNew);
        topLayout.setSpacing(true);

        return topLayout;
    }

    private VerticalLayout createLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Exceptions> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        grid.addSelectionListener(e ->{
            e.getFirstSelectedItem().ifPresent(this::viewRegister);
        });

        grid.addColumn(Exceptions::getInternalCode).setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true).setResizable(true).setWidth(UIUtils.COLUMN_WIDTH_M)
                .setHeader("Codigo");
        grid.addColumn(Exceptions::getTypeException).setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true).setResizable(true).setFlexGrow(1).setHeader("Tipo");
        grid.addColumn(Exceptions::getLimitTime).setSortable(true).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_S).setHeader("Tiempo");
        grid.addColumn(Exceptions::getDescription).setResizable(true)
                .setFlexGrow(1).setHeader("Excepcion");
        grid.addColumn(new ComponentRenderer<>(this::createStateExceptions)).setResizable(true)
                .setFlexGrow(0).setHeader("Estado");
        layout.add(grid);

        return layout;
    }

    private Component createStateExceptions(Exceptions exceptions){
        Icon icon;
        if (exceptions.getState().equals("ACTIVO")) {
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private void viewRegister(Exceptions exceptions){
        if(exceptions.getId()==null){
            UI.getCurrent().navigate(ExceptionsRegister.class,"NUEVO");
        }else{
            UI.getCurrent().navigate(ExceptionsRegister.class,exceptions.getInternalCode());
        }
    }
}
