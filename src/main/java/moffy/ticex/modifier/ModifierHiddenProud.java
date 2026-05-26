package moffy.ticex.modifier;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.utils.TicEXSBUtils;
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

import java.util.Map.Entry;
import java.util.Random;

public class ModifierHiddenProud extends NoLevelsModifier implements EmbossmentModifierHook {

    protected TagKey<Item> proudSoulKey;

    public ModifierHiddenProud() {
        proudSoulKey = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "proudsouls"));
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

        if (input.isEnchanted()) {
            Random random = new Random();
            for (Entry<Enchantment, Integer> enchantmentEntry : input.getAllEnchantments().entrySet()) {
                if (TicEXSBUtils.disallowedEnchantments.contains(enchantmentEntry.getKey())) {
                    context.setErrorMsg(Component.translatable("recipe.ticex.not_allowed_enchantment_slashblade"));
                    return false;
                }
                for(int i = 0; i < input.getCount(); i++){
                    var probability = 1.0F;
                    if (input.is(SBItems.proudsoul_tiny)) probability = 0.25F;
                    if (input.is(SBItems.proudsoul)) probability = 0.5F;
                    if (input.is(SBItems.proudsoul_ingot)) probability = 0.75F;
                    if (random.nextFloat() <= probability) {
                        TicEXSBUtils.applyEnchantment(toolStack, enchantmentEntry.getKey(), enchantmentEntry.getValue());
                    }
                }
            }
        }

        toolStack
            .getCapability(ItemSlashBlade.BLADESTATE)
            .ifPresent(s -> {
                s.deserializeNBT(toolStack.getOrCreateTag().getCompound("bladeState"));
                s.setProudSoulCount(s.getProudSoulCount() + input.getCount() * Math.min(5000, enchantmentLevel * 10));

                if (input.hasTag()) {
                    CompoundTag nbt = input.getTag();
                    if (nbt.contains("SpecialAttackType")) {
                        s.setSlashArtsKey(ResourceLocation.tryParse(nbt.getString("SpecialAttackType")));
                    } else if (nbt.contains("SpecialEffectType")) {
                        s.addSpecialEffect(ResourceLocation.tryParse(nbt.getString("SpecialEffectType")));
                    }
                }

                if (s.getRefine() < refineLimit) {
                    s.setRefine(Math.max(s.getRefine() + input.getCount(), refineLimit));
                    if (s.getRefine() < 200) s.setMaxDamage(s.getMaxDamage() + 1);
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
