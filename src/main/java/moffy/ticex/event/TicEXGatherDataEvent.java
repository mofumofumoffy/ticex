package moffy.ticex.event;

import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.fluid.FluidTextureProvider;
import moffy.ticex.datagen.general.LootProvider;
import moffy.ticex.datagen.general.TicEXDamageTypeProvider;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.datagen.general.sprite.TicEXSpriteSourceProvider;
import moffy.ticex.datagen.general.tag.BlockTagProvider;
import moffy.ticex.datagen.general.tag.FluidTagProvider;
import moffy.ticex.datagen.general.tag.ItemTagProvider;
import moffy.ticex.datagen.layout.TicEXStationSlotLayoutProvider;
import moffy.ticex.datagen.models.TicEXBlockstateProvider;
import moffy.ticex.datagen.modifier.ModifierProvider;
import moffy.ticex.datagen.modifier.ModifierTagProvider;
import moffy.ticex.datagen.tool.*;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.fluids.data.FluidBlockstateModelProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = TicEX.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TicEXGatherDataEvent {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();

        boolean server = event.includeServer();
        boolean client = event.includeClient();

        TicEXDamageTypeProvider.register(registrySetBuilder);

        DatapackBuiltinEntriesProvider registryProvider = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, registrySetBuilder, Set.of(TicEX.MODID));
        generator.addProvider(server, registryProvider);

        //tags
        BlockTagProvider blockTags = new BlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(server, blockTags);
        generator.addProvider(
            server,
            new ItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper)
        );
        generator.addProvider(server, new FluidTagProvider(packOutput, lookupProvider, existingFileHelper));

        //common
        generator.addProvider(client, new FluidTextureProvider(packOutput));
        generator.addProvider(server, new TicEXRecipeProvider(packOutput));
        generator.addProvider(server, new LootProvider(packOutput));
        generator.addProvider(client, new FluidBlockstateModelProvider(packOutput, TicEX.MODID));
        generator.addProvider(client, new TicEXBlockstateProvider(packOutput, existingFileHelper));
        generator.addProvider(client, new TicEXSpriteSourceProvider(packOutput, existingFileHelper));

        //tinkers slot
        generator.addProvider(server, new TicEXStationSlotLayoutProvider(packOutput));

        //modifiers
        generator.addProvider(server, new ModifierProvider(packOutput));
        generator.addProvider(server, new ModifierTagProvider(packOutput, existingFileHelper));

        //materials
        MaterialDefinitionProvider materialDefinitionProvider = new MaterialDefinitionProvider(packOutput);
        generator.addProvider(server, materialDefinitionProvider);
        generator.addProvider(server, new MaterialStatsProvider(packOutput, materialDefinitionProvider));
        generator.addProvider(server, new MaterialTraitsProvider(packOutput, materialDefinitionProvider));
        generator.addProvider(server, new MaterialTagProvider(packOutput, existingFileHelper));

        //tools
        generator.addProvider(server, new ToolDefinitionProvider(packOutput));
    }
}
