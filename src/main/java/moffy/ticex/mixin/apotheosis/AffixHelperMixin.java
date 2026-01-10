package moffy.ticex.mixin.apotheosis;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Map;

@Mixin(value = AffixHelper.class, remap = false)
public class AffixHelperMixin {
    @Inject(at = @At("TAIL"), method = "setAffixes")
    private static void setApothSupplier(ItemStack stack, Map<DynamicHolder<? extends Affix>, AffixInstance> affixes, CallbackInfo ci){
        if(stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            if(tool.getModifierLevel(TicEXRegistry.APOTH_SUPPLIER_MODIFIER.get()) < 1){
                tool.addModifier(TicEXRegistry.APOTH_SUPPLIER_MODIFIER.getId(), 1);
            }
        }
    }
}
