/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 4/29/2024
 * Version: 1.0
 */

package org.bjit.oauth.dto;

import lombok.*;
import org.springframework.http.HttpStatus;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private String  message;
    private boolean success;
    private HttpStatus status;
    @Getter
    private String token;

    public ApiResponse(String token) {
        this.token = token;
    }
}
