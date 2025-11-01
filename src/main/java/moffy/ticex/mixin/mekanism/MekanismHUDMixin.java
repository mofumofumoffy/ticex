package moffy.ticex.mixin.mekanism;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mekanism.client.render.hud.MekanismHUD;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MekanismHUD.class, remap = false)
public class MekanismHUDMixin {
    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    public boolean allowMekanicModifier(ItemStack instance, TagKey<Item> pTag, Operation<Boolean> original){
        return instance.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent() || original.call(instance, pTag);
    }
}
