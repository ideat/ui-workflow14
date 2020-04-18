package com.mindware.workflow.ui.ui.views.contract;

import com.mindware.workflow.ui.backend.entity.contract.ContractCreditRequestDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class ContractCreditRequestDtoDataProvider extends ListDataProvider<ContractCreditRequestDto> {
    private String filterText="";

    public ContractCreditRequestDtoDataProvider(Collection<ContractCreditRequestDto> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser nulo");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(contractCreditRequestDto ->
                passesFilter(contractCreditRequestDto.getFullName(),filterText)
                || passesFilter(contractCreditRequestDto.getCity(),filterText)
                || passesFilter(contractCreditRequestDto.getNameOffice(),filterText)
                || passesFilter(contractCreditRequestDto.getNumberApplicant(),filterText)
                || passesFilter(contractCreditRequestDto.getNumberRequest(),filterText)
                );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
