package moffy.ticex.lib.utils;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IModuleDataProvider;
import mekanism.common.registries.MekanismItems;
import mekanism.common.util.StorageUtils;
import meranha.mekaweapons.MekaWeapons;
import meranha.mekaweapons.items.modules.WeaponAttackAmplificationUnit;
import meranha.mekaweapons.items.modules.WeaponsModules;
import moffy.ticex.TicEX;
import moffy.ticex.entity.mekanism.MekanicProjectile;
import moffy.ticex.item.projectile.MekanicShotItem;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
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
        TicEXRegistry.CATALYST_MEKA_TANA = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_meka_tana",
                () -> new ToolPartItem(new Item.Properties(), CatalystMaterialStatsType.getOrMakeType("catalyst_meka_tana").getId())
        );
        TicEXRegistry.CATALYST_MEKA_BOW = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_meka_bow",
                () -> new ToolPartItem(new Item.Properties(), CatalystMaterialStatsType.getOrMakeType("catalyst_meka_bow").getId())
        );

        TicEXRegistry.MEKANIC_PROJECTILE = TicEXRegistry.ENTITIES.register("mekanic", () ->
                EntityType.Builder.<MekanicProjectile>of(MekanicProjectile::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .setTrackingRange(10)
                        .setUpdateInterval(20)
                        .setShouldReceiveVelocityUpdates(false)
                        .build(TicEX.MODID + ":mekanic_arrow")
        );
        TicEXRegistry.MEKANIC_ARROW = TicEXRegistry.ITEMS.register("mekanic_arrow", ()->new MekanicShotItem(new Item.Properties()));
    }

    public static float getAmplifier(ItemStack stack){
        if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            IModule<WeaponAttackAmplificationUnit> unit = mekaGear.getModule(stack, WeaponsModules.ATTACKAMPLIFICATION_UNIT);
            if(unit != null){
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                FloatingLong energy = energyContainer != null ? energyContainer.getEnergy() : FloatingLong.ZERO;
                int unitDamage = energy.greaterOrEqual(MekaWeapons.general.mekaTanaEnergyUsage.get()) ? unit.getCustomInstance().getCurrentUnit() : 0;
                if(unit.getInstalledCount() > 4){
                    return (unitDamage - 1) / 5f * (unit.getInstalledCount() + 1);
                }
                return unitDamage - 1;
            }
        }
        return 0;
    }
}
