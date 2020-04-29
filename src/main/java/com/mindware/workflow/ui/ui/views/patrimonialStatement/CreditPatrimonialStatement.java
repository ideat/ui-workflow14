package com.mindware.workflow.ui.ui.views.patrimonialStatement;

import com.mindware.workflow.ui.backend.entity.dto.CreditRequestApplicantDto;
import com.mindware.workflow.ui.backend.rest.creditRequestApplicantDto.CreditRequestApplicantDtoRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.mindware.workflow.ui.ui.views.creditRequest.CreditRequestApplicantDataProvider;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Route(value ="creditPatrimonialStatement", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Declaracion Patrimonial")
public class CreditPatrimonialStatement extends ViewFrame implements RouterLayout {

    private Grid<CreditRequestApplicantDto> grid;
    private CreditRequestApplicantDtoRestTemplate restTemplate;
    private CreditRequestApplicantDataProvider dataProvider;
    private List<CreditRequestApplicantDto> creditRequestApplicantList;
    private Map<String,List<String>> param;


    private TextField filterText;

    @Override
    protected void onAttach(AttachEvent event){
        super.onAttach(event);
        restTemplate = new CreditRequestApplicantDtoRestTemplate();
        setViewHeader(createTopBar());
        setViewContent(createContent());


    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;

    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Nro solicitud, Solicitante");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
        topLayout.add(filterText);
        topLayout.expand(filterText);
        topLayout.setSpacing(true);
        return topLayout;
    }

    private Grid createGrid(){

        grid = new Grid<>();
        String isSupervisor = VaadinSession.getCurrent().getAttribute("is-supervisor").toString();
        String scopeUser = VaadinSession.getCurrent().getAttribute("scope-user").toString();
        String login = VaadinSession.getCurrent().getAttribute("login").toString();
        String cityOffice = VaadinSession.getCurrent().getAttribute("city").toString();
        if(isSupervisor.equals("NO") && (scopeUser.equals("LOCAL") || scopeUser.equals("AGENCIA"))){
            creditRequestApplicantList = new ArrayList<>(restTemplate.getByIdUserRegister(login));
        }else if(isSupervisor.equals("SI") && scopeUser.equals("LOCAL")){
            creditRequestApplicantList = new ArrayList<>(restTemplate.getAllByCity(cityOffice));
        }else{
            creditRequestApplicantList = new ArrayList<>(restTemplate.getAll());
        }
        dataProvider = new CreditRequestApplicantDataProvider(creditRequestApplicantList);
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(e ->{
            param = new HashMap<>();
            List<String> numberApplicantList = new ArrayList<>();
            numberApplicantList.add(e.getFirstSelectedItem().get().getNumberApplicant().toString());
            param.put("number-applicant", numberApplicantList);
            List<String> typeRelationList = new ArrayList<>();
            typeRelationList.add(e.getFirstSelectedItem().get().getTypeRelation());
            param.put("type-relation", typeRelationList);
            List<String> numberRequesetList = new ArrayList<>();
            numberRequesetList.add(e.getFirstSelectedItem().get().getNumberRequest());
            param.put("number-request",numberRequesetList);
            List<String> idCreditRequest = new ArrayList<>();
            idCreditRequest.add(e.getFirstSelectedItem().get().getIdCreditRequest().toString());
            param.put("id-credit-request",idCreditRequest);
            List<String> fullNameList = new ArrayList<>();
            fullNameList.add(e.getFirstSelectedItem().get().getFullName());
            param.put("full-name",fullNameList);
            List<String> idApplicant = new ArrayList<>();
            idApplicant.add(e.getFirstSelectedItem().get().getIdApplicant().toString());
            param.put("id-applicant",idApplicant);

            List<String> idCreditRequestApplicant = new ArrayList<>();
            idCreditRequestApplicant.add(e.getFirstSelectedItem().get().getId().toString());
            param.put("id-credit-request-applicant",idCreditRequestApplicant);

            QueryParameters qp = new QueryParameters(param);
            UI.getCurrent().navigate("patrimonial-statement-options", qp);

        });

        grid.addColumn(CreditRequestApplicantDto::getNumberRequest).setFlexGrow(1)
                .setSortable(true).setResizable(true).setHeader("# Solicitud")
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createNameInfo))
                .setFlexGrow(1).setResizable(true).setSortable(true)
                .setHeader("Solicitante").setAutoWidth(true);
        grid.addColumn(CreditRequestApplicantDto::getTypeRelation).setHeader("Relacion")
                .setSortable(true).setAutoWidth(true).setResizable(true);
        grid.addColumn(CreditRequestApplicantDto::getCurrency).setHeader("Moneda")
                .setSortable(true).setAutoWidth(true).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Monto")
                .setSortable(true).setFlexGrow(1).setResizable(true)
                .setAutoWidth(true);

        grid.addColumn(TemplateRenderer.<CreditRequestApplicantDto> of("[[item.requestDate]]")
                .withProperty("requestDate",
                        creditRequestApplicantDto -> UIUtils.formatDate(creditRequestApplicantDto.getRequestDate())))
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


}
