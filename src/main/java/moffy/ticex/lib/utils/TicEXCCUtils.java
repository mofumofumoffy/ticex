package moffy.ticex.lib.utils;

import java.util.HashMap;
import java.util.Map;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import moffy.ticex.lib.IEntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class TicEXCCUtils {
    public static Map<String, Object>createEntityMap(Entity entity){
        Map<String, Object> entityMap = new HashMap<>();
        
        entityMap.put("name", entity.getDisplayName().getString());
        entityMap.put("uuid", entity.getUUID().toString());
        entityMap.put("pos", new Object[]{entity.position().x, entity.position().y, entity.position().z});

        if(entity instanceof Player player){

        }

        if(entity instanceof IEntityDataAccessor){
            IEntityDataAccessor accessor = (IEntityDataAccessor)entity;
            entityMap.put("getData", (ILuaFunction)(args)->{
                return MethodResult.of(accessor.getAllFields());
            });
        }

        return entityMap;
    }
}
