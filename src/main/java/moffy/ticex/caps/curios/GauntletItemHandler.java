package moffy.ticex.caps.curios;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.function.Supplier;

public class GauntletItemHandler extends ToolInventoryCapability implements ICurio {

    protected IToolStackView tool;
    protected ItemStack stack;
    protected int[] itemCooldowns;

    public GauntletItemHandler(ItemStack stack, Supplier<? extends IToolStackView> tool) {
        super(tool);
        this.tool = tool.get();
        this.stack = stack;
        this.itemCooldowns = new int[getSlots()];
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
            return itemHandler.findFirstCurio(TicEXRegistry.RESONANCE_GAUNTLET.get()).isEmpty();
        }
        return ICurio.super.canEquip(slotContext);
    }

    public int getItemCooldown(int index) {
        return (index >= 0 && index < getSlots()) ? this.itemCooldowns[index] : 0;
    }

    public void setItemCooldown(int index, int cooldown) {
        if (index >= 0 && index < getSlots()) {
            this.itemCooldowns[index] = cooldown;
        }
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        for (int i = 0; i < this.itemCooldowns.length; i++) {
            if (this.itemCooldowns[i] > 0) {
                this.itemCooldowns[i] = this.itemCooldowns[i] - 1;
            }
        }
    }
}
