package moffy.ticex.client.render.shader;

import moffy.ticex.client.render.custom.PartPredicate;
import moffy.ticex.client.render.provider.context.armor.RenderArmorPartContext;
import moffy.ticex.client.render.provider.context.tool.RenderGenericContext;
import moffy.ticex.client.render.provider.context.tool.RenderQuadContext;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class ToolShaderMap<WRAPPER, PROVIDER extends ShaderProvider<WRAPPER, ?>> {
    private final Map<MaterialVariantId, PROVIDER> cacheByMaterial = new HashMap<>();
    private final Map<ModifierId, PROVIDER> cacheByModifier = new HashMap<>();
    protected Map<PROVIDER, PartPredicate<?>> shaderMap;

    public ToolShaderMap() {
        this.shaderMap = new HashMap<>();
    }

    @Nullable
    public PROVIDER getShaderProvider(ModifierId modifierId) {
        if (cacheByModifier.containsKey(modifierId)) {
            return cacheByModifier.get(modifierId);
        }

        for (PROVIDER provider : shaderMap.keySet()) {
            PartPredicate<?> predicate = shaderMap.get(provider);
            if (predicate instanceof PartPredicate.Modifier modifierPredicate) {
                if (modifierPredicate.testPredicate(modifierId)) {
                    cacheByModifier.put(modifierId, provider);
                    return provider;
                }
            }
        }
        return null;
    }

    @Nullable
    public PROVIDER getShaderProvider(MaterialVariantId materialVariantId) {
        if (cacheByMaterial.containsKey(materialVariantId)) {
            return cacheByMaterial.get(materialVariantId);
        }

        for (PROVIDER provider : shaderMap.keySet()) {
            PartPredicate<?> predicate = shaderMap.get(provider);
            if (predicate instanceof PartPredicate.Material materialPredicate) {
                if (materialPredicate.testPredicate(materialVariantId)) {
                    cacheByMaterial.put(materialVariantId, provider);
                    return provider;
                }
            }
        }
        return null;
    }

    public int size() {
        return shaderMap.size();
    }

    public void clearCache() {
        cacheByMaterial.clear();
        cacheByModifier.clear();
    }

    public void addShader(PartPredicate<?> predicate, PROVIDER provider) {
        this.shaderMap.put(provider, predicate);
        clearCache();
    }

    public void addShader(MaterialVariantId materialVariantId, PROVIDER provider) {
        addShader(new PartPredicate.Material(materialVariantId), provider);
    }

    public void addShader(ModifierId modifierId, PROVIDER provider) {
        addShader(new PartPredicate.Modifier(modifierId), provider);
    }

    public static class Tool extends ToolShaderMap<RenderQuadContext, ShaderProvider.Tool> {
        public boolean isToolTarget(IToolStackView tool) {
            for (MaterialVariant variant : tool.getMaterials().getList()) {
                ShaderProvider.Tool shaderProvider = getShaderProvider(variant.getId());
                if (shaderProvider != null) {
                    return true;
                }
            }

            for (ModifierEntry modifierEntry : tool.getModifierList()) {
                ShaderProvider.Tool shaderProvider = getShaderProvider(modifierEntry.getId());
                if (shaderProvider != null) {
                    return true;
                }
            }

            return false;
        }
    }

    public static class Armor extends ToolShaderMap<RenderArmorPartContext, ShaderProvider.Armor> {
    }

    public static class Generic extends ToolShaderMap<RenderGenericContext, ShaderProvider.Generic> {
    }
}
