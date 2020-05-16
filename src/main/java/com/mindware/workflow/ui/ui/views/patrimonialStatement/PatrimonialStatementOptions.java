package com.mindware.workflow.ui.ui.views.patrimonialStatement;

import com.mindware.workflow.ui.backend.entity.CreditRequestApplicant;
import com.mindware.workflow.ui.backend.entity.patrimonialStatement.PatrimonialStatement;
import com.mindware.workflow.ui.backend.entity.dto.PatrimonialStatementOptionsDto;
import com.mindware.workflow.ui.backend.rest.applicantStatement.ApplicantStatementRestTemplate;
import com.mindware.workflow.ui.backend.rest.creditRequestApplicant.CreditRequestAplicantRestTemplate;
import com.mindware.workflow.ui.backend.rest.patrimonialStatement.PatrimonialStatementRestTemplate;
import com.mindware.workflow.ui.backend.util.DownloadLink;
import com.mindware.workflow.ui.backend.util.UtilValues;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.components.navigation.bar.AppBar;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

@Route(value = "patrimonial-statement-options", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Opciones Registro Declaracion Patrimonial")
public class PatrimonialStatementOptions extends SplitViewFrame implements HasUrlParameter<String>, RouterLayout {

    private PatrimonialStatementRestTemplate patrimonialStatementRestTemplate;
    private CreditRequestApplicant creditRequestApplicant;
    private CreditRequestAplicantRestTemplate creditRequestAplicantRestTemplate;
    private Map<String,List<String>> param;

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);

        AppBar appBar = initBar();
        appBar.setTitle("Declaracion Patrimonial - "+ param.get("full-name").get(0) + " - "+ param.get("type-relation").get(0).toUpperCase() );
        setViewContent(createContent());


    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        patrimonialStatementRestTemplate = new PatrimonialStatementRestTemplate();
        creditRequestAplicantRestTemplate = new CreditRequestAplicantRestTemplate();

        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        param = queryParameters.getParameters();

        creditRequestApplicant = creditRequestAplicantRestTemplate.getCreditRequestApplicant(
                Integer.parseInt(param.get("number-request").get(0)),
                Integer.parseInt(param.get("number-applicant").get(0)),
                param.get("type-relation").get(0));

    }

    private AppBar initBar(){
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e ->{
            UI.getCurrent().navigate(CreditPatrimonialStatement.class);
        });

        return appBar;
    }

    private StreamResource getStreamResource(String filename, String content) {
        return new StreamResource(filename,
                () -> new ByteArrayInputStream(content.getBytes()));
    }

    private Component createContent() {
        Button btnPrint = new Button("Imprimir");
        btnPrint.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_CONTRAST,ButtonVariant.LUMO_SMALL);

        Path paths = Paths.get(System.getProperties().get("user.home").toString());
        String pathfile = paths.toString() + "/home_verification/" + "homeVerification_"+param.get("number-request").get(0)+".xls";
        File in = new File(pathfile);
        DownloadLink downloadLink = new DownloadLink(in);
        downloadLink.setVisible(false);

        ComboBox<String> cmbReport = new ComboBox<>();
        cmbReport.setWidth("400px");
        cmbReport.setItems("Solicitud Credito","Solicitud Credito Persona Juridica","Verificacion domiciliaria y Laboral","Declaracion Jurada de Bienes e Ingresos");
        cmbReport.setPlaceholder("Seleccionar Reporte");
        cmbReport.addValueChangeListener(event ->{
           if (event.getValue().equals("Verificacion domiciliaria y Laboral")){
               btnPrint.setText("Generar");

           }else{
               btnPrint.setText("Imprmir");
               downloadLink.setVisible(false);
           }
        });

        btnPrint.addClickListener(event -> {
            if(cmbReport.getValue()!=null) {
                Map<String, List<String>> paramPso = new HashMap<>();
                List<String> path = new ArrayList<>();
                List<String> origin = new ArrayList<>();
                if (cmbReport.getValue().equals("Declaracion Jurada de Bienes e Ingresos")) {
                    origin.add("Declaracion Jurada de Bienes e Ingresos");
                }else
                if (cmbReport.getValue().equals("Solicitud Credito")) {
                    origin.add("Solicitud Credito");
                }else if (cmbReport.getValue().equals("Solicitud Credito Persona Juridica")) {
                    origin.add("Solicitud Credito Persona Juridica");
                }else if(cmbReport.getValue().equals("Verificacion domiciliaria y Laboral")){
                    origin.add("Verificacion domiciliaria y Laboral");
                    ApplicantStatementRestTemplate restTemplate = new ApplicantStatementRestTemplate();

                    restTemplate.reportHomeVerification(param.get("number-request").get(0),
                            param.get("id-applicant").get(0),
                            param.get("id-credit-request-applicant").get(0));
                    UIUtils.showNotification("Declaracion generada, puede descargar");
                    downloadLink.setVisible(true);
                    return;
                }

                path.add("patrimonial-statement-options");

                paramPso.put("path", path);
                paramPso.put("origin", origin);
                paramPso.put("type-relation", param.get("type-relation"));
                paramPso.put("id-applicant", param.get("id-applicant"));
                paramPso.put("id-credit-request", param.get("id-credit-request"));
                paramPso.put("full-name", param.get("full-name"));
                paramPso.put("number-applicant", param.get("number-applicant"));
                paramPso.put("number-request", param.get("number-request"));
                paramPso.put("id-credit-request-applicant", param.get("id-credit-request-applicant"));

                QueryParameters qp = new QueryParameters(paramPso);
                UI.getCurrent().navigate("report-preview", qp);
            }else{
                UIUtils.showNotification("Seleccione Reporte");
                cmbReport.focus();
            }
        });

        Div content = new Div(cmbReport,btnPrint, downloadLink, createMenuPatrimonialStatement());
//        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
//        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private FormLayout createMenuPatrimonialStatement(){
        List<PatrimonialStatementOptionsDto> assets = getOptionsPatrimonialStatement("ACTIVO");
        List<PatrimonialStatementOptionsDto> liabilities = getOptionsPatrimonialStatement("PASIVO");
        List<PatrimonialStatementOptionsDto> earnings = getOptionsPatrimonialStatement("INGRESOS");
        List<PatrimonialStatementOptionsDto> expenses = getOptionsPatrimonialStatement("EGRESOS");

        Accordion accordionA = new Accordion();
        Accordion accordionL = new Accordion();
        Accordion accordionE = new Accordion();
        Accordion accordionX = new Accordion();


        FormLayout formOptions = new FormLayout();

        Grid<PatrimonialStatementOptionsDto> gridAssets = new Grid<>();
        gridAssets.addThemeVariants(GridVariant.LUMO_COMPACT);
        gridAssets.addSelectionListener(e -> {
            paramLocation(e,"ACTIVO");
        });
        gridAssets.setItems(assets);
        gridAssets.addColumn(TemplateRenderer.<PatrimonialStatementOptionsDto>  of (
                "<div style='white-space:normal'> <small> [[item.element]] </small> </div>"
        ) .withProperty("element",PatrimonialStatementOptionsDto::getElement)).setHeader("Activo");
        gridAssets.addColumn(PatrimonialStatementOptionsDto::getAmount).setHeader("Total");

        Grid<PatrimonialStatementOptionsDto> gridLiabilities = new Grid<>();
        gridLiabilities.addThemeVariants(GridVariant.LUMO_COMPACT);
        gridLiabilities.setItems(liabilities);
        gridLiabilities.addSelectionListener(e ->{
            paramLocation(e,"PASIVO");
        });


        gridLiabilities.addColumn(TemplateRenderer.<PatrimonialStatementOptionsDto>  of (
                "<div style='white-space:normal'> <small> [[item.element]] </small> </div>"
        ) .withProperty("element",PatrimonialStatementOptionsDto::getElement)).setHeader("Pasivo");
        gridLiabilities.addColumn(PatrimonialStatementOptionsDto::getAmount).setHeader("Total");


        accordionA.add("Activos",gridAssets);
        accordionL.add("Pasivos",gridLiabilities);


        Grid<PatrimonialStatementOptionsDto> gridEarnings = new Grid<>();
        gridEarnings.addThemeVariants(GridVariant.LUMO_COMPACT);
        gridEarnings.addSelectionListener(e ->{
           paramLocation(e, "INGRESOS");
        });
        gridEarnings.setItems(earnings);
        gridEarnings.addColumn(TemplateRenderer.<PatrimonialStatementOptionsDto>  of (
                "<div style='white-space:normal'> <small> [[item.element]] </small> </div>"
        ) .withProperty("element",PatrimonialStatementOptionsDto::getElement)).setHeader("Ingreso");
        gridEarnings.addColumn(PatrimonialStatementOptionsDto::getAmount).setHeader("Total");

        Grid<PatrimonialStatementOptionsDto> gridExpenses = new Grid<>();
        gridExpenses.setItems(expenses);
        gridExpenses.addThemeVariants(GridVariant.LUMO_COMPACT);
        gridExpenses.addSelectionListener(e ->{
           paramLocation(e,"EGRESOS");
        });
        gridExpenses.addColumn(TemplateRenderer.<PatrimonialStatementOptionsDto>  of (
                "<div style='white-space:normal'> <small> [[item.element]] </small> </div>"
        ) .withProperty("element",PatrimonialStatementOptionsDto::getElement)).setHeader("Egreso");
        gridExpenses.addColumn(PatrimonialStatementOptionsDto::getAmount).setHeader("Total");

        accordionE.add("Ingresos",gridEarnings);
        accordionX.add("Egresos",gridExpenses);

        formOptions.add(accordionA,accordionL,accordionE,accordionX);

        return formOptions;
    }

    private List<PatrimonialStatementOptionsDto> getOptionsPatrimonialStatement(String category){
        List<PatrimonialStatementOptionsDto> optionsList = new ArrayList<>();
        List<String> elements = new ArrayList<>();

        elements = UtilValues.getParamterValue(category);

        for(String s : elements){
            PatrimonialStatementOptionsDto statements = new PatrimonialStatementOptionsDto();
            statements.setElement(s);
            String total = "0";
            if (creditRequestApplicant!=null)
                total = getTotalCategoryElement(creditRequestApplicant.getId(),category,s);
            statements.setAmount(total);
            optionsList.add(statements);
        }
        return optionsList;
    }

    private String getTotalCategoryElement(UUID id, String category, String element){
        List<PatrimonialStatement> list = patrimonialStatementRestTemplate.getByIdCreditRequestApplicantCategoryElement(id,category, element);
        Double total = 0.0;
        for(PatrimonialStatement p : list){
            if(p.getFieldDouble1()==null) {
                total = 0.0;
            }else {
                total = total + p.getFieldDouble1();
            }
        }
        DecimalFormat totalString = (DecimalFormat)DecimalFormat.getNumberInstance(Locale.GERMAN);

        return totalString.format(total);
    }

    private void paramLocation(SelectionEvent<Grid<PatrimonialStatementOptionsDto>, PatrimonialStatementOptionsDto> e, String type) {
        Map<String, List<String>> paramPatrimonial = new HashMap<>();
        List<String> categoryList = new ArrayList<>();
        List<String> elementList = new ArrayList<>();

        categoryList.add(type);
        paramPatrimonial.put("category",categoryList);

        elementList.add(e.getFirstSelectedItem().get().getElement());
        paramPatrimonial.put("element", elementList);

        paramPatrimonial.put("number-request", param.get("number-request") );

        paramPatrimonial.put("number-applicant", param.get("number-applicant"));

        paramPatrimonial.put("type-relation",param.get("type-relation"));

        List<String> idCreditRequestList = new ArrayList<>();
        idCreditRequestList.add(param.get("id-credit-request").get(0));
        paramPatrimonial.put("id-credit-request",idCreditRequestList);
        paramPatrimonial.put("full-name",param.get("full-name"));
        paramPatrimonial.put("id-applicant",param.get("id-applicant"));
        paramPatrimonial.put("id-credit-request-applicant", param.get("id-credit-request-applicant"));

        QueryParameters qp = new QueryParameters(paramPatrimonial);

        UI.getCurrent().navigate("patrimonial-statement",qp);
    }

}
