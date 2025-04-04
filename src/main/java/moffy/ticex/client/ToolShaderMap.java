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

public abstract class ToolShaderMap<S, T extends ShaderProvider<S>>{
    protected Map<PartPredicate, T> shaderMap;

    public ToolShaderMap(){
        this.shaderMap = new HashMap<>();
    }

    public T getProvider(ModifierId modifierId){
        for(PartPredicate predicate : shaderMap.keySet()){
            if(predicate.isModifierId() && predicate.getModifierId().equals(modifierId)){
                return shaderMap.get(predicate);
            }
        }
        return null;
    }
    
    public T getProvider(MaterialVariantId materialVariantId){
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

    public T getProvider(PartPredicate predicate){
        for(PartPredicate p : shaderMap.keySet()){
            if(p.equals(predicate)){
                return shaderMap.get(p);
            }
        }
        return null;
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

    public abstract void addShader(MaterialVariantId materialVariantId, Consumer<S> renderConsumerOverlay);
    public abstract void addShader(ModifierId modifierId, Consumer<S> renderConsumerOverlay);
    public abstract void addShader(MaterialVariantId materialVariantId, Consumer<S> renderConsumerOverlay, Consumer<S> renderConsumerUnderlay);
    public abstract void addShader(ModifierId modifierId, Consumer<S> renderConsumerOverlay, Consumer<S> renderConsumerUnderlay);

    public static class Tool extends ToolShaderMap<RenderQuadArgsWrapper, ShaderProvider.Tool>{
        public void addShader(MaterialVariantId materialVariantId, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Tool(renderConsumerOverlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Tool(renderConsumerOverlay));
        }
    
        public void addShader(MaterialVariantId materialVariantId, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay, Consumer<RenderQuadArgsWrapper> renderConsumerUnderlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Tool(renderConsumerOverlay, renderConsumerUnderlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<RenderQuadArgsWrapper> renderConsumerOverlay, Consumer<RenderQuadArgsWrapper> renderConsumerUnderlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Tool(renderConsumerOverlay, renderConsumerUnderlay));
        }
    }

    public static class Armor extends ToolShaderMap<ArmorRenderArgsWrapper, ShaderProvider.Armor>{
        public void addShader(MaterialVariantId materialVariantId, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Armor(renderConsumerOverlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Armor(renderConsumerOverlay));
        }
    
        public void addShader(MaterialVariantId materialVariantId, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay, Consumer<ArmorRenderArgsWrapper> renderConsumerUnderlay){
            this.shaderMap.put(new PartPredicate(materialVariantId), new ShaderProvider.Armor(renderConsumerOverlay, renderConsumerUnderlay));
        }
    
        public void addShader(ModifierId modifierId, Consumer<ArmorRenderArgsWrapper> renderConsumerOverlay, Consumer<ArmorRenderArgsWrapper> renderConsumerUnderlay){
            this.shaderMap.put(new PartPredicate(modifierId), new ShaderProvider.Armor(renderConsumerOverlay, renderConsumerUnderlay));
        }

    }
}
