package moffy.ticex.mixin.mekanism;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.api.gear.IHUDElement;
import mekanism.api.gear.IModuleHelper;
import mekanism.client.render.HUDRenderer;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.StorageUtils;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = HUDRenderer.class, remap = false)
public abstract class HUDHandlerMixin {

    @Final
    @Shadow
    private static ResourceLocation[] ARMOR_ICONS;

    @Shadow
    protected abstract void renderHUDElement(Font font, GuiGraphics guiGraphics, int x, int y, IHUDElement element, int color, boolean iconRight);

    @Inject(at = @At("TAIL"), method = "renderMekaSuitEnergyIcons")
    public void renderMekaSuitEnergyIcons(
        Player player,
        Font font,
        GuiGraphics guiGraphics,
        int color,
        CallbackInfo cb
    ) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(10, 10, 0);
        int posX = 0;
        Predicate<ItemStack> showArmorPercent = stack -> stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent();
        for (int i = 0; i < EnumUtils.ARMOR_SLOTS.length; i++) {
            posX += mekanism_capable_tool$renderEnergyIcon(
                player,
                font,
                guiGraphics,
                posX,
                color,
                ARMOR_ICONS[i],
                EnumUtils.ARMOR_SLOTS[i],
                showArmorPercent
            );
        }
        pose.popPose();
    }

    @Unique
    private int mekanism_capable_tool$renderEnergyIcon(Player player, Font font, GuiGraphics guiGraphics, int posX, int color, ResourceLocation icon, EquipmentSlot slot, Predicate<ItemStack> showPercent) {
        ItemStack stack = player.getItemBySlot(slot);
        if (showPercent.test(stack)) {
            this.renderHUDElement(font, guiGraphics, posX, 0, IModuleHelper.INSTANCE.hudElementPercent(icon, StorageUtils.getEnergyRatio(stack)), color, false);
            return 48;
        } else {
            return 0;
        }
    }
}
