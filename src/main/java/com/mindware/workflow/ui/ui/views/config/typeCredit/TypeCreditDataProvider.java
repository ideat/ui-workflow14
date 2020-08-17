package com.mindware.workflow.ui.ui.views.config.typeCredit;

import com.mindware.workflow.ui.backend.entity.config.TypeCredit;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class TypeCreditDataProvider extends ListDataProvider<TypeCredit> {
    private String filterText ="";

    public TypeCreditDataProvider(Collection<TypeCredit> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText, "Filtro no puede ser nulo");
        if (Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();
        setFilter(typeCredit ->
                passesFilter(typeCredit.getExternalCode(), filterText)
                        || passesFilter(typeCredit.getDescription(),filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
