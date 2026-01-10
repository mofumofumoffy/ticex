package moffy.ticex.client.render.provider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.renderer.IArmorPartContextRenderer;
import net.minecraft.client.model.Model;
import slimeknights.tconstruct.library.client.armor.AbstractArmorModel;

public class ArmorContextRenderer implements IArmorPartContextRenderer {
    public static final ArmorContextRenderer RENDERER = new ArmorContextRenderer();

    @Override
    public void renderArmorPart(Model model, VertexConsumer consumer, float red, float green, float blue, float alpha, PoseStack poseStack, int combinedLight, int combinedOverlay) {
        AbstractArmorModel.renderColored(
                model,
                poseStack,
                consumer,
                combinedLight,
                combinedOverlay,
                -1,
                red,
                green,
                blue,
                alpha
        );
    }
}
