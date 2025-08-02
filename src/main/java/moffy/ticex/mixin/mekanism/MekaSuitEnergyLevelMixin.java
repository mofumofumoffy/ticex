package moffy.ticex.mixin.mekanism;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.render.hud.MekaSuitEnergyLevel;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.StorageUtils;
import moffy.ticex.item.modifiable.ModifiableMekaSuitArmor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekaSuitEnergyLevel.class, remap = false)
public class MekaSuitEnergyLevelMixin {
    @Shadow
    @Final
    private static ResourceLocation POWER_BAR;

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight, CallbackInfo ci) {
        if (!gui.getMinecraft().options.hideGui && gui.shouldDrawSurvivalElements()) {
            gui.setupOverlayRenderState(true, false);
            FloatingLong capacity = FloatingLong.ZERO, stored = FloatingLong.ZERO;
            for (ItemStack stack : gui.getMinecraft().player.getArmorSlots()) {
                if (stack.getItem() instanceof ItemMekaSuitArmor || stack.getItem() instanceof ModifiableMekaSuitArmor) {
                    IEnergyContainer container = StorageUtils.getEnergyContainer(stack, 0);
                    if (container != null) {
                        capacity = capacity.plusEqual(container.getMaxEnergy());
                        stored = stored.plusEqual(container.getEnergy());
                    }
                }
            }
            if (!capacity.isZero()) {
                int x = screenWidth / 2 - 91;
                int y = screenHeight - gui.leftHeight + 2;
                int length = (int) Math.round(stored.divide(capacity).doubleValue() * 79);
                GuiUtils.renderExtendedTexture(guiGraphics, GuiBar.BAR, 2, 2, x, y, 81, 6);
                guiGraphics.blit(POWER_BAR, x + 1, y + 1, length, 4, 0, 0, length, 4, 79, 4);
                gui.leftHeight += 8;
            }
        }
        ci.cancel();
    }
}
