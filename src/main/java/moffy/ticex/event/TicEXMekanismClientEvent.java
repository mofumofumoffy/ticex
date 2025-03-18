package moffy.ticex.event;

import moffy.ticex.TicEX;
import moffy.ticex.client.MekaPlateModelCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TicEX.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TicEXMekanismClientEvent {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onLoadAdditionalModel(ModelEvent.RegisterAdditional event){
        if(ModList.get().isLoaded("mekanism"))
            MekaPlateModelCache.INSTANCE.setup(event);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModelBake(BakingCompleted event){
        if(ModList.get().isLoaded("mekanism"))
            MekaPlateModelCache.INSTANCE.onBake(event);
    }
}
