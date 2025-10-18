package org.arya.banking.user.dto;

import jakarta.validation.Valid;
import org.arya.banking.common.model.Address;

public record UpdateAddressDto(

        @Valid
        Address address) {
}
