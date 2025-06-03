package moffy.ticex.caps.slashblade;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

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
