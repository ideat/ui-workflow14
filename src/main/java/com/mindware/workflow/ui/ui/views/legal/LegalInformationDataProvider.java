package com.mindware.workflow.ui.ui.views.legal;

import com.mindware.workflow.ui.backend.entity.legal.dto.LegalInformationCreditRequestDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class LegalInformationDataProvider extends ListDataProvider<LegalInformationCreditRequestDto> {
    private String filterText = "";
    public LegalInformationDataProvider(Collection<LegalInformationCreditRequestDto> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser omitido");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(legalInformationCreditRequestDto ->
                passesFilter(legalInformationCreditRequestDto.getFullName(),filterText)
                || passesFilter(legalInformationCreditRequestDto.getNumberRequest(),filterText)
                || passesFilter(legalInformationCreditRequestDto.getCity(),filterText)
                || passesFilter(legalInformationCreditRequestDto.getOfficial(),filterText)
                );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
