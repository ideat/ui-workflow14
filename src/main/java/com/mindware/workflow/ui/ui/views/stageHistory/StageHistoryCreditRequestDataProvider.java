package com.mindware.workflow.ui.ui.views.stageHistory;

import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistoryCreditRequestDto;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class StageHistoryCreditRequestDataProvider  extends ListDataProvider<StageHistoryCreditRequestDto> {
    private String filter="";

    public StageHistoryCreditRequestDataProvider(Collection<StageHistoryCreditRequestDto> items) {
        super(items);
    }

    public void setFilter(String filter){
        Objects.requireNonNull(filter,"Filtro no puede ser omitido");
        if(Objects.equals(this.filter,filter.trim())){
            return;
        }
        this.filter = filter.trim();
        setFilter(StageHistoryCreditRequestDto ->
                passesFilter(StageHistoryCreditRequestDto.getFullName(),filter)
              || passesFilter(StageHistoryCreditRequestDto.getNumberRequest(),filter)
              || passesFilter(StageHistoryCreditRequestDto.getOfficer(),filter)
              || passesFilter(StageHistoryCreditRequestDto.getCity(),filter)
        );
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText.toLowerCase());
    }
}
