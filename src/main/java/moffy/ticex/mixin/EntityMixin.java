package moffy.ticex.mixin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Mixin;

import moffy.ticex.TicEX;
import moffy.ticex.lib.IEntityDataAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityDataAccessor{
    
    private Map<String, Field> accessorMap;

    private void initializeAccessorMap(Entity entity) {
        accessorMap = new HashMap<>();
        if(entity instanceof LivingEntity){
            for (Field field : LivingEntity.class.getDeclaredFields()) {
                field.setAccessible(true);
                accessorMap.put(field.getName(), field);
            }
        }
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            accessorMap.put(field.getName(), field);
        }
    }

    @Override
    public Map<String, Object> getAllFields() {
        if (accessorMap == null) {
            initializeAccessorMap((Entity)((Object)this));
        }
        Map<String, Object> fields = new HashMap<>();
        for(Entry<String, Field> entry : accessorMap.entrySet()){
            Field key = entry.getValue();
            try {
                if(EntityDataAccessor.class.isAssignableFrom(key.getType())){
                    fields.put(entry.getKey(),((Entity)((Object)this)).getEntityData().get(((EntityDataAccessor<?>)key.get(this))));
                } else {
                    fields.put(entry.getKey(), key.get(this));
                }
            } catch (Exception e) {
                TicEX.LOGGER.error("", e);
            }
        }
        return fields;
    }
    
    @Override
    public Field getField(String keyName) {
        if (accessorMap == null) {
            initializeAccessorMap((Entity)((Object)this));
        }
        return accessorMap.get(keyName);
    }

    @Override
    public void setValue(Field field, Object value) {
        try{
            field.set((Entity)((Object)this), value);
        }catch(Exception e){
            TicEX.LOGGER.error("", e);
        }
    }
}
