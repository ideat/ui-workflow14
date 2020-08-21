package com.mindware.workflow.ui.ui.views.config.workflowProduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.config.TypeCredit;
import com.mindware.workflow.ui.backend.entity.config.dto.TypeCreditObjectCreditDto;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.typeCredit.TypeCreditRestTemplate;
import com.mindware.workflow.ui.backend.util.TypeCreditDto;
import com.mindware.workflow.ui.ui.MainLayout;
import com.mindware.workflow.ui.ui.components.FlexBoxLayout;
import com.mindware.workflow.ui.ui.layout.size.Horizontal;
import com.mindware.workflow.ui.ui.layout.size.Top;
import com.mindware.workflow.ui.ui.layout.size.Vertical;
import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.BoxSizing;
import com.mindware.workflow.ui.ui.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "workflowProduct", layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle("Lista Productos Credito")
public class WorflowProductView extends ViewFrame implements RouterLayout {
    private List<Parameter> productCredits;

    private ParameterRestTemplate parameterRestTemplate;
    List<TypeCreditObjectCreditDto> typeCreditObjectCreditDtoList;

    private WorflowProductView() throws JsonProcessingException {
        getProductList();
        setViewContent(createContent());
    }

    private void getProductList() throws JsonProcessingException {
//        parameterRestTemplate = new ParameterRestTemplate();
//        productCredits = parameterRestTemplate.getParametersByCategory("PRODUCTOS");

        typeCreditObjectCreditDtoList = TypeCreditDto.getAllTypeCreditObjectCreditDto();
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
        grid.setItems(typeCreditObjectCreditDtoList);
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

        grid.addColumn(TypeCreditObjectCreditDto::getCodeTypeCredit).setFlexGrow(0).setHeader("Cod. Tipo credito")
                .setResizable(true).setWidth(UIUtils.COLUMN_WIDTH_M);
        grid.addColumn(TypeCreditObjectCreditDto::getExternalCodeObjectCredit).setFlexGrow(1).setHeader("Cod. Objeto credito")
                .setResizable(true);
        grid.addColumn(TypeCreditObjectCreditDto::getObjectCreditDescription).setFlexGrow(1).setHeader("Objeto de credito")
                .setResizable(true);
        layout.add(grid);
        return layout;
    }



}
