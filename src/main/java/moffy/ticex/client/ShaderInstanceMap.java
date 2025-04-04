package moffy.ticex.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.renderer.ShaderInstance;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class ShaderInstanceMap {
    protected final Map<PartPredicate, InstanceProvider> shaderInstanceMap;

    public ShaderInstanceMap(){
        shaderInstanceMap = new HashMap<>();
    }

    public InstanceProvider getInstanceProvider(ModifierId modifierId){
        for(PartPredicate predicate : shaderInstanceMap.keySet()){
            if(predicate.isModifierId() && predicate.getModifierId().equals(modifierId)){
                return shaderInstanceMap.get(predicate);
            }
        }
        return null;
    }
    
    public InstanceProvider getInstanceProvider(MaterialVariantId materialVariantId){
        for(PartPredicate predicate : shaderInstanceMap.keySet()){
            if(predicate.isMaterialVariantId() && predicate.getMaterialVariantId().equals(materialVariantId)){
                return shaderInstanceMap.get(predicate);
            }
        }
        return null;
    }

    public int size(){
        return shaderInstanceMap.size();
    }
    public void addShader(MaterialVariantId materialVariantId, Supplier<ShaderInstance> instanceProvider){
        addShader(materialVariantId, instanceProvider, ()->{});
    }

    public void addShader(ModifierId modifierId, Supplier<ShaderInstance> instanceProvider){
        addShader(modifierId, instanceProvider, ()->{});
    }

    public void addShader(MaterialVariantId materialVariantId, Supplier<ShaderInstance> instanceProvider, Runnable setupMethod){
        this.shaderInstanceMap.put(new PartPredicate(materialVariantId), new InstanceProvider(instanceProvider, setupMethod));
    }

    public void addShader(ModifierId modifierId, Supplier<ShaderInstance> instanceProvider, Runnable setupMethod){
        this.shaderInstanceMap.put(new PartPredicate(modifierId), new InstanceProvider(instanceProvider, setupMethod));
    }

    public static class InstanceProvider {
        protected Supplier<ShaderInstance> instanceSupplier;
        protected Runnable setupMethod;

        public InstanceProvider(Supplier<ShaderInstance> instanceSupplier, Runnable setupMethod){
            this.instanceSupplier = instanceSupplier;
            this.setupMethod = setupMethod;
        }

        public Supplier<ShaderInstance> getInstanceSupplier() {
            return instanceSupplier;
        }

        public Runnable getSetupMethod() {
            return setupMethod;
        }
    }
}
