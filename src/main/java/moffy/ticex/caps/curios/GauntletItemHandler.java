package moffy.ticex.caps.curios;

import java.util.function.Supplier;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class GauntletItemHandler extends ToolInventoryCapability implements ICurio{

    protected IToolStackView tool;
    protected ItemStack stack;

    public GauntletItemHandler(ItemStack stack, Supplier<? extends IToolStackView> tool) {
        super(tool);
        this.tool = tool.get();
        this.stack = stack;
    }

    @Override
    public int getSlots() {
        return 6;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        ICurio.super.onEquip(slotContext, prevStack);
    }

    @Override
    public boolean canEquip(SlotContext slotContext) {
        LivingEntity livingEntity = slotContext.entity();
        if(CuriosApi.getCuriosInventory(livingEntity).isPresent()){
            ICuriosItemHandler itemHandler = CuriosApi.getCuriosInventory(livingEntity).orElseThrow(IllegalStateException::new);
            return !itemHandler.findFirstCurio(TicEXRegistry.RESONANCE_GAUNTLET.get()).isPresent();
        }
        return ICurio.super.canEquip(slotContext);
    }
}
