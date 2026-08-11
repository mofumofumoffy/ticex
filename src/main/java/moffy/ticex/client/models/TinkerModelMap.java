package moffy.ticex.client.models;

import moffy.ticex.client.lib.PartPredicate;
import moffy.ticex.client.providers.ExtraArmorModelProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TinkerModelMap {
    private final Map<MaterialVariantId, Supplier<ExtraArmorModelProvider>> cacheByMaterial = new HashMap<>();
    private final Map<ModifierId, Supplier<ExtraArmorModelProvider>> cacheByModifier = new HashMap<>();
    protected Map<Supplier<ExtraArmorModelProvider>, PartPredicate<?>> modelMap;

    public TinkerModelMap(){
        this.modelMap = new HashMap<>();
    }

    public Supplier<ExtraArmorModelProvider> getModelProvider(MaterialVariantId materialVariantId){
        if(cacheByMaterial.containsKey(materialVariantId)){
            return cacheByMaterial.get(materialVariantId);
        }

        for(Supplier<ExtraArmorModelProvider> provider : modelMap.keySet()){
            PartPredicate<?> predicate = modelMap.get(provider);
            if (predicate instanceof PartPredicate.Material materialPredicate) {
                if (materialPredicate.testPredicate(materialVariantId)) {
                    cacheByMaterial.put(materialVariantId, provider);
                    return provider;
                }
            }
        }

        return null;
    }

    public Supplier<ExtraArmorModelProvider> getModelProvider(ModifierId modifierId){
        if(cacheByModifier.containsKey(modifierId)){
            return cacheByModifier.get(modifierId);
        }

        for(Supplier<ExtraArmorModelProvider> provider : modelMap.keySet()){
            PartPredicate<?> predicate = modelMap.get(provider);
            if (predicate instanceof PartPredicate.Modifier modifierPredicate) {
                if (modifierPredicate.testPredicate(modifierId)) {
                    cacheByModifier.put(modifierId, provider);
                    return provider;
                }
            }
        }

        return null;
    }

    public int size() {
        return modelMap.size();
    }

    public void clearCache() {
        cacheByMaterial.clear();
        cacheByModifier.clear();
    }

    public void addModel(PartPredicate<?> predicate, Supplier<ExtraArmorModelProvider> provider){
        this.modelMap.put(provider, predicate);
        clearCache();
    }

    public void addModel(MaterialVariantId materialVariantId, Supplier<ExtraArmorModelProvider> provider){
        addModel(new PartPredicate.Material(materialVariantId), provider);
    }

    public void addModel(ModifierId modifierId, Supplier<ExtraArmorModelProvider> provider){
        addModel(new PartPredicate.Modifier(modifierId), provider);
    }
}
