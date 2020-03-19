package com.mindware.workflow.ui.ui.views.authorizerExceptions;

import com.mindware.workflow.ui.backend.entity.exceptions.AuthorizerExceptionsCreditRequestDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class AuthorizerExceptionsCreditRequestDtoDataProvider extends ListDataProvider<AuthorizerExceptionsCreditRequestDto> {
    private String filterText="";

    public AuthorizerExceptionsCreditRequestDtoDataProvider(Collection<AuthorizerExceptionsCreditRequestDto> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede ser nulo");
        if(Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(authorizerExceptionsCreditRequestDto ->
                passesFilter(authorizerExceptionsCreditRequestDto.getFullName(),filterText)
                        || passesFilter(authorizerExceptionsCreditRequestDto.getLoginUser(),filterText)
                        || passesFilter(authorizerExceptionsCreditRequestDto.getCurrency(),filterText)
                        || passesFilter(authorizerExceptionsCreditRequestDto.getNumberRequest(),filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
