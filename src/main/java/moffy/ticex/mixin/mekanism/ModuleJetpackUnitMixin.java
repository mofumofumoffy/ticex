package moffy.ticex.mixin.mekanism;

import mekanism.api.chemical.gas.GasStack;
import mekanism.api.gear.IHUDElement;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.StorageUtils;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IGasTankItem;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(value = ModuleJetpackUnit.class, remap = false)
public class ModuleJetpackUnitMixin {

    @Shadow
    private IModuleConfigItem<IJetpackItem.JetpackMode> jetpackMode;

    @Inject(at = @At("HEAD"), method = "addHUDElements", cancellable = true)
    public void addHUDElements(
        IModule<ModuleJetpackUnit> module,
        Player player,
        Consumer<IHUDElement> hudElementAdder,
        CallbackInfo cb
    ) {
        Predicate<ItemStack> hasCap = stack -> stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent();
        ItemStack container = module.getContainer();
        if (hasCap.test(container)) {
            IMekaGear mekaGear = container.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            if (module.isEnabled()) {
                GasStack stored = null;
                if(mekaGear instanceof IGasTankItem gasTankGear){
                    stored = gasTankGear.getContainedGas(
                            container,
                            MekanismGases.HYDROGEN.get()
                    );
                }
                double ratio = StorageUtils.getRatio(
                    stored != null ? stored.getAmount() : 0,
                    MekanismConfig.gear.mekaSuitJetpackMaxStorage.getAsLong()
                );
                hudElementAdder.accept(
                    IModuleHelper.INSTANCE.hudElementPercent(
                        (this.jetpackMode.get()).getHUDIcon(),
                        ratio
                    )
                );
            }
            cb.cancel();
        }
    }
}
