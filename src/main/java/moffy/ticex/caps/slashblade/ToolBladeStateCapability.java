package moffy.ticex.caps.slashblade;

import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolBladeStateCapability extends SlashBladeState{

      protected ItemStack toolStack;
      protected IToolStackView tool;

      public ToolBladeStateCapability(ItemStack toolStack, IToolStackView tool) {
            super(toolStack);
            this.toolStack = toolStack;
            this.tool = tool;
            CompoundTag persistentTag = tool.getPersistentData().getCompound(ModifiableSlashBladeItem.BLADE_STATE_LOCATION);
            if(persistentTag != null && !persistentTag.isEmpty()){
                CompoundTag copy = persistentTag.copy();
                deserializeNBT(copy);
                toolStack.getOrCreateTag().put("bladeState", copy);
                tool.getPersistentData().remove(ModifiableSlashBladeItem.BLADE_STATE_LOCATION);
            }
      }

    @Override
    public int getMaxDamage() {
        return ToolDamageUtil.getFakeMaxDamage(toolStack);
    }

    @Override
    public int getDamage() {
        return 0;
    }
}
