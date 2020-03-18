package com.mindware.workflow.ui.ui.views.config.authorizer;

import com.mindware.workflow.ui.backend.entity.exceptions.UserAuthorizer;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class UserAuthorizerDataProvider extends ListDataProvider<UserAuthorizer> {
    private String filterText ="";

    public UserAuthorizerDataProvider(Collection<UserAuthorizer> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText,"Filtro no puede es nulo");
        if (Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        this.filterText = filterText.trim();

        setFilter(userAuthorizer ->
                passesFilter(userAuthorizer.getFullName(), filterText)
                        || passesFilter(userAuthorizer.getCity(), filterText)
                        || passesFilter(userAuthorizer.getScope(), filterText));

    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
