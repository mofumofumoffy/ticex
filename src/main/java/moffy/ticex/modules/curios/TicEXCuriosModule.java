package moffy.ticex.modules.curios;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.modifier.ModifierIncomparable;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class TicEXCuriosModule extends AddonModule{
    public TicEXCuriosModule(){
        TicEXRegistry.INCOMPARABLE_MODIFIER = TicEXRegistry.MODIFIERS.register("incomparable", ModifierIncomparable::new);

        CuriosApi.registerCurioPredicate(new ResourceLocation(TicEX.MODID, "incomparable"), (result)->validate(result));
    }

    public boolean validate(SlotResult result){
        ItemStack stack = result.stack();
        Item item = stack.getItem();

        if(item instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            if(tool.getModifierLevel(TicEXRegistry.INCOMPARABLE_MODIFIER.get()) > 0){
                if(item instanceof ArmorItem armor){
                    switch (armor.getType()) {
                        case HELMET:
                            return result.slotContext().identifier().equals("incomparable_head");
                        
                        case CHESTPLATE:
                            return result.slotContext().identifier().equals("incomparable_chest");

                        case LEGGINGS:
                            return result.slotContext().identifier().equals("incomparable_legs");

                        case BOOTS:
                        return result.slotContext().identifier().equals("incomparable_feet");
                    }
                } else {
                    return true;
                }
            }
        }

        return false;
    }
}
