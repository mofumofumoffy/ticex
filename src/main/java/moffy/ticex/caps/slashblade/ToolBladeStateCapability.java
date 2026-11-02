package moffy.ticex.caps.slashblade;

import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Optional;

public class ToolBladeStateCapability extends SlashBladeState {

    protected ItemStack toolStack;
    protected IToolStackView tool;

    public ToolBladeStateCapability(ItemStack toolStack, IToolStackView tool) {
        super(toolStack);
        this.toolStack = toolStack;
        this.tool = tool;
        CompoundTag persistentTag = tool.getPersistentData().getCompound(ModifiableSlashBladeItem.BLADE_STATE_LOCATION);
        if (!persistentTag.isEmpty()) {
            CompoundTag copy = persistentTag.copy();
            deserializeNBT(copy);
            toolStack.getOrCreateTag().put("bladeState", copy);
            tool.getPersistentData().remove(ModifiableSlashBladeItem.BLADE_STATE_LOCATION);
        }
        if(this.carryType.isEmpty()){
            this.carryType = Optional.of(CarryType.DEFAULT);
        }
    }

    @Override
    public boolean isBroken() {
        return ToolDamageUtil.isBroken(toolStack);
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
