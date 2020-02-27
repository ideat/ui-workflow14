package com.mindware.workflow.ui.ui.views.config.office;


import com.mindware.workflow.ui.backend.entity.Office;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OfficeDataProvider extends ListDataProvider<Office> {
    private String filterText="";

    public OfficeDataProvider(List<Office> items) {
        super(items);
    }


    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede es nulo");
        if (Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();

        setFilter(office ->
                passesFilter(office.getName(), filterText)
                        || passesFilter(office.getTypeOffice(), filterText)
                        || passesFilter(office.getInternalCode(), filterText)
                        || passesFilter(office.getCity(),filterText));

    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
