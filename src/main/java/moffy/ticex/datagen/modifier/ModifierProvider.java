package moffy.ticex.datagen.modifier;

import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;

import dev.shadowsoffire.apotheosis.ench.Ench;
import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule;
import slimeknights.tconstruct.library.modifiers.modules.display.DurabilityBarColorModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.tools.modules.combat.FieryAttackModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class ModifierProvider extends AbstractModifierProvider implements IConditionBuilder {

    public ModifierProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public String getName() {
        return "TiCEX Modifiers";
    }

    @Override
    protected void addModifiers() {
        IJsonPredicate<Item> harvest = ItemPredicate.tag(HARVEST);

        //general
        if (TicEXRegistry.REBIRTH_MODIFIER != null) buildModifier(TicEXRegistry.REBIRTH_MODIFIER).levelDisplay(
                ModifierLevelDisplay.NO_LEVELS
        );

        //avaritia
        LootingModule WEAPON_LOOTING = LootingModule.builder()
                .toolItem(ItemPredicate.or(ItemPredicate.set(Items.AIR), ItemPredicate.tag(MELEE)))
                .level(10)
                .weapon();
        EnchantmentModule CONSTANT_FORTUNE = EnchantmentModule.builder(Enchantments.BLOCK_FORTUNE)
                .toolItem(harvest)
                .level(10)
                .constant();
        EnchantmentModule BLAZING_FIRE_ASPECT = EnchantmentModule.builder(Enchantments.FIRE_ASPECT)
                .toolItem(ItemPredicate.tag(MELEE))
                .level(10)
                .constant();

        if (TicEXRegistry.COSMIC_LUCK_MODIFIER != null) buildModifier(TicEXRegistry.COSMIC_LUCK_MODIFIER)
                .addModules(WEAPON_LOOTING, CONSTANT_FORTUNE, StatBoostModule.add(ToolStats.LURE).eachLevel(10))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER != null) buildModifier(TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .priority(125)
                .addModule(new DurabilityBarColorModule(0xff0000))
                .addModule(ReduceToolDamageModule.builder().flat(1.0f));
        if (TicEXRegistry.TRANSCENDENTAL_MODIFIER != null) buildModifier(TicEXRegistry.TRANSCENDENTAL_MODIFIER)
                .addModule(
                        AttributeModule.builder(TicEXRegistry.DAMAGE_TAKEN.get(), Operation.ADDITION)
                                .unique("1dc2b568-1b38-47a0-97d6-ac83a390c67c")
                                .eachLevel(-0.25f)
                )
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.DENSE_MODIFIER != null) buildModifier(TicEXRegistry.DENSE_MODIFIER)
                .addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).eachLevel(0.25f))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if(TicEXRegistry.SKULLFIRE_MODIFIER != null) buildModifier(TicEXRegistry.SKULLFIRE_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if(TicEXRegistry.BLAZING_FORTUNE_MODIFIER != null) buildModifier(TicEXRegistry.BLAZING_FORTUNE_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new FieryAttackModule(LevelingValue.flat(800)));
        if(TicEXRegistry.BLAZING_FLAME_MODIFIER != null) buildModifier(TicEXRegistry.BLAZING_FLAME_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(BLAZING_FIRE_ASPECT);

        if (TicEXRegistry.ETERNITY_MODIFIER != null) buildModifier(TicEXRegistry.ETERNITY_MODIFIER)
                .addModules(
                        ModifierSlotModule.slot(SlotType.ABILITY).eachLevel(1),
                        ModifierSlotModule.slot(SlotType.UPGRADE).eachLevel(1),
                        ModifierSlotModule.slot(SlotType.DEFENSE).eachLevel(1)
                )
                .levelDisplay(ModifierLevelDisplay.DEFAULT);

        //mekanism
        if (TicEXRegistry.RADIATION_SHIELDING_MODIFIER != null) buildModifier(
                TicEXRegistry.RADIATION_SHIELDING_MODIFIER
        ).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        //draconicevolution
        if (TicEXRegistry.INJECT_MODIFIER != null) buildModifier(TicEXRegistry.INJECT_MODIFIER)
                .addModules(
                        ModifierSlotModule.slot(SlotType.ABILITY).flat(1),
                        ModifierSlotModule.slot(SlotType.UPGRADE).flat(3)
                )
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        //create
        if (TicEXRegistry.CARDBOARD_MODIFIER != null) buildModifier(TicEXRegistry.CARDBOARD_MODIFIER).levelDisplay(
                ModifierLevelDisplay.NO_LEVELS
        );

        //computercraft
        if (TicEXRegistry.MODEM_MODIFIER != null) buildModifier(TicEXRegistry.MODEM_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(ModifierSlotModule.slot(SlotType.ABILITY).flat(1));

        //botania
        if (TicEXRegistry.AHRIM_MODIFIER != null) buildModifier(TicEXRegistry.AHRIM_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.DHAROK_MODIFIER != null) buildModifier(TicEXRegistry.DHAROK_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.GUTHAN_MODIFIER != null) buildModifier(TicEXRegistry.GUTHAN_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.TORAG_MODIFIER != null) buildModifier(TicEXRegistry.TORAG_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.VERAC_MODIFIER != null) buildModifier(TicEXRegistry.VERAC_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.KARIL_MODIFIER != null) buildModifier(TicEXRegistry.KARIL_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if (TicEXRegistry.NECTAR_MODIFIER != null) buildModifier(TicEXRegistry.NECTAR_MODIFIER)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierSlotModule.slot(SlotType.DEFENSE).eachLevel(1));
    }
}
