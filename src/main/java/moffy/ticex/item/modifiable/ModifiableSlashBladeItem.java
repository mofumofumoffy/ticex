package moffy.ticex.item.modifiable;

import moffy.ticex.TicEX;
import moffy.ticex.client.slashblade.SBToolISTER;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.modules.general.TicEXRegistry;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ReachModifier;
import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerToolActions;

public class ModifiableSlashBladeItem extends ItemSlashBlade implements IModifiableDisplay{

    protected static final UUID ATTACK_DAMAGE_AMPLIFIER = UUID.fromString("2D988C13-595B-4E58-B254-39BB6FA077FD");
	protected static final UUID PLAYER_REACH_AMPLIFIER = UUID.fromString("2D988C13-595B-4E58-B254-39BB6FA077FE");

	public static final ResourceLocation BLADE_STATE_LOCATION = new ResourceLocation(TicEX.MODID, "bladestate");
	public static final ResourceLocation INPUT_STATE_LOCATION = new ResourceLocation(TicEX.MODID, "inputstate");

    private final ToolDefinition toolDefinition;
    private ItemStack toolForRendering;

    public ModifiableSlashBladeItem(Properties properties, ToolDefinition toolDefinition) {
        super(Tiers.NETHERITE, 1, 1, properties);
        this.toolDefinition = toolDefinition;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> toolMultimap = ArrayListMultimap.create(getAttributeModifiers(ToolStack.from(stack), slot));
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState -> {
                StatsNBT stats = ToolStack.from(stack).getStats();
                EnumSet<SwordType> swordType = SwordType.from(stack);

                float baseAttackModifier = stats.get(ToolStats.ATTACK_DAMAGE);

                float attackAmplifier = bladeState.getAttackAmplifier();;
                int refine = bladeState.getRefine();

                if (bladeState.isBroken()){
					attackAmplifier = -0.5F - baseAttackModifier;
				}else{
					float refineFactor = swordType.contains(SwordType.FIERCEREDGE) ? 0.1F : 0.05F;
					attackAmplifier = (1.0F - (1.0F / (1.0F + (refineFactor * refine)))) * baseAttackModifier;
				}

                AttributeModifier attack = new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
						(double) baseAttackModifier + attackAmplifier - 1F, AttributeModifier.Operation.ADDITION);

				toolMultimap.remove(Attributes.ATTACK_DAMAGE, attack);
				toolMultimap.put(Attributes.ATTACK_DAMAGE, attack);

                toolMultimap.put(ForgeMod.ENTITY_REACH.get(),
						new AttributeModifier(PLAYER_REACH_AMPLIFIER, "Reach amplifer",
								bladeState.isBroken() ? ReachModifier.BrokendReach() : ReachModifier.BladeReach(),
								AttributeModifier.Operation.ADDITION));
            });
        }
        return ImmutableMultimap.copyOf(toolMultimap);
    }

    @Override
  public int getMaxStackSize(ItemStack stack) {
    return 1;
  }

  /* Basic properties */

  @Override
  public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
    return true;
  }


  /* Enchanting */

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
  public Map<Enchantment,Integer> getAllEnchantments(ItemStack stack) {
    return EnchantmentModifierHook.getAllEnchantments(stack);
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
  public boolean isFoil(ItemStack stack) {
    return ModifierUtil.checkVolatileFlag(stack, SHINY);
  }

  @Nullable
  @Override
  public Entity createEntity(Level world, Entity original, ItemStack stack) {
    return IndestructibleItemEntity.createFrom(world, original, stack);
  }


  /* Damage/Durability */

  @Override
  public boolean isRepairable(ItemStack stack) {
    // handle in the tinker station
    return false;
  }

  @Override
  public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
    return false;
  }

  @Override
  public boolean canBeDepleted() {
    return true;
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    return ToolDamageUtil.getFakeMaxDamage(stack);
  }

  @Override
  public int getDamage(ItemStack stack) {
    if (!canBeDepleted()) {
      return 0;
    }
    return ToolStack.from(stack).getDamage();
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    if (canBeDepleted()) {
      ToolStack.from(stack).setDamage(damage);
    }
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    ToolDamageUtil.handleDamageItem(stack, amount, damager, onBroken);
    return 0;
  }



  @Override
  public boolean isBarVisible(ItemStack stack) {
    return stack.getCount() == 1 && DurabilityDisplayModifierHook.showDurabilityBar(stack);
  }

  @Override
  public int getBarColor(ItemStack pStack) {
    return DurabilityDisplayModifierHook.getDurabilityRGB(pStack);
  }

  @Override
  public int getBarWidth(ItemStack pStack) {
    return DurabilityDisplayModifierHook.getDurabilityWidth(pStack);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
    return stack.getCount() > 1 || EntityInteractionModifierHook.leftClickEntity(stack, player, target) || super.onLeftClickEntity(stack, player, target);
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return canPerformAction(stack, TinkerToolActions.SHIELD_DISABLE) || super.canDisableShield(stack, shield, entity, attacker);
  }


  /* Harvest logic */

  @Override
  public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
    return IsEffectiveToolHook.isEffective(ToolStack.from(stack), state);
  }

  @Override
  public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
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


  /* Modifier interactions */

  @Override
  public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  @Override
  public boolean overrideStackedOnOther(ItemStack held, Slot slot, ClickAction action, Player player) {
    return SlotStackModifierHook.overrideStackedOnOther(held, slot, action, player) || super.overrideStackedOnOther(held, slot, action, player);
  }

  @Override
  public boolean overrideOtherStackedOnMe(ItemStack slotStack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
    return SlotStackModifierHook.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access) || super.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access);
  }


  /* Right click hooks */

  /** If true, this interaction hook should defer to the offhand */
  protected static boolean shouldInteract(@Nullable LivingEntity player, ToolStack toolStack, InteractionHand hand) {
    IModDataView volatileData = toolStack.getVolatileData();
    if (volatileData.getBoolean(NO_INTERACTION)) {
      return false;
    }
    // off hand always can interact
    if (hand == InteractionHand.OFF_HAND) {
      return true;
    }
    // main hand may wish to defer to the offhand if it has a tool
    return player == null || !volatileData.getBoolean(DEFER_OFFHAND) || player.getOffhandItem().isEmpty();
  }

  @Override
  public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
    if (stack.getCount() == 1) {
      ToolStack tool = ToolStack.from(stack);
      InteractionHand hand = context.getHand();
      if (shouldInteract(context.getPlayer(), tool, hand)) {
        for (ModifierEntry entry : tool.getModifierList()) {
          InteractionResult result = entry.getHook(ModifierHooks.BLOCK_INTERACT).beforeBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
          if (result.consumesAction()) {
            return result;
          }
        }
      }
    }
    return super.onItemUseFirst(stack, context);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    ItemStack stack = context.getItemInHand();
    if (stack.getCount() == 1) {
      ToolStack tool = ToolStack.from(stack);
      InteractionHand hand = context.getHand();
      if (shouldInteract(context.getPlayer(), tool, hand)) {
        for (ModifierEntry entry : tool.getModifierList()) {
          InteractionResult result = entry.getHook(ModifierHooks.BLOCK_INTERACT).afterBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
          if (result.consumesAction()) {
            return result;
          }
        }
      }
    }
    return super.useOn(context);
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
    ToolStack tool = ToolStack.from(stack);
    if (shouldInteract(playerIn, tool, hand)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        InteractionResult result = entry.getHook(ModifierHooks.ENTITY_INTERACT).afterEntityUse(tool, entry, playerIn, target, hand, InteractionSource.RIGHT_CLICK);
        if (result.consumesAction()) {
          return result;
        }
      }
    }
    return super.interactLivingEntity(stack, playerIn, target, hand);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    ItemStack stack = playerIn.getItemInHand(hand);
    if (stack.getCount() > 1) {
      return InteractionResultHolder.pass(stack);
    }
    ToolStack tool = ToolStack.from(stack);
    if (shouldInteract(playerIn, tool, hand)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        InteractionResult result = entry.getHook(ModifierHooks.GENERAL_INTERACT).onToolUse(tool, entry, playerIn, hand, InteractionSource.RIGHT_CLICK);
        if (result.consumesAction()) {
          return new InteractionResultHolder<>(result, stack);
        }
      }
    }

    InteractionResultHolder<ItemStack> resultHolder = new InteractionResultHolder<>(ToolInventoryCapability.tryOpenContainer(stack, tool, playerIn, Util.getSlotType(hand)), stack);
    if(resultHolder.getResult() != InteractionResult.SUCCESS){
        resultHolder = super.use(worldIn, playerIn, hand);
    }
    return resultHolder;
  }

  @Override
  public void onUseTick(Level pLevel, LivingEntity entityLiving, ItemStack stack, int timeLeft) {
    ToolStack tool = ToolStack.from(stack);
    ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
    if (activeModifier != ModifierEntry.EMPTY) {
        ((GeneralInteractionModifierHook)activeModifier.getHook(ModifierHooks.GENERAL_INTERACT)).onUsingTick(tool, activeModifier, entityLiving, timeLeft);
    }
    super.onUseTick(pLevel, entityLiving, stack, timeLeft);
  }

  @Override
  public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
    if (super.canContinueUsing(oldStack, newStack)) {
      if (oldStack != newStack) {
        GeneralInteractionModifierHook.finishUsing(ToolStack.from(oldStack));
      }
    }
    return super.canContinueUsing(oldStack, newStack);
  }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
        if (activeModifier != ModifierEntry.EMPTY) {
            ((GeneralInteractionModifierHook)activeModifier.getHook(ModifierHooks.GENERAL_INTERACT)).onFinishUsing(tool, activeModifier, entityLiving);
        }

        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

  @Override
  public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
    ToolStack tool = ToolStack.from(stack);
      ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
      if (activeModifier != ModifierEntry.EMPTY) {
         ((GeneralInteractionModifierHook)activeModifier.getHook(ModifierHooks.GENERAL_INTERACT)).onStoppedUsing(tool, activeModifier, entityLiving, timeLeft);
      }
      super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
    }

  @Override
  public void onStopUsing(ItemStack stack, LivingEntity entity, int timeLeft) {
        GeneralInteractionModifierHook.finishUsing(ToolStack.from(stack));
        super.onStopUsing(stack, entity, timeLeft);
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
    if (activeModifier != ModifierEntry.EMPTY) {
      return activeModifier.getHook(ModifierHooks.GENERAL_INTERACT).getUseDuration(tool, activeModifier);
    }
    return super.getUseDuration(stack);
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
    if (activeModifier != ModifierEntry.EMPTY) {
      return activeModifier.getHook(ModifierHooks.GENERAL_INTERACT).getUseAction(tool, activeModifier);
    }
    return super.getUseAnimation(stack);
  }

  @Override
  public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
    return (stack.getCount() == 1 && ModifierUtil.canPerformAction(ToolStack.from(stack), toolAction)) || super.canPerformAction(stack, toolAction);
  }


  /* Tooltips */

  @Override
  public Component getName(ItemStack stack) {
    return TooltipUtil.getDisplayName(stack, getToolDefinition());
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, level, tooltip, flag);
    TooltipUtil.addInformation(this, stack, level, tooltip, SafeClientAccess.getTooltipKey(), flag);
  }

  @Override
  public int getDefaultTooltipHideFlags(ItemStack stack) {
    return TooltipUtil.getModifierHideFlags(getToolDefinition());
  }

  @Override
  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
    return shouldCauseReequipAnimation(oldStack, newStack, false);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return ModifiableItem.shouldCauseReequip(oldStack, newStack, slotChanged);
  }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
        return AttributesModifierHook.getHeldAttributeModifiers(tool, slot);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ToolCapabilityProvider(stack);
    }

    @Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

    @Override
	@SuppressWarnings("unchecked")
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		if (!(entity instanceof SBToolItemEntity)){
			Level world = entity.level();
			SBToolItemEntity e = new SBToolItemEntity((EntityType<SBToolItemEntity>)TicEXRegistry.SLASHBLADE_TOOL_ITEM_ENTITY.get(), world);
			e.restoreFrom(entity);
			e.init();
			entity.discard();
			world.addFreshEntity(e);
		}
		return false;
	}

    @Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {

		consumer.accept(new IClientItemExtensions() {
			BlockEntityWithoutLevelRenderer renderer = new SBToolISTER(
					Minecraft.getInstance().getBlockEntityRenderDispatcher(),
					Minecraft.getInstance().getEntityModels());

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

    @Override
    public ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }

    @Override
    public ItemStack getRenderTool() {
        if (toolForRendering == null) {
            toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
        }
        return toolForRendering;
    }
}
