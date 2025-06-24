package moffy.ticex.lib.utils;

import com.google.common.collect.ImmutableMap;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moffy.ticex.TicEX;
import moffy.ticex.lib.IEntityDataAccessor;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXCCUtils {

    public static Map<String, Object> createEntityMapWithProps(Player player) {
        Map<String, Object> entityMap = createEntityMap(player);
        entityMap.put(
            "getProperties",
            (ILuaFunction) args -> {
                return MethodResult.of(gatherProperties(player));
            }
        );
        return entityMap;
    }

    public static Map<String, Object> createEntityMap(Entity entity) {
        Map<String, Object> entityMap = new HashMap<>();

        if (entity != null) {
            entityMap.put("name", entity.getDisplayName().getString());
            entityMap.put("uuid", entity.getUUID().toString());
            entityMap.put("pos", new Object[] { entity.position().x, entity.position().y, entity.position().z });

            if (entity instanceof IEntityDataAccessor) {
                IEntityDataAccessor accessor = (IEntityDataAccessor) entity;
                entityMap.put(
                    "getData",
                    (ILuaFunction) args -> {
                        return MethodResult.of(ImmutableMap.copyOf(accessor.getAllFields()));
                    }
                );
            }
        }

        return entityMap;
    }

    public static Map<String, Object> gatherProperties(Player player) {
        Map<String, Object> result = new HashMap<>();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Map<String, Object> properties = new HashMap<>();
            ItemStack stack = player.getItemBySlot(slot);

            if (stack.getItem() instanceof IModifiable) {
                for (ModifierEntry entry : ToolStack.from(stack).getModifierList()) {
                    properties.putAll(
                        entry.getHook(TicEXRegistry.PROPERTY_PROVIDER_HOOK).getPropertyProvider().apply(player, stack)
                    );
                }
            }

            result.put(slot.getName(), properties);
        }

        if (ModList.get().isLoaded("curios")) {
            List<ItemStack> curioStacks = TicEXCuriosUtils.getAllToolStackInCurios(player, curioStack ->
                curioStack.getItem() instanceof IModifiable
            );
            curioStacks
                .stream()
                .forEach(curioStack -> {
                    Map<String, Object> properties = new HashMap<>();
                    for (ModifierEntry entry : ToolStack.from(curioStack).getModifierList()) {
                        properties.putAll(
                            entry
                                .getHook(TicEXRegistry.PROPERTY_PROVIDER_HOOK)
                                .getPropertyProvider()
                                .apply(player, curioStack)
                        );
                    }
                    result.put(TicEXCuriosUtils.getEquipmentSlotNameInCurios(player, curioStack), properties);
                });
        }

        return result;
    }
}
