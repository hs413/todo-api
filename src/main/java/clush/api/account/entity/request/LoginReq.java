package clush.api.account.entity.request;

import jakarta.validation.constraints.NotBlank;

public record LoginReq(
        @NotBlank(message = "REQUIRED_EMAIL")
        String email,

        @NotBlank(message = "REQUIRED_PASSWORD")
        String password
) {

}
