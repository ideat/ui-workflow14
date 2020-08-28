package com.mindware.workflow.ui.ui.views.config.workflowProduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.config.WorkflowProduct;
import com.mindware.workflow.ui.backend.entity.config.dto.TypeCreditObjectCreditDto;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.util.TypeCreditDto;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "workflowProduct", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista Productos Credito")
public class WorflowProductView extends ViewFrame implements RouterLayout {
    private List<Parameter> productCredits;

    List<TypeCreditObjectCreditDto> typeCreditObjectCreditDtoList;
    ListDataProvider<TypeCreditObjectCreditDto> dataProvider;

    private TextField codeTypeCreditFilter;
    private TextField typeCreditDescriptionFilter;
    private TextField externalCodeObjectCreditFilter;
    private TextField objectCreditDescriptionFilter;

    private WorflowProductView() throws JsonProcessingException {
        getProductList();
        setViewContent(createContent());
    }

    private void getProductList() throws JsonProcessingException {

        typeCreditObjectCreditDtoList = TypeCreditDto.getAllTypeCreditObjectCreditDto();
        dataProvider = new ListDataProvider<>(typeCreditObjectCreditDtoList);
    }

    private Component createContent(){
        FlexBoxLayout content = new FlexBoxLayout(createLayout());
        content.addClassName("grid-view");
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        return content;
    }

    private VerticalLayout createLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<TypeCreditObjectCreditDto> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();

        grid.addSelectionListener(e ->{
            Map<String,List<String>> p = new HashMap<>();
            List<String> codeTypeCredit = new ArrayList<>();
            codeTypeCredit.add(e.getFirstSelectedItem().get().getCodeTypeCredit());
            List<String> codeObjectCredit = new ArrayList<>();
            codeObjectCredit.add(e.getFirstSelectedItem().get().getExternalCodeObjectCredit().toString());
            List<String> objectDescription = new ArrayList<>();
            objectDescription.add(e.getFirstSelectedItem().get().getObjectCreditDescription());

            p.put("code-type-credit",codeTypeCredit);
            p.put("code-object-credit",codeObjectCredit);
            p.put("product",objectDescription);

            QueryParameters qp = new QueryParameters(p);
            UI.getCurrent().navigate("workflow-product-register",qp);
        });

        grid.addColumn(TypeCreditObjectCreditDto::getCodeTypeCredit)
                .setFlexGrow(0)
                .setHeader("Cod. Tipo credito")
                .setResizable(true)
                .setKey("codeTypeCredit")
                .setWidth(UIUtils.COLUMN_WIDTH_M)
                .setSortable(true);
        grid.addColumn(TypeCreditObjectCreditDto::getTypeCreditDescription)
                .setFlexGrow(0)
                .setHeader("Descripcion Tipo credito")
                .setResizable(true)
                .setKey("typeDescription")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(TypeCreditObjectCreditDto::getExternalCodeObjectCredit)
                .setFlexGrow(1)
                .setHeader("Cod. Objeto credito")
                .setSortable(true)
                .setKey("externalCodeObjectCredit")
                .setResizable(true).setSortable(true);
        grid.addColumn(TypeCreditObjectCreditDto::getObjectCreditDescription)
                .setFlexGrow(1)
                .setHeader("Objeto de credito")
                .setResizable(true)
                .setKey("objectCreditDescription")
                .setSortable(true);

        HeaderRow hr = grid.appendHeaderRow();

        codeTypeCreditFilter = new TextField();
        codeTypeCreditFilter.setValueChangeMode(ValueChangeMode.EAGER);
        codeTypeCreditFilter.setWidthFull();
        codeTypeCreditFilter.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("codeTypeCredit")).setComponent(codeTypeCreditFilter);

        typeCreditDescriptionFilter = new TextField();
        typeCreditDescriptionFilter.setValueChangeMode(ValueChangeMode.EAGER);
        typeCreditDescriptionFilter.setWidthFull();
        typeCreditDescriptionFilter.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("typeDescription")).setComponent(typeCreditDescriptionFilter);

        externalCodeObjectCreditFilter = new TextField();
        externalCodeObjectCreditFilter.setValueChangeMode(ValueChangeMode.EAGER);
        externalCodeObjectCreditFilter.setWidthFull();
        externalCodeObjectCreditFilter.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("externalCodeObjectCredit")).setComponent(externalCodeObjectCreditFilter);

        objectCreditDescriptionFilter = new TextField();
        objectCreditDescriptionFilter.setValueChangeMode(ValueChangeMode.EAGER);
        objectCreditDescriptionFilter.setWidthFull();
        objectCreditDescriptionFilter.addValueChangeListener(e -> applyFilter(dataProvider));
        hr.getCell(grid.getColumnByKey("objectCreditDescription")).setComponent(objectCreditDescriptionFilter);

        layout.add(grid);
        return layout;
    }

    private void applyFilter(ListDataProvider<TypeCreditObjectCreditDto> dataProvider){
        dataProvider.clearFilters();
        if(!codeTypeCreditFilter.getValue().trim().equals("")){
            dataProvider.addFilter(workflowProduct ->
                    StringUtils.containsIgnoreCase(workflowProduct.getCodeTypeCredit(),codeTypeCreditFilter.getValue()));
        }
        if(!typeCreditDescriptionFilter.getValue().trim().equals("")){
            dataProvider.addFilter(workflowProduct ->
                    StringUtils.containsIgnoreCase(workflowProduct.getTypeCreditDescription(),typeCreditDescriptionFilter.getValue()));
        }
        if(!externalCodeObjectCreditFilter.getValue().trim().equals("")){
            dataProvider.addFilter(workflowProduct ->
                    StringUtils.containsIgnoreCase(workflowProduct.getExternalCodeObjectCredit().toString(),externalCodeObjectCreditFilter.getValue()));

        }
        if(!objectCreditDescriptionFilter.getValue().trim().equals("")){
            dataProvider.addFilter(workflowProduct ->
                    StringUtils.containsIgnoreCase(workflowProduct.getObjectCreditDescription(),objectCreditDescriptionFilter.getValue()));

        }

    }

}
