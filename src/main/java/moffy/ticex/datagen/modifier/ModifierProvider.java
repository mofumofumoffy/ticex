package moffy.ticex.datagen.modifier;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule;
import slimeknights.tconstruct.library.modifiers.modules.display.DurabilityBarColorModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;
import static slimeknights.tconstruct.common.TinkerTags.Items.WORN_ARMOR;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.LEVEL;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.MULTIPLIER;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.VALUE;
import static slimeknights.tconstruct.library.modifiers.modules.behavior.RepairModule.FACTOR;
import static slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial.ARMOR_SLOTS;

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
        IJsonPredicate<Item> armor = ItemPredicate.tag(WORN_ARMOR);
        
        ModifierSlotModule UPGRADE = new ModifierSlotModule(SlotType.UPGRADE);

        //general
        if(TicEXRegistry.REBIRTH_MODIFIER != null)buildModifier(TicEXRegistry.REBIRTH_MODIFIER).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        
        //avaritia
        LootingModule WEAPON_LOOTING = LootingModule.builder().toolItem(ItemPredicate.or(ItemPredicate.set(Items.AIR), ItemPredicate.tag(MELEE))).level(10).weapon();
        EnchantmentModule CONSTANT_FORTUNE = EnchantmentModule.builder(Enchantments.BLOCK_FORTUNE).toolItem(harvest).level(10).constant();

        if(TicEXRegistry.COSMIC_LUCK_MODIFIER != null)buildModifier(TicEXRegistry.COSMIC_LUCK_MODIFIER).addModules(WEAPON_LOOTING, CONSTANT_FORTUNE).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if(TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER != null)buildModifier(TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER)
            .levelDisplay(ModifierLevelDisplay.NO_LEVELS).priority(125)
            .addModule(new DurabilityBarColorModule(0xff0000))
            .addModule(ReduceToolDamageModule.builder().flat(1.0f));
        if(TicEXRegistry.TRANSCENDENTAL_MODIFIER != null)buildModifier(TicEXRegistry.TRANSCENDENTAL_MODIFIER).addModule(AttributeModule.builder(TicEXRegistry.DAMAGE_TAKEN.get(), Operation.ADDITION).unique("1dc2b568-1b38-47a0-97d6-ac83a390c67c").eachLevel(-0.25f)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        if(TicEXRegistry.DENSE_MODIFIER != null)buildModifier(TicEXRegistry.DENSE_MODIFIER).addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).eachLevel(0.25f)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        //mekanism
        if(TicEXRegistry.RADIATION_SHIELDING_MODIFIER != null)buildModifier(TicEXRegistry.RADIATION_SHIELDING_MODIFIER).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        //draconicevolution
        if(TicEXRegistry.INJECT_MODIFIER != null)buildModifier(TicEXRegistry.INJECT_MODIFIER).addModules(new ModifierSlotModule(SlotType.ABILITY, 1), new ModifierSlotModule(SlotType.UPGRADE, 3)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        //irons_spellbook
        if(TicEXRegistry.OVERCASTING_MODIFIER != null)buildModifier(TicEXRegistry.OVERCASTING_MODIFIER).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        //create
        if(TicEXRegistry.CARDBOARD_MODIFIER != null)buildModifier(TicEXRegistry.CARDBOARD_MODIFIER).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        //computercraft
        if(TicEXRegistry.MODEM_MODIFIER != null)buildModifier(TicEXRegistry.MODEM_MODIFIER).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(UPGRADE);
    
        //curios
        if(TicEXRegistry.INCOMPARABLE_MODIFIER != null)buildModifier(TicEXRegistry.INCOMPARABLE_MODIFIER).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    }
    
}
