package com.mindware.workflow.ui.ui.views.applicant;

import com.mindware.workflow.ui.backend.entity.Applicant;
import com.mindware.workflow.ui.backend.rest.applicant.ApplicantRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "applicant", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Aplicante")
public class ApplicantView extends ViewFrame implements RouterLayout {
    private Grid<Applicant> grid;

    private ApplicantDataProvider dataProvider;

    private Button btnNew;

    private TextField filterText;

    private ComboBox<String> cmbTypePerson;

    private Applicant current;

    private List<Applicant> applicantList;

    private ApplicantRestTemplate restTemplate = new ApplicantRestTemplate();


    public ApplicantView(){
        getListApplicant();
        setViewHeader(createTopBar());
        setViewContent(createContent());
    }

    private void getListApplicant(){
        applicantList = new ArrayList<>(restTemplate.getAllApplicants());
        dataProvider = new ApplicantDataProvider(applicantList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setWidthFull();
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro Nombre, Nro Solicitante, Carnet, Ocupacion, Fecha registro");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        cmbTypePerson = new ComboBox<>();
        List<String> list = new ArrayList<>();
        list.add("natural"); list.add("juridica");
        cmbTypePerson.setItems(list);
        cmbTypePerson.setPlaceholder("Tipo Persona");
        cmbTypePerson.setRequired(true);

        btnNew = new Button("Nuevo");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e -> {
            if(cmbTypePerson.getValue()!=null && !cmbTypePerson.getValue().isEmpty()) {
                viewRegister(new Applicant());
            }else{
                UIUtils.showNotification("Seleccione el tipo de Persona");
                cmbTypePerson.focus();
            }
        });

        btnNew.setEnabled(GrantOptions.grantedOption("Solicitantes"));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.add(cmbTypePerson);
        topLayout.add(btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,cmbTypePerson,btnNew);
        topLayout.setSpacing(true);

        return topLayout;
    }

    private Grid createGrid(){
        grid = new Grid<>();
        grid.setId("applicants");

        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::viewRegister));

        grid.setDataProvider(dataProvider);
        grid.setSizeFull();

        grid.addColumn(new ComponentRenderer<>(this::createInfoApplicant))
                .setHeader("Solicitante").setWidth(UIUtils.COLUMN_WIDTH_XXL)
                .setSortable(true).setResizable(true).setFlexGrow(0);
        grid.addColumn(Applicant::getNumberApplicant).setFlexGrow(0).setHeader("# Aplicante")
                .setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_S);
        grid.addColumn(Applicant::getIdCardComplet).setFlexGrow(0).setHeader("Carnet")
                .setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createInfoContact)).setHeader("Contacto")
                .setWidth(UIUtils.COLUMN_WIDTH_L).setFlexGrow(0);
        grid.addColumn(Applicant::getProfession).setFlexGrow(0).setSortable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setHeader("Ocupacion").setSortable(true);
        grid.addColumn(new LocalDateRenderer<>(Applicant::getRegisterDate, DateTimeFormatter.ofPattern("MMM dd, YYYY")))
                .setComparator(Applicant::getRegisterDate).setFlexGrow(0).setHeader("Registro")
                .setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true).setSortable(true);

        return grid;
    }

    private Component createInfoApplicant(Applicant applicant){
        ListItem item = new ListItem(applicant.getFullName(), applicant.getHomeaddress());
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createInfoContact(Applicant applicant){
        ListItem item = new ListItem("Celular: "+ applicant.getCellphone(), "Telf casa: " +applicant.getHomephone());
        item.setHorizontalPadding(false);
        return item;
    }

    private void viewRegister(Applicant applicant){
        if (applicant.getId()==null) {
            UI.getCurrent().navigate(ApplicantRegister.class, "NUEVO-"+cmbTypePerson.getValue());
        }else{
            UI.getCurrent().navigate(ApplicantRegister.class, applicant.getId().toString());

        }

    }

}
