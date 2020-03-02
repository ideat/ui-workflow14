package com.mindware.workflow.ui.ui.views.cashFlow;

import com.mindware.workflow.ui.backend.entity.cashFlow.CashFlowCreditRequestApplicantDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class CashFlowCreditRequestApplicantDtoDataProvider extends ListDataProvider<CashFlowCreditRequestApplicantDto> {
    private String filterText ="";

    public CashFlowCreditRequestApplicantDtoDataProvider(Collection<CashFlowCreditRequestApplicantDto> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser omitido");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(cashFlowCreditRequestApplicantDto ->
                passesFilter(cashFlowCreditRequestApplicantDto.getFullName(),filterText)
                        || passesFilter(cashFlowCreditRequestApplicantDto.getNumberRequest(),filterText)
                        || passesFilter(cashFlowCreditRequestApplicantDto.getCity(),filterText)
                        || passesFilter(cashFlowCreditRequestApplicantDto.getOfficial(),filterText)
        );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
