package org.arya.banking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.arya.banking.common.model.SecurityQuestions;

import java.util.List;

@Schema(description = "Payload for updating user security credentials")
public record UpdateSecurityDetailsDto(

        @Valid
        List<SecurityQuestions> securityQuestions,

        @Schema(description = "Whether the last login attempt failed (triggers lockout counter)", example = "false")
        boolean loginFailed
) {

}
