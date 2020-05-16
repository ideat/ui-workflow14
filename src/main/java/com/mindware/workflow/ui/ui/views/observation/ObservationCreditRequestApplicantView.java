package com.mindware.workflow.ui.ui.views.observation;

import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.observation.dto.ObservationCreditRequestApplicant;
import com.mindware.workflow.ui.backend.rest.observationCreditRequestApplicant.ObservationCreditRequestApplicantRestTemplate;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.Badge;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "observacionCreditoApplicant", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Solicitudes de credito - Observaciones")
public class ObservationCreditRequestApplicantView extends ViewFrame implements RouterLayout{
    private Grid<ObservationCreditRequestApplicant> grid;
    private ObservationCreditRequestApplicantDataProvider dataProvider;
    private List<ObservationCreditRequestApplicant> observationCreditRequestApplicantList;
    private ObservationCreditRequestApplicantRestTemplate restTemplate = new ObservationCreditRequestApplicantRestTemplate();

    private TextField filterText;
    private Button btnNew;
    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        getListObservationCreditRequestApplicant();
        setViewHeader(createTopBar());
        setViewContent(createContent());

    }

    private void getListObservationCreditRequestApplicant(){
        String rol = VaadinSession.getCurrent().getAttribute("rol").toString();
        String login = VaadinSession.getCurrent().getAttribute("login").toString();
        Integer codOffice = Integer.parseInt(VaadinSession.getCurrent().getAttribute("idOffice").toString());
        if(rol.equals("OFICIAL")) {
            observationCreditRequestApplicantList = new ArrayList<>(restTemplate.getByUser(login));
        }else if(rol.equals("RIESGOS")){
            OfficeRestTemplate officeRestTemplate = new OfficeRestTemplate();
            Office office = officeRestTemplate.getByCode(codOffice);
            observationCreditRequestApplicantList = new ArrayList<>(restTemplate.getByCity(office.getCity()));
        }else{
            observationCreditRequestApplicantList  = new ArrayList<>(restTemplate.getAll());
        }
        dataProvider = new ObservationCreditRequestApplicantDataProvider(observationCreditRequestApplicantList);
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Nro solicitud, Nro Solicitante, Solicitante");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

//        btnNew = new Button("Nueva");
//        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
//        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
//        btnNew.addClickListener();

        HorizontalLayout  topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
//        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,btnNew);
        topLayout.setSpacing(true);
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
        grid.addSelectionListener(event ->{
            viewRegister(event.getFirstSelectedItem().get());
        });

        ComponentRenderer<Badge, ObservationCreditRequestApplicant> badgeRenderer = new ComponentRenderer<>(
                observationCreditRequestApplicant -> {
                    ObservationCreditRequestApplicant.State state = observationCreditRequestApplicant.getState();
                    Badge badge = new Badge(state.getName(),
                            state.getTheme());
                    UIUtils.setTooltip(state.getDesc(), badge);
                    return badge;
                });
        grid.addColumn(badgeRenderer).setHeader("Estado")
                .setAutoWidth(true)
                .setFlexGrow(1).setSortable(true).setResizable(true);
        grid.addColumn(ObservationCreditRequestApplicant::getNumberRequest)
                .setAutoWidth(true)
                .setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("# Solicitud");
        grid.addColumn(new ComponentRenderer<>(this::createNameInfo))
                .setAutoWidth(true)
                .setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Solicitante");
        grid.addColumn(ObservationCreditRequestApplicant::getCurrency)
                .setAutoWidth(true)
                .setFlexGrow(1).setSortable(true).setResizable(true)
                .setHeader("Moneda");
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Monto")
                .setSortable(true).setFlexGrow(1).setResizable(true)
                .setAutoWidth(true);
        return grid;
    }

    private Component createAmount(ObservationCreditRequestApplicant observationCreditRequestApplicant){
        Double amount = observationCreditRequestApplicant.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private Component createNameInfo(ObservationCreditRequestApplicant observationCreditRequestApplicant){
        ListItem item = new ListItem(
                UIUtils.createInitials(observationCreditRequestApplicant.getInitials()), observationCreditRequestApplicant.getFullName()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private void viewRegister(ObservationCreditRequestApplicant observationCreditRequestApplicant){
        Map<String,List<String>> param = new HashMap<>();
        List<String> numberRequest = new ArrayList<>();
        List<String> numberApplicant = new ArrayList<>();
        List<String> task = new ArrayList<>();

        numberApplicant.add(observationCreditRequestApplicant.getNumberApplicant().toString());
        numberRequest.add(observationCreditRequestApplicant.getNumberRequest().toString());
        task.add("INFORME ANALISTA");
        param.put("number-request",numberRequest);
        param.put("number-applicant",numberApplicant);
        param.put("task",task);

        QueryParameters qp = new QueryParameters(param);
        UI.getCurrent().navigate("register-observation",qp);
    }
}
