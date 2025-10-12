package org.arya.banking.user.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateContactDto(

        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Contact number must begin from 6,7,8 or 9 and have only 10 digits")
        String contactNumber,

        boolean isPrimary) {
}
