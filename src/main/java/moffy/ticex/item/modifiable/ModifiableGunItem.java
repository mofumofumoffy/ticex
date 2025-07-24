package moffy.ticex.item.modifiable;

import com.google.common.collect.Sets;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.helper.*;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ModifiableGunItem extends ModernKineticGunItem implements IModifiableDisplay {

    private final ToolDefinition toolDefinition;
    private final int maxStackSize;

    protected ItemStack toolForRendering;

    public ModifiableGunItem(ToolDefinition toolDefinition, int maxStackSize) {
        super();
        this.toolDefinition = toolDefinition;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.isDamaged() ? 1 : maxStackSize;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.isCurse() && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return EnchantmentModifierHook.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        return EnchantmentModifierHook.getAllEnchantments(stack);
    }

    @Override
    public ItemStack getRenderTool() {
        if (toolForRendering == null) {
            toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
        }
        return toolForRendering;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ToolCapabilityProvider(stack);
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag nbt) {
        ToolStack.verifyTag(this, nbt, getToolDefinition());
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        ToolStack.ensureInitialized(stack, getToolDefinition());
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        int rarity = ModifierUtil.getVolatileInt(stack, RarityModule.RARITY);
        return Rarity.values()[Mth.clamp(rarity, 0, 3)];
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return IndestructibleItemEntity.hasCustomEntity(stack);
    }

    @Override
    public Entity createEntity(Level world, Entity original, ItemStack stack) {
        return IndestructibleItemEntity.createFrom(world, original, stack);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return false;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ToolDamageUtil.getFakeMaxDamage(stack);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
        ToolDamageUtil.handleDamageItem(stack, amount, damager, onBroken);
        return 0;
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return DurabilityDisplayModifierHook.getDurabilityRGB(pStack);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean mineBlock(
        ItemStack stack,
        Level worldIn,
        BlockState state,
        BlockPos pos,
        LivingEntity entityLiving
    ) {
        return ToolHarvestLogic.mineBlock(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return stack.getCount() == 1 ? MiningSpeedToolHook.getDestroySpeed(stack, state) : 0;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        return stack.getCount() > 1 || ToolHarvestLogic.handleBlockBreak(stack, pos, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack held, Slot slot, ClickAction action, Player player) {
        return SlotStackModifierHook.overrideStackedOnOther(held, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(
        ItemStack slotStack,
        ItemStack held,
        Slot slot,
        ClickAction action,
        Player player,
        SlotAccess access
    ) {
        return SlotStackModifierHook.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access);
    }

    @Nullable
    private static Component nameFor(String itemKey, Component itemName, MaterialVariantId variantId) {
        String materialKey = MaterialTooltipCache.getKey(variantId);
        String key = itemKey + "." + materialKey;
        if (Util.canTranslate(key)) {
            return Component.translatable(key);
        } else {
            String formatKey = materialKey + ".format";
            if (Util.canTranslate(formatKey)) {
                return Component.translatable(formatKey, itemName);
            } else {
                return Util.canTranslate(materialKey)
                        ? Component.translatable(
                        TooltipUtil.KEY_FORMAT,
                        Component.translatable(materialKey), itemName)
                        : null;
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        ToolStack tool = ToolStack.from(stack);
        String name = TooltipUtil.getDisplayName(stack);
        if (!name.isEmpty()) {
            return Component.literal(name);
        } else {
            List<MaterialStatsId> components = ToolMaterialHook.stats(toolDefinition);
            Component baseName = super.getName(stack);
            if (components.isEmpty()) {
                return baseName;
            } else {
                if (tool == null) {
                    tool = ToolStack.from(stack);
                }

                MaterialNBT materials = tool.getMaterials();
                if (materials.size() != components.size()) {
                    return baseName;
                } else {
                    Set<Component> nameMaterials = Sets.newLinkedHashSet();
                    MaterialVariantId firstMaterial = null;
                    IMaterialRegistry registry = MaterialRegistry.getInstance();

                    for (int i = 0; i < components.size(); ++i) {
                        if (i < materials.size() && registry.canRepair(components.get(i))) {
                            MaterialVariantId material = materials.get(i).getVariant();
                            if (!IMaterial.UNKNOWN_ID.equals(material)) {
                                if (firstMaterial == null) {
                                    firstMaterial = material;
                                }

                                nameMaterials.add(MaterialTooltipCache.getDisplayName(material));
                            }
                        }
                    }

                    return getMaterialItemName(stack, baseName, firstMaterial);
                }
            }
        }
    }

    private static Component getMaterialItemName(ItemStack stack, Component itemName, MaterialVariantId material) {
        String itemKey = stack.getDescriptionId();
        Component component;
        if (material.hasVariant()) {
            component = nameFor(itemKey, itemName, material);
            if (component != null) {
                return component;
            }
        }

        component = nameFor(itemKey, itemName, material.getId());
        return component != null ? component : itemName;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        /* ToolStack shader = ToolStack.from(stack);
         tooltip.add(Component.translatable("tooltip.ticex.modifier_stability", shader.isBroken() ? Component.translatable("tooltip.ticex.modifier_stability.lost").getString() : String.format(" %d %%", 100 - (int)Math.ceil(shader.getDamage()/shader.getStats().get(ToolStats.DURABILITY)*100))).withStyle(ChatFormatting.GREEN)); */
        TooltipUtil.addInformation(this, stack, level, tooltip, SafeClientAccess.getTooltipKey(), flag);
    }

    @Override
    public int getDefaultTooltipHideFlags(ItemStack stack) {
        return TooltipUtil.getModifierHideFlags(getToolDefinition());
    }
}
