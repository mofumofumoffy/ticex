package moffy.ticex.caps;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class EmbossmentMaterialCapability {

    public static final Capability<EmbossmentMaterialCapability> EMBOSSMENT_MATERIAL_CAPABILITY = CapabilityManager.get(new CapabilityToken<EmbossmentMaterialCapability>() {});
    public static final ResourceLocation MATERIAL_MAP = new ResourceLocation(TicEX.MODID, "embossed_material_map");

    protected final Map<String, MaterialId> embossedMaterials;
    protected final IToolStackView tool;
    
    public EmbossmentMaterialCapability(IToolStackView tool){
        this.embossedMaterials = new HashMap<>();
        this.tool = tool;
        deserializeNBT(tool.getPersistentData().getCompound(MATERIAL_MAP));
    }

    public void accept(ItemStack toolStack, ItemStack partStack, ToolPartItem part){
        MaterialVariantId materialVariantId = part.getMaterial(partStack);
        ToolStack tool = ToolStack.from(toolStack);

        remove(toolStack, partStack, part);
        embossedMaterials.put(part.getStatType().toString(), part.getMaterial(partStack).getId());

        for(ModifierEntry modifierEntry : MaterialRegistry.getInstance().getTraits(materialVariantId.getId(), part.getStatType())){
            tool.addModifier(modifierEntry.getId(), modifierEntry.getLevel());
        }

        tool.getPersistentData().put(MATERIAL_MAP, serializeNBT());
    }

    public void remove(ItemStack toolStack, ItemStack partStack, ToolPartItem part){
        MaterialStatsId stat = part.getStatType();
        MaterialId materialId = embossedMaterials.get(stat.toString());
        ToolStack tool = ToolStack.from(toolStack);

        embossedMaterials.remove(stat.toString());
        
        for(ModifierEntry modifierEntry : MaterialRegistry.getInstance().getTraits(materialId, part.getStatType())){
            tool.removeModifier(modifierEntry.getId(), modifierEntry.getLevel());
        }
    }

    public CompoundTag serializeNBT(){
        CompoundTag nbt = new CompoundTag();
        ListTag embossedMaterialList = new ListTag();
        for(Entry<String, MaterialId> entry : embossedMaterials.entrySet()){
            CompoundTag materialTag = new CompoundTag();
            materialTag.putString("stat", entry.getKey());
            materialTag.putString("id", entry.getValue().toString());
            embossedMaterialList.add(materialTag);
        }
        nbt.put("materials", embossedMaterialList);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt){
        ListTag embossedMaterialList = nbt.getList("materials", Tag.TAG_COMPOUND);
        for(int i = 0; i < embossedMaterialList.size();i++){
            CompoundTag materialTag = embossedMaterialList.getCompound(i);
            embossedMaterials.put(materialTag.getString("stat"), new MaterialId(materialTag.getString("id")));
        }
    }
}
