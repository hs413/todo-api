package todo.api.todo.entity.enums;

public enum TodosPriority {
    LOW(1),
    MID(2),
    HIGH(3);

    private final int value;

    TodosPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TodosPriority fromValue(int value) {
        for (TodosPriority priority : values()) {
            if (priority.value == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
