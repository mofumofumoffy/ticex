package moffy.ticex.lib.modules.mekanism.interfaces;

import net.minecraft.world.item.ItemStack;

public interface IMekanicArrow {
    ItemStack getBowItem();
    ItemStack getOriginalAmmo();
    ItemStack getCopiedOriginalAmmo();
    void setBowItem(ItemStack bowItem);
    void setOriginalAmmo(ItemStack originalAmmo);

    boolean isEnergizedArrow();
    void shrinkAmmo(int needed);
}
