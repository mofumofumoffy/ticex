package moffy.ticex.modifier;

import com.brandon3055.brandonscore.api.TechLevel;
import moffy.ticex.lib.hook.DamageSourceModifierHook;
import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.lib.utils.TicEXDEUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

import java.util.List;
import java.util.Optional;

public class ModifierDragonForce extends Modifier implements DamageSourceModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXModifierHooks.DAMAGE_SOURCE);
    }

    @Override
    public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
        TechLevel techLevel = getEffectiveTechLevel(tool);
        return this.getDisplayName(techLevel.index + 1);
    }

    @Override
    public DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, DamageSource currentSource, DamageSource original) {
        Entity entity = currentSource.getDirectEntity();

        if(entity != null){
            Level level = entity.level();
            Registry<DamageType> damageTypeRegistry = level.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE);
            Optional<ResourceKey<DamageType>> keyOptional = damageTypeRegistry.getResourceKey(currentSource.type());
            if(keyOptional.isPresent()){
                TechLevel currentLevel = TicEXDEUtils.getTechLevel(keyOptional.get());
                TechLevel dragonForceLevel = getEffectiveTechLevel(tool);
                if(currentLevel == null) {
                    return new DamageSource(damageTypeRegistry.getHolderOrThrow(TicEXDEUtils.getDamageTag(dragonForceLevel.index + 1)), currentSource.getDirectEntity(), currentSource.getEntity(), currentSource.getSourcePosition());
                }else if(dragonForceLevel != null && currentLevel.index < dragonForceLevel.index){
                    return new DamageSource(damageTypeRegistry.getHolderOrThrow(TicEXDEUtils.getDamageTag(dragonForceLevel.index + 1)), currentSource.getDirectEntity(), currentSource.getEntity(), currentSource.getSourcePosition());
                }
            }
        }

        return currentSource;
    }

    protected TechLevel getEffectiveTechLevel(IToolStackView tool){
        int effectiveLevel = 0;
        MaterialNBT materials = tool.getMaterials();
        List<MaterialStatsId> statTypes = ToolMaterialHook.stats(tool.getDefinition());
        for(int i = 0; i < materials.size(); i++){
            MaterialVariant variant = materials.get(i);
            if(i < statTypes.size()){
                List<ModifierEntry> traits = MaterialRegistry.getInstance().getTraits(variant.getId(), statTypes.get(i));
                for(ModifierEntry trait : traits){
                    if(trait.getModifier().getId().equals(this.getId())){
                        effectiveLevel = Math.max(effectiveLevel, trait.getLevel() - 1);
                    }
                }
            }
        }
        return TechLevel.byIndex(effectiveLevel);
    }
}
