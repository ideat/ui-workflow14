package com.mindware.workflow.ui.ui.views.rol;

import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.util.ArrayList;
import java.util.List;

@Route(value = "roles", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista Roles")
public class RolView extends ViewFrame implements RouterLayout {

    private ListDataProvider<Rol> dataProvider;

    private List<Rol> rolList;

    private RolRestTemplate restTemplate = new RolRestTemplate();

    private RolView(){
        getRolList();
        setViewContent(createContent());

    }

    private void getRolList(){
        rolList = new ArrayList<>(restTemplate.getAllRols());
        dataProvider = new ListDataProvider<>(rolList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout (createLayout());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private VerticalLayout createLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Button btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);
        btnNew.setEnabled(GrantOptions.grantedOption("Roles"));
        btnNew.addClickListener(e->{
            UI.getCurrent().navigate(RolRegister.class,"NUEVO");
        });

        Grid<Rol> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        grid.addSelectionListener(e ->{
            UI.getCurrent().navigate(RolRegister.class,e.getFirstSelectedItem().get().getId().toString());
        });

        grid.addColumn(Rol::getName).setFlexGrow(1).setHeader("Nombre Rol")
                .setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(Rol::getScope).setFlexGrow(1).setHeader("Alcance")
                .setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(Rol::getDescription).setFlexGrow(1).setHeader("Descripcion del Rol")
                .setResizable(true).setAutoWidth(true);

        layout.add(btnNew,grid);

        return layout;
    }


}
