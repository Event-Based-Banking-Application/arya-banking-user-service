package org.arya.banking.user.dto;

import jakarta.validation.Valid;

public record UserUpdateDto(

        boolean isLockUser,

        @Valid
        UpdateContactDto updateContactDto,

        @Valid
        UpdateAddressDto updateAddressDto) {
}
