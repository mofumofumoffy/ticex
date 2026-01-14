package moffy.ticex.client.modules.mekanism;

import mekanism.client.model.BaseModelCache;
import moffy.ticex.TicEX;
import moffy.ticex.client.modules.mekanism.MekaPlateMultilayerModel.ModuleOBJModelData;
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
    private final Set<ModuleOBJModelData> mekaSuitModules = new HashSet<>();
    public final Set<ModuleOBJModelData> MEKASUIT_MODULES = Collections.unmodifiableSet(mekaSuitModules);

    protected MekaPlateModelCache() {
        super(TicEX.MODID);
    }

    public void registerMekaSuitModuleModel(ResourceLocation rl) {
        ModuleOBJModelData data = register(rl, ModuleOBJModelData::new);
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
