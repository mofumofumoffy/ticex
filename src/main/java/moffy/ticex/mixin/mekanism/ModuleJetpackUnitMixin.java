package moffy.ticex.mixin.mekanism;

import java.util.function.Consumer;
import mekanism.api.chemical.gas.Gas;
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
import moffy.ticex.item.modifiable.ModifiableMekaSuitArmor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = ModuleJetpackUnit.class, remap = false)
public class ModuleJetpackUnitMixin {

    @Shadow
    private IModuleConfigItem<IJetpackItem.JetpackMode> jetpackMode;

    @Inject(at = @At("head"), method = "addHUDElements", cancellable = true)
    public void addHUDElements(
        IModule<ModuleJetpackUnit> module,
        Player player,
        Consumer<IHUDElement> hudElementAdder,
        CallbackInfo cb
    ) {
        ItemStack container = module.getContainer();
        if (container.getItem() instanceof IModifiable) {
            if (module.isEnabled()) {
                GasStack stored =
                    ((ModifiableMekaSuitArmor) container.getItem()).getContainedGas(
                            container,
                            (Gas) MekanismGases.HYDROGEN.get()
                        );
                double ratio = StorageUtils.getRatio(
                    stored.getAmount(),
                    MekanismConfig.gear.mekaSuitJetpackMaxStorage.getAsLong()
                );
                hudElementAdder.accept(
                    IModuleHelper.INSTANCE.hudElementPercent(
                        ((IJetpackItem.JetpackMode) this.jetpackMode.get()).getHUDIcon(),
                        ratio
                    )
                );
            }
            cb.cancel();
        }
    }
}
