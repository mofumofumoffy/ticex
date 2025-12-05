package moffy.ticex.modifier;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class ModifierHurricane
    extends NoLevelsModifier
    implements InventoryTickModifierHook, TooltipModifierHook, AttributesModifierHook {

    private static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("A4334312-DFF8-4582-9F4F-62AD0C070475");

    private static final UUID STEP_ASSIST_MODIFIER_UUID = UUID.fromString("4726C09D-FD86-46D0-92DD-49ED952A12D2");
    public static final UUID ATTRIBUTE_MODIFIER_UUID = UUID.fromString("39377487-3632-4a51-9128-6c211265b7c5");
    private static final AttributeModifier STEP_ASSIST = new AttributeModifier(
        STEP_ASSIST_MODIFIER_UUID,
        "Step Assist",
        0.4,
        Operation.ADDITION
    );

    public static final ResourceLocation HURRICANE_DATA = new ResourceLocation(TicEX.MODID, "hurricane");

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOLTIP, ModifierHooks.ATTRIBUTES);
    }

    public static void toggleStepAssist(IToolStackView boots, Player player) {
        boolean value;
        ModDataNBT bootsTag = boots.getPersistentData();
        if (bootsTag.contains(HURRICANE_DATA, Tag.TAG_BYTE)) {
            value = !bootsTag.getBoolean(HURRICANE_DATA);
            bootsTag.putBoolean(HURRICANE_DATA, value);
        } else {
            bootsTag.putBoolean(HURRICANE_DATA, true);
            value = true;
        }
        if (value) {
            player.sendSystemMessage(PELang.STEP_ASSIST.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED));
        } else {
            player.sendSystemMessage(PELang.STEP_ASSIST.translate(ChatFormatting.RED, PELang.GEM_DISABLED));
        }
    }

    private static boolean isJumpPressed() {
        return DistExecutor.unsafeRunForDist(
            () -> () -> Minecraft.getInstance().options.keyJump.isDown(),
            () -> () -> false
        );
    }

    @Override
    public void onInventoryTick(
        IToolStackView tool,
        ModifierEntry entry,
        Level level,
        LivingEntity entity,
        int itemSlot,
        boolean isSelected,
        boolean isCorrectSlot,
        ItemStack stack
    ) {
        Item item = tool.getItem();

        if (
            item instanceof ArmorItem armorItem &&
            armorItem.getType() == ArmorItem.Type.BOOTS &&
            entity instanceof Player player &&
            itemSlot == 0
        ) {
            if (!level.isClientSide) {
                ServerPlayer playerMP = (ServerPlayer) player;
                playerMP.fallDistance = 0;
            } else {
                if (!player.getAbilities().flying && isJumpPressed()) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, 0.1, 0));
                }
                if (!player.onGround()) {
                    if (player.getDeltaMovement().y() <= 0) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0.9, 1));
                    }
                    if (!player.getAbilities().flying) {
                        if (player.zza < 0) {
                            player.setDeltaMovement(player.getDeltaMovement().multiply(0.9, 1, 0.9));
                        } else if (player.zza > 0 && player.getDeltaMovement().lengthSqr() < 3) {
                            player.setDeltaMovement(player.getDeltaMovement().multiply(1.1, 1, 1.1));
                        }
                    }
                }
            }

            ModDataNBT persistentData = tool.getPersistentData();
            boolean stepAssistEnabled = false;
            if (persistentData.contains(HURRICANE_DATA, Tag.TAG_BYTE)) {
                stepAssistEnabled = persistentData.getBoolean(HURRICANE_DATA);
            }

            AttributeInstance attributeInstance = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
            if (attributeInstance != null) {
                AttributeModifier existing = attributeInstance.getModifier(STEP_ASSIST_MODIFIER_UUID);
                if (stepAssistEnabled) {
                    if (existing == null) {
                        attributeInstance.addTransientModifier(STEP_ASSIST);
                    }
                } else if (existing != null) {
                    attributeInstance.removeModifier(existing);
                }
            }
        }
    }

    @Override
    public void addTooltip(
        IToolStackView tool,
        ModifierEntry entry,
        Player player,
        List<Component> tooltips,
        TooltipKey tooltipKey,
        TooltipFlag tooltipFlag
    ) {
        tooltips.add(PELang.GEM_LORE_FEET.translate());
    }

    @Override
    public void addAttributes(
        IToolStackView tool,
        ModifierEntry entry,
        EquipmentSlot slot,
        BiConsumer<Attribute, AttributeModifier> modifierGetter
    ) {
        if (tool.getPersistentData().getBoolean(HURRICANE_DATA) && slot == EquipmentSlot.FEET) {
            modifierGetter.accept(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(MOVEMENT_SPEED_MODIFIER_UUID, "Armor modifier", 1.0, Operation.MULTIPLY_TOTAL)
            );
        }
        modifierGetter.accept(TicEXRegistry.DAMAGE_TAKEN.get(), new AttributeModifier(ATTRIBUTE_MODIFIER_UUID, "gem_modifier", -0.2f, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return advanced;
    }
}
