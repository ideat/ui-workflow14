package com.mindware.workflow.ui.ui.views.creditRequest;

import com.mindware.workflow.ui.backend.entity.dto.CreditRequestApplicantDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class CreditRequestApplicantDataProvider extends ListDataProvider<CreditRequestApplicantDto> {
    private String filterText="";

    public CreditRequestApplicantDataProvider(Collection<CreditRequestApplicantDto> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser nulo");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.filterText = filterText.trim();
        setFilter(creditRequestApplicantDto ->
                passesFilter(creditRequestApplicantDto.getFullName(),filterText)
                    || passesFilter(creditRequestApplicantDto.getRequestDate().format(formatters),filterText)
                    || passesFilter(creditRequestApplicantDto.getCurrency(),filterText)
                    || passesFilter(creditRequestApplicantDto.getNumberRequest(),filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
