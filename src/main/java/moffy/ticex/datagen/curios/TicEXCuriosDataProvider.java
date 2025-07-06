package moffy.ticex.datagen.curios;

import moffy.ticex.TicEX;
import moffy.ticex.lib.utils.TicEXCuriosUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TicEXCuriosDataProvider extends CuriosDataProvider {
    public TicEXCuriosDataProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(TicEX.MODID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider provider, ExistingFileHelper existingFileHelper) {
        if(ModList.get().isLoaded("curios")) {
            this.generateCuriosSlots();
        }
    }

    public void generateCuriosSlots() {
        List<String> incomparableSlots = new ArrayList<>();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            String id = TicEXCuriosUtils.resolveEquipmentSlot(slot);
            this.createSlot(id)
                    .size(1)
                    .icon(new ResourceLocation(TicEX.MODID, "slot/" + slot.getName()))
                    .addValidator(new ResourceLocation(TicEX.MODID, "incomparable"));
            incomparableSlots.add(id);
        }

        this.createEntities("entities")
                .addPlayer()
                .addEntities(EntityType.ARMOR_STAND)
                .addSlots(incomparableSlots.toArray(String[]::new));
    }
}
