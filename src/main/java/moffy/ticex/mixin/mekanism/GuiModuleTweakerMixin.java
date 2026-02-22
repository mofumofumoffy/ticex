package moffy.ticex.mixin.mekanism;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.client.gui.GuiModuleTweaker;
import mekanism.client.gui.element.button.TranslationButton;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.common.TinkerTags;

@Mixin(value = GuiModuleTweaker.class, remap = false)
public abstract class GuiModuleTweakerMixin {
    @Shadow
    private TranslationButton optionsButton;

    @Shadow
    private ItemStack getStack(int index) {
        return null;
    }

    @Inject(
            at = @At("RETURN"),
            method = "select"
    )
    private void selectMekaGear(int index, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue() == true){
            ItemStack stack = getStack(index);
            if(stack != null){
                this.optionsButton.active = this.optionsButton.active || (stack.is(TinkerTags.Items.HELMETS) && stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent());
            }
        }
    }
}
