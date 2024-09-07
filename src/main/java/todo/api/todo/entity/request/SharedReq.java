package todo.api.todo.entity.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import todo.api.account.entity.Users;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.TodosSharing;
import todo.api.todo.entity.enums.SharingPermission;

public record SharedReq(
        @NotBlank(message = "REQUIRED_EMAIL")
        @Email(message = "INVALID_EMAIL_PATTERN")
        String email,

        SharingPermission permission
) {

    public TodosSharing toEntity(Users sharedUser, Todos todo) {
        return TodosSharing.builder()
                .sharedUser(sharedUser)
                .todos(todo)
                .permission(permission == null ? SharingPermission.READ_ONLY : permission)
                .build();
    }

}
