package clush.api.todo.entity.request;

public record TodoListReq(
        String type,
        String keyword
) {

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

}
