package moffy.ticex.caps.curios;

import java.util.UUID;

import com.google.common.collect.Multimap;

import moffy.ticex.lib.utils.TicEXCuriosUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class IncomparableCuriosCapability implements ICurio{
    public static final String INCOMPARABLE_REMOVE_FLAG = "incomparable_remove";

    protected final ItemStack stack;
    protected final IToolStackView tool;

    private ItemStack preStack = ItemStack.EMPTY;

    public IncomparableCuriosCapability(ItemStack stack, IToolStackView tool){
        this.stack = stack;
        this.tool = tool;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        LivingEntity entity = slotContext.entity();
        CuriosApi.getCuriosInventory(entity).ifPresent(inv->{
            for(EquipmentSlot equipmentSlot : EquipmentSlot.values()){
                inv.findCurio(TicEXCuriosUtils.resolveEquipmentSlot(equipmentSlot), 0).ifPresent(result -> {

                    if(entity.getItemBySlot(equipmentSlot).isEmpty()){
                        ItemStack hollowStack = result.stack().copy();
                        entity.setItemSlot(equipmentSlot, hollowStack);
                        preStack = hollowStack;
                    }
                });
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        return stack.getAttributeModifiers(TicEXCuriosUtils.resolveSlot(slotContext));
    }

    @Override
    public boolean canWalkOnPowderedSnow(SlotContext slotContext) {
        return stack.canWalkOnPowderedSnow(slotContext.entity());
    }

    @Override
    public boolean isEnderMask(SlotContext context, EnderMan enderMan) {
        if(context.entity() instanceof Player player){
            return stack.isEnderMask(player, enderMan);
        }
        return ICurio.super.isEnderMask(context, enderMan);
    }

    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext) {
        return stack.makesPiglinsNeutral(slotContext.entity());
    }

    @Override
    public CompoundTag writeSyncData(SlotContext slotContext) {
        CompoundTag result = ICurio.super.writeSyncData(slotContext);
        return result;
    }

    @Override
    public void readSyncData(SlotContext slotContext, CompoundTag compound) {
        ICurio.super.readSyncData(slotContext, compound);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        preStack.shrink(preStack.getCount());
    }


}
