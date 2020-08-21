package com.mindware.workflow.ui.backend.entity.stageHistory;

import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.lumo.BadgeColor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class StageHistoryCreditRequestDto {
    private UUID idStageHistory;

    private Integer numberRequest;

    private Integer numberApplicant;

    private String firstName;

    private String lastName;

    private Double amount;

    private String currency;

    private String userTask;//login user create creditrequest

    private Instant startDateTime;

    private Instant initDateTime;

    private Instant finishedDateTime;

    private Integer timeElapsed;

    private Integer timeToBeAssigned;

    private String city;

    private String productCredit;

    private Integer totalHours;

    private Integer totalHoursStage;

    private Integer hoursLeft;

    private String stage;

    private Integer productCode;

    private State state;

    private String codeTypeCredit;

    private Integer codeObjectCredit;

    public String getFullName(){
        return Optional.ofNullable(this.lastName).orElse("")+" "
                +Optional.ofNullable(this.firstName).orElse("");
    }

    public StageHistoryCreditRequestDto.State getState(String state){
        return StageHistoryCreditRequestDto.State.valueOf(state);
    }

    public enum State {
        PENDIENTE(VaadinIcon.CLOCK, "PENDIENTE",
                "ETAPA PENDIENTE",
                BadgeColor.CONTRAST),
        CONCLUIDO(VaadinIcon.CHECK,
                "CONCLUIDO", "ETAPA CONCLUIDA.",
                BadgeColor.SUCCESS),
        OBSERVADO(VaadinIcon.WARNING,
                "OBSERVADO", "ETAPA OBSERVADA",
                BadgeColor.ERROR),
        RECHAZADO(VaadinIcon.CHECK,
                "RECHAZADO", "SOLICITUD RECHAZADA.",
                BadgeColor.ERROR_PRIMARY);
//        APROBADO(VaadinIcon.CHECK,
//                "aprobado", "SOLICITUD APROBADA.",
//                BadgeColor.SUCCESS),
//        DESEMBOLSADO(VaadinIcon.CHECK,
//                "desembolsado", "SOLICITUD DESEMBOLSADA.",
//                 BadgeColor.SUCCESS);

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
                case PENDIENTE:
                    icon = UIUtils.createSecondaryIcon(this.icon);
                    break;
                case CONCLUIDO:
                    icon = UIUtils.createPrimaryIcon(this.icon);
                    break;
                case OBSERVADO:
                    icon = UIUtils.createErrorIcon(this.icon);
                    break;
                case RECHAZADO:
                    icon = UIUtils.createErrorIcon(this.icon);
                    break;
//                case APROBADO:
//                    icon = UIUtils.createSuccessIcon(this.icon);
//                    break;
//                case DESEMBOLSADO:
//                    icon = UIUtils.createSuccessIcon(this.icon);
//                    break;
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
