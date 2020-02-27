package com.mindware.workflow.ui.ui.views.config.parameter;

import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ParameterDataProvider extends ListDataProvider<Parameter> {
    private String valueFilter = "";
    private String descriptionFilter = "";
    private String categoryFilter = "";
    
    public ParameterDataProvider(List<Parameter> items) {
        super(items);
    }

    public void setFilter(String valueFilter, String descriptionFilter, String categoryFilter){
        Objects.requireNonNull(valueFilter, "Filtro valor no puede ser nulo");
        Objects.requireNonNull(categoryFilter, "Filtro categoria no puede ser nulo");
        Objects.requireNonNull(descriptionFilter, "Filtro descripcion no puede ser nulo");
        if (Objects.equals(this.valueFilter,valueFilter.trim())
            && Objects.equals(this.categoryFilter,categoryFilter)
            && Objects.equals(this.descriptionFilter,descriptionFilter)){
            return;
        }
        this.valueFilter = valueFilter.trim();
        this.categoryFilter = categoryFilter.trim();
        this.descriptionFilter = descriptionFilter.trim();


        setFilter(parameter ->
            passesFilter(parameter.getValue(), valueFilter)
                    || passesFilter(parameter.getCategory(), categoryFilter)
                    || passesFilter(parameter.getDescription(), descriptionFilter)
        );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
