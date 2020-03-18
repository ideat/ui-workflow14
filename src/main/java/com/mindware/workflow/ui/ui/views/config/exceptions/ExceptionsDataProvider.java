package com.mindware.workflow.ui.ui.views.config.exceptions;

import com.mindware.workflow.ui.backend.entity.exceptions.Exceptions;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class ExceptionsDataProvider extends ListDataProvider<Exceptions> {
    private String filterText = "";

    public ExceptionsDataProvider(Collection<Exceptions> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText, "Filtro no puede ser nulo");
        if (Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(exceptions ->
                passesFilter(exceptions.getInternalCode(), filterText)
                        || passesFilter(exceptions.getDescription(),filterText)
                        || passesFilter(exceptions.getTypeException(),filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
