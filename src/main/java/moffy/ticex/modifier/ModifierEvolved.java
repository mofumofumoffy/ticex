package moffy.ticex.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.inventory.InventoryDynamic;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.modules.ModuleHelper;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AOEData;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.client.keybinding.KeyBindings;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.mojang.datafixers.util.Pair;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.math.MathHelper;
import moffy.ticex.TicEX;
import moffy.ticex.modules.TicEXRegistry;
import moffy.ticex.utils.TicEXDEUtils;
import moffy.ticex.utils.TicEXUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class ModifierEvolved extends Modifier implements ToolDamageModifierHook, TooltipModifierHook, MeleeHitModifierHook, BlockBreakModifierHook, RequirementsModifierHook, ValidateModifierHook{

    public static final ResourceLocation MODULE_HOST_LOCATION = new ResourceLocation(TicEX.MODID, "module_host");
    public static final ResourceLocation OP_STORAGE_LOCATION = new ResourceLocation(TicEX.MODID, "op_storage");

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE, ModifierHooks.TOOLTIP, ModifierHooks.MELEE_HIT, ModifierHooks.BLOCK_BREAK, ModifierHooks.REQUIREMENTS, ModifierHooks.VALIDATE);
    }

    @Override
    public int onDamageTool(IToolStackView arg0, ModifierEntry arg1, int arg2, LivingEntity arg3) {
        return 0;
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry entry, Player player, List<Component> components, TooltipKey tooltipKey,
            TooltipFlag tooltipFlag) {

        ItemStack toolStack = TicEXUtils.getToolStack(player, TicEXRegistry.EVOLVED_MODIFIER.get());

        if(!toolStack.isEmpty()){
            components.add(Component.translatable("[Modular Item]").withStyle(ChatFormatting.BLUE));
            toolStack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).ifPresent(host -> {
                host.getModuleEntities().forEach(e -> e.addHostHoverText(toolStack, player.level(), components, tooltipFlag));
                host.getInstalledTypes().map(host::getModuleData).filter(Objects::nonNull).forEach(data -> data.addHostHoverText(toolStack, player.level(), components, tooltipFlag));
            });
            EnergyUtils.addEnergyInfo(toolStack, components);
            if (EnergyUtils.isEnergyItem(toolStack) && EnergyUtils.getMaxEnergyStored(toolStack) == 0) {
                components.add(Component.translatable("modular_item.draconicevolution.requires_energy").withStyle(ChatFormatting.RED));
                if (KeyBindings.toolModules != null && KeyBindings.toolModules.getTranslatedKeyMessage() != null) {
                    components.add(Component.translatable("modular_item.draconicevolution.requires_energy_press", KeyBindings.toolModules.getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.BLUE));
                }
            }
        }
        //components.add(Component.translatable("[Modular Item]").withStyle(ChatFormatting.BLUE));
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context,
            float damageDealt) {
        Player player = context.getPlayerAttacker();
        if(player != null){
            Entity target = context.getTarget();
            ItemStack stack = player.getItemInHand(context.getHand());
            if(stack != null && !stack.isEmpty() && ToolStack.from(stack).getModifierLevel(this) > 0){
                ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                IOPStorage opStorage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);
                int attackDamage = getDamageBonus(host, opStorage);
                int extracted = opStorage.extractEnergy(tool.getStats().getInt(ToolStats.ATTACK_DAMAGE) + attackDamage, false);
                hurt(player, target, stack, Math.min(extracted, attackDamage));
                double aoe = getAttackAoe(host);
                if (!context.isExtraAttack() && aoe > 0 && target instanceof LivingEntity) {
                    dealAOEDamage(tool, context, player, (LivingEntity)target, stack, Math.min(extracted, attackDamage) * 0.8F, aoe);
                }
            }
        }
    }

    private int getDamageBonus(ModuleHost host, IOPStorage opStorage){
        double damage = host.getModuleData(ModuleTypes.DAMAGE, new DamageData(0)).damagePoints();
        
        if (opStorage.getEnergyStored() < EquipCfg.energyAttack * damage) {
            damage = 0;
        }
        return (int)Math.round(damage + ((TicEXDEUtils.getTier(host.getHostTechLevel()).getAttackDamageBonus() * EquipCfg.staffDamageMultiplier) - 1));
    }

    private double getAttackAoe(ModuleHost host){
        double aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).aoe() * 1.5;
        if (host instanceof PropertyProvider && ((PropertyProvider) host).hasDecimal("attack_aoe")) {
            aoe = ((PropertyProvider) host).getDecimal("attack_aoe").getValue();
        }
        return aoe;
    }

    private void dealAOEDamage(IToolStackView tool, ToolAttackContext context, Player player, LivingEntity target, ItemStack stack, float damage, double aoe) {
        IOPStorage opStorage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);

        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(aoe, 0.25D, aoe));
        double aoeAngle = 100;
        double yaw = player.getYRot() - 180;
        int fireAspect = EnchantmentHelper.getFireAspect(player);

        for (LivingEntity entity : entities) {
            float distance = player.distanceTo(entity);
            if (entity == player || entity == target || player.isAlliedTo(entity) || distance < 1 || entity.distanceTo(target) > aoe) continue;
            double angle = Math.atan2(player.getX() - entity.getX(), player.getZ() - entity.getZ()) * MathHelper.todeg;
            double relativeAngle = Math.abs((angle + yaw) % 360);
            if (relativeAngle <= aoeAngle / 2 || relativeAngle > 360 - (aoeAngle / 2)) {
                boolean lit = false;
                float health = entity.getHealth();
                if (fireAspect > 0 && !entity.isOnFire()) {
                    lit = true;
                    entity.setSecondsOnFire(1);
                }

                int extracted = opStorage.extractEnergy((int)(EquipCfg.energyAttack * damage), false);
                ToolAttackUtil.extraEntityAttack(tool, player, context.getHand(), entity);
                if (hurt(player, entity, stack, Math.min(extracted, (int)damage))) {
                    float damageDealt = health - entity.getHealth();
                    entity.knockback(0.4F, MathHelper.sin(player.getYRot() * MathHelper.torad), (-MathHelper.cos(player.getYRot() * MathHelper.torad)));

                    if (fireAspect > 0) {
                        entity.setSecondsOnFire(fireAspect * 4);
                    }

                    if (player.level() instanceof ServerLevel && damageDealt > 2.0F) {
                        int k = (int)((double)damage * 0.5D);
                        ((ServerLevel)player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(0.5D), entity.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                    }

                    player.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
                    if (player.level() instanceof ServerLevel && damageDealt > 2.0F) {
                        int k = (int) ((double) damageDealt * 0.5D);
                        ((ServerLevel) player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(0.5D), entity.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                    }
                } else if (lit) {
                    entity.clearFire();
                }
            }
        }
    }

    private boolean hurt(Player player, Entity target, ItemStack stack, int damage){
        boolean result;
        IOPStorage opStorage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);

        int dealDamage = Math.min((EquipCfg.energyAttack * damage), opStorage.getEnergyStored()) / EquipCfg.energyAttack;
        result = target.hurt(new DamageSource(target.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(TicEXDEUtils.getDamageTag(ToolStack.from(stack)))), dealDamage);

        if(!result){
            if (player.level() instanceof ServerLevel && dealDamage > 2.0F) {
                int k = (int)((double)damage * 0.5D);
                ((ServerLevel)player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
            }

            player.awardStat(Stats.DAMAGE_DEALT, Math.round(dealDamage * 10.0F));
            if (player.level() instanceof ServerLevel && dealDamage > 2.0F) {
                int k = (int) ((double) dealDamage * 0.5D);
                ((ServerLevel) player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
            }
            opStorage.receiveEnergy(EquipCfg.energyAttack * dealDamage, false);
        }

        return result;
    }

    @Override
    public void afterBlockBreak(IToolStackView tool, ModifierEntry modifierEntry, ToolHarvestContext context) {
        Player player = context.getPlayer();
        if(player != null && !context.isAOE()){
            ItemStack stack = TicEXUtils.getToolStack(player, TicEXRegistry.EVOLVED_MODIFIER.get());
            if(!stack.isEmpty()){
                ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
                IOPStorage storage = stack.getCapability(DECapabilities.OP_STORAGE).orElseThrow(IllegalStateException::new);

                int aoe = host.getModuleData(ModuleTypes.AOE, new AOEData(0)).aoe();
                boolean aoeSafe = true;
                if (host instanceof PropertyProvider propertyProvider) {
                    if (propertyProvider.hasInt("mining_aoe")) {
                        aoe = propertyProvider.getInt("mining_aoe").getValue();
                    }
                    if (propertyProvider.hasBool("aoe_safe")) {
                        aoeSafe = propertyProvider.getBool("aoe_safe").getValue();
                    }
                }
                
                breakAOEBlocks(tool, host, storage, stack, context.getPos(), context.getSideHit(), aoe + tool.getModifierLevel(TinkerModifiers.expanded.get()), 0, player, aoeSafe, context);
            }
        }
    }

    private boolean breakAOEBlocks(IToolStackView tool, ModuleHost host, IOPStorage storage, ItemStack stack, BlockPos pos, Direction sideHit, int breakRadius, int breakDepth, Player player, boolean aoeSafe, ToolHarvestContext context) {
        BlockState blockState = player.level().getBlockState(pos);

        InventoryDynamic inventoryDynamic = new InventoryDynamic();
        float refStrength = blockStrength(blockState, player, player.level(), pos);
        Pair<BlockPos, BlockPos> aoe = getMiningArea(pos, sideHit, player, breakRadius, breakDepth);
        List<BlockPos> aoeBlocks = BlockPos.betweenClosedStream(aoe.getFirst(), aoe.getSecond()).map(BlockPos::new).toList();

        if (aoeSafe) {
            for (BlockPos block : aoeBlocks) {
                if (!player.level().isEmptyBlock(block) && player.level().getBlockEntity(block) != null) {
                    if (player.level().isClientSide) player.sendSystemMessage(Component.translatable("item_prop.draconicevolution.aoe_safe.blocked"));
                    else ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(((ServerPlayer) player).level(), block));
                    return true;
                }
            }
        }

        aoeBlocks.forEach(block -> breakAOEBlock(tool, stack, player.level(), block, player, refStrength, inventoryDynamic, player.level().getRandom().nextInt(Math.max(5, (breakRadius * breakDepth) / 5)) == 0, context, storage));
        List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, new AABB(aoe.getFirst(), aoe.getSecond().offset(1, 1, 1)));
        for (ItemEntity item : items) {
            if (!player.level().isClientSide && item.isAlive()) {
                InventoryUtils.insertItem(inventoryDynamic, item.getItem(), false);
                item.discard();
            }
        }

        ModuleHelper.handleItemCollection(player, host, storage, inventoryDynamic);
        return true;
    }

    

    @Override
    public Component getDisplayName(int level) {
        return Component.translatable(this.getTranslationKey()+"."+level);
    }

    private  Pair<BlockPos, BlockPos> getMiningArea(BlockPos pos, Direction direction, @SuppressWarnings("unused") Player player, int breakRadius, int breakDepth) {

        int sideHit = direction.get3DDataValue();

        int xMax = breakRadius;
        int xMin = breakRadius;
        int yMax = breakRadius;
        int yMin = breakRadius;
        int zMax = breakRadius;
        int zMin = breakRadius;
        int yOffset = 0;

        switch (sideHit) {
            case 0 -> {
                yMax = breakDepth;
                yMin = 0;
                zMax = breakRadius;
            }
            case 1 -> {
                yMin = breakDepth;
                yMax = 0;
                zMax = breakRadius;
            }
            case 2 -> {
                xMax = breakRadius;
                zMin = 0;
                zMax = breakDepth;
                yOffset = breakRadius - 1;
            }
            case 3 -> {
                xMax = breakRadius;
                zMax = 0;
                zMin = breakDepth;
                yOffset = breakRadius - 1;
            }
            case 4 -> {
                xMax = breakDepth;
                xMin = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
            }
            case 5 -> {
                xMin = breakDepth;
                xMax = 0;
                zMax = breakRadius;
                yOffset = breakRadius - 1;
            }
        }

        if (breakRadius == 0) {
            yOffset = 0;
        }

        return new Pair<>(pos.offset(-xMin, yOffset - yMin, -zMin), pos.offset(xMax, yOffset + yMax, zMax));
    }


    static float blockStrength(BlockState state, Player player, Level world, BlockPos pos) {
        float hardness = state.getDestroySpeed(world, pos);
        if (hardness < 0.0F) {
            return 0.0F;
        }

        if (!ForgeHooks.isCorrectToolForDrops(state, player)) {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        } else {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
    }

    private void breakAOEBlock(IToolStackView tool, ItemStack stack, Level world, BlockPos pos, Player player, @SuppressWarnings("unused") float refStrength, InventoryDynamic inventory, @SuppressWarnings("unused") boolean breakFX, ToolHarvestContext context, IOPStorage storage) {
        if (world.isEmptyBlock(pos)) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        if (!isCorrectToolForDrops(tool, stack, state)) {
            return;
        }

        if(storage.getEnergyStored() < EquipCfg.energyHarvest){
            return;
        }

        ToolHarvestContext newContext = context.forPosition(pos, state);
        if(ModList.get().isLoaded("avaritia") && tool.getModifierLevel(TicEXRegistry.BEDROCK_BREAKER_MODIFIER.get()) > 0){
            state.getBlock().playerDestroy(player.level(), player, pos, state, player.level().getBlockEntity(pos), player.getMainHandItem());
        }
        ToolHarvestLogic.breakExtraBlock(ToolStack.from(stack), stack, newContext);
        storage.extractEnergy(EquipCfg.energyHarvest, false);
    }

    private boolean isCorrectToolForDrops(IToolStackView tool, ItemStack stack, BlockState state) {
        if(ModList.get().isLoaded("avaritia") && tool.getModifierLevel(TicEXRegistry.BEDROCK_BREAKER_MODIFIER.get()) > 0)return true;
        return stack.isCorrectToolForDrops(state);
    }

    

    @Override
    public List<ModifierEntry> displayModifiers(ModifierEntry entry) {
        List<ModifierEntry> entries = new ArrayList<>();
        if(entry.getLevel() == 1){
            entries.add(new ModifierEntry(ModifierIds.reinforced, 5));
            entries.add(new ModifierEntry(ModifierIds.netherite, 1));
        }
        return entries;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry entry) {
        if(entry.getLevel() == 1 && (tool.getModifierLevel(ModifierIds.reinforced) < 5 || tool.getModifierLevel(ModifierIds.netherite) < 1)){
            return Component.translatable("recipe.ticex.modifier.evolved_requirements_1");
        }
        return null;
    }

    
}


