package com.mindware.workflow.ui.ui.views.stageHistory;

import com.flowingcode.vaadin.addons.orgchart.OrgChart;
import com.flowingcode.vaadin.addons.orgchart.OrgChartItem;
import com.flowingcode.vaadin.addons.orgchart.client.enums.ChartDirectionEnum;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistoryCreditRequestDto;
import com.mindware.workflow.ui.backend.rest.stageHistoryCreditRequestDto.StageHistoryCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.nils_bauer.PureTimeline;
import de.nils_bauer.PureTimelineItem;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Route(value = "historyGlobal", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Listado global de workflow")
public class StageHistoryGlobalView extends SplitViewFrame {

    private StageHistoryCreditRequestDataProvider dataProvider;
    private StageHistoryCreditRequestDtoRestTemplate restTemplate = new StageHistoryCreditRequestDtoRestTemplate();
    private List<StageHistoryCreditRequestDto> stageHistoryCreditRequestDtoList = new ArrayList<>();

    private TextField filterText;
    private String loginUser;
    private String rol;
    private String scopeRol;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;

//    public StageHistoryGlobalView(){
//        getListStageGlobalHistoryCreditRequest();
//        setViewHeader(createTopBar());
//        setViewContent(createContent());
//        setViewDetails(createDetailsDrawer());
//        setViewDetailsPosition(Position.RIGHT);
//    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getListStageGlobalHistoryCreditRequest();
        setViewHeader(createTopBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(Position.RIGHT);
    }

    private void getListStageGlobalHistoryCreditRequest(){
        rol = VaadinSession.getCurrent().getAttribute("rol").toString();
        loginUser = VaadinSession.getCurrent().getAttribute("login").toString();
        scopeRol = VaadinSession.getCurrent().getAttribute("scope-rol").toString();
        String city = VaadinSession.getCurrent().getAttribute("city").toString();

        if(scopeRol.equals("NACIONAL")){
            stageHistoryCreditRequestDtoList = restTemplate.getGlobalDetail();
        }else{
            if(rol.equals("OFICIAL")){
                stageHistoryCreditRequestDtoList = restTemplate.getGlobalDetailByUser(loginUser);
            }else {
                stageHistoryCreditRequestDtoList = restTemplate.getGlobalDetailByCity(city);
            }
        }

        dataProvider = new StageHistoryCreditRequestDataProvider(stageHistoryCreditRequestDtoList);
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.addClassName("grid-view");
        content.setHeightFull();
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
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

    private DetailsDrawer createDetailsDrawer(){
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        return detailsDrawer;
    }

    private Grid createGrid(){
        Grid<StageHistoryCreditRequestDto> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails) );

//        grid.addSelectionListener(event ->{
//           if(event.getFirstSelectedItem().isPresent()) {
//               Map<String,List<String>> param = new HashMap<>();
//               List<String> numberRequestList = new ArrayList<>();
//               numberRequestList.add(event.getFirstSelectedItem().get().getNumberRequest().toString());
//               param.put("number-request",numberRequestList);
//               QueryParameters qp = new QueryParameters(param);
//               UI.getCurrent().navigate("stage-hitory-timeline",qp);
//           }
//        });
        grid.addColumn(StageHistoryCreditRequestDto::getNumberRequest).setFlexGrow(1).setHeader("Nro solicitud")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getFullName).setFlexGrow(1).setHeader("Solicitante")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createAmount)).setFlexGrow(1).setHeader("Monto")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getCurrency).setFlexGrow(1).setHeader("Moneda")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getUserTask).setFlexGrow(1).setHeader("Resp. Tarea")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getTotalHours).setFlexGrow(1).setHeader("Hrs. asignadas")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getProductCredit).setFlexGrow(1).setHeader("Nombre Producto")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getTimeToBeAssigned).setFlexGrow(1).setHeader("Hrs. en asignarse")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getTimeElapsed).setFlexGrow(1).setHeader("Hrs. trabajo")
                .setResizable(true).setAutoWidth(true);
        grid.addColumn(StageHistoryCreditRequestDto::getHoursLeft).setFlexGrow(1).setHeader("Hrs. restantes")
                .setResizable(true).setAutoWidth(true);


        return grid;
    }

    private Component createAmount(StageHistoryCreditRequestDto stageHistoryCreditRequestDto){
        Double amount = stageHistoryCreditRequestDto.getAmount();
        return UIUtils.createAmountLabel(amount);
    }

    private void showDetails(StageHistoryCreditRequestDto stageHistoryCreditRequestDto){
        List<StageHistoryCreditRequestDto> list = restTemplate.getDetailByNumberRequest(stageHistoryCreditRequestDto.getNumberRequest());
        detailsDrawerHeader.setTitle("Cronograma Solicitud: "+ stageHistoryCreditRequestDto.getNumberRequest().toString());
        detailsDrawer.setContent(createDetails(list));
        detailsDrawer.show();
    }

    private VerticalLayout createDetails(List<StageHistoryCreditRequestDto> stageHistoryCreditRequestDtoList){

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        PureTimeline timeline = new PureTimeline();
        timeline.setWidthFull();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM )
                        .withLocale( Locale.UK )
                        .withZone( ZoneId.systemDefault() );
        for(StageHistoryCreditRequestDto st:stageHistoryCreditRequestDtoList){
            PureTimelineItem item = new PureTimelineItem(st.getStage()+":"+st.getState().getName());
            String body1 = String.format("Registro: %s"
                    , formatter.format(st.getStartDateTime()));
            String body2 = String.format("Inicio: %s"
                    ,st.getInitDateTime()==null?"":formatter.format(st.getInitDateTime()));
            String body3 = String.format("Fin: %s"
                    ,st.getFinishedDateTime()==null?"":formatter.format(st.getFinishedDateTime()));
            String hours = String.format("Hrs. asig.: %s Hrs.Trab.: %s", st.getTimeToBeAssigned(),st.getTimeElapsed());
            String hoursDif = String.format("Hrs. Etapa: %s Hrs. Rest: %s", st.getTotalHoursStage(),
                    st.getTotalHoursStage()-(st.getTimeElapsed()+st.getTimeToBeAssigned()));
            String userTask = String.format("Usr. etapa: %s", st.getUserTask());
            item.add(new H6(body1));
            item.add(new H6(body2));
            item.add(new H6(body3));
            item.add(new H6(hours));
            item.add(new H6(hoursDif));
            item.add(new H6(userTask));
            timeline.add(item);
        }
        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(true);
        verticalLayout.add(timeline);

        return verticalLayout;
    }

//    private VerticalLayout createDetails(List<StageHistoryCreditRequestDto> stageHistoryCreditRequestDtoList){
//        VerticalLayout verticalLayout = new VerticalLayout();
//        verticalLayout.setSizeFull();
//        int i=1;
//        OrgChartItem item=null;
//        List<OrgChartItem> itemList = new ArrayList<>();
//        for(StageHistoryCreditRequestDto st:stageHistoryCreditRequestDtoList){
//
//            String body = String.format("Start: %s \n Init: %s \n Finish: %s \n %s"
//                    , st.getStartDateTime().toString(), st.getInitDateTime() == null ? "" : st.getInitDateTime().toString()
//                    , st.getFinishedDateTime() == null ? "" : st.getFinishedDateTime().toString()
//                    , st.getState());
//            String header = String.format(st.getStage() + "\n" + st.getUserTask());
//            if(i==1) {
//
//                item = new OrgChartItem(i, body, header);
//                item.setData("Tiempos",body);
//            }else{
//                OrgChartItem item1 = new OrgChartItem(i,body,header);
//                item1.setData("Tiempos",body);
//                itemList.add(item1);
//
//            }
//            i++;
//        }
//        item.setChildren(itemList);
//        OrgChart orgChart = new OrgChart(item);
//        orgChart.addClassName("chart-container");
//
//        orgChart.setTitle("Cronograma de la solicitud");
//        orgChart.setChartDirection(ChartDirectionEnum.TOP_TO_BOTTOM.getAbreviation());
//        orgChart.initializeChart();
//        verticalLayout.add(orgChart);
//
//        return verticalLayout;
//    }



//
//    public OrgChart getExample1() {
//        OrgChartItem item1 = new OrgChartItem(1, "John Williams", "Director");
//        item1.setData("mail", "jwilliams@example.com");
//        item1.setClassName("blue-node");
//        OrgChartItem item2 = new OrgChartItem(2, "Anna Thompson", "Administration");
//        item2.setData("mail", "athomp@example.com");
//        item2.setClassName("blue-node");
//        OrgChartItem item3 = new OrgChartItem(3, "Timothy Albert Henry Jones ", "Sub-Director of Administration Department");
//        item3.setData("mail", "timothy.albert.jones@example.com");
//        item1.setChildren(Arrays.asList(item2, item3));
//        OrgChartItem item4 = new OrgChartItem(4, "Louise Night", "Department 1");
//        item4.setData("mail", "lnight@example.com");
//        OrgChartItem item5 = new OrgChartItem(5, "John Porter", "Department 2");
//        item5.setData("mail", "jporter@example.com");
//        OrgChartItem item6 = new OrgChartItem(6, "Charles Thomas", "Department 3");
//        item6.setData("mail", "ctomas@example.com");
//        item2.setChildren(Arrays.asList(item4, item5, item6));
//        OrgChartItem item7 = new OrgChartItem(7, "Michael Wood", "Section 3.1");
//        OrgChartItem item8 = new OrgChartItem(8, "Martha Brown", "Section 3.2");
//        OrgChartItem item9 = new OrgChartItem(9, "Mary Parker", "Section 3.3");
//        OrgChartItem item10 = new OrgChartItem(10, "Mary Williamson", "Section 3.4");
//        item6.setChildren(Arrays.asList(item7, item8, item9, item10));
//        return new OrgChart(item1);
//    }
}
