package moffy.ticex.mixin.mekanism;

import mekanism.api.gear.ModuleData;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.registries.MekanismItems;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.utils.TicEXMekanismWeaponsUtils;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.Map;
import java.util.Set;

@Mixin(value = ModuleHelper.class, remap = false)
public class ModuleHelperMixin {

    @Shadow
    @Final
    private Map<Item, Set<ModuleData<?>>> supportedModules;

    @Inject(
            at = @At("RETURN"),
            method = "getSupported(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Set;",
            cancellable = true
    )
    public void getSupported(ItemStack container, CallbackInfoReturnable<Set<ModuleData<?>>> cir){
        if(container.getItem() instanceof IModifiable && container.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            cir.setReturnValue(supportedModules.getOrDefault(ticex_1_20_1$getAlternativeItem(container), cir.getReturnValue()));
        }
    }

    @Unique
    private Item ticex_1_20_1$getAlternativeItem(ItemStack stack){
        if(stack.getItem() instanceof ArmorItem armorItem){
            return switch (armorItem.getType()){
                case HELMET -> MekanismItems.MEKASUIT_HELMET.get();
                case CHESTPLATE -> MekanismItems.MEKASUIT_BODYARMOR.get();
                case LEGGINGS -> MekanismItems.MEKASUIT_PANTS.get();
                case BOOTS -> MekanismItems.MEKASUIT_BOOTS.get();
            };
        } else if(stack.is(TinkerTags.Items.MELEE_WEAPON) && ModList.get().isLoaded("mekaweapons")){
            return TicEXMekanismWeaponsUtils.getAlternativeWeapon(stack);
        }
        return MekanismItems.MEKA_TOOL.get();
    }
}
