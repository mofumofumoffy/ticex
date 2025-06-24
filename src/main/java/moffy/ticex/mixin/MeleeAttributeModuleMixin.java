package moffy.ticex.mixin;

import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@Mixin(value = MeleeAttributeModule.class, remap = false)
public class MeleeAttributeModuleMixin {

    @Shadow
    public ModifierCondition<IToolStackView> condition;

    @Shadow
    public Attribute attribute;

    @Shadow
    public String unique;

    @Shadow
    public UUID uuid;

    @Shadow
    public Operation operation;

    @Shadow
    public LevelingValue amount;

    @Inject(at = @At("head"), method = "beforeMeleeHit", cancellable = true)
    public void beforeMeleeHitExtension(
        IToolStackView tool,
        ModifierEntry modifier,
        ToolAttackContext context,
        float damage,
        float baseKnockback,
        float knockback,
        CallbackInfoReturnable<Float> cb
    ) {
        if (condition.matches(tool, modifier)) {
            LivingEntity target = context.getLivingTarget();
            if (target != null) {
                AttributeModifier attributeModifier = new AttributeModifier(
                    uuid,
                    unique,
                    amount.compute(modifier.getEffectiveLevel()),
                    operation
                );
                AttributeInstance instance = target.getAttribute(attribute);
                if (instance != null && !instance.hasModifier(attributeModifier)) {
                    instance.addTransientModifier(attributeModifier);
                }
            }
        }
        cb.setReturnValue(knockback);
    }
}
