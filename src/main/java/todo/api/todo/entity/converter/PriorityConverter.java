package todo.api.todo.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import todo.api.todo.entity.enums.TodosPriority;

@Converter(autoApply = true)
public class PriorityConverter implements AttributeConverter<TodosPriority, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TodosPriority priority) {
        if (priority == null) {
            return null;
        }
        return priority.getValue();
    }

    @Override
    public TodosPriority convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        return TodosPriority.fromValue(value);
    }
}
