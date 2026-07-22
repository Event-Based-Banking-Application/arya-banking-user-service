package org.arya.banking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(description = "Request payload for updating user profile details")
public record UserUpdateDto(

        @Schema(description = "Whether to lock the user account", example = "false")
        boolean isLockUser,

        @Valid
        UpdateContactDto updateContactDto,

        @Valid
        UpdateAddressDto updateAddressDto) {
}
