package moffy.ticex.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import moffy.ticex.TicEX;
import moffy.ticex.lib.IEntityDataAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityDataAccessor {

    @Shadow
    @Final
    protected SynchedEntityData entityData;

    @Unique
    private Map<String, Field> ticex$accessorMap;

    @Unique
    private void ticex$initializeAccessorMap(Entity entity) {
        ticex$accessorMap = new HashMap<>();
        if (entity instanceof LivingEntity) {
            for (Field field : LivingEntity.class.getDeclaredFields()) {
                field.setAccessible(true);
                ticex$accessorMap.put(field.getName(), field);
            }
        }
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            ticex$accessorMap.put(field.getName(), field);
        }
    }

    @Override
    public Map<String, Object> ticex$getAllFields() {
        if (ticex$accessorMap == null) {
            ticex$initializeAccessorMap((Entity) ((Object) this));
        }
        Map<String, Object> fields = new HashMap<>();
        for (Entry<String, Field> entry : ticex$accessorMap.entrySet()) {
            Field key = entry.getValue();
            try {
                if (EntityDataAccessor.class.isAssignableFrom(key.getType())) {
                    entityData.get(((EntityDataAccessor<?>) key.get(this)));
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
    public Field ticex$getField(String keyName) {
        if (ticex$accessorMap == null) {
            ticex$initializeAccessorMap((Entity) ((Object) this));
        }
        return ticex$accessorMap.get(keyName);
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public <T> boolean ticex$setValue(Field field, Class<T> cls, Object value) {
        Optional<DataItemAccessor> accessorOptional = ticex$toAccessor(entityData);
        if (accessorOptional.isPresent()) {
            DataItemAccessor accessor = accessorOptional.get();
            Int2ObjectMap<SynchedEntityData.DataItem<?>> items = accessor.getItems();
            try {
                Entity entity = (Entity) ((Object) this);
                if (EntityDataAccessor.class.isAssignableFrom(field.getType())) {
                    EntityDataAccessor<T> dataAccessor = (EntityDataAccessor<T>) field.get(entity);
                    SynchedEntityData.DataItem<T> dataitem = (SynchedEntityData.DataItem<T>) items.get(
                        dataAccessor.getId()
                    );
                    dataitem.setValue((T) value);
                    (entity).onSyncedDataUpdated(dataAccessor);
                    dataitem.setDirty(true);
                    accessor.setDirtyByTicEX(true);

                    return true;
                } else if (value.getClass().isAssignableFrom(field.getType())) {
                    field.set(entity, value);
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Unique
    private static Optional<DataItemAccessor> ticex$toAccessor(SynchedEntityData data) {
        if (data instanceof DataItemAccessor) {
            return Optional.of((DataItemAccessor) data);
        }
        return Optional.empty();
    }
}
