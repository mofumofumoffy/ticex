package moffy.ticex.lib.utils;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.math.FloatingLong;
import mekanism.common.registries.MekanismItems;
import mekanism.common.util.StorageUtils;
import meranha.mekaweapons.MekaWeapons;
import meranha.mekaweapons.items.modules.DrawSpeedUnit;
import meranha.mekaweapons.items.modules.WeaponAttackAmplificationUnit;
import meranha.mekaweapons.items.modules.WeaponsModules;
import moffy.ticex.TicEX;
import moffy.ticex.client.modules.mekanism.weapons.MekanicArrowRenderer;
import moffy.ticex.entity.mekanism.MekanicProjectile;
import moffy.ticex.item.projectile.MekanicShotItem;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import moffy.ticex.registry.TicEXRegistry;
import moffy.ticex.registry.TicEXEntities;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class TicEXMekanismWeaponsUtils {
    public static Item getAlternativeWeapon(ItemStack stack){
        if(stack.is(TinkerTags.Items.RANGED)){
            return MekaWeapons.MEKA_BOW.get();
        } else if(stack.is(TinkerTags.Items.MELEE_WEAPON)){
            return MekaWeapons.MEKA_TANA.get();
        }
        return MekanismItems.MEKA_TOOL.get();
    }

    public static void register(){
        TicEXItems.CATALYST_MEKA_TANA = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_meka_tana",
                () -> new ToolPartItem(new Item.Properties(), CatalystMaterialStatsType.getOrMakeType("catalyst_meka_tana").getId())
        );
        TicEXItems.CATALYST_MEKA_BOW = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_meka_bow",
                () -> new ToolPartItem(new Item.Properties(), CatalystMaterialStatsType.getOrMakeType("catalyst_meka_bow").getId())
        );

        TicEXEntities.MEKANIC_PROJECTILE = TicEXRegistry.ENTITIES.register("mekanic", () ->
                EntityType.Builder.<MekanicProjectile>of(MekanicProjectile::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .setTrackingRange(10)
                        .setUpdateInterval(20)
                        .setShouldReceiveVelocityUpdates(false)
                        .build(TicEX.MODID + ":mekanic_arrow")
        );
        TicEXItems.MEKANIC_ARROW = TicEXRegistry.ITEMS.register("mekanic_arrow", ()->new MekanicShotItem(new Item.Properties()));
    }

    public static float getAmplifier(ItemStack stack){
        if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            IModule<WeaponAttackAmplificationUnit> unit = mekaGear.getModule(stack, WeaponsModules.ATTACKAMPLIFICATION_UNIT);
            if(unit != null){
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                FloatingLong energy = energyContainer != null ? energyContainer.getEnergy() : FloatingLong.ZERO;
                FloatingLong usage = stack.is(TinkerTags.Items.RANGED) ? MekaWeapons.general.mekaBowEnergyUsage.get() : MekaWeapons.general.mekaTanaEnergyUsage.get();
                int unitDamage = energy.greaterOrEqual(usage) ? unit.getCustomInstance().getCurrentUnit() : 0;
                if(unit.getInstalledCount() > 4){
                    return (unitDamage - 1) / 5f * (unit.getInstalledCount() + 1);
                }
                return unitDamage - 1;
            }
        }
        return 0;
    }

    public static void handleAutoFire(LivingEntity entity, IToolStackView tool, int useDuration, int timeLeft){
        if(tool.hasTag(TinkerTags.Items.RANGED) && entity instanceof Player player){
            ItemStack toolStack = TicEXUtils.getToolStack(tool, entity, TicEXModifiers.MEKANIC_MODIFIER.get());
            toolStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).ifPresent(mekaGear -> {
                if (entity.isAlive() && mekaGear.isModuleEnabled(toolStack, WeaponsModules.AUTOFIRE_UNIT) && useDuration - timeLeft == getUseTick(toolStack, mekaGear)) {
                    entity.stopUsingItem();
                    toolStack.releaseUsing(entity.level(), entity, 0);
                    toolStack.use(player.level(), player, player.getUsedItemHand());
                }
            });
        }
    }

    private static float getUseTick(@NotNull ItemStack stack, IMekaGear mekaGear) {
        float useTick = 20.0F;
        IModule<DrawSpeedUnit> drawSpeedUnit = mekaGear.getModule(stack, WeaponsModules.DRAWSPEED_UNIT);
        if (drawSpeedUnit != null && drawSpeedUnit.isEnabled()) {
            useTick -= 5.0f * drawSpeedUnit.getCustomInstance().getDrawSpeed();
        }
        return useTick;
    }

    public static boolean hasEnergyArrow(ToolStack tool){
        ItemStack stack = TicEXUtils.getToolStack(tool);
        return IModuleHelper.INSTANCE.isEnabled(stack, WeaponsModules.ARROWENERGY_UNIT);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerArrowRenderer(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(TicEXEntities.MEKANIC_PROJECTILE.get(), MekanicArrowRenderer::new);
    }
}
