package moffy.ticex.mixin.mekanism;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import mekanism.client.render.HUDRenderer;
import mekanism.common.util.EnumUtils;
import moffy.ticex.item.modifiable.ItemModifiableMekaSuitArmor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

@Mixin(value = HUDRenderer.class, remap = false)
public abstract class HUDHandlerMixin {

    @Shadow
    private static ResourceLocation[] ARMOR_ICONS;

    @Shadow
    private int renderEnergyIcon(Player player, Font font, GuiGraphics guiGraphics, int posX, int color, ResourceLocation icon, EquipmentSlot slot,
          Predicate<Item> showPercent){
            return 0;
    }

    @Inject(
        at = @At("tail"),
        method="renderMekaSuitEnergyIcons"
    )
    public void renderMekaSuitEnergyIcons(Player player, Font font, GuiGraphics guiGraphics, int color, CallbackInfo cb){
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(10, 10, 0);
        int posX = 0;
        Predicate<Item> showArmorPercent = item -> item instanceof ItemModifiableMekaSuitArmor;
        for (int i = 0; i < EnumUtils.ARMOR_SLOTS.length; i++) {
            posX += renderEnergyIcon(player, font, guiGraphics, posX, color, ARMOR_ICONS[i], EnumUtils.ARMOR_SLOTS[i], showArmorPercent);
        }
        pose.popPose();
    }
}
