package com.mindware.workflow.ui.ui.views.contract;

import com.mindware.workflow.ui.backend.entity.contract.ContractCreditRequestDto;
import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.rest.contract.ContractCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.rol.RolRestTemplate;
import com.mindware.workflow.ui.backend.util.DownloadLink;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "contract-creditrequest", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Operaciones en fase de formalizacion y generacion de contrato")
public class ContractCreditRequestDtoView extends SplitViewFrame implements RouterLayout {
    private Grid<ContractCreditRequestDto> grid;
    private ContractCreditRequestDtoDataProvider dataProvider;
    private List<ContractCreditRequestDto> contractCreditRequestDtoList;
    private ContractCreditRequestDtoRestTemplate restTemplate;
    private Map<String,List<String>> param;

    private TextField filterText;

    @Value("${contract}")
    private String pathContract;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        restTemplate = new ContractCreditRequestDtoRestTemplate();
        param = new HashMap<>();
        getListContractCreditRequestDto();
        setViewHeader(createTopBar());
        setViewContent(createContent());

    }

    private void getListContractCreditRequestDto(){
        String rol = VaadinSession.getCurrent().getAttribute("rol").toString();
        String login = VaadinSession.getCurrent().getAttribute("login").toString();
        String city = VaadinSession.getCurrent().getAttribute("city").toString();
        if(rol.equals("OFICIAL")){
            contractCreditRequestDtoList = restTemplate.getByUser(login);
        }else{
            RolRestTemplate rolRestTemplate = new RolRestTemplate();
            Rol rol1 = rolRestTemplate.getRolByName(rol);
            if(rol1.getScope().equals("LOCAL")){
                contractCreditRequestDtoList = restTemplate.getByCity(city);
            }else{
                contractCreditRequestDtoList = restTemplate.getAll();
            }
        }
        dataProvider = new ContractCreditRequestDtoDataProvider(contractCreditRequestDtoList);
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro: #Solicitante, #Solicitud, Nombre solicitante, Ciudad, Oficial, Oficina");
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

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createContractCreditRequestDto());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private Grid createContractCreditRequestDto(){
        grid = new Grid<>();
        grid.addSelectionListener(event ->{
            List<String> numberRequest = new ArrayList<>();
            numberRequest.add(event.getFirstSelectedItem().get().getNumberRequest().toString());
            param.put("number-request",numberRequest);
            QueryParameters qp = new QueryParameters(param);
            UI.getCurrent().navigate("contract-register",qp);
        });

        grid.setDataProvider(dataProvider);
        grid.setHeightFull();
        grid.addColumn(ContractCreditRequestDto::getNumberRequest).setFlexGrow(0)
                .setHeader("# Solicitud").setSortable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true);
        grid.addColumn(ContractCreditRequestDto::getNumberApplicant).setFlexGrow(0)
                .setHeader("# Solicitante").setSortable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createApplicantInfo)).setFlexGrow(1).setAutoWidth(true)
                .setHeader("Nombre solicitante").setResizable(true).setTextAlign(ColumnTextAlign.START);
        grid.addColumn(ContractCreditRequestDto::getNameOfficer).setFlexGrow(1)
                .setHeader("Oficial creditos").setSortable(true).setResizable(true).setAutoWidth(true);
        grid.addColumn(ContractCreditRequestDto::getCity).setResizable(true).setFlexGrow(1)
                .setHeader("Ciudad").setResizable(true).setAutoWidth(true);
        grid.addColumn(ContractCreditRequestDto::getNameOffice).setSortable(true).setResizable(true)
                .setAutoWidth(true).setHeader("Oficina").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createDownloadLink)).setResizable(true);

        return grid;
    }

    private Component createApplicantInfo(ContractCreditRequestDto contractCreditRequestDto){
        ListItem item = new ListItem(
          UIUtils.createInitials(contractCreditRequestDto.getInitials()),
                contractCreditRequestDto.getFullName().trim()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createDownloadLink(ContractCreditRequestDto contractCreditRequestDto){
        Div content = new Div();
        String pathFile = contractCreditRequestDto.getPathContract();
        if(pathFile!=null) {

            try {
                restTemplate.getContractFile(pathFile, pathContract);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            File in = new File(pathFile);
            DownloadLink downloadLink = new DownloadLink(in);

            content = new Div(downloadLink);

        }
        return content;
    }
}
