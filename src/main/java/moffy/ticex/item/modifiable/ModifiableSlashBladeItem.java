package moffy.ticex.item.modifiable;

import moffy.ticex.TicEX;
import moffy.ticex.client.slashblade.SBToolISTER;
import moffy.ticex.entity.slashblade.SBToolItemEntity;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;

import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ReachModifier;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.util.InputCommand;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.item.ModifiableSwordItem;

public class ModifiableSlashBladeItem extends ModifiableSwordItem{

    protected static final UUID ATTACK_DAMAGE_AMPLIFIER = UUID.fromString("2D988C13-595B-4E58-B254-39BB6FA077FD");
	protected static final UUID PLAYER_REACH_AMPLIFIER = UUID.fromString("2D988C13-595B-4E58-B254-39BB6FA077FE");

	public static final ResourceLocation BLADE_STATE_LOCATION = new ResourceLocation(TicEX.MODID, "bladestate");
	public static final ResourceLocation INPUT_STATE_LOCATION = new ResourceLocation(TicEX.MODID, "inputstate");

    public ModifiableSlashBladeItem(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> toolMultimap = ArrayListMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == EquipmentSlot.MAINHAND) {
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
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new ToolCapabilityProvider(stack);
	}

    @Override
	public Rarity getRarity(ItemStack stack) {
		EnumSet<SwordType> swordType = SwordType.from(stack);
		if (swordType.contains(SwordType.BEWITCHED))
			return Rarity.EPIC;
		if (swordType.contains(SwordType.ENCHANTED))
			return Rarity.RARE;
		return Rarity.COMMON;
	}

    public int getUseDuration(ItemStack stack) {
		return 72000;
	}

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		if (handIn == InteractionHand.OFF_HAND && !(playerIn.getMainHandItem().getItem() instanceof ModifiableSlashBladeItem)) {
			return InteractionResultHolder.pass(itemstack);
		}
		boolean result = itemstack.getCapability(ItemSlashBlade.BLADESTATE).map((state) -> {

			playerIn.getCapability(ItemSlashBlade.INPUT_STATE).ifPresent((s) -> s.getCommands().add(InputCommand.R_CLICK));

			ResourceLocation combo = state.progressCombo(playerIn);

			playerIn.getCapability(ItemSlashBlade.INPUT_STATE).ifPresent((s) -> s.getCommands().remove(InputCommand.R_CLICK));

			if (!combo.equals(ComboStateRegistry.NONE.getId()))
				playerIn.swing(handIn);
			ToolStack tool = ToolStack.from(itemstack);
			Iterator<ModifierEntry> var6 = tool.getModifierList().iterator();

            while(var6.hasNext()) {
               ModifierEntry entry = (ModifierEntry)var6.next();
               ((GeneralInteractionModifierHook)entry.getHook(ModifierHooks.GENERAL_INTERACT)).onToolUse(tool, entry, playerIn, handIn, InteractionSource.RIGHT_CLICK);
            }

			return true;
		}).orElse(false);

		playerIn.startUsingItem(handIn);
		return new InteractionResultHolder<>(result ? InteractionResult.SUCCESS : InteractionResult.FAIL, itemstack);
	}

    @Override
	public boolean onLeftClickEntity(ItemStack itemstack, Player playerIn, Entity entity) {
		Optional<ISlashBladeState> stateHolder = itemstack.getCapability(ItemSlashBlade.BLADESTATE)
				.filter((state) -> !state.onClick());

		stateHolder.ifPresent((state) -> {
			playerIn.getCapability(ItemSlashBlade.INPUT_STATE).ifPresent((s) -> s.getCommands().add(InputCommand.L_CLICK));

			state.progressCombo(playerIn);

			playerIn.getCapability(ItemSlashBlade.INPUT_STATE).ifPresent((s) -> s.getCommands().remove(InputCommand.L_CLICK));
		});

		return super.onLeftClickEntity(itemstack, playerIn, entity) && stateHolder.isPresent();
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {

		ToolStack tool = ToolStack.from(stack);
		super.setDamage(stack, damage);
		
		if(tool.getPersistentData().contains(BLADE_STATE_LOCATION, Tag.TAG_COMPOUND)){
			stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state->{
				if(tool.getCurrentDurability() <= 0){
					state.setBroken(true);
				} else if(state.isBroken()){
					state.setBroken(false);
				}
			});
		
			
		}
        
	}

	@Override
	public ItemStack getDefaultInstance() {
		return super.getDefaultInstance();
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		return !stack.getCapability(ItemSlashBlade.BLADESTATE).filter(s -> s.getLastActionTime() == entity.level().getGameTime())
				.isPresent();
	}

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {

        var cap = stack.getCapability(ItemSlashBlade.BLADESTATE).orElseThrow(NullPointerException::new);
		boolean current = cap.isBroken();

		amount = super.damageItem(stack, amount, damager, onBroken);
		if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
			amount = 0;
			stack.setDamageValue(stack.getMaxDamage() - 1);
			cap.setBroken(true);
		}

		if (current != cap.isBroken()) {
			onBroken.accept(damager);
			if (damager instanceof ServerPlayer player) {
				CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
			}

			if (damager instanceof Player player)
				player.awardStat(Stats.ITEM_BROKEN.get(stack.getItem()));
		}

        return amount;
    }

    @Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        boolean result = super.hurtEnemy(stack, target, attacker);

		if(target.invulnerableTime < 1){
			target.invulnerableTime = 1;
		}

		stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
			ResourceLocation loc = state.resolvCurrentComboState(attacker);
			ComboState cs = ComboStateRegistry.REGISTRY.get().getValue(loc) != null
					? ComboStateRegistry.REGISTRY.get().getValue(loc)
					: ComboStateRegistry.NONE.get();

			if (MinecraftForge.EVENT_BUS.post(new SlashBladeEvent.HitEvent(stack, state, target, attacker)))
				return;

			if(attacker instanceof Player){
				ToolStack tool = ToolStack.from(stack);
				ToolAttackContext context = new ToolAttackContext(attacker, (Player)attacker, InteractionHand.MAIN_HAND, target, target, false, 0, false);

				float damage = ToolAttackUtil.getAttributeAttackDamage(tool, target, EquipmentSlot.MAINHAND);
				float damageTmp = damage;
				for(ModifierEntry entry : tool.getModifierList()){
					entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, entry, context, damageTmp, damage);
				}

				if(damage <= 0){
					return;
				}

				for(ModifierEntry entry : tool.getModifierList()){
					entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, entry, context, damage, 0, 0);
				}

				cs.hitEffect(target, attacker);

				if(result){
					for(ModifierEntry entry : tool.getModifierList()){
						entry.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(tool, entry, context, damage);
					}
				} else {
					for(ModifierEntry entry : tool.getModifierList()){
						entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, entry, context, damage);
					}
				}
			}

			/* if(attacker instanceof Player){
				ToolAttackUtil.attackEntity(stack, (Player)attacker, target);
			} else {
				cs.hitEffect(target, attacker);
			} */
			stack.hurtAndBreak(1, attacker, ItemSlashBlade.getOnBroken(stack));
		});

		return result;
	}

    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos,
			LivingEntity entityLiving) {

        boolean result = super.mineBlock(stack, worldIn, state, pos, entityLiving);

		if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
			stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((s) -> {
				stack.hurtAndBreak(1, entityLiving, ItemSlashBlade.getOnBroken(stack));
			});
		}

		return result;
	}

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        super.releaseUsing(stack, worldIn, entityLiving, timeLeft);

        int elapsed = this.getUseDuration(stack) - timeLeft;

		if (!worldIn.isClientSide()) {

			stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
				EnumSet<SwordType> swordType = SwordType.from(stack);
				if (state.isBroken() || !swordType.contains(SwordType.ENCHANTED))
					return;

				ResourceLocation sa = state.doChargeAction(entityLiving, elapsed);

				if (!sa.equals(ComboStateRegistry.NONE.getId())) {
					
					var cost = state.getSlashArts().getProudSoulCost();
					if(state.getProudSoulCount() >= cost) 
						state.setProudSoulCount(state.getProudSoulCount()-cost);
					else 
						stack.hurtAndBreak(1, entityLiving, ItemSlashBlade.getOnBroken(stack));
					
					entityLiving.swing(InteractionHand.MAIN_HAND);
				}
			});
		}
    }

    @Override
	public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {

        super.onUseTick(level, player, stack, count);

		stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {

			(ComboStateRegistry.REGISTRY.get().getValue(state.getComboSeq()) != null
					? ComboStateRegistry.REGISTRY.get().getValue(state.getComboSeq())
					: ComboStateRegistry.NONE.get()).holdAction(player);
            ToolStack tool = ToolStack.from(stack);
			if (state.isBroken() || state.isSealed() || tool.getModifierLevel(TicEXRegistry.KONPAKU_MODIFIER.get()) == 0)
				return;
			if (!player.level().isClientSide()) {
				int ticks = player.getTicksUsingItem();
				int fullChargeTicks = state.getFullChargeTicks(player);
				if (0 < ticks) {
					if (ticks == fullChargeTicks) {
						Vec3 pos = player.getEyePosition(1.0f).add(player.getLookAngle());
						((ServerLevel) player.level()).sendParticles(ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 7, 0.7,
								0.7, 0.7, 0.02);
					}
				}
			}
		});
	}

    @Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (stack == null)
			return;
		if (entityIn == null)
			return;

		stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {

			if (MinecraftForge.EVENT_BUS
					.post(new SlashBladeEvent.UpdateEvent(stack, state, worldIn, entityIn, itemSlot, isSelected)))
				return;

			if (!isSelected) {
				if (entityIn instanceof Player player) {
					boolean hasHunger = player.hasEffect(MobEffects.HUNGER) && SlashBladeConfig.HUNGER_CAN_REPAIR.get();
					EnumSet<SwordType> swordType = SwordType.from(stack);
					if (swordType.contains(SwordType.BEWITCHED) || hasHunger) {
						if (stack.getDamageValue() > 0 && player.getFoodData().getFoodLevel() > 0) {
							int hungerAmplifier = hasHunger ? player.getEffect(MobEffects.HUNGER).getAmplifier() : 0;
							int level = 1 + Math.abs(hungerAmplifier);
							player.causeFoodExhaustion(
									SlashBladeConfig.BEWITCHED_HUNGER_EXHAUSTION.get().floatValue() * level);
							stack.setDamageValue(stack.getDamageValue() - level);
						}
					}
				}
			}
			if (entityIn instanceof LivingEntity living) {
				entityIn.getCapability(ItemSlashBlade.INPUT_STATE).ifPresent(mInput -> {
					mInput.getScheduler().onTick(living);
				});

				ResourceLocation loc = state.resolvCurrentComboState(living);
				ComboState cs = ComboStateRegistry.REGISTRY.get().getValue(loc) != null
						? ComboStateRegistry.REGISTRY.get().getValue(loc)
						: ComboStateRegistry.NONE.get();
				if(isSelected)
					cs.tickAction(living);

				state.sendChanges(living);
			}
		});

		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}

    private String stackDefaultDescriptionId(ItemStack stack) {
		String key = stack.getOrCreateTagElement("bladeState").getString("translationKey");
		return !key.isBlank() ? key : super.getDescriptionId(stack);
	}

    @Override
    public String getDescriptionId(ItemStack pStack) {
        return pStack.getCapability(ItemSlashBlade.BLADESTATE).filter((s) -> !s.getTranslationKey().isBlank())
				.map((state) -> state.getTranslationKey()).orElseGet(() -> stackDefaultDescriptionId(pStack));
    }
    
    @OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {
			this.appendSwordType(stack, worldIn, tooltip, flagIn);
			this.appendProudSoulCount(tooltip, stack, s);
			this.appendKillCount(tooltip, stack, s);
			this.appendSlashArt(stack, tooltip, s);
			this.appendRefineCount(tooltip, stack, s);
			this.appendSpecialEffects(tooltip, s);
		});
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

    @OnlyIn(Dist.CLIENT)
	public void appendSlashArt(ItemStack stack, List<Component> tooltip, @NotNull ISlashBladeState s) {
		EnumSet<SwordType> swordType = SwordType.from(stack);
		if (swordType.contains(SwordType.BEWITCHED)) {
			tooltip.add(Component.translatable("slashblade.tooltip.slash_art", s.getSlashArts().getDescription())
					.withStyle(ChatFormatting.GRAY));
		}
	}

    RangeMap<Comparable<?>, Object> refineColor = ImmutableRangeMap.builder()
			.put(Range.lessThan(10), ChatFormatting.GRAY).put(Range.closedOpen(10, 50), ChatFormatting.YELLOW)
			.put(Range.closedOpen(50, 100), ChatFormatting.GREEN).put(Range.closedOpen(100, 150), ChatFormatting.AQUA)
			.put(Range.closedOpen(150, 200), ChatFormatting.BLUE).put(Range.atLeast(200), ChatFormatting.LIGHT_PURPLE)
			.build();

	@OnlyIn(Dist.CLIENT)
	public void appendRefineCount(List<Component> tooltip, @NotNull ItemStack stack, @NotNull ISlashBladeState s) {
		int refine = s.getRefine();
		if (refine > 0) {
			tooltip.add(Component.translatable("slashblade.tooltip.refine", refine)
					.withStyle((ChatFormatting) refineColor.get(refine)));
		}
	}

    @OnlyIn(Dist.CLIENT)
	public void appendProudSoulCount(List<Component> tooltip, @NotNull ItemStack stack, @NotNull ISlashBladeState s) {
		int proudsoul = s.getProudSoulCount();
		if (proudsoul > 0) {
			MutableComponent countComponent = Component
					.translatable("slashblade.tooltip.proud_soul", proudsoul)
					.withStyle(ChatFormatting.GRAY);
			if (proudsoul > 1000)
				countComponent = countComponent.withStyle(ChatFormatting.DARK_PURPLE);
			tooltip.add(countComponent);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void appendKillCount(List<Component> tooltip, @NotNull ItemStack stack, @NotNull ISlashBladeState s) {
		int killCount =  s.getKillCount();
		if (killCount > 0) {
			MutableComponent killCountComponent = Component
					.translatable("slashblade.tooltip.killcount", killCount).withStyle(ChatFormatting.GRAY);
			if (killCount > 1000)
				killCountComponent = killCountComponent.withStyle(ChatFormatting.DARK_PURPLE);
			tooltip.add(killCountComponent);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void appendSpecialEffects(List<Component> tooltip, @NotNull ISlashBladeState s) {
		if (s.getSpecialEffects().isEmpty())
			return;

		Minecraft mcinstance = Minecraft.getInstance();
		Player player = mcinstance.player;

		s.getSpecialEffects().forEach(se -> {
			tooltip.add(Component.translatable("slashblade.tooltip.special_effect", SpecialEffect.getDescription(se),
					Component.literal(String.valueOf(SpecialEffect.getRequestLevel(se)))
							.withStyle(SpecialEffect.isEffective(se, player.experienceLevel) ? ChatFormatting.RED
									: ChatFormatting.DARK_GRAY))
					.withStyle(ChatFormatting.GRAY));
		});
	}

	@OnlyIn(Dist.CLIENT)
	public void appendSwordType(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		EnumSet<SwordType> swordType = SwordType.from(stack);
		if (swordType.contains(SwordType.BEWITCHED)) {
			tooltip.add(
					Component.translatable("slashblade.sword_type.bewitched").withStyle(ChatFormatting.DARK_PURPLE));
		} else if (swordType.contains(SwordType.ENCHANTED)) {
			tooltip.add(Component.translatable("slashblade.sword_type.enchanted").withStyle(ChatFormatting.DARK_AQUA));
		} else {
			tooltip.add(Component.translatable("slashblade.sword_type.noname").withStyle(ChatFormatting.DARK_GRAY));
		}
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
	public int getEntityLifespan(ItemStack itemStack, Level world) {
		return super.getEntityLifespan(itemStack, world);
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
}
