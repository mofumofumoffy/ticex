package moffy.ticex.client.modules.mekanism;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModelDispatcher;

public abstract class MekaPlateDispatcher extends ArmorModelDispatcher {

    @Override
    protected abstract @NotNull ResourceLocation getName();

    @Override
    public @NotNull Model getGenericArmorModel(
            @NotNull LivingEntity living,
            @NotNull ItemStack stack,
            EquipmentSlot slot,
            @NotNull HumanoidModel<?> original
    ) {
        return switch (slot) {
            case HEAD -> MekaPlateMultilayerModel.HEAD.setup(living, stack, slot, original, this.getModel(stack));
            case CHEST ->
                    MekaPlateMultilayerModel.CHESTPLATE.setup(living, stack, slot, original, this.getModel(stack));
            case LEGS -> MekaPlateMultilayerModel.LEGGINGS.setup(living, stack, slot, original, this.getModel(stack));
            case FEET -> MekaPlateMultilayerModel.BOOTS.setup(living, stack, slot, original, this.getModel(stack));
            default -> super.getGenericArmorModel(living, stack, slot, original);
        };
    }
}
