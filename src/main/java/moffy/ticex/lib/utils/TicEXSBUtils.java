package moffy.ticex.lib.utils;

import com.google.common.base.Suppliers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class TicEXSBUtils {

    public static Set<Enchantment> disallowedEnchantments = new HashSet<>();

    static {
        disallowedEnchantments.add(Enchantments.UNBREAKING);
        disallowedEnchantments.add(Enchantments.MENDING);
        disallowedEnchantments.add(Enchantments.SHARPNESS);
        disallowedEnchantments.add(Enchantments.BANE_OF_ARTHROPODS);
        disallowedEnchantments.add(Enchantments.SMITE);
        disallowedEnchantments.add(Enchantments.FIRE_ASPECT);
        disallowedEnchantments.add(Enchantments.KNOCKBACK);
        disallowedEnchantments.add(Enchantments.MOB_LOOTING);
    }

    public static Supplier<Matrix4f> defaultTransform = Suppliers.memoize(() -> {
        Matrix4f m = new Matrix4f();
        m.identity();
        return m;
    });

    public static int calcEnchLevel(ItemStack stack, Enchantment key, int value) {
        int currentLv = stack.getEnchantmentLevel(key);
        int levelCap = key.getMaxLevel();
        if (value == currentLv) {
            return Math.min(value + 1, levelCap);
        }
        return Math.min(Math.max(value, currentLv), levelCap);
    }

    public static boolean applyEnchantment(ItemStack toolStack, Enchantment enchantment, int level) {
        for (Enchantment disallowed : disallowedEnchantments) {
            if (!enchantment.getDescriptionId().equals(disallowed.getDescriptionId())) {
                if (toolStack.getEnchantmentLevel(enchantment) > 0) {
                    CompoundTag nbt = toolStack.getOrCreateTag();
                    if (!nbt.contains("Enchantments", Tag.TAG_LIST)) {
                        nbt.put("Enchantments", new ListTag());
                    }

                    ListTag listTag = nbt.getList("Enchantments", Tag.TAG_COMPOUND);
                    ListTag newListTag = new ListTag();
                    for (int i = 0; i < listTag.size(); i++) {
                        CompoundTag enchantmentTag = listTag.getCompound(i);
                        if (
                                enchantmentTag
                                        .getString("id")
                                        .equals(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(enchantment)).toString())
                        ) {
                            newListTag.add(
                                    EnchantmentHelper.storeEnchantment(
                                            ResourceLocation.tryParse(enchantmentTag.getString("id")),
                                            calcEnchLevel(toolStack, enchantment, level)
                                    )
                            );
                        } else {
                            newListTag.add(enchantmentTag);
                        }
                    }
                    nbt.put("Enchantments", newListTag);
                } else {
                    toolStack.enchant(enchantment, Math.min(enchantment.getMaxLevel(), level));
                }
                return true;
            }
        }
        return false;
    }
}
