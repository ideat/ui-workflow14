package com.mindware.workflow.ui.ui.views.report;

import com.mindware.workflow.ui.backend.rest.applicantStatement.ApplicantStatementRestTemplate;
import com.mindware.workflow.ui.backend.rest.cashFlow.CashFlowCreditRequestReportRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditResolution.CreditResolutionRestTemplate;
import com.mindware.workflow.ui.backend.rest.legal.LegalInformationReportDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.observation.ObservationRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementDtoRestTemplate;
import com.mindware.workflow.ui.backend.rest.paymentPlan.PaymentPlanDtoRestTemplate;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.detailsdrawer.DetailsDrawer;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.EmbeddedPdfDocument;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.util.css.FlexDirection;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "report-preview", layout = MainLayout.class)
@PageTitle("Previsualizacion Reporte")
public class ReportPreview extends SplitViewFrame implements HasUrlParameter<String> {

    private byte[] file;
    private String title;
    private String previousPage;
    private FlexBoxLayout contentReport;

//    private PaymentPlanDtoRestTemplate restTemplate;
    private QueryParameters qp;
    private Map<String, List<String>> paramPrev;

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        AppBar appBar =initAppBar();
        appBar.setTitle(title);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {


        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> param = queryParameters.getParameters();
        paramPrev = new HashMap<>();

        if(param.get("origin").get(0).equals("payment-plan")) {
            PaymentPlanDtoRestTemplate restTemplate = new PaymentPlanDtoRestTemplate();
            title = param.get("title").get(0);
            Integer numberRequest = Integer.parseInt(param.get("number-request").get(0));
            previousPage = param.get("path").get(0);
            file = restTemplate.report(numberRequest);
            paramPrev.put("numberRequest",param.get("number-request"));
            paramPrev.put("typeRelation", param.get("type-relation"));
            paramPrev.put("numberApplicant",param.get("number-applicant"));
            paramPrev.put("idCreditRequest", param.get("id-credit-request"));
            paramPrev.put("fullName", param.get("full-name"));

        }else if(param.get("origin").get(0).equals("earning")){
            PatrimonialStatementDtoRestTemplate restTemplate = new PatrimonialStatementDtoRestTemplate();
            title = param.get("title").get(0);
            previousPage = param.get("path").get(0);
            file = restTemplate.reportSales(param.get("id-credit-request-applicant").get(0),
                    param.get("element").get(0),param.get("activity").get(0),param.get("id-applicant").get(0), param.get("id-patrimonial-statement").get(0));
            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramPrev.put("element",param.get("element"));
            paramPrev.put("id-applicant",param.get("id-applicant"));
            paramPrev.put("category", param.get("category"));
            paramPrev.put("activity", param.get("activity"));
            paramPrev.put("id-patrimonial-statement",param.get("id-patrimonial-statement"));

        }else if(param.get("origin").get(0).equals("vaeIndependent")){
            PatrimonialStatementDtoRestTemplate restTemplate = new PatrimonialStatementDtoRestTemplate();
            title = param.get("title").get(0);
            previousPage = param.get("path").get(0);

            file = restTemplate.reportVaeIndependent(param.get("id-patrimonial-statement").get(0),
                    param.get("id-applicant").get(0));


            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramPrev.put("element",param.get("element"));
            paramPrev.put("id-applicant",param.get("id-applicant"));
            paramPrev.put("category", param.get("category"));
            paramPrev.put("activity", param.get("activity"));
            paramPrev.put("id-patrimonial-statement",param.get("id-patrimonial-statement"));

        }else if(param.get("origin").get(0).equals("costProduct")){
            PatrimonialStatementDtoRestTemplate restTemplate = new PatrimonialStatementDtoRestTemplate();
            title = param.get("title").get(0);
            previousPage = param.get("path").get(0);
            file = restTemplate.reportCostProduct(param.get("product").get(0),param.get("id-patrimonial-statement").get(0),
                    param.get("id-applicant").get(0));

            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramPrev.put("element",param.get("element"));
            paramPrev.put("id-applicant",param.get("id-applicant"));
            paramPrev.put("category", param.get("category"));
            paramPrev.put("activity", param.get("activity"));
            paramPrev.put("id-patrimonial-statement",param.get("id-patrimonial-statement"));

        }else if(param.get("origin").get(0).equals("inventoryProduction")){
            PatrimonialStatementDtoRestTemplate restTemplate = new PatrimonialStatementDtoRestTemplate();
            title = param.get("title").get(0);
            previousPage = param.get("path").get(0);
            file = restTemplate.reportInventoryProduction(param.get("id-patrimonial-statement").get(0),
                    param.get("id-applicant").get(0));

            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramPrev.put("element",param.get("element"));
            paramPrev.put("id-applicant",param.get("id-applicant"));
            paramPrev.put("category", param.get("category"));
            paramPrev.put("activity", param.get("activity"));
            paramPrev.put("id-patrimonial-statement",param.get("id-patrimonial-statement"));
        }else if(param.get("origin").get(0).equals("inventorySales")){
            PatrimonialStatementDtoRestTemplate restTemplate = new PatrimonialStatementDtoRestTemplate();
            title = param.get("title").get(0);
            previousPage = param.get("path").get(0);
            file = restTemplate.reportInventorySales(param.get("id-patrimonial-statement").get(0),
                    param.get("id-applicant").get(0));

            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramPrev.put("element",param.get("element"));
            paramPrev.put("id-applicant",param.get("id-applicant"));
            paramPrev.put("category", param.get("category"));
            paramPrev.put("activity", param.get("activity"));
            paramPrev.put("id-patrimonial-statement",param.get("id-patrimonial-statement"));
        }else if(param.get("origin").get(0).contains("Solicitud Credito")) {
            ApplicantStatementRestTemplate restTemplate = new ApplicantStatementRestTemplate();
            previousPage = param.get("path").get(0);
            file = restTemplate.reportApplicantStatement(param.get("number-request").get(0),
                    param.get("id-applicant").get(0), param.get("id-credit-request-applicant").get(0),param.get("origin").get(0));
            paramPrev.put("type-relation", param.get("type-relation"));
            paramPrev.put("id-applicant", param.get("id-applicant"));
            paramPrev.put("id-credit-request", param.get("id-credit-request"));
            paramPrev.put("full-name", param.get("full-name"));
            paramPrev.put("number-applicant", param.get("number-applicant"));
            paramPrev.put("number-request", param.get("number-request"));
            paramPrev.put("id-credit-request-applicant", param.get("id-credit-request-applicant"));
        }else if(param.get("origin").get(0).equals("Declaracion Jurada de Bienes e Ingresos")){
            ApplicantStatementRestTemplate restTemplate = new ApplicantStatementRestTemplate();
            previousPage = param.get("path").get(0);
            file = restTemplate.reportApplicantStatement(param.get("number-request").get(0),
                    param.get("id-applicant").get(0),param.get("id-credit-request-applicant").get(0),param.get("origin").get(0));
            paramPrev.put("type-relation",param.get("type-relation"));
            paramPrev.put("id-applicant",param.get("id-applicant"));
            paramPrev.put("id-credit-request",param.get("id-credit-request"));
            paramPrev.put("full-name",param.get("full-name"));
            paramPrev.put("number-applicant",param.get("number-applicant"));
            paramPrev.put("number-request",param.get("number-request"));
            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
        }else if(param.get("origin").get(0).equals("Observation Analisis Crediticio")){
            ObservationRestTemplate restTemplate = new ObservationRestTemplate();
            previousPage = param.get("path").get(0);
            file = restTemplate.report(param.get("number-request").get(0),param.get("number-applicant").get(0),
                    param.get("task").get(0));
            paramPrev.put("number-applicant",param.get("number-applicant"));
            paramPrev.put("number-request",param.get("number-request"));
            paramPrev.put("task",param.get("task"));
        }else if(param.get("origin").get(0).equals("credit-resolution")){
            CreditResolutionRestTemplate restTemplate = new CreditResolutionRestTemplate();
            previousPage = param.get("path").get(0);
            file = restTemplate.reportCreditResolution(param.get("number-request").get(0),
                    param.get("number-applicant").get(0),param.get("id-credit-request-applicant").get(0));
            paramPrev.put("number-applicant",param.get("number-applicant"));
            paramPrev.put("number-request",param.get("number-request"));
            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));
            paramPrev.put("full-name",param.get("full-name"));
        }else if(param.get("origin").get(0).equals("legal-information-report")){
            LegalInformationReportDtoRestTemplate restTemplate = new LegalInformationReportDtoRestTemplate();
            previousPage = param.get("path").get(0);
            file = restTemplate.report(param.get("number-request").get(0),
                    param.get("created-by").get(0), param.get("number-applicant").get(0));
            paramPrev.put("number-request",param.get("number-request"));
            paramPrev.put("number-applicant",param.get("number-applicant"));
            paramPrev.put("created-by",param.get("created-by"));
            paramPrev.put("task",param.get("task"));
            paramPrev.put("full-name",param.get("full-name"));
        }else if(param.get("origin").get(0).equals("cashFlow")){
            CashFlowCreditRequestReportRestTemplate restTemplate = new CashFlowCreditRequestReportRestTemplate();
            file = restTemplate.report(param.get("number-request").get(0));
            previousPage = param.get("path").get(0);
            paramPrev.put("number-request",param.get("number-request"));
            paramPrev.put("full-name", param.get("full-name"));
            paramPrev.put("id-credit-request-applicant",param.get("id-credit-request-applicant"));



        }

        qp = new QueryParameters(paramPrev);
        contentReport = (FlexBoxLayout) createContent(createReportView());
        setViewContent(contentReport);
    }

    private AppBar initAppBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(
          e -> UI.getCurrent().navigate(previousPage,qp)
        );

        return appBar;
    }

    private Component createContent(DetailsDrawer component){
        FlexBoxLayout content = new FlexBoxLayout(component);
        content.setFlexDirection(FlexDirection.ROW);
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);

        return content;
    }

    private DetailsDrawer createReportView(){
        Div layout = new Div();
        layout.setHeightFull();
        ByteArrayInputStream bis = new ByteArrayInputStream(file);
        StreamResource s = new StreamResource("reporte.pdf", () -> bis);
        layout.add(new EmbeddedPdfDocument(s));

        DetailsDrawer detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeight("90%");
        detailsDrawer.setWidthFull();
        detailsDrawer.setContent(layout);
        return detailsDrawer;

    }

}
