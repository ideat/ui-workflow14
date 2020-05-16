package com.mindware.workflow.ui.ui.views.stageHistory;

import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistoryCreditRequestDto;
import com.mindware.workflow.ui.backend.rest.stageHistoryCreditRequestDto.StageHistoryCreditRequestDtoRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import de.nils_bauer.PureTimeline;
import de.nils_bauer.PureTimelineItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Route(value = "stage-hitory-timeline", layout = MainLayout.class)

public class StageHistoryTimelineView extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {
    private StageHistoryCreditRequestDtoRestTemplate restTemplate = new StageHistoryCreditRequestDtoRestTemplate();
    private List<StageHistoryCreditRequestDto> stageHistoryCreditRequestDtoList = new ArrayList<>();
    private Map<String, List<String>> paramStage;
    private FlexBoxLayout contentTimeLine;

    @Override
    public void setParameter(BeforeEvent beforeEvent,  @OptionalParameter String s) {
        restTemplate = new StageHistoryCreditRequestDtoRestTemplate();
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        paramStage = queryParameters.getParameters();
        stageHistoryCreditRequestDtoList = restTemplate.getDetailByNumberRequest(Integer.parseInt(paramStage.get("number-request").get(0)));
        contentTimeLine = (FlexBoxLayout) createContent(createTimeLine(stageHistoryCreditRequestDtoList));

        setViewContent(contentTimeLine);
        setViewDetailsPosition(Position.BOTTOM);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle("Cronograma");

    }

    private AppBar initAppBar() {
        MainLayout.get().getAppBar().reset();
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e -> UI.getCurrent().navigate(StageHistoryView.class));
        return appBar;
    }

    private Component createContent(DetailsDrawer component){
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setMargin(Vertical.AUTO, Vertical.RESPONSIVE_L);
//        content.setMaxWidth("1024px");

        return content;
    }

    private DetailsDrawer createTimeLine(List<StageHistoryCreditRequestDto> stageHistoryCreditRequestDtoList){

        PureTimeline timeline = new PureTimeline();
        timeline.setWidthFull();

        for(StageHistoryCreditRequestDto st:stageHistoryCreditRequestDtoList){
            PureTimelineItem item = new PureTimelineItem("11:30");
//            item.setBoxText(String.format("Start: %n \n Init: %n \n Finish: %n"
//                    , st.getStartDateTime().toString(),st.getInitDateTime()==null?"":st.getInitDateTime().toString()
//                    ,st.getFinishedDateTime()==null?"":st.getFinishedDateTime().toString()));
            item.add(new H3(st.getStage()));
            item.add(new Paragraph(st.getState().getDesc()));
//            PureTimelineItem item = new PureTimelineItem("11:30", new H3("Test Item 1"), new Paragraph("Text content...."));

            timeline.add(item);
        }

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidth("100%");
        detailsDrawer.setContent(timeline);

        return detailsDrawer;
    }

}
