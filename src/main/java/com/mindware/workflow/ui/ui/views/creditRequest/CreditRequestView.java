package com.mindware.workflow.ui.ui.views.creditRequest;

import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.entity.dto.CreditRequestApplicantDto;
import com.mindware.workflow.ui.backend.rest.creditRequest.CreditRequestRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditRequestApplicantDto.CreditRequestApplicantDtoRestTemplate;
import com.mindware.workflow.ui.backend.util.GrantOptions;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.Badge;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "Solicitud", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Solicitud credito")
public class CreditRequestView extends SplitViewFrame implements RouterLayout {

    private Grid<CreditRequestApplicantDto> grid;
    private CreditRequestApplicantDataProvider dataProvider;
    private List<CreditRequestApplicantDto> creditRequestList;
    private CreditRequestApplicantDtoRestTemplate restTemplate = new CreditRequestApplicantDtoRestTemplate();
    private CreditRequestRestTemplate creditRequestRestTemplate = new CreditRequestRestTemplate();


    private Button btnNew;
    private TextField filterText;
    private String idApplicant;
    private String fullName;
    private Integer numberApplicant;
    private Integer numberRequest;
    private AppBar appBar;
    private List<String> listTabs = new ArrayList<>();

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        setId("CreditRequestView");
//        String user =  MainLayout.get().user;
        getListCreditRequest();
        initAppBar();
        setViewHeader(createTopBar());
        setViewContent(createContent());
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private void getListCreditRequest(){
        String login="";
        if(VaadinSession.getCurrent().getAttribute("rol").toString().equals("OFICIAL")){
            login = VaadinSession.getCurrent().getAttribute("login").toString();
        }else{
            login = "%";
        }

        creditRequestList = new ArrayList<>(restTemplate.getByLoginUserTypeRelation(login,"deudor"));
        dataProvider = new CreditRequestApplicantDataProvider(creditRequestList);
    }

    private void initAppBar(){
        MainLayout.get().getAppBar().reset();
        appBar = MainLayout.get().getAppBar();
//        listTabs.clear();
//        for(CreditRequestApplicantDto.State state: CreditRequestApplicantDto.State.values()){
//            appBar.addTab(state.getName());
//            listTabs.add(state.getName());
//        }
//        appBar.addTabSelectionListener(e ->{
//            if(e.getSource().getSelectedTab()!=null)
//              if(listTabs.contains(e.getSource().getSelectedTab().getLabel()))
//                filter(e.getSource().getSelectedTab().getLabel());
//        });
//        appBar.centerTabs();
//        filter("ANALISIS_PREVIO");
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Nro solicitud, Solicitante, Moneda, Fecha solicitud (dd/mm/yyyy)");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        btnNew = new Button("Nueva");
        btnNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNew.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        btnNew.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        btnNew.addClickListener(e -> viewRegister(new CreditRequest()));
        btnNew.setEnabled(GrantOptions.grantedOption("Solicitud"));

        HorizontalLayout  topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText,btnNew);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,btnNew);
        topLayout.setSpacing(true);
        return topLayout;

    }


    private void viewRegister(CreditRequest creditRequest){
        Map<String,List<String>> param = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<String> typeRelationList = new ArrayList<>();
        if(creditRequest.getId()==null){
            list.add("NUEVO");
            param.put("numberRequest",list);
            typeRelationList.add("deudor");
            param.put("typeRelation",typeRelationList);
            QueryParameters qp = new QueryParameters(param);

            UI.getCurrent().navigate("creditrequest-register",qp);
        }else{
            List<String> numberRequestList = new ArrayList<>();
            List<String> numberApplicantList = new ArrayList<>();
            List<String> fullNameList = new ArrayList<>();
            List<String> idCreditRequestList = new ArrayList<>();

            numberRequestList.add(creditRequest.getNumberRequest().toString());
            param.put("numberRequest", numberRequestList);
            numberApplicantList.add(numberApplicant.toString());
            param.put("numberApplicant", numberApplicantList);
            typeRelationList.add("deudor");
            param.put("typeRelation",typeRelationList);
            idCreditRequestList.add(creditRequest.getId().toString());
            param.put("idCreditRequest", idCreditRequestList);
            fullNameList.add(fullName);
            param.put("fullName",fullNameList);

            QueryParameters qp = new QueryParameters(param);
//
            UI.getCurrent().navigate("creditrequest-register",qp);
        }
    }

    private Grid createGrid(){
        grid = new Grid<>();
        grid.setId("creditrequest");
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(event -> {
            CreditRequest creditRequest = creditRequestRestTemplate.getCreditRequestById(event.getFirstSelectedItem().get().getIdCreditRequest());
            CreditRequestApplicantDto dto = event.getFirstSelectedItem().get();
            idApplicant = dto.getIdApplicant().toString();
            numberApplicant = dto.getNumberApplicant();
            fullName = dto.getFullName();
            viewRegister(creditRequest);

        });

        ComponentRenderer<Badge, CreditRequestApplicantDto> badgeRenderer = new ComponentRenderer<>(
                creditRequestApplicantDto -> {
                    CreditRequestApplicantDto.State state = creditRequestApplicantDto.getState();
                    Badge badge = new Badge(state.getName(),
                            state.getTheme());
                    UIUtils.setTooltip(state.getDesc(), badge);
                    return badge;
                });

//        grid.addColumn(badgeRenderer).setHeader("Estado")
//                .setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0);
        grid.addColumn(CreditRequestApplicantDto::getNumberRequest).setFlexGrow(1)
                .setSortable(true).setResizable(true).setHeader("# Solicitud")
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createNameInfo))
                .setFlexGrow(1).setResizable(true).setSortable(true)
                .setHeader("Solicitante");//.setWidth(UIUtils.COLUMN_WIDTH_XL);
        grid.addColumn(CreditRequestApplicantDto::getCurrency).setHeader("Moneda")
                .setSortable(true).setAutoWidth(true).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Monto")
                .setSortable(true).setFlexGrow(1).setResizable(true)
                .setAutoWidth(true);
        grid.addColumn(new LocalDateRenderer<>(CreditRequestApplicantDto::getRequestDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setHeader("Fecha solicitud").setComparator(CreditRequestApplicantDto::getRequestDate)
                .setAutoWidth(true).setFlexGrow(1).setSortable(true);

        return grid;
    }

    private Component createNameInfo(CreditRequestApplicantDto creditRequestApplicantDto){
        ListItem item = new ListItem(
            UIUtils.createInitials(creditRequestApplicantDto.getInitials()), creditRequestApplicantDto.getFullName()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createAmount(CreditRequestApplicantDto creditRequestApplicantDto){
        Double amount = creditRequestApplicantDto.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private void filter(String filter){

        Tab selectedTab = MainLayout.get().getAppBar().getSelectedTab();
        if (selectedTab != null) {
            if (dataProvider.getItems().size() > 0)
                dataProvider.setFilterByValue(CreditRequestApplicantDto::getState, CreditRequestApplicantDto.State
                        .valueOf(filter));
        }

    }

}
