package moffy.ticex.client.render.slashblade;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.TicEXConfig;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class KoshiraeIconDecorator implements IItemDecorator {
    private static final ResourceLocation OVERLAY = TConstruct.getResource("textures/gui/modifiers/tier.png");

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int xOffset, int yOffset) {
        if(TicEXConfig.SHOW_TOOL_ICON != null && TicEXConfig.SHOW_TOOL_ICON.get() && itemStack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(itemStack);
            if(tool.getModifierLevel(TicEXRegistry.KOSHIRAE_MODIFIER.get()) > 0){
                guiGraphics.pose().pushPose();

                ScaledOffset scaledOffset = new ScaledOffset(xOffset, yOffset);

                guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                scaledOffset.scale(guiGraphics.pose(), 0.75F, 0.75F);

                guiGraphics.blit(
                        OVERLAY,
                        scaledOffset.getxOffset() - 9, scaledOffset.getyOffset() + 6,
                        0, 0,
                        16, 16,
                        16, 16
                );

                guiGraphics.pose().popPose();
                return true;
            }
        }
        return false;
    }

    static class ScaledOffset{
        private int xOffset, yOffset;

        public ScaledOffset(int xOffset, int yOffset){
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        public void scale(PoseStack poseStack, float xScale, float yScale){
            poseStack.scale(xScale, yScale, 1.0F);
            if(xScale != 0F){
                xOffset = (int) (xOffset / xScale);
            }

            if(yScale != 0F){
                yOffset = (int) (yOffset / yScale);
            }
        }

        public int getxOffset() {
            return xOffset;
        }

        public int getyOffset() {
            return yOffset;
        }
    }
}
