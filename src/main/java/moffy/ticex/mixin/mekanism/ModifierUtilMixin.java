package moffy.ticex.mixin.mekanism;

import mekanism.api.gear.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.mekasuit.ModuleElytraUnit;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismModules;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IGasTankItem;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;

@Mixin(value = ModifierUtil.class, remap = false)
public class ModifierUtilMixin {
    @Inject(
            at = @At("RETURN"),
            method = "checkVolatileFlag",
            cancellable = true
    )
    private static void checkVolatileFlag(ItemStack stack, ResourceLocation flag, CallbackInfoReturnable<Boolean> cir) {
        if(flag.equals(ModifiableArmorItem.ELYTRA) && stack.getItem() instanceof ArmorItem armorItem && stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            if (armorItem.getType() == ArmorItem.Type.CHESTPLATE && mekaGear instanceof IGasTankItem gasTankItem) {
                IModule<ModuleElytraUnit> module = mekaGear.getModule(stack, MekanismModules.ELYTRA_UNIT);
                if (module != null && module.isEnabled()) {
                    IModule<ModuleJetpackUnit> jetpack = mekaGear.getModule(stack, MekanismModules.JETPACK_UNIT);
                   cir.setReturnValue(jetpack == null ||
                           !jetpack.isEnabled() ||
                           jetpack.getCustomInstance().getMode() != IJetpackItem.JetpackMode.HOVER ||
                           gasTankItem.getContainedGas(stack, MekanismGases.HYDROGEN.get()).isEmpty());
                }
            }
        }
    }
}
