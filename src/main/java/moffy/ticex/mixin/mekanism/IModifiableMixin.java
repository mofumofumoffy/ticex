package moffy.ticex.mixin.mekanism;

import mekanism.api.radial.RadialData;
import mekanism.api.radial.mode.IRadialMode;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.radial.IGenericRadialModeItem;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.List;

@Mixin(value = IModifiable.class, remap = false)
public interface IModifiableMixin extends IModuleContainerItem, IGenericRadialModeItem {
    @Override
    default boolean supportsSlotType(ItemStack stack, @NotNull EquipmentSlot slotType) {
        boolean slotTypeMatched = false;
        if(stack.getItem() instanceof ArmorItem armorItem){
            slotTypeMatched = armorItem.getType().getSlot() == slotType;
        } else {
            slotTypeMatched = IGenericRadialModeItem.super.supportsSlotType(stack, slotType);
        }
        return slotTypeMatched && getModules(stack).stream().anyMatch(mekanism.common.content.gear.Module::handlesAnyModeChange);
    }

    @Nullable
    @Override
    default Component getScrollTextComponent(@NotNull ItemStack stack) {
        if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            return getModules(stack).stream().filter(Module::handlesModeChange).findFirst().map(module -> module.getModeScrollComponent(stack)).orElse(null);
        }
        return IGenericRadialModeItem.super.getScrollTextComponent(stack);
    }

    @Override
    default List<Module<?>> getModules(ItemStack stack) {
        LazyOptional<IMekaGear> mekaGearsCapabilityLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
        if(mekaGearsCapabilityLazyOptional.isPresent()){
            IMekaGear mekaGearsCapability = mekaGearsCapabilityLazyOptional.orElseThrow(IllegalStateException::new);
            return mekaGearsCapability.getModules(stack);
        }
        return List.of();
    }

    @Override
    default @Nullable RadialData<?> getRadialData(ItemStack stack) {
        LazyOptional<IMekaGear> mekaGearsCapabilityLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
        if(mekaGearsCapabilityLazyOptional.isPresent()){
            IMekaGear mekaGearsCapability = mekaGearsCapabilityLazyOptional.orElseThrow(IllegalStateException::new);
            return mekaGearsCapability.getRadialData(stack);
        }
        return null;
    }

    @Override
    default  <M extends IRadialMode> @Nullable M getMode(ItemStack stack, RadialData<M> radialData) {
        LazyOptional<IMekaGear> mekaGearsCapabilityLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
        if(mekaGearsCapabilityLazyOptional.isPresent()){
            IMekaGear mekaGearsCapability = mekaGearsCapabilityLazyOptional.orElseThrow(IllegalStateException::new);
            return mekaGearsCapability.getMode(stack, radialData);
        }
        return null;
    }

    @Override
    default  <M extends IRadialMode> void setMode(ItemStack stack, Player player, RadialData<M> radialData, M mode) {
        LazyOptional<IMekaGear> mekaGearsCapabilityLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
        if(mekaGearsCapabilityLazyOptional.isPresent()){
            IMekaGear mekaGearsCapability = mekaGearsCapabilityLazyOptional.orElseThrow(IllegalStateException::new);
            mekaGearsCapability.setMode(stack, player, radialData, mode);
        }
    }

    @Override
   default void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        LazyOptional<IMekaGear> mekaGearsCapabilityLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
        if (mekaGearsCapabilityLazyOptional.isPresent()) {
            IMekaGear mekaGearsCapability = mekaGearsCapabilityLazyOptional.orElseThrow(IllegalStateException::new);
            mekaGearsCapability.changeMode(player, stack, shift, displayChange);
        }
    }
}
