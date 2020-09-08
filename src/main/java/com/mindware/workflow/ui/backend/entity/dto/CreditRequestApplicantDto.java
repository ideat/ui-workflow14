package com.mindware.workflow.ui.backend.entity.dto;

import com.mindware.workflow.ui.ui.util.UIUtils;
import com.mindware.workflow.ui.ui.util.css.lumo.BadgeColor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreditRequestApplicantDto {
    private String numberRequest;
    private Double amount;
    private String currency;
    private LocalDate requestDate;
    private State state;
    private String firstName;
    private String lastName;
    private String homeAddress;
    private String idCard;
    private String idCardExpedition;
    private String civilStatus;
    private String workaddress;
    private String profession;
    private String city;
    private String province;
    private String block;
    private String typeHome;

    private UUID idApplicant;
    private UUID idCreditRequest;
    private UUID id;
    private String loginUser;
    private Integer numberApplicant;
    private String typeRelation;
    private Integer numberApplicantSpouse;
    private String firstNameSpouse;
    private String lastNameSpouse;
    private String homeaddressSpouse;
    private String idCardSpouse;
    private String idCardExpeditionSpouse;
    private String civilStatusSpouse;
    private String workaddressSpouse;
    private String professionSpouse;
    private String citySpouse;
    private String provinceSpouse;
    private String blockSpouse;
    private String typeHomeSpouse;
    private String cityOffice;
    private String typeCredit;
    private String objectCredit;

    public String getFullName(){
        return firstName + " " + lastName;
    }
    public String getFullNameSpouse(){
        return firstNameSpouse + " " + lastNameSpouse;
    }
    public String getFullIdCard(){
        return idCard+idCardExpedition;
    }

    public String getFullIdCardSpouse(){
        return idCardSpouse+idCardExpeditionSpouse;
    }

    public  CreditRequestApplicantDto.State getState(String state){
        return CreditRequestApplicantDto.State.valueOf(state);
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

//    public CreditRequestApplicantDto(String numberRequest, Double amount,
//                                     String currency, LocalDate requestDate,
//                                     State state, String firstName, String lastName,
//                                     UUID numberApplicant, UUID numberRequest,
//                                     String loginUser) {
//        this.numberRequest = numberRequest;
//        this.amount = amount;
//        this.currency = currency;
//        this.requestDate = requestDate;
//        this.state = state;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.numberApplicant = numberApplicant;
//        this.numberRequest = numberRequest;
//        this.loginUser = loginUser;
//    }

    public String getInitials() {
        return (firstName.substring(0, 1) + lastName.substring(0, 1))
                .toUpperCase();
    }




}
