package moffy.ticex.lib.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import moffy.ticex.TicEXConfig;
import moffy.ticex.lib.IEntityDataAccessor;
import moffy.ticex.lib.IPropertyProviderModifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXCCUtils {
    public static Map<String, Object>createEntityMap(Entity entity){
        Map<String, Object> entityMap = new HashMap<>();
        
        entityMap.put("name", entity.getDisplayName().getString());
        entityMap.put("uuid", entity.getUUID().toString());
        entityMap.put("pos", new Object[]{entity.position().x, entity.position().y, entity.position().z});

        if(entity instanceof Player player){
            entityMap.put("properties", gatherProperties(player));
        }

        if(entity instanceof IEntityDataAccessor){
            IEntityDataAccessor accessor = (IEntityDataAccessor)entity;
            entityMap.put("getData", (ILuaFunction)(args)->{
                return MethodResult.of(accessor.getAllFields());
            });
        }

        return entityMap;
    }

    public static Map<String, Object> gatherProperties(Player player){
        Multimap<String, Object> properties = ArrayListMultimap.create();
        Map<String, Object> result = new HashMap<>();

        if(TicEXConfig.PROVIDE_PROPERTIES.get()){
            for(EquipmentSlot slot : EquipmentSlot.values()){
                ItemStack stack = player.getItemBySlot(slot);
                if(stack.getItem() instanceof IModifiable){
                    ToolStack tool = ToolStack.from(stack);
                    for(ModifierEntry modifierEntry : tool.getModifierList()){
                        if(modifierEntry.getModifier() instanceof IPropertyProviderModifier){
                            IPropertyProviderModifier propertyProviderModifier = (IPropertyProviderModifier)modifierEntry.getModifier();
                            properties.putAll(propertyProviderModifier.getPropertyProvider().apply(stack));
                        }
                    }
                }
            }

            properties.asMap().entrySet().forEach(entry->{
                Map<String, Object> values = new HashMap<>();
                for(Object object : entry.getValue()){
                    values.put("", object);
                }
                result.put(entry.getKey(), values);
            });
        }
        return result;
    }
}