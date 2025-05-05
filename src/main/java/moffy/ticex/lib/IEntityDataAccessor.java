package moffy.ticex.lib;

import java.lang.reflect.Field;

public interface IEntityDataAccessor {
    public Field getField(String keyName);
    public void setValue(Field field, Object value);
} 
