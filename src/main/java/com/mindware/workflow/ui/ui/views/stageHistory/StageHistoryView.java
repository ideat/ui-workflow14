package com.mindware.workflow.ui.ui.views.stageHistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.RequestStage;
import com.mindware.workflow.ui.backend.entity.config.WorkflowProduct;
import com.mindware.workflow.ui.backend.entity.config.States;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistoryCreditRequestDto;
import com.mindware.workflow.ui.backend.rest.stageHistory.StageHistoryRestTemplate;
import com.mindware.workflow.ui.backend.rest.stageHistoryCreditRequestDto.StageHistoryCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.workflowProducdt.WorkflowProductRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.Badge;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import lombok.SneakyThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route(value = "stateHistoryView", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Listado de pendientes")
//@HtmlImport("static/frontend/grid-styles.html")
@CssImport(value = "grid-styles.css", themeFor = "vaadin-grid")
public class StageHistoryView extends SplitViewFrame implements RouterLayout {

    private StageHistoryCreditRequestDataProvider dataProvider;
    private StageHistoryCreditRequestDtoRestTemplate restTemplate = new StageHistoryCreditRequestDtoRestTemplate();
    private StageHistoryRestTemplate stageHistoryRestTemplate = new StageHistoryRestTemplate();

    private WorkflowProductRestTemplate workflowProductRestTemplate = new WorkflowProductRestTemplate();
    private List<StageHistoryCreditRequestDto> stageHistoryCreditRequestDtoList = new ArrayList<>();

    private AppBar appBar;
    private TextField filterText;
    private Grid<StageHistoryCreditRequestDto> grid;

    private String loginUser;
    private String rol;
    private String initStatesByRol;
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getListStageHistoryCreditRequest();
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

    @SneakyThrows
    private void getListStageHistoryCreditRequest(){
        rol = VaadinSession.getCurrent().getAttribute("rol").toString();
        loginUser = VaadinSession.getCurrent().getAttribute("login").toString();
        initStatesByRol = getInitStateByRol(rol);
        stageHistoryCreditRequestDtoList = restTemplate.getByUserRolState(loginUser,initStatesByRol,rol);
        dataProvider = new StageHistoryCreditRequestDataProvider(stageHistoryCreditRequestDtoList);
    }

    private void initAppBar(){
        MainLayout.get().getAppBar().reset();
        appBar = MainLayout.get().getAppBar();
    }

    private HorizontalLayout createTopBar(){
        filterText = new TextField();
        filterText.setPlaceholder("Filtro por Nro solicitud, Solicitante, Moneda, Fecha solicitud");
        filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        filterText.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        HorizontalLayout  topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filterText);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START,filterText);
        topLayout.expand(filterText);

        return topLayout;

    }

    private Grid createGrid(){
        grid = new Grid<>();

        grid.setSizeFull();
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener(event -> {
            if(event.getFirstSelectedItem().isPresent()) {
                if (event.getFirstSelectedItem().get().getOfficer() == null) {
                    UIUtils.showNotification("Primero asignese la etapa");
                }else{
                    Map<String,List<String>> param = new HashMap<>();
                    List<String> idStageHistory = new ArrayList<>();
                    idStageHistory.add(event.getFirstSelectedItem().get().getIdStageHistory().toString());
                    param.put("id-stage-history",idStageHistory);
                    List<String> stage = new ArrayList<>();
                    stage.add(event.getFirstSelectedItem().get().getStage());
                    param.put("stage",stage);
                    List<String> codeProduct = new ArrayList<>();
                    codeProduct.add(event.getFirstSelectedItem().get().getProductCode().toString());
                    param.put("code-product",codeProduct);

                    QueryParameters qp = new QueryParameters(param);
                    UI.getCurrent().navigate("stage-history-register",qp);

                }
            }
        });

        ComponentRenderer<Badge,StageHistoryCreditRequestDto> badgeRenderer = new ComponentRenderer<>(
                stageHistoryCreditRequestDto -> {
                   StageHistoryCreditRequestDto.State state = stageHistoryCreditRequestDto.getState();
                    Badge badge = new Badge(state.getName(),
                            state.getTheme());
                    UIUtils.setTooltip(state.getDesc(), badge);
                    return badge;
                });

        grid.addColumn(badgeRenderer).setResizable(true).setHeader("Estado").setFlexGrow(1);
        grid.addColumn(StageHistoryCreditRequestDto::getNumberRequest).setFlexGrow(1)
                .setSortable(true).setResizable(true).setHeader("Nro solicitud");
        grid.addColumn(StageHistoryCreditRequestDto::getFullName).setFlexGrow(1)
                .setResizable(true).setHeader("Solicitante");
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setHeader("Monto")
                .setSortable(true).setFlexGrow(1).setResizable(true);
        grid.addColumn(StageHistoryCreditRequestDto::getCurrency)
                .setResizable(true).setHeader("Moneda").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(StageHistoryCreditRequestDto::getTotalHours).setFlexGrow(1)
                .setSortable(true).setHeader("Total horas").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(StageHistoryCreditRequestDto::getTimeElapsed).setResizable(true).setFlexGrow(1)
                .setSortable(true).setHeader("Horas transcurridas").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(StageHistoryCreditRequestDto::getHoursLeft).setResizable(true).setFlexGrow(1)
                .setSortable(true).setHeader("Horas restantes").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(new ComponentRenderer<>(this::createButtonAssing))
                .setFlexGrow(1).setResizable(true);
        grid.getClassNames().add("my-grid-theme");
        grid.setClassNameGenerator(
                dto -> dto.getHoursLeft()>=24?"fine"
                        :dto.getHoursLeft()>=15 && dto.getHoursLeft()<24?"warning"
                        :dto.getHoursLeft()<15?"danger":"");

        return grid;
    }

    private Component createAmount(StageHistoryCreditRequestDto stageHistoryCreditRequestDto){
        Double amount = stageHistoryCreditRequestDto.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private Component createButtonAssing(StageHistoryCreditRequestDto stageHistoryCreditRequestDto){
        Button btn = new Button();
        if(stageHistoryCreditRequestDto.getOfficer()!=null) {
            btn.setText("Asignada");
            btn.setEnabled(false);
        }else{
            btn.setText("Asignarse");
            btn.setEnabled(true);
        }
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SMALL);
        btn.addClickListener(e -> {
            StageHistory stageHistory = stageHistoryRestTemplate.getById(stageHistoryCreditRequestDto.getIdStageHistory());
            stageHistory.setUserTask(loginUser);
            stageHistory.setInitDateTime(Instant.now());
            stageHistoryRestTemplate.update(stageHistory);

            UIUtils.showNotification("Etapa asignada al usuario " + loginUser);
            btn.setText("Asignada");
            btn.setEnabled(false);

            stageHistoryCreditRequestDtoList = restTemplate.getByUserRolState(loginUser, initStatesByRol,rol);
            dataProvider = new StageHistoryCreditRequestDataProvider(stageHistoryCreditRequestDtoList);
            grid.setDataProvider(dataProvider);
        });

        return btn;
    }

    private String getInitState(Integer codeProduct, String stage)  {

        ObjectMapper mapper = new ObjectMapper();
        WorkflowProductRestTemplate workflowProductRestTemplate = new WorkflowProductRestTemplate();
        WorkflowProduct workflowProduct = workflowProductRestTemplate.getByCode(codeProduct.toString());

        List<States> statesList = new ArrayList<>();
        try {
            List<RequestStage> requestStageList = mapper.readValue(workflowProduct.getRequestStage(), new TypeReference<List<RequestStage>>() {});
            RequestStage requestStage = requestStageList.stream().filter(r -> r.getStage().equals(stage)).findFirst().get();
            statesList = mapper.readValue(requestStage.getStates(), new TypeReference<List<States>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        List<States> statesInit = statesList.stream().filter(s -> s.isInitState()==true).collect(Collectors.toList());
        List<String> str = new ArrayList<>();
        for(States s:statesInit){
           str.add(s.getState());
        }
        return str.stream().collect(Collectors.joining(","));
    }

    private String getInitStateByRol(String rolName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WorkflowProductRestTemplate workflowProductRestTemplate = new WorkflowProductRestTemplate();
        List<WorkflowProduct> workflowProductList = workflowProductRestTemplate.getAll();
        List<String> resultList = new ArrayList<>();
        for(WorkflowProduct w:workflowProductList){
            List<RequestStage> requestStageList = mapper.readValue(w.getRequestStage(), new TypeReference<List<RequestStage>>() {});
            requestStageList = requestStageList.stream().filter(r -> r.getRols().equals(rolName)).collect(Collectors.toList());
            List<States> statesList = new ArrayList<>();
            for(RequestStage rs:requestStageList){
                statesList = mapper.readValue(rs.getStates(), new TypeReference<List<States>>() {});
                statesList = statesList.stream().filter(s -> s.isInitState()==true).collect(Collectors.toList());
                for(States st:statesList){
                    resultList.add(st.getState());
                }

            }
        }

        return resultList.stream().distinct().collect(Collectors.joining(","));
    }

}
