package moffy.ticex.lib;

import java.lang.reflect.Field;
import java.util.Map;

public interface IEntityDataAccessor {
    Map<String, Object> ticex$getAllFields();

    Field ticex$getField(String keyName);

    <T> boolean ticex$setValue(Field field, Class<T> cls, Object value);

    default <T> boolean setValue(Field field, T value) {
        return ticex$setValue(field, value.getClass(), value);
    }
}
