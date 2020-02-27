package com.mindware.workflow.ui.ui.views.observation;

import com.mindware.workflow.ui.backend.entity.observation.dto.ObservationCreditRequestApplicant;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class ObservationCreditRequestApplicantDataProvider extends ListDataProvider<ObservationCreditRequestApplicant> {
    private String filterText ="";

    public ObservationCreditRequestApplicantDataProvider(Collection<ObservationCreditRequestApplicant> items){
        super(items);
    }
    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser omitido");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(observationCreditRequestApplicant ->
                passesFilter(observationCreditRequestApplicant.getFullName(),filterText)
                || passesFilter(observationCreditRequestApplicant.getNumberRequest(),filterText)
                || passesFilter(observationCreditRequestApplicant.getNumberApplicant(),filterText)
                );
    }
    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
