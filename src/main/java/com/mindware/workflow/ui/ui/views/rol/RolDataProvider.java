package com.mindware.workflow.ui.ui.views.rol;

import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;

public class RolDataProvider extends ListDataProvider<Rol> {
    private String filterText = "";

    public RolDataProvider(Collection<Rol> items) {
        super(items);
    }
}
