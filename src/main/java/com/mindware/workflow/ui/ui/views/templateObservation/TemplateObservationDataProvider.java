package com.mindware.workflow.ui.ui.views.templateObservation;

import com.mindware.workflow.ui.backend.entity.templateObservation.TemplateObservation;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TemplateObservationDataProvider extends ListDataProvider<TemplateObservation> {
    private String filterText="";

    public TemplateObservationDataProvider(List<TemplateObservation> items){
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText, "Filtro no puede ser nulo");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();

        setFilter(templateObservation ->
            passesFilter(templateObservation.getCategory(),filterText)
            || passesFilter(templateObservation.getTask(),filterText)
            );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
