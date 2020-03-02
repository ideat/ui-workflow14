package com.mindware.workflow.ui.ui.views.cashFlow;

import com.mindware.workflow.ui.backend.entity.cashFlow.CashFlowCreditRequestApplicantDto;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.util.List;

@Route(value = "cashFlowCreditRequestApplicantView", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista de solicitudes relacionadas a flujos de caja")
public class CashFlowView extends SplitViewFrame implements RouterLayout {
    private Grid<CashFlowCreditRequestApplicantDto> grid;
    private CashFlowCreditRequestApplicantDtoDataProvider dataProvider;
    private List<CashFlowCreditRequestApplicantDto> cashFlowCreditRequestApplicantDtoList;


}
