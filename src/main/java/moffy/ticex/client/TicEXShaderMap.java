package moffy.ticex.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import moffy.ticex.client.ShaderProvider.ArmorRenderArgsWrapper;
import moffy.ticex.client.ShaderProvider.RenderQuadArgsWrapper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public abstract class TicEXShaderMap<T>{
    protected Map<PartPredicate, ShaderProvider<T>> shaderMap;

    public TicEXShaderMap(){
        this.shaderMap = new HashMap<>();
    }

    public ShaderProvider<T> getProvider(ModifierId modifierId){
        for(PartPredicate predicate : shaderMap.keySet()){
            if(predicate.isModifierId() && predicate.getModifierId().equals(modifierId)){
                return shaderMap.get(predicate);
            }
        }
        return null;
    }
    
    public ShaderProvider<T> getProvider(MaterialVariantId materialVariantId){
        for(PartPredicate predicate : shaderMap.keySet()){
            if(predicate.isMaterialVariantId() && predicate.getMaterialVariantId().equals(materialVariantId)){
                return shaderMap.get(predicate);
            }
        }
        return null;
    }

    public int size(){
        return shaderMap.size();
    }

    public ShaderProvider<T> getProvider(PartPredicate predicate){
        for(PartPredicate p : shaderMap.keySet()){
            if(p.equals(predicate)){
                return shaderMap.get(p);
            }
        }
        return null;
    }

    public void addShader(ModifierId modifierId, ShaderProvider<T> provider){
        this.shaderMap.put(new PartPredicate(modifierId), provider);
    }

    

    @Nullable
    public PartPredicate getPredicate(ModifierId modifierId){
        for(PartPredicate predicate : shaderMap.keySet()){
            if(predicate.isModifierId() && predicate.getModifierId().equals(modifierId)){
                return predicate;
            }
        }
        return null;
    }

    public PartPredicate getPredicate(MaterialVariantId materialVariantId){
        for(PartPredicate predicate : shaderMap.keySet()){
            if(predicate.isMaterialVariantId() && predicate.getMaterialVariantId().equals(materialVariantId)){
                return predicate;
            }
        }
        return null;
    }

    public boolean isToolTarget(IToolStackView tool){
        for(MaterialVariant variant : tool.getMaterials().getList()){
            if(getPredicate(variant.getId()) != null){
                return true;
            }
        }

        for(ModifierEntry modifierEntry : tool.getModifierList()){
            if(getPredicate(modifierEntry.getId()) != null){
                return true;
            }
        }
        return false;
    }

    public abstract void addShader(MaterialVariantId materialVariantId, Consumer<T> renderConsumerOverlay);
    public abstract void addShader(ModifierId materialVariantId, Consumer<T> renderConsumerOverlay);
    public abstract void addShader(MaterialVariantId materialVariantId, Consumer<T> renderConsumerUnderlay, Consumer<T> renderConsumerOverlay);
    public abstract void addShader(ModifierId modifierId, Consumer<T> renderConsumerUnderlay, Consumer<T> renderConsumerOverlay);

    public static class Tool extends TicEXShaderMap<RenderQuadArgsWrapper>{
        public void addShader(MaterialVariantId materialVariantId, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Tool(renderConsumerOverlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Tool(renderConsumerOverlay));
        }
    
        public void addShader(MaterialVariantId materialVariantId, Consumer<RenderQuadArgsWrapper> renderConsumerUnderlay, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Tool(renderConsumerUnderlay, renderConsumerOverlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<RenderQuadArgsWrapper> renderConsumerUnderlay, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Tool(renderConsumerUnderlay, renderConsumerOverlay));
        }
    }

    public static class Armor extends TicEXShaderMap<ArmorRenderArgsWrapper>{
        public void addShader(MaterialVariantId materialVariantId, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Armor(renderConsumerOverlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Armor(renderConsumerOverlay));
        }
    
        public void addShader(MaterialVariantId materialVariantId, Consumer<ArmorRenderArgsWrapper> renderConsumerUnderlay, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Armor(renderConsumerUnderlay, renderConsumerOverlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<ArmorRenderArgsWrapper> renderConsumerUnderlay, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Armor(renderConsumerUnderlay, renderConsumerOverlay));
        }
    }
}
