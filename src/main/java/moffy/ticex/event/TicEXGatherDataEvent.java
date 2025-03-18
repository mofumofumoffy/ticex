package moffy.ticex.event;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.materials.TicEXMaterialDataGen;
import moffy.ticex.datagen.materials.TicEXMaterialStatsDataGen;
import moffy.ticex.datagen.materials.TicEXMaterialTraitsDataGen;
import moffy.ticex.datagen.tinkering.TicEXModifiersDataGen;
import moffy.ticex.datagen.tinkering.TicEXToolDefinitionsDataGen;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TicEX.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TicEXGatherDataEvent {
    public static void gatherData(GatherDataEvent event){
        DataGenerator gen = event.getGenerator();
        TicEXMaterialDataGen materialDataGen = new TicEXMaterialDataGen(gen.getPackOutput());
        gen.addProvider(true, materialDataGen);
        gen.addProvider(true, new TicEXMaterialStatsDataGen(gen.getPackOutput(), materialDataGen));
        gen.addProvider(true, new TicEXModifiersDataGen(gen.getPackOutput()));
        gen.addProvider(true, new TicEXMaterialTraitsDataGen(gen.getPackOutput(), materialDataGen));
        gen.addProvider(true, new TicEXToolDefinitionsDataGen(gen.getPackOutput(), TicEX.MODID));
    }
}
