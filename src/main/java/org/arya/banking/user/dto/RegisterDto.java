package org.arya.banking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request payload for new user registration")
public record RegisterDto(

        @Schema(description = "User's first name (alphabets only)", example = "John")
        @Pattern(regexp = "^[A-Za-z]+$", message = "First name should contain only alphabets")
        String firstName,

        @Schema(description = "User's last name (alphabets only)", example = "Doe")
        @Pattern(regexp = "^[A-Za-z]+$", message = "Lst name should contain only alphabets")
        String lastName,

        @Schema(description = "Email address", example = "john.doe@example.com")
        @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email must have correct format")
        String emailId,

        @Schema(description = "Password — min 15 chars with uppercase, lowercase, digit, and special character", example = "MySecureP@ss1!")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{15,}$", message = "Password must be at least 15 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&).")
        String password,

        @Schema(description = "Primary contact number (10 digits, starting with 6-9)", example = "9876543210")
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Contact number must begin from 6,7,8 or 9 and have only 10 digits")
        String primaryContactNumber) {
}
