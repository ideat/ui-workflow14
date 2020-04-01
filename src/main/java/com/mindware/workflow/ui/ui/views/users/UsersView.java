package com.mindware.workflow.ui.ui.views.users;

import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.util.ArrayList;
import java.util.List;


@Route(value = "user-view", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Usuarios")
public class UsersView extends ViewFrame implements RouterLayout {

//    private Grid<Users> grid;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;
    private UserDataProvider dataProvider;
    private List<Users> usersList;
    private TextField filterText;
    private Button btnNew;

    UserRestTemplate restTemplate = new UserRestTemplate();


    public UsersView(){
        getUserList();
        setViewContent(createContent());
        setViewHeader(createTopBar());
    }

    private void getUserList(){
        usersList = new ArrayList<>(restTemplate.getAllUsers());
        dataProvider = new UserDataProvider(usersList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createLayout());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Login, Nombre, Estado, Rol, Cargo");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.setEnabled(GrantOptions.grantedOption("Usuarios"));
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e ->{
            UI.getCurrent().navigate(UsersRegister.class,"NUEVO");
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,btnNew);
        topLayout.setSpacing(true);

        return topLayout;
    }

    private VerticalLayout createLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();


        Grid<Users> grid = new Grid<>();

        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        grid.addSelectionListener(e ->{
            UI.getCurrent().navigate(UsersRegister.class,e.getFirstSelectedItem().get().getId().toString());
        });

        grid.addColumn(Users::getLogin).setFlexGrow(1).setFrozen(true)
                .setHeader("Login").setSortable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_XS).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createUserInfo)).setFlexGrow(1)
                .setHeader("Nombre").setWidth(UIUtils.COLUMN_WIDTH_XL);
        grid.addColumn(new ComponentRenderer<>(this::createActive))
                .setFlexGrow(1).setHeader("Estado")
                .setWidth(UIUtils.COLUMN_WIDTH_XS)
                .setTextAlign(ColumnTextAlign.END);
        grid.addColumn(Users::getRol).setFlexGrow(1)
                .setHeader("Rol").setSortable(true);
        grid.addColumn(Users::getPosition).setFlexGrow(1)
                .setHeader("Cargo").setSortable(true).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createDate)).setFlexGrow(1)
                .setHeader("Fecha cambio password").setWidth(UIUtils.COLUMN_WIDTH_S)
                .setTextAlign(ColumnTextAlign.CENTER);

        layout.add(grid);

        return layout;
    }

    private Component createUserInfo(Users users){
        ListItem item = new ListItem(
                UIUtils.createInitials(users.getInitials()), users.getFullName(),
                users.getEmail());
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createActive(Users users) {
        Icon icon;
        if (users.getState().equals("ACTIVO")) {
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createDate(Users users) {
        return new Span(UIUtils.formatDate(users.getDateUpdatePassword()));
    }


}
