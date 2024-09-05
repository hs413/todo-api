package clush.api.account.entity.request;


import clush.api.account.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountCreateReq(
        @NotBlank(message = "REQUIRED_NAME")
        String username,

        @NotBlank(message = "REQUIRED_EMAIL")
        @Email(message = "INVALID_EMAIL_PATTERN")
        String email,

        @NotBlank(message = "REQUIRED_PASSWORD")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
                message = "INVALID_PASSWORD_PATTERN"
        )
        String password,

        @NotBlank(message = "REQUIRED_PASSWORD_CONFIRM")
        String passwordConfirm
) {

    public Users toEntity(String password) {
        return Users.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
    }
}
