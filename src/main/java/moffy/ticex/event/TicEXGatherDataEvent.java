package moffy.ticex.event;

import java.util.concurrent.CompletableFuture;

import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.fluid.FluidTextureProvider;
import moffy.ticex.datagen.general.LootProvider;
import moffy.ticex.datagen.general.recipes.AvaritiaRecipeProvider;
import moffy.ticex.datagen.general.recipes.CCRecipeProvider;
import moffy.ticex.datagen.general.recipes.CommonRecipeProvider;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.datagen.general.tag.BlockTagProvider;
import moffy.ticex.datagen.general.tag.FluidTagProvider;
import moffy.ticex.datagen.general.tag.ItemTagProvider;
import moffy.ticex.datagen.modifier.ModifierProvider;
import moffy.ticex.datagen.modifier.ModifierTagProvider;
import moffy.ticex.datagen.tool.MaterialDefinitionProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TicEX.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TicEXGatherDataEvent {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        CraftingHelper.register(new ModsAvailableCondition.Serializer());

        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        
        boolean server = event.includeServer();
        boolean client = event.includeClient();

        //tags
        BlockTagProvider blockTags = new BlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(server, blockTags);
        generator.addProvider(server, new ItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(server, new FluidTagProvider(packOutput, lookupProvider, existingFileHelper));

        //common
        generator.addProvider(client, new FluidTextureProvider(packOutput));
        generator.addProvider(server, new TicEXRecipeProvider(packOutput));
        generator.addProvider(server, new LootProvider(packOutput));        

        //modifiers
        generator.addProvider(server, new ModifierProvider(packOutput));
        generator.addProvider(server, new ModifierTagProvider(packOutput, existingFileHelper));
        
        //materials
        generator.addProvider(server, new MaterialDefinitionProvider(packOutput));
    }
}
