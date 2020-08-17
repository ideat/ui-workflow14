package com.mindware.workflow.ui.ui.views.applicant;

import com.mindware.workflow.ui.backend.entity.Applicant;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class ApplicantDataProvider extends ListDataProvider<Applicant> {
    private String filterText="";

    public ApplicantDataProvider(Collection<Applicant> items) {
        super(items);
    }

    public void setFilter(String filterText){
        Objects.requireNonNull(filterText, "Filtro no puede ser nulo");
        if (Objects.equals(this.filterText,filterText.trim())){
            return;
        }
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.filterText = filterText.trim();
        setFilter(applicant ->
                passesFilter(applicant.getFullName(), filterText)
                    || passesFilter(applicant.getFullIdCard(),filterText)
                    || passesFilter(applicant.getProfession(),filterText)
                    || passesFilter(applicant.getRegisterDate().format(formatters),filterText)
                    || passesFilter(applicant.getNumberApplicant(),filterText));
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.GERMAN)
                .contains(filterText.toLowerCase());
    }
}
