package moffy.ticex.client.modules.mekanism;

import mekanism.client.model.BaseModelCache;
import moffy.ticex.TicEX;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MekaPlateModelCache extends BaseModelCache {

    public static final MekaPlateModelCache INSTANCE = new MekaPlateModelCache();
    public final OBJModelData MEKASUIT_EXO = registerOBJ(
            TicEX.getResource("models/entity/modifiable_mekasuit_exo.obj")
    );
    private final Set<Runnable> callbacks = new HashSet<>();
    private final Set<MekaPlateModelProvider.ModuleOBJModelData> mekaSuitModules = new HashSet<>();
    public final Set<MekaPlateModelProvider.ModuleOBJModelData> MEKASUIT_MODULES = Collections.unmodifiableSet(mekaSuitModules);

    protected MekaPlateModelCache() {
        super(TicEX.MODID);
    }

    public void registerMekaSuitModuleModel(ResourceLocation rl) {
        MekaPlateModelProvider.ModuleOBJModelData data = register(rl, MekaPlateModelProvider.ModuleOBJModelData::new);
        mekaSuitModules.add(data);
    }

    public void reloadCallback(Runnable callback) {
        callbacks.add(callback);
    }

    @Override
    public void onBake(BakingCompleted evt) {
        super.onBake(evt);
        callbacks.forEach(Runnable::run);
    }
}
