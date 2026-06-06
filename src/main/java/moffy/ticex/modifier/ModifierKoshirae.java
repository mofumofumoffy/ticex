package moffy.ticex.modifier;

import java.util.EnumSet;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import moffy.ticex.TicEX;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ModifierKoshirae extends NoLevelsModifier implements EmbossmentModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXModifierHooks.EMBOSSMENT);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack input = context.getInputStack(inputIndex);
        ItemStack toolStack = context.getToolStack();

        EnumSet<SwordType> swordTypes = SwordType.from(toolStack);
        if (swordTypes.contains(SwordType.BEWITCHED)) {
            toolStack
                .getCapability(ItemSlashBlade.BLADESTATE)
                .ifPresent(resultState -> {
                    CompoundTag compoundTag = input
                            .getOrCreateTag()
                            .getCompound("embossed");
                    CompoundTag bladeStateTag;
                    if(compoundTag.contains("tag")){
                        bladeStateTag = compoundTag.getCompound("tag").getCompound("bladeState");
                    } else {
                        bladeStateTag = compoundTag.getCompound("bladeState");
                    }

                    int currentProudSoul = resultState.getProudSoulCount();
                    int currentKillCount = resultState.getKillCount();
                    int currentRefineCount = resultState.getRefine();

                    bladeStateTag.putInt("proudSoul", Math.max(bladeStateTag.getInt("proudSoul"), currentProudSoul));
                    bladeStateTag.putInt("killCount", Math.max(bladeStateTag.getInt("killCount"), currentKillCount));
                    bladeStateTag.putInt("RepairCounter", Math.max(bladeStateTag.getInt("RepairCounter"), currentRefineCount));

                    resultState.deserializeNBT(bladeStateTag);
                    toolStack.getOrCreateTag().put("bladeState", bladeStateTag);
                });
            return true;
        } else {
            context.setErrorMsg(Component.translatable("recipe.ticex.not_be_witched"));
        }
        return false;
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return advanced;
    }
}
