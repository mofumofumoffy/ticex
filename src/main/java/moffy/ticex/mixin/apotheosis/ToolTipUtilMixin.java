package moffy.ticex.mixin.apotheosis;

import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = TooltipUtil.class, remap = false)
public class ToolTipUtilMixin {
    @Inject(at=@At("HEAD"), method = "addModifierNames", cancellable = true)
    private static void addModifierNameWithModify(ItemStack stack, IToolStackView tool, Player player, List<Component> tooltips, TooltipFlag flag, CallbackInfo ci){
        if(!SocketHelper.getGems(stack).isEmpty()){
            RegistryAccess access = player == null ? null : player.level().registryAccess();
            for (ModifierEntry entry : tool.getModifierList()) {
                if (entry.getModifier().shouldDisplay(false)) {
                    Component name = entry.getModifier().getDisplayName(tool, entry, access);
                    if (flag.isAdvanced() && Config.CLIENT.modifiersIDsInAdvancedTooltips.get()) {
                        tooltips.add(Component.translatable(TooltipUtil.KEY_ID_FORMAT, name, Component.literal(entry.getModifier().getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        tooltips.add(name);
                    }
                }
            }
            if (!stack.isEmpty()) {
                Map<Enchantment, Integer> realLevels = new HashMap<>(stack.getAllEnchantments());

                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("Enchantments", Tag.TAG_LIST)) {
                    ListTag enchantments = tag.getList("Enchantments", Tag.TAG_COMPOUND);
                    for (int i = 0; i < enchantments.size(); ++i) {
                        CompoundTag enchantmentTag = enchantments.getCompound(i);

                        BuiltInRegistries.ENCHANTMENT.getOptional(ResourceLocation.tryParse(enchantmentTag.getString("id")))
                                .ifPresent(enchantment -> {
                                    int level = enchantmentTag.getInt("lvl");
                                    int realLevel = realLevels.remove(enchantment);

                                    if(level == realLevel){
                                        tooltips.add(enchantment.getFullname(level));
                                    } else {
                                        ticex_1_20_1$appendModifiedEnchTooltip(tooltips, enchantment, realLevel, level);
                                    }
                                });
                    }
                }
            }
            ci.cancel();
        };
    }

    @Unique
    private static void ticex_1_20_1$appendModifiedEnchTooltip(List<Component> tooltip, Enchantment ench, int realLevel, int nbtLevel) {
        MutableComponent mc = ench.getFullname(realLevel).copy();
        mc.getSiblings().clear();
        Component nbtLevelComp = Component.translatable("enchantment.level." + nbtLevel);
        Component realLevelComp = Component.translatable("enchantment.level." + realLevel);
        if (realLevel != 1 || EnchHooks.getMaxLevel(ench) != 1) mc.append(CommonComponents.SPACE).append(realLevelComp);

        int diff = realLevel - nbtLevel;
        char sign = diff > 0 ? '+' : '-';
        Component diffComp = Component.translatable("(%s " + sign + " %s)", nbtLevelComp, Component.translatable("enchantment.level." + Math.abs(diff))).withStyle(ChatFormatting.DARK_GRAY);
        mc.append(CommonComponents.SPACE).append(diffComp);
        if (realLevel == 0) {
            mc.withStyle(ChatFormatting.DARK_GRAY);
        }
        tooltip.add(mc);
    }
}
