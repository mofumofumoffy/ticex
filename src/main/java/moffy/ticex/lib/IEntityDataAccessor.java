package moffy.ticex.lib;

import java.lang.reflect.Field;
import java.util.Map;

public interface IEntityDataAccessor {
    public Map<String, Object> getAllFields();
    public Field getField(String keyName);
    public <T> boolean setValue(Field field, T value);
} 
