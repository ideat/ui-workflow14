package com.mindware.workflow.ui.ui.views.creditResolution;

import com.mindware.workflow.ui.backend.entity.creditResolution.CreditResolutionCreditRequestDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class CreditResolutionCreditRequestDataProvider extends ListDataProvider<CreditResolutionCreditRequestDto> {
    private String filterText ="";

    public CreditResolutionCreditRequestDataProvider(Collection<CreditResolutionCreditRequestDto> items){
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser nulo");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(creditResolutionCreditRequestDto ->
            passesFilter(creditResolutionCreditRequestDto.getFullName(),filterText)
            || passesFilter(creditResolutionCreditRequestDto.getCity(),filterText)
            || passesFilter(creditResolutionCreditRequestDto.getNumberRequest(),filterText)
            || passesFilter(creditResolutionCreditRequestDto.getNumberApplicant(),filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
