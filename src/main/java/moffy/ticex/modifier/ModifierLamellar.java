package moffy.ticex.modifier;

import moffy.ticex.caps.EmbossmentMaterialCapability;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class ModifierLamellar extends NoLevelsModifier implements EmbossmentModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack toolStack = context.getToolStack();
        ItemStack input = context.getInputStack(inputIndex);
        if (input.getItem() instanceof ToolPartItem part) {
            MaterialStatsId partStat = part.getStatType();
            if (toolStack.getItem() instanceof ArmorItem armor) {
                if (
                    partStat.equals(getPartStat(armor.getType())) ||
                    partStat.equals(TinkerToolParts.shieldCore.get().getStatType())
                ) {
                    toolStack
                        .getCapability(EmbossmentMaterialCapability.EMBOSSMENT_MATERIAL_CAPABILITY)
                        .ifPresent(materialCap -> {
                            materialCap.accept(toolStack, input, part);
                        });
                    return true;
                }
            }
        }
        return false;
    }

    public MaterialStatsId getPartStat(ArmorItem.Type type) {
        EnumObject<ArmorItem.Type, ToolPartItem> platingObj = TinkerToolParts.plating;
        return platingObj.get(type).getStatType();
    }
}
