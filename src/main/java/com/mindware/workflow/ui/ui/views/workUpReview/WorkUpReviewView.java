package com.mindware.workflow.ui.ui.views.workUpReview;

import com.mindware.workflow.ui.backend.entity.workUpReview.WorkUpReview;
import com.mindware.workflow.ui.backend.rest.workUpReview.WorkUpReviewRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.mindware.workflow.ui.ui.views.patrimonialStatement.CreditPatrimonialStatement;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.*;

import java.util.List;
import java.util.Map;

public class WorkUpReviewView extends SplitViewFrame implements HasUrlParameter<String> {
    private Accordion accordion;
    private Grid<WorkUpReview> gridWorkUpReview;
    private List<WorkUpReview> workUpReviewList;
    private Map<String,List<String>> param;
    private WorkUpReviewRestTemplate workUpReviewRestTemplate;

    @Override
    protected  void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        AppBar appBar = initBar();
        appBar.setTitle("Observaciones solicitud - " + param.get("number-request").get(0));


    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {

        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        param = queryParameters.getParameters();
    }

    private AppBar initBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(CreditPatrimonialStatement.class);
        });

        return appBar;
    }
}
