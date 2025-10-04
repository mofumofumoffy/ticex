package moffy.ticex.datagen.material.trim;

import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public class TicEXConditionProvider extends ForgeConditionProvider {
    public TicEXConditionProvider(PackOutput output) {
        super(output, PackOutput.Target.DATA_PACK);
    }

    @Override
    public void gatherConditions() {
        this.addConditions(
                new ResourceLocation[] {
                        new ResourceLocation(TicEX.MODID, "trim_material/draconium"),
                        new ResourceLocation(TicEX.MODID, "trim_material/wyvern"),
                        new ResourceLocation(TicEX.MODID, "trim_material/draconic"),
                        new ResourceLocation(TicEX.MODID, "trim_material/chaotic")
                },
                modsAvailable(new ResourceLocation(TicEX.MODID, "draconicevolution_compat"))
        );
        this.addCondition(
                new ResourceLocation(TicEX.MODID, "trim_material/infinity"),
                modsAvailable(new ResourceLocation(TicEX.MODID, "avaritia_compat"))
        );
    }

    public static ICondition modsAvailable(ResourceLocation rl) {
        return new ModsAvailableCondition(new ResourceLocation(AddonAPI.MODID, "mods_available"), rl);
    }

    @Override
    public @NotNull String getName() {
        return "TicEX Conditions";
    }
}
