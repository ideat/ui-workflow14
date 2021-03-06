package com.mindware.workflow.ui.ui.views.creditResolution;

import com.mindware.workflow.ui.backend.entity.creditResolution.CreditResolutionCreditRequestDto;
import com.mindware.workflow.ui.backend.rest.creditResolution.CreditResolutionCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
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

@Route(value = "CreditResolutionView", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Solicitudes para registro de resoluciones")
public class CreditResolutionCreditRequestDtoView extends SplitViewFrame implements RouterLayout {
    private Grid<CreditResolutionCreditRequestDto> grid;
    private CreditResolutionCreditRequestDataProvider dataProvider;
    private List<CreditResolutionCreditRequestDto> creditResolutionCreditRequestDtoList;
    private CreditResolutionCreditRequestDtoRestTemplate restTemplate = new CreditResolutionCreditRequestDtoRestTemplate();

    private TextField filterText;
    private Map<String,List<String>> param;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        getListCreditResolutionCreditRequest();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        param = new HashMap<>();
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createCreditResolutionCreditRequest());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private void getListCreditResolutionCreditRequest(){
        String rol = VaadinSession.getCurrent().getAttribute("rol").toString();
        String login = VaadinSession.getCurrent().getAttribute("login").toString();
        String rolScope = VaadinSession.getCurrent().getAttribute("scope-rol").toString();
        String city = VaadinSession.getCurrent().getAttribute("city").toString();
        if(rol.equals("OFICIAL")){
            creditResolutionCreditRequestDtoList = new ArrayList<>(restTemplate.getByLogin(login));
        }if(rolScope.equals("LOCAL")) {
            creditResolutionCreditRequestDtoList = new ArrayList<>(restTemplate.getByCity(city));
        }
        else {
            creditResolutionCreditRequestDtoList = new ArrayList<>(restTemplate.getAll());
        }
        dataProvider = new CreditResolutionCreditRequestDataProvider(creditResolutionCreditRequestDtoList);
    }

    private Grid createCreditResolutionCreditRequest(){
        grid = new Grid<>();
        grid.addSelectionListener(event -> {
            List<String> numberRequest = new ArrayList<>();
            numberRequest.add(event.getFirstSelectedItem().get().getNumberRequest().toString());
            param.put("number-request",numberRequest);
            List<String> fullName = new ArrayList<>();
            fullName.add(event.getFirstSelectedItem().get().getFullName());
            param.put("full-name",fullName);
            List<String> idCreditRequestApplicant = new ArrayList<>();
            idCreditRequestApplicant.add(event.getFirstSelectedItem().get().getIdCreditRequestApplicant().toString());
            param.put("id-credit-request-applicant",idCreditRequestApplicant);
            List<String> numberApplicant = new ArrayList<>();
            numberApplicant.add(event.getFirstSelectedItem().get().getNumberApplicant().toString());
            param.put("number-applicant",numberApplicant);
            List<String> amount = new ArrayList<>();
            amount.add(event.getFirstSelectedItem().get().getAmount().toString());
            param.put("amount",amount);
            List<String> currency = new ArrayList<>();
            currency.add(event.getFirstSelectedItem().get().getCurrency());
            param.put("currency",currency);
            List<String> typeCredit = new ArrayList<>();
            typeCredit.add(event.getFirstSelectedItem().get().getTypeCredit());
            param.put("type-credit",typeCredit);
            QueryParameters qp = new QueryParameters(param);
            UI.getCurrent().navigate("credit-resolution-register",qp);
        });
        grid.setDataProvider(dataProvider);
        grid.setHeight("100%");
        grid.addColumn(CreditResolutionCreditRequestDto::getNumberRequest).setFlexGrow(1)
                .setHeader("Nro solicitud").setSortable(true)
                .setAutoWidth(true).setResizable(true);
        grid.addColumn(CreditResolutionCreditRequestDto::getNumberApplicant).setFlexGrow(1)
                .setHeader("# Solicitante").setSortable(true)
                .setAutoWidth(true).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createApplicantInfo)).setFlexGrow(1)
                .setHeader("Nombre solicitante").setAutoWidth(true)
                .setResizable(true).setTextAlign(ColumnTextAlign.START);
        grid.addColumn(CreditResolutionCreditRequestDto::getCity).setFlexGrow(1)
                .setHeader("Ciudad").setAutoWidth(true)
                .setResizable(true).setSortable(true);
        grid.addColumn(CreditResolutionCreditRequestDto::getNameUser).setFlexGrow(1)
                .setHeader("Oficial").setAutoWidth(true)
                .setResizable(true).setSortable(true);

        return grid;
    }

    private Component createApplicantInfo(CreditResolutionCreditRequestDto creditResolutionCreditRequestDto){
        ListItem item = new ListItem(
                UIUtils.createInitials(creditResolutionCreditRequestDto.getInitials()),
                        creditResolutionCreditRequestDto.getFullName().trim(),
                        creditResolutionCreditRequestDto.getAmount().toString().trim() +
                        creditResolutionCreditRequestDto.getCurrency().trim());
        item.setHorizontalPadding(false);
        return item;
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro: #Solicitante, Nombre solicitante, Ciudad, Oficial");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
        topLayout.setSpacing(true);
        topLayout.setPadding(true);

        return topLayout;
    }


}
