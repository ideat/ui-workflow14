package com.mindware.workflow.ui.backend.entity.cashFlow;

import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.lumo.BadgeColor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class CashFlowCreditRequestApplicantDto {
    private Integer numberRequest;
    private String firstName;
    private String lastName;
    private String state;
    private Double amount;
    private String currency;
    private String city;
    private String official;
    private boolean hasPaymentPlan;
    private boolean hasPatrimonialStatement;
    private UUID idCreditRequestApplicant;

    public String getFullName(){
        return Optional.ofNullable(this.lastName).orElse("")+" "
                +Optional.ofNullable(this.firstName).orElse("");
    }

    public enum State {
        ANALISIS_PREVIO(VaadinIcon.CLOCK, "ANALISIS_PREVIO",
                "Solicitud creada, aun no enviada.",
                BadgeColor.CONTRAST),
        EVALUACION_CREDITO(VaadinIcon.QUESTION_CIRCLE,
                "EVALUACION_CREDITO", "Solicitud enviada para analisis.",
                BadgeColor.NORMAL),
        LEGAL(VaadinIcon.CHECK,
                "LEGAL", "Realiza el informe legal.",
                BadgeColor.SUCCESS),
        APROBACION(VaadinIcon.CHECK,
                "APROBACION", "Solicitud aproba.",
                BadgeColor.SUCCESS),
        FORMALIZACION(VaadinIcon.CHECK,
                "FORMALIZACION", "Se genera el contrato de la solicitud.",
                BadgeColor.SUCCESS),
        DESEMBOLSO(VaadinIcon.CHECK,
                "DESEMBOLSO", "Se realiza el desembolso del credito.",
                BadgeColor.SUCCESS),
        RECHAZADA(VaadinIcon.WARNING,
                "RECHAZADA", "Solicitud rechazada.",
                BadgeColor.ERROR);

        private VaadinIcon icon;
        private String name;
        private String desc;
        private BadgeColor theme;


        State(VaadinIcon icon, String name, String desc, BadgeColor theme) {
            this.icon = icon;
            this.name = name;
            this.desc = desc;
            this.theme = theme;
        }
        public Icon getIcon() {
            Icon icon;
            switch (this) {
                case ANALISIS_PREVIO:
                    icon = UIUtils.createSecondaryIcon(this.icon);
                    break;
                case EVALUACION_CREDITO:
                    icon = UIUtils.createPrimaryIcon(this.icon);
                    break;
                case LEGAL:
                    icon = UIUtils.createSuccessIcon(this.icon);
                    break;
                case APROBACION:
                    icon = UIUtils.createSuccessIcon(this.icon);
                    break;
                case FORMALIZACION:
                    icon = UIUtils.createSuccessIcon(this.icon);
                    break;
                case DESEMBOLSO:
                    icon = UIUtils.createSuccessIcon(this.icon);
                    break;
                default:
                    icon = UIUtils.createErrorIcon(this.icon);
                    break;
            }
            return icon;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public BadgeColor getTheme() {
            return theme;
        }
    }

    public String getInitials() {
        return (firstName.substring(0, 1) + lastName.substring(0, 1))
                .toUpperCase();
    }
}
