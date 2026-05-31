package moffy.ticex.lib.utils;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.equipment.DETier;
import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class TicEXDEUtils {

    public static ResourceKey<DamageType> TOOL_DRACONIUM = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        TicEX.getResource("tool_draconium")
    );
    public static ResourceKey<DamageType> TOOL_WYVERN = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        TicEX.getResource("tool_wyvern")
    );
    public static ResourceKey<DamageType> TOOL_DRACONIC = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        TicEX.getResource("tool_draconic")
    );
    public static ResourceKey<DamageType> TOOL_CHAOTIC = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        TicEX.getResource("tool_chaotic")
    );

    @Nullable
    public static TechLevel getTechLevel(ResourceKey<DamageType> damageType){
        if(damageType.equals(TOOL_DRACONIUM)) return TechLevel.DRACONIUM;
        else if(damageType.equals(TOOL_WYVERN)) return TechLevel.WYVERN;
        else if(damageType.equals(TOOL_DRACONIC)) return TechLevel.DRACONIC;
        else if(damageType.equals(TOOL_CHAOTIC)) return TechLevel.CHAOTIC;
        return null;
    }

    @Nullable
    public static TechLevel getTechLevel(IToolStackView tool, ModifierId modifierId) {
        if (tool != null) {
            return switch (tool.getModifierLevel(modifierId)) {
                case 1 -> TechLevel.DRACONIUM;
                case 2 -> TechLevel.WYVERN;
                case 3 -> TechLevel.DRACONIC;
                case 4 -> TechLevel.CHAOTIC;
                default -> null;
            };
        }
        return null;
    }

    public static DETier getTier(TechLevel techLevel) {
        if (techLevel == TechLevel.DRACONIC) return DEContent.DRACONIC_TIER;
        else if (techLevel == TechLevel.CHAOTIC) return DEContent.CHAOTIC_TIER;
        else return DEContent.WYVERN_TIER;
    }

    public static ResourceKey<DamageType> getDamageTag(IToolStackView tool, ModifierId modifierId) {
        int level = tool.getModifierLevel(modifierId);
        return switch (level) {
            case 2 -> TOOL_WYVERN;
            case 3 -> TOOL_DRACONIC;
            case 4 -> TOOL_CHAOTIC;
            default -> TOOL_DRACONIUM;
        };
    }
}
