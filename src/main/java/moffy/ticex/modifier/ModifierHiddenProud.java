package moffy.ticex.modifier;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ModifierHiddenProud extends NoLevelsModifier implements EmbossmentModifierHook{

    protected TagKey<Item> proudSoulKey;

    public ModifierHiddenProud(){
        proudSoulKey = TagKey.create(Registries.ITEM, new ResourceLocation(SlashBlade.MODID, "proudsouls"));
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(ItemStack toolStack, ItemStack input, boolean simulate) {
        int level = input.getEnchantmentValue();
        int refineLimit = Math.max(10, level);

        ToolStack tool = ToolStack.from(toolStack);

        toolStack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {
            s.deserializeNBT(tool.getPersistentData().getCompound(ModifiableSlashBladeItem.BLADE_STATE_LOCATION));
            s.setProudSoulCount(s.getProudSoulCount() + input.getCount() * Math.min(5000, level * 10));

            if(input.hasTag()){
                CompoundTag nbt = input.getTag();
                if(nbt.contains("SpecialAttackType")){
                    s.setSlashArtsKey(new ResourceLocation(nbt.getString("SpecialAttackType")));
                } else if(nbt.contains("SpecialEffectType")){
                    s.addSpecialEffect(new ResourceLocation(nbt.getString("SpecialEffectType")));
                }
            }

            if (s.getRefine() < refineLimit) {
                s.setRefine(s.getRefine() + 1);
                if(s.getRefine() < 200)
                    s.setMaxDamage(s.getMaxDamage() + 1);
            }

            tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, s.serializeNBT());
        });

        return true;
    }
    
    @Override
    public boolean shouldDisplay(boolean advanced) {
        return advanced;
    }
}
