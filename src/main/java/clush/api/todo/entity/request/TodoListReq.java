package clush.api.todo.entity.request;

import clush.api.todo.entity.TodosStatus;
import org.springframework.util.StringUtils;

public record TodoListReq(
        String keyword,
        String type, // title,description
        TodosStatus status
) {

    public String[] getTypes() {
        if (StringUtils.hasText(type)) {
            return type.split(",");
        }
        return null;
    }
}
