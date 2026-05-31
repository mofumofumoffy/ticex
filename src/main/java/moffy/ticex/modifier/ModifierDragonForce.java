package moffy.ticex.modifier;

import com.brandon3055.brandonscore.api.TechLevel;
import moffy.ticex.lib.hook.DamageSourceModifierHook;
import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.lib.utils.TicEXDEUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Optional;

public class ModifierDragonForce extends Modifier implements DamageSourceModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXModifierHooks.DAMAGE_SOURCE);
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        return super.getDisplayName(level).copy().withStyle(TechLevel.byIndex(level - 1).getTextColour());
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
                TechLevel dragonForceLevel = TicEXDEUtils.getTechLevel(tool, getId());
                if(currentLevel != null && dragonForceLevel != null && currentLevel.index < dragonForceLevel.index){
                    return new DamageSource(damageTypeRegistry.getHolderOrThrow(TicEXDEUtils.getDamageTag(tool, getId())), currentSource.getDirectEntity(), currentSource.getEntity(), currentSource.getSourcePosition());
                }
            }
        }

        return currentSource;
    }
}
