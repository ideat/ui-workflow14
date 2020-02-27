package com.mindware.workflow.ui.ui.views.config.workflowProduct;

import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
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

    private WorflowProductView(){
        getProductList();
        setViewContent(createContent());
    }

    private void getProductList(){
        parameterRestTemplate = new ParameterRestTemplate();
        productCredits = parameterRestTemplate.getParametersByCategory("PRODUCTOS");
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

        Grid<Parameter> grid = new Grid<>();
        grid.setItems(productCredits);
        grid.setSizeFull();

        grid.addSelectionListener(e ->{
            Map<String,List<String>> p = new HashMap<>();
            List<String> code = new ArrayList<>();
            code.add(e.getFirstSelectedItem().get().getValue());
            List<String> product = new ArrayList<>();
            product.add(e.getFirstSelectedItem().get().getDescription());

            p.put("code",code);
            p.put("product",product);

            QueryParameters qp = new QueryParameters(p);
            UI.getCurrent().navigate("workflow-product-register",qp);
        });

        grid.addColumn(Parameter::getValue).setFlexGrow(0).setHeader("Cod. Producto")
                .setResizable(true).setWidth(UIUtils.COLUMN_WIDTH_M);
        grid.addColumn(Parameter::getDescription).setFlexGrow(1).setHeader("Producto")
                .setResizable(true);
        layout.add(grid);
        return layout;
    }



}
