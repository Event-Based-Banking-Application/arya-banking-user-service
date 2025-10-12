package org.arya.banking.user.dto;

import jakarta.validation.Valid;
import org.arya.banking.common.model.SecurityQuestions;

import java.util.List;

public record UpdateSecurityDetailsDto(

        @Valid
        List<SecurityQuestions> securityQuestions
) {


}
