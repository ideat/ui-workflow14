package com.mindware.workflow.ui.ui.views.legal;

import com.mindware.workflow.ui.backend.entity.legal.dto.LegalInformationCreditRequestDto;
import com.mindware.workflow.ui.backend.rest.legal.LegalInformationCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.Badge;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.ListItem;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "legalInformationView", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista de creditos a registro para registrar Informe Legal")
public class LegalInformationView extends SplitViewFrame {
    private Grid<LegalInformationCreditRequestDto> grid;
    private LegalInformationDataProvider dataProvider;
    private List<LegalInformationCreditRequestDto> legalInformationCreditRequestDtoList;
    private LegalInformationCreditRequestDtoRestTemplate restTemplate = new LegalInformationCreditRequestDtoRestTemplate();

    private TextField filterText;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getLegalInformationCreditRequestDtoList();
        setViewHeader(createTopBar());
        setViewContent(createContent());
    }

    private void getLegalInformationCreditRequestDtoList(){
        legalInformationCreditRequestDtoList = new ArrayList<>(restTemplate.getAll());
        dataProvider = new LegalInformationDataProvider(legalInformationCreditRequestDtoList);
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por: Solicitante, Nro Solicitud, Ciudad, Oficial");
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

    private Grid createGrid() {
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(event ->{
            viewRegister(event.getFirstSelectedItem().get());
        });

        ComponentRenderer<Badge, LegalInformationCreditRequestDto> badgeRenderer = new ComponentRenderer<>(
                legalInformationCreditRequestDto -> {
                    LegalInformationCreditRequestDto.State state = legalInformationCreditRequestDto.getState();
                    Badge badge = new Badge(state.getName(),
                            state.getTheme());
                    UIUtils.setTooltip(state.getDesc(), badge);
                    return badge;
                });

        grid.addColumn(LegalInformationCreditRequestDto::getNumberRequest)
                .setWidth(UIUtils.COLUMN_WIDTH_S)
                .setFlexGrow(0).setSortable(true).setResizable(true)
                .setHeader("# Solicitud");
        grid.addColumn(new ComponentRenderer<>(this::createNameInfo))
                .setWidth(UIUtils.COLUMN_WIDTH_XL)
                .setFlexGrow(0).setSortable(true).setResizable(true)
                .setHeader("Solicitante");
        grid.addColumn(LegalInformationCreditRequestDto::getCurrency)
                .setWidth(UIUtils.COLUMN_WIDTH_S)
                .setFlexGrow(0).setSortable(true).setResizable(true)
                .setHeader("Moneda");
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Monto")
                .setSortable(true).setFlexGrow(0).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_S);
        grid.addColumn(LegalInformationCreditRequestDto::getCity).setHeader("Ciudad")
                .setSortable(true).setFlexGrow(0).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M);
        grid.addColumn(LegalInformationCreditRequestDto::getOfficial).setHeader("Oficial")
                .setSortable(true).setFlexGrow(0).setResizable(true)
                .setWidth(UIUtils.COLUMN_WIDTH_M);
        grid.addColumn(new ComponentRenderer<>(this::createHasGuarantee))
                .setFlexGrow(0).setHeader("Garantia?").setWidth(UIUtils.COLUMN_WIDTH_S);
        grid.addColumn(new ComponentRenderer<>(this::createHasGuarantor))
                .setFlexGrow(0).setHeader("Garante?").setWidth(UIUtils.COLUMN_WIDTH_S);
        grid.addColumn(new ComponentRenderer<>(this::createHasPatrimonialStatement))
                .setFlexGrow(0).setHeader("Dec. Patri?").setWidth(UIUtils.COLUMN_WIDTH_S);

        return grid;
    }

    private Component createNameInfo(LegalInformationCreditRequestDto legalInformationCreditRequestDto){
        ListItem item = new ListItem(
                UIUtils.createInitials(legalInformationCreditRequestDto.getInitials()),legalInformationCreditRequestDto.getFullName()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createAmount(LegalInformationCreditRequestDto legalInformationCreditRequestDto){
        Double amount = legalInformationCreditRequestDto.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private Component createHasGuarantee(LegalInformationCreditRequestDto legalInformationCreditRequestDto){
        Icon icon;
        if(legalInformationCreditRequestDto.isHasGuarantee()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createHasGuarantor(LegalInformationCreditRequestDto legalInformationCreditRequestDto){
        Icon icon;
        if(legalInformationCreditRequestDto.isHasGuarantor()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private Component createHasPatrimonialStatement(LegalInformationCreditRequestDto legalInformationCreditRequestDto){
        Icon icon;
        if(legalInformationCreditRequestDto.isHasPatrimonialStatement()){
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        }else{
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
    }

    private void viewRegister(LegalInformationCreditRequestDto legalInformationCreditRequestDto){
        Map<String,List<String>> param = new HashMap<>();
        List<String> numberRequest = new ArrayList<>();
        List<String> task = new ArrayList<>();
        List<String> fullName = new ArrayList<>();
        List<String> numberApplicant = new ArrayList<>();
        numberApplicant.add(legalInformationCreditRequestDto.getNumberApplicant().toString());
        numberRequest.add(legalInformationCreditRequestDto.getNumberRequest().toString());
        task.add("INFORME LEGAL");
        fullName.add(legalInformationCreditRequestDto.getFullName());
        param.put("number-request",numberRequest);
        param.put("task",task);
        param.put("full-name",fullName);
        param.put("number-applicant",numberApplicant);

        QueryParameters qp = new QueryParameters(param);
        UI.getCurrent().navigate("register-legal-information",qp);
    }
}
