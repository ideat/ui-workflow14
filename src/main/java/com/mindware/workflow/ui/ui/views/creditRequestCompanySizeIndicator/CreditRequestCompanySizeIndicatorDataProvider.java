package com.mindware.workflow.ui.ui.views.creditRequestCompanySizeIndicator;

import com.mindware.workflow.ui.backend.entity.creditRequest.CompanySizeIndicator;
import com.mindware.workflow.ui.backend.entity.dto.CreditRequestCompanySizeIndicatorDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class CreditRequestCompanySizeIndicatorDataProvider extends ListDataProvider<CreditRequestCompanySizeIndicatorDto> {
    private String filterText ="";

    public CreditRequestCompanySizeIndicatorDataProvider(Collection<CreditRequestCompanySizeIndicatorDto> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText, "Filtro no puede ser nulo");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();

        setFilter(creditRequestCompanySizeIndicator ->
                passesFilter(creditRequestCompanySizeIndicator.getNumberRequest(),filterText)
                        || passesFilter(creditRequestCompanySizeIndicator.getFullName(),filterText)
                        || passesFilter(creditRequestCompanySizeIndicator.getCity(),filterText)
                        || passesFilter(creditRequestCompanySizeIndicator.getLoginUser(),filterText)
        );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
