package moffy.ticex.modifier;

import com.hollingsworth.arsnouveau.api.perk.*;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class ModifierAlterative extends NoLevelsModifier implements InventoryTickModifierHook, AttributesModifierHook {
    public static final UUID MAX_MANA_UUID = UUID.fromString("c6fd022c-8b76-449a-8fb5-a6431f6bb799");
    public static final UUID MANA_REGEN_UUID = UUID.fromString("d6ceeb73-f1c4-424a-a782-48732503e036");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.ATTRIBUTES);
    }

    @Override
    public void addAttributes(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentSlot equipmentSlot, BiConsumer<Attribute, AttributeModifier> biConsumer) {
        if(iToolStackView instanceof ToolStack toolStack){
            ItemStack stack = toolStack.createStack();
            IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
            if (perkHolder != null) {
                biConsumer.accept(PerkAttributes.MAX_MANA.get(), new AttributeModifier(MAX_MANA_UUID, "max_mana_armor", 30 * (perkHolder.getTier() + 1), AttributeModifier.Operation.ADDITION));
                biConsumer.accept(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(MANA_REGEN_UUID, "mana_regen_armor", perkHolder.getTier() + 1, AttributeModifier.Operation.ADDITION));
                for (PerkInstance perkInstance : perkHolder.getPerkInstances()) {
                    IPerk perk = perkInstance.getPerk();
                    perk.getModifiers(equipmentSlot, stack, perkInstance.getSlot().value).forEach(biConsumer);
                }
            }
        }
    }

    @Override
    public void onInventoryTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, Level level, LivingEntity livingEntity, int i, boolean b, boolean b1, ItemStack stack) {
        if (level.isClientSide())
            return;
        if(livingEntity instanceof Player player){
            RepairingPerk.attemptRepair(stack, player);
            IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
            if (perkHolder == null)
                return;
            for (PerkInstance instance : perkHolder.getPerkInstances()) {
                if (instance.getPerk() instanceof ITickablePerk tickablePerk) {
                    tickablePerk.tick(stack, level, player, instance);
                }
            }
        }
    }
}
