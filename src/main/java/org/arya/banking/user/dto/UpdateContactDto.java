package org.arya.banking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Contact number update payload")
public record UpdateContactDto(

        @Schema(description = "Contact number (10 digits, starting with 6-9)", example = "9876543210")
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Contact number must begin from 6,7,8 or 9 and have only 10 digits")
        String contactNumber,

        @Schema(description = "Whether this is the primary contact number", example = "true")
        boolean isPrimary) {
}
