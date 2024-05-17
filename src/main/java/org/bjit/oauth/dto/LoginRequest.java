/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 5/2/2024
 * Version: 1.0
 */

package org.bjit.oauth.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.bjit.oauth.validation.ValidPassword;

@Data
public class LoginRequest {
    @Email
    private String email;
    @ValidPassword
    private String password;
}
