package moffy.ticex.mixin.mekanism;

import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.Module;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(
            at=@At("RETURN"),
            method="useOn",
            cancellable = true
    )
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult interaction = cir.getReturnValue();
        if (!interaction.consumesAction()) {
            return;
        }

        ItemStack stack = context.getItemInHand();
        stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).ifPresent(mekagear -> {
            for (Module<?> module : mekagear.getModules(context.getItemInHand())) {
                if (module.isEnabled()) {
                    InteractionResult moduleResult = ticex$onModuleUse(module, context);
                    if (moduleResult != InteractionResult.PASS) {
                        cir.setReturnValue(moduleResult);
                    }
                }
            }
        });
    }

    @Unique
    private <MODULE extends ICustomModule<MODULE>> InteractionResult ticex$onModuleUse(IModule<MODULE> module, UseOnContext context) {
        return module.getCustomInstance().onItemUse(module, context);
    }
}
