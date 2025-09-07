// DTO for PATCH update
package com.rakshaashtankar.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPatchRequest {
    @Size(min = 5, max = 50)
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min=8, message = "Password must be at least 6 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long, contain digit, uppercase, lowercase and special character, and have no whitespace"
    )
    private String password;

    @Pattern(regexp = "ADMIN|STUDENT|FACULTY",
            message = "Role must be ADMIN, STUDENT, or FACULTY"
    )
    private String role;
}
