package moffy.ticex.client;

import moffy.ticex.TicEX;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModelDispatcher;

public abstract class MekaPlateDispatcher extends ArmorModelDispatcher{


    @Override
    protected abstract ResourceLocation getName();
    
    @Override
    public Model getGenericArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot,
            HumanoidModel<?> original) {
        switch (slot) {
            case HEAD:
                return MekaPlateMultilayerModel.HEAD.setup(living, stack, slot, original, this.getModel(stack));
            case CHEST:
                return MekaPlateMultilayerModel.CHESTPLATE.setup(living, stack, slot, original, this.getModel(stack));
            case LEGS:
                return MekaPlateMultilayerModel.LEGGINGS.setup(living, stack, slot, original, this.getModel(stack));
            case FEET:
                return MekaPlateMultilayerModel.BOOTS.setup(living, stack, slot, original, this.getModel(stack));
            default:
                return super.getGenericArmorModel(living, stack, slot, original);
        }
    }
}
