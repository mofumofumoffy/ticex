package moffy.ticex.client.mekanism;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mekanism.client.model.BaseModelCache;
import moffy.ticex.TicEX;
import moffy.ticex.client.mekanism.MekaPlateMultilayerModel.ModuleOBJModelData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;

public class MekaPlateModelCache extends BaseModelCache{
    public static final MekaPlateModelCache INSTANCE = new MekaPlateModelCache();
    private final Set<Runnable> callbacks = new HashSet<>();

    public final OBJModelData MEKASUIT_EXO = registerOBJ(new ResourceLocation(TicEX.MODID,"models/entity/modifiable_mekasuit_exo.obj")); 
    private final Set<ModuleOBJModelData> mekaSuitModules = new HashSet<>();
    public final Set<ModuleOBJModelData> MEKASUIT_MODULES = Collections.unmodifiableSet(mekaSuitModules);

    protected MekaPlateModelCache() {
        super(TicEX.MODID);
    }
    
    public ModuleOBJModelData registerMekaSuitModuleModel(ResourceLocation rl) {
        ModuleOBJModelData data = register(rl, ModuleOBJModelData::new);
        mekaSuitModules.add(data);
        return data;
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
