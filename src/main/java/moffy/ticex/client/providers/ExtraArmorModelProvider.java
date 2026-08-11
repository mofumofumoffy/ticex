package moffy.ticex.client.providers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;

public abstract class ExtraArmorModelProvider {
    public abstract void providerSetup(
            @NotNull LivingEntity living,
            @NotNull ItemStack stack,
            @NotNull EquipmentSlot slot,
            @NotNull HumanoidModel<?> base,
            @NotNull ArmorModelManager.ArmorModel model
    );

    public abstract void renderExtraModel(
            @NotNull PoseStack matrices,
            @NotNull VertexConsumer bufferIn,
            int packedLightIn,
            int packedOverlayIn,
            float red,
            float green,
            float blue,
            float alpha
    );
}
