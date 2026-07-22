package org.arya.banking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.arya.banking.common.model.Address;

@Schema(description = "Address update payload")
public record UpdateAddressDto(

        @Valid
        Address address) {
}
