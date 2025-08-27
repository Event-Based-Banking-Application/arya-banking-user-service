package org.arya.banking.user.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.arya.banking.common.model.Address;

public record UserDto(

        @Pattern(regexp = "^[A-Za-z]+$", message = "First name should contain only alphabets")
        String firstName,

        @Pattern(regexp = "^[A-Za-z]+$", message = "Lst name should contain only alphabets")
        String lastName,

        @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email must have correct format")
        String emailId,

        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Contact number must begin from 6,7,8 or 9 and have only 10 digits")
        String primaryContactNumber,

        @Valid
        Address primaryAddress,

        String status,

        String role) {
}
