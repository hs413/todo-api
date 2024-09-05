package clush.api.account.entity.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshReq(
        @NotBlank(message = "NO_ACCESS_TOKEN")
        String accessToken,

        @NotBlank(message = "NO_REFRESH_TOKEN")
        String refreshToken
) {


}
