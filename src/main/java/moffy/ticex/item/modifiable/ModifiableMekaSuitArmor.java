package moffy.ticex.item.modifiable;

import com.google.common.collect.ImmutableMultimap.Builder;
import mekanism.api.NBTConstants;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import moffy.ticex.registry.TicEXToolDefinitions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.tools.item.armor.MultilayerArmorItem;

import java.util.function.Consumer;

public class ModifiableMekaSuitArmor
    extends MultilayerArmorItem implements IAttributeRefresher {

    public ModifiableMekaSuitArmor(ArmorItem.Type slot, Item.Properties properties) {
        super(TicEXToolDefinitions.MEKAPLATE_DEFINITION, slot, properties);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(
                new ArmorModelManager.ArmorModelDispatcher() {
                    @Override
                    protected ResourceLocation getName() {
                        return TicEXToolDefinitions.MEKAPLATE_DEFINITION.getId();
                    }
                });
    }

    @Override
    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return true;
    }

    @Override
    public boolean isEnderMask(@NotNull ItemStack stack, @NotNull Player player, @NotNull EnderMan enderman) {
        return type == ArmorItem.Type.HELMET;
    }

    @Override
    public boolean canWalkOnPowderedSnow(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return type == ArmorItem.Type.BOOTS;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getEnergyBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return MekanismConfig.client.energyColor.get();
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return MutableComponent.create(super.getName(stack).getContents()).withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    public boolean isNotReplaceableByPickAction(@NotNull ItemStack stack, @NotNull Player player, int inventorySlot) {
        return (
            super.isNotReplaceableByPickAction(stack, player, inventorySlot) ||
            ItemDataUtils.hasData(stack, NBTConstants.MODULES, Tag.TAG_COMPOUND)
        );
    }

    @Override
    public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
        return false;
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, @NotNull Enchantment enchantment) {
        if (stack.isEmpty()) {
            return 0;
        }

        ListTag enchantments = ItemDataUtils.getList(stack, NBTConstants.ENCHANTMENTS);
        return Math.max(
            MekanismUtils.getEnchantmentLevel(enchantments, enchantment),
            super.getEnchantmentLevel(stack, enchantment)
        );
    }
    @Override
    public int getDefense() {
        return getMaterial().getDefenseForType(getType());
    }

    @Override
    public float getToughness() {
        return getMaterial().getToughness();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void addToBuilder(Builder<Attribute, AttributeModifier> builder) {}
}
