package moffy.ticex.modules.mekanism;


import mekanism.api.providers.IModuleDataProvider;
import mekanism.common.Mekanism;
import mekanism.common.registries.MekanismModules;
import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.caps.mekanism.MekItemCapability;
import moffy.ticex.client.MekaPlateMultilayerModel;
import moffy.ticex.client.MekaPlateModelCache;
import moffy.ticex.event.TicEXMekanismEvent;
import moffy.ticex.item.modifiable.ItemModifiableMekaSuitArmor;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.ArmorDefinitions;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;

public class TicEXMekanismModule extends AddonModule{
    

    public static final String ADD_MEKAPLATE_HELMET_MODULES = "add_mekaplate_helmet_modules";
    public static final String ADD_MEKAPLATE_CHESTPLATE_MODULES = "add_mekaplate_chestplate_modules";
    public static final String ADD_MEKAPLATE_LEGGINGS_MODULES = "add_mekaplate_leggings_modules";
    public static final String ADD_MEKAPLATE_BOOTS_MODULES = "add_mekaplate_boots_modules";

    public TicEXMekanismModule(){
        Item.Properties PROPS = new Item.Properties();

        ToolCapabilityProvider.register(MekItemCapability::new);

        TicEXRegistry.MEKAPLATE_ARMOR = TicEXRegistry.ITEMS_EXTENDED.registerEnum("mekaplate", ArmorItem.Type.values(), type -> new ItemModifiableMekaSuitArmor(ArmorDefinitions.PLATE, type, new Item.Properties().stacksTo(1)));
    
        TicEXRegistry.CATALYST_MEKAPLATE = TicEXRegistry.ITEMS_EXTENDED.registerEnum("catalyst_mekaplate", ArmorItem.Type.values(), type -> new ToolPartItem(PROPS, ((MaterialStatType<PlatingMaterialStats>)PlatingMaterialStats.TYPES.get(type.ordinal())).getId()));
    }

    @Override
    public void enqueueIMC(InterModEnqueueEvent event) {
        sendModuleIMC(ADD_MEKAPLATE_HELMET_MODULES, MekanismModules.COLOR_MODULATION_UNIT,MekanismModules.ELECTROLYTIC_BREATHING_UNIT, MekanismModules.INHALATION_PURIFICATION_UNIT,
              MekanismModules.VISION_ENHANCEMENT_UNIT, MekanismModules.NUTRITIONAL_INJECTION_UNIT, MekanismModules.ENERGY_UNIT, MekanismModules.LASER_DISSIPATION_UNIT, MekanismModules.RADIATION_SHIELDING_UNIT);
        sendModuleIMC(ADD_MEKAPLATE_CHESTPLATE_MODULES, MekanismModules.COLOR_MODULATION_UNIT,MekanismModules.JETPACK_UNIT, MekanismModules.GRAVITATIONAL_MODULATING_UNIT, MekanismModules.CHARGE_DISTRIBUTION_UNIT,
        MekanismModules.DOSIMETER_UNIT, MekanismModules.GEIGER_UNIT, MekanismModules.ELYTRA_UNIT, MekanismModules.ENERGY_UNIT, MekanismModules.LASER_DISSIPATION_UNIT, MekanismModules.RADIATION_SHIELDING_UNIT);
        sendModuleIMC(ADD_MEKAPLATE_LEGGINGS_MODULES, MekanismModules.COLOR_MODULATION_UNIT,MekanismModules.LOCOMOTIVE_BOOSTING_UNIT, MekanismModules.GYROSCOPIC_STABILIZATION_UNIT,
        MekanismModules.HYDROSTATIC_REPULSOR_UNIT, MekanismModules.MOTORIZED_SERVO_UNIT, MekanismModules.ENERGY_UNIT, MekanismModules.LASER_DISSIPATION_UNIT, MekanismModules.RADIATION_SHIELDING_UNIT);
        sendModuleIMC(ADD_MEKAPLATE_BOOTS_MODULES, MekanismModules.COLOR_MODULATION_UNIT,MekanismModules.HYDRAULIC_PROPULSION_UNIT, MekanismModules.MAGNETIC_ATTRACTION_UNIT, MekanismModules.FROST_WALKER_UNIT, MekanismModules.ENERGY_UNIT, MekanismModules.LASER_DISSIPATION_UNIT, MekanismModules.RADIATION_SHIELDING_UNIT);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        MekaPlateModelCache.INSTANCE.registerMekaSuitModuleModel(new ResourceLocation(TicEX.MODID, "models/entity/modifiable_mekasuit_modules.obj"));
        MinecraftForge.EVENT_BUS.register(new TicEXMekanismEvent());
        MekaPlateMultilayerModel.registerModule("jetpack", MekanismModules.JETPACK_UNIT, EquipmentSlot.CHEST, (entity)->true);
        MekaPlateMultilayerModel.registerModule("modulator", MekanismModules.GRAVITATIONAL_MODULATING_UNIT, EquipmentSlot.CHEST, (entity)->true);
        MekaPlateMultilayerModel.registerModule("elytra", MekanismModules.ELYTRA_UNIT, EquipmentSlot.CHEST, LivingEntity::isFallFlying);
    }

    private static void sendModuleIMC(String method, IModuleDataProvider<?>... moduleDataProviders) {
        if (moduleDataProviders == null || moduleDataProviders.length == 0) {
            throw new IllegalArgumentException("No module data providers given.");
        }
        InterModComms.sendTo(Mekanism.MODID, method, () -> moduleDataProviders);
    }
}
