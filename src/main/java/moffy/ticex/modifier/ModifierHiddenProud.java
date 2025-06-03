package moffy.ticex.modifier;

import java.util.Random;
import java.util.Map.Entry;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.utils.TicEXSBUtil;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
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
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack input = context.getInputStack(inputIndex);
        ItemStack toolStack = context.getToolStack();

        int enchantmentLevel = context.getInputStack(inputIndex).getEnchantmentValue();
        int refineLimit = Math.max(10, enchantmentLevel);

        ToolStack tool = ToolStack.from(toolStack);

        if(input.isEnchanted()){
            Random random = new Random();
            for(Entry<Enchantment, Integer> enchantmentEntry : input.getAllEnchantments().entrySet()){
                if(TicEXSBUtil.disallowedEnchantments.contains(enchantmentEntry.getKey())){
                    context.setErrorMsg(Component.translatable("recipe.ticex.not_allowed_enchantment_slashblade"));
                    return false;
                }
                var probability = 1.0F;
                if (input.is(SBItems.proudsoul_tiny))
                    probability = 0.25F;
                if (input.is(SBItems.proudsoul))
                    probability = 0.5F;
                if (input.is(SBItems.proudsoul_ingot))
                    probability = 0.75F;
                if (random.nextFloat() <= probability) {
                    TicEXSBUtil.applyEnchantment(toolStack, enchantmentEntry.getKey(), enchantmentLevel);
                }
            }
        }

        toolStack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {
            s.deserializeNBT(toolStack.getOrCreateTag().getCompound("bladeState"));
            s.setProudSoulCount(s.getProudSoulCount() + input.getCount() * Math.min(5000, enchantmentLevel * 10));

            if(input.hasTag()){
                CompoundTag nbt = input.getTag();
                if(nbt.contains("SpecialAttackType")){
                    s.setSlashArtsKey(new ResourceLocation(nbt.getString("SpecialAttackType")));
                } else if(nbt.contains("SpecialEffectType")){
                    s.addSpecialEffect(new ResourceLocation(nbt.getString("SpecialEffectType")));
                }
            }

            if (s.getRefine() < refineLimit) {
                s.setRefine(s.getRefine() + input.getCount());
                if(s.getRefine() < 200)
                    s.setMaxDamage(s.getMaxDamage() + 1);
            }

            toolStack.getOrCreateTag().put("bladeState", s.serializeNBT());
        });

        return true;
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return advanced;
    }
}
