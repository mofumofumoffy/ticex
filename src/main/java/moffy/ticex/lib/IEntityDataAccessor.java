package moffy.ticex.lib;

import java.lang.reflect.Field;
import java.util.Map;

public interface IEntityDataAccessor {
    public Map<String, Object> getAllFields();

    public Field getField(String keyName);

    public <T> boolean setValue(Field field, Class<T> cls, Object value);

    public default <T> boolean setValue(Field field, Object value) {
        return setValue(field, value.getClass(), value);
    }
}
