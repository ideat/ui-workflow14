package com.mindware.workflow.ui.ui.views.authorizerExceptions;

import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.users.Users;
import com.mindware.workflow.ui.backend.entity.exceptions.Authorizer;
import com.mindware.workflow.ui.backend.entity.exceptions.AuthorizerExceptionsCreditRequestDto;
import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequest;
import com.mindware.workflow.ui.backend.rest.exceptions.AuthorizerExceptionsCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.AuthorizerRestTemplate;
import com.mindware.workflow.ui.backend.rest.exceptions.ExceptionsCreditRequestRestTemplate;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.backend.rest.users.UserRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import dev.mett.vaadin.tooltip.Tooltips;

import java.util.*;

@Route(value = "authorizer-exception", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Solicitudes con excepciones")
public class AuthorizerExceptionsCreditRequestDtoView extends ViewFrame implements RouterLayout {

    private Grid<AuthorizerExceptionsCreditRequestDto> grid;
    private AuthorizerExceptionsCreditRequestDtoDataProvider dataProvider;
    private List<AuthorizerExceptionsCreditRequestDto> authorizerExceptionsCreditRequestDtoList;
    private AuthorizerExceptionsCreditRequestDtoRestTemplate restTemplate;
    private TextField filterText;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getListAuthorizerExceptionCreditRequestDto();
        setViewHeader(createTopBar());
        setViewContent(createContent());
    }

    private void getListAuthorizerExceptionCreditRequestDto(){
        restTemplate = new AuthorizerExceptionsCreditRequestDtoRestTemplate();
        UserRestTemplate userRestTemplate = new UserRestTemplate();
        String login = VaadinSession.getCurrent().getAttribute("login").toString();
        Users users = userRestTemplate.getByIdUser(login);
        OfficeRestTemplate officeRestTemplate = new OfficeRestTemplate();
        Office office = officeRestTemplate.getByCode(users.getCodeOffice());
        AuthorizerRestTemplate authorizerRestTemplate = new AuthorizerRestTemplate();
        Authorizer authorizer =  authorizerRestTemplate.getByLoginUser(login);
//        authorizerExceptionsCreditRequestDtoList = new LinkedList<>();
        if(authorizer.getId()==null){
            authorizerExceptionsCreditRequestDtoList = new ArrayList<>(restTemplate.getByUser(login));
        }else{
            if(authorizer.getScope().equals("LOCAL")){
//                authorizerExceptionsCreditRequestDtoList = new ArrayList<>(restTemplate.getByCityCurrencyAmounts(office.getCity(),
//                        "BS", authorizer.getMinimumAmountBs(),authorizer.getMaximumAmountBs()));
//                authorizerExceptionsCreditRequestDtoList.addAll(restTemplate.getByCityCurrencyAmounts(office.getCity(),
//                        "$US", authorizer.getMinimumAmountSus(),authorizer.getMaximumAmountSus()));
                authorizerExceptionsCreditRequestDtoList = new ArrayList<>(restTemplate.getByRiskTypeCity(authorizer.getRiskType(),office.getCity()));
            }else if (authorizer.getScope().equals("NACIONAL")){
//                authorizerExceptionsCreditRequestDtoList = new ArrayList<>(restTemplate.getByCurrencyAmounts("BS",
//                        authorizer.getMinimumAmountBs(),authorizer.getMaximumAmountBs()));
//                authorizerExceptionsCreditRequestDtoList.addAll(restTemplate.getByCurrencyAmounts("$US",
//                        authorizer.getMinimumAmountSus(),authorizer.getMaximumAmountSus()));
                authorizerExceptionsCreditRequestDtoList = new ArrayList<>(restTemplate.getByRiskType(authorizer.getRiskType()));

            }
        }
        dataProvider = new AuthorizerExceptionsCreditRequestDtoDataProvider(authorizerExceptionsCreditRequestDtoList);
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Nro solicitud, Nro Solicitante, Solicitante, Ciudad");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);
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
        grid.addSelectionListener(event -> {
            if(event.getFirstSelectedItem().isPresent()) {
                viewRegister(event.getFirstSelectedItem().get());
            }
        });

        grid.addColumn(new ComponentRenderer<>(this::createNameInfo)).setHeader("Solicitante")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(AuthorizerExceptionsCreditRequestDto::getLoginUser).setHeader("Oficial")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(AuthorizerExceptionsCreditRequestDto::getNumberRequest).setHeader("Nro Solicitud")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Monto")
                .setSortable(true).setResizable(true).setFlexGrow(1).setAutoWidth(true);
        grid.addColumn(AuthorizerExceptionsCreditRequestDto::getCurrency).setHeader("Moneda")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(AuthorizerExceptionsCreditRequestDto::getCity).setHeader("Ciudad")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(AuthorizerExceptionsCreditRequestDto::getTotalExceptionsApproved).setHeader("Ex. Aprob.")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(AuthorizerExceptionsCreditRequestDto::getTotalExceptionsRejected).setHeader("Ex. Recha.")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(AuthorizerExceptionsCreditRequestDto::getTotalExceptionsProposal).setHeader("Ex. Prop.")
                .setFlexGrow(1).setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createButtonPrint)).setResizable(true)
                .setFlexGrow(1).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createButtonPrintPermanent)).setResizable(true)
                .setFlexGrow(1).setAutoWidth(true);

        return grid;
    }

    private Component createButtonPrint(AuthorizerExceptionsCreditRequestDto authorizerExceptionsCreditRequestDto){
        Button btn = new Button();
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST);
        btn.setIcon(VaadinIcon.PRINT.create());
        Tooltips.getCurrent().setTooltip(btn,"Autorizaciones TEMPORALES");

        btn.addClickListener(e ->{
            String listException = "";
            ExceptionsCreditRequestRestTemplate exceptionsCreditRequestRestTemplate = new ExceptionsCreditRequestRestTemplate();
            List<ExceptionsCreditRequest> exceptionsCreditRequestList =
                    exceptionsCreditRequestRestTemplate.getByNumberRequest(authorizerExceptionsCreditRequestDto.getNumberRequest());
            boolean allowPrint = true;
            for(ExceptionsCreditRequest ec:exceptionsCreditRequestList){
                if(!ec.getState().equals("APROBADA")){
                  allowPrint = false;
                  listException = listException +"-"+ ec.getCodeException()+"-";
                }
            }
            if(allowPrint){
                Map<String,List<String>> paramAuthException = new HashMap<>();
                List<String> origin = new ArrayList<>();
                origin.add("authorizer-exception");
                List<String> path = new ArrayList<>();
                path.add("authorizer-exception");
                List<String> numberRequestList = new ArrayList<>();
                numberRequestList.add(authorizerExceptionsCreditRequestDto.getNumberRequest().toString());

                List<String> typeException = new ArrayList<>();
                typeException.add("TEMPORAL");
                List<String> titleReport = new ArrayList<>();
                titleReport.add("Autorizacion Excepciones ");
                paramAuthException.put("path",path);
                paramAuthException.put("title",titleReport);
                paramAuthException.put("origin",origin);
                paramAuthException.put("number-request",numberRequestList);
                paramAuthException.put("type-exception",typeException);
                QueryParameters qp = new QueryParameters(paramAuthException);
                UI.getCurrent().navigate("report-preview",qp);
            }else{
                UIUtils.showNotification("Faltan aprobar excecpciones "+listException);
            }

        });

        return btn;
    }

    private Component createButtonPrintPermanent(AuthorizerExceptionsCreditRequestDto authorizerExceptionsCreditRequestDto){
        Button btn = new Button();
        btn.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_PRIMARY);
        btn.setIcon(VaadinIcon.PRINT.create());
        Tooltips.getCurrent().setTooltip(btn,"Autorizaciones PERMANENTES");

        btn.addClickListener(e ->{
            String listException = "";
            ExceptionsCreditRequestRestTemplate exceptionsCreditRequestRestTemplate = new ExceptionsCreditRequestRestTemplate();
            List<ExceptionsCreditRequest> exceptionsCreditRequestList =
                    exceptionsCreditRequestRestTemplate.getByNumberRequest(authorizerExceptionsCreditRequestDto.getNumberRequest());
            boolean allowPrint = true;
            for(ExceptionsCreditRequest ec:exceptionsCreditRequestList){
                if(!ec.getState().equals("APROBADA")){
                    allowPrint = false;
                    listException = listException +"-"+ ec.getCodeException()+"-";
                }
            }
            if(allowPrint){
                Map<String,List<String>> paramAuthException = new HashMap<>();
                List<String> origin = new ArrayList<>();
                origin.add("authorizer-exception");
                List<String> path = new ArrayList<>();
                path.add("authorizer-exception");
                List<String> numberRequestList = new ArrayList<>();
                numberRequestList.add(authorizerExceptionsCreditRequestDto.getNumberRequest().toString());

                List<String> typeException = new ArrayList<>();
                typeException.add("PERMANENTE");
                List<String> titleReport = new ArrayList<>();
                titleReport.add("Autorizacion Excepciones ");
                paramAuthException.put("path",path);
                paramAuthException.put("title",titleReport);
                paramAuthException.put("origin",origin);
                paramAuthException.put("number-request",numberRequestList);
                paramAuthException.put("type-exception",typeException);
                QueryParameters qp = new QueryParameters(paramAuthException);
                UI.getCurrent().navigate("report-preview",qp);
            }else{
                UIUtils.showNotification("Faltan aprobar excecpciones "+listException);
            }

        });

        return btn;
    }

    private Component createAmount(AuthorizerExceptionsCreditRequestDto authorizerExceptionsCreditRequestDto){
        Double amount = authorizerExceptionsCreditRequestDto.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private Component createNameInfo(AuthorizerExceptionsCreditRequestDto authorizerExceptionsCreditRequestDto){
        ListItem item = new ListItem(
                UIUtils.createInitials(authorizerExceptionsCreditRequestDto.getInitials()),authorizerExceptionsCreditRequestDto.getFullName());
         item.setHorizontalPadding(false);
         return item;
    }

    private void viewRegister(AuthorizerExceptionsCreditRequestDto authorizerExceptionsCreditRequestDto){
        Map<String,List<String>> param = new HashMap<>();
        List<String> numberRequestList = new LinkedList<>();
        numberRequestList.add(authorizerExceptionsCreditRequestDto.getNumberRequest().toString());
        List<String> fullName = new ArrayList<>();
        fullName.add(authorizerExceptionsCreditRequestDto.getFullName());

        param.put("number-request",numberRequestList);
        param.put("full-name",fullName);
        QueryParameters qp = new QueryParameters(param);
        UI.getCurrent().navigate("authorizer-exceptions-creditrequest",qp);
    }
}
