/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 5/2/2024
 * Version: 1.0
 */

package org.bjit.oauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bjit.oauth.model.UserRole;
import org.bjit.oauth.validation.ValidPassword;

import java.util.HashSet;
import java.util.Set;

@Data
public class RegistrationRequest {
    @NotEmpty
    @NotNull
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotNull
    private Set<UserRole> roles = new HashSet<>();

    @Email
    @NotEmpty
    private String email;

    @ValidPassword
    private String password;
}
