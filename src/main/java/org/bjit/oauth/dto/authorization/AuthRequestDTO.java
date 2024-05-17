/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 5/3/2024
 * Version: 1.0
 */

package org.bjit.oauth.dto.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequestDTO {
    private String username;
    private String password;
}