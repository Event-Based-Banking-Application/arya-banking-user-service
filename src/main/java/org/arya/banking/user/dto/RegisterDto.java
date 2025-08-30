package org.arya.banking.user.dto;

import jakarta.validation.constraints.Pattern;

public record RegisterDto(

        @Pattern(regexp = "^[A-Za-z]+$", message = "First name should contain only alphabets")
        String firstName,

        @Pattern(regexp = "^[A-Za-z]+$", message = "Lst name should contain only alphabets")
        String lastName,

        @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email must have correct format")
        String emailId,

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{15,}$\n", message = "Password must be at least 15 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&).")
        String password,

        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Contact number must begin from 6,7,8 or 9 and have only 10 digits")
        String primaryContactNumber) {
}
