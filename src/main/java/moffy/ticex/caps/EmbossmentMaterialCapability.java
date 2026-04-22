package moffy.ticex.caps;

import moffy.ticex.TicEX;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EmbossmentMaterialCapability {

    public static final Capability<EmbossmentMaterialCapability> EMBOSSMENT_MATERIAL_CAPABILITY = CapabilityManager.get(
        new CapabilityToken<EmbossmentMaterialCapability>() {}
    );
    public static final ResourceLocation EMBOSSED_MATERIAL = TicEX.getResource("embossed_material");

    protected MaterialId embossedMaterialId;
    protected MaterialStatsId embossedMaterialStatType;
    protected final IToolStackView tool;

    public EmbossmentMaterialCapability(IToolStackView tool) {
        this.tool = tool;
        deserializeNBT(tool.getPersistentData().getCompound(EMBOSSED_MATERIAL));
    }

    public void accept(ItemStack toolStack, ItemStack partStack, ToolPartItem part) {
        MaterialId materialId = part.getMaterial(partStack).getId();
        ToolStack tool = ToolStack.from(toolStack);

        remove(toolStack);
        embossedMaterialId = materialId;
        embossedMaterialStatType = part.getStatType();

        for (ModifierEntry modifierEntry : MaterialRegistry.getInstance()
            .getTraits(materialId.getId(), part.getStatType())) {
            tool.addModifier(modifierEntry.getId(), modifierEntry.getLevel());
        }

        tool.getPersistentData().put(EMBOSSED_MATERIAL, serializeNBT());
    }

    public void remove(ItemStack toolStack) {
        ToolStack tool = ToolStack.from(toolStack);

        if (embossedMaterialId == null) return;

        for (ModifierEntry modifierEntry : MaterialRegistry.getInstance().getTraits(embossedMaterialId, embossedMaterialStatType)) {
            tool.removeModifier(modifierEntry.getId(), modifierEntry.getLevel());
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        CompoundTag materialTag = new CompoundTag();
        if(embossedMaterialId != null){
            materialTag.putString("stat", embossedMaterialStatType.toString());
            materialTag.putString("id", embossedMaterialId.toString());
            nbt.put("material", materialTag);
        }

        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag embossedMaterial = nbt.getCompound("material");
        embossedMaterialId = MaterialId.tryParse(embossedMaterial.getString("id"));
        embossedMaterialStatType = MaterialStatsId.tryParse(embossedMaterial.getString("stat"));
    }
}
