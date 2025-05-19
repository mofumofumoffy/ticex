package moffy.ticex.lib.utils;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.items.equipment.DETier;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class TicEXDEUtils {
    public static ResourceKey<DamageType> TOOL_DRACONIUM = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(TicEX.MODID, "tool_draconium"));
    public static ResourceKey<DamageType> TOOL_WYVERN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(TicEX.MODID, "tool_wyvern"));
    public static ResourceKey<DamageType> TOOL_DRACONIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(TicEX.MODID, "tool_draconic"));
    public static ResourceKey<DamageType> TOOL_CHAOTIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(TicEX.MODID, "tool_chaotic"));

    public static TechLevel getTechLevel(IToolStackView tool){
        switch (tool.getModifierLevel(TicEXRegistry.EVOLVED_MODIFIER.get())) {
            case 1:
                return TechLevel.DRACONIUM;
            case 2:
                return TechLevel.WYVERN;
            case 3:
                return TechLevel.DRACONIC;
            case 4:
                return TechLevel.CHAOTIC;    
        }
        return null;
    }
    
    public static DETier getTier(TechLevel techLevel){
        if(techLevel == TechLevel.DRACONIC) return DEContent.DRACONIC_TIER;
        else if(techLevel == TechLevel.CHAOTIC) return DEContent.CHAOTIC_TIER;
        else return DEContent.WYVERN_TIER;
    }

    public static ResourceKey<DamageType> getDamageTag(IToolStackView tool){
        int level = tool.getModifierLevel(TicEXRegistry.EVOLVED_MODIFIER.get());
        switch (level) {
            case 2:
                return TOOL_WYVERN;
            case 3:
                return TOOL_DRACONIC;
            case 4:
                return TOOL_CHAOTIC;
            default:
                return TOOL_DRACONIUM;
        }
    }
}
