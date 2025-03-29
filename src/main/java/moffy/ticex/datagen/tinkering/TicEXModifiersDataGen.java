package moffy.ticex.datagen.tinkering;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TicEXModifiersDataGen extends AbstractModifierProvider{

    public TicEXModifiersDataGen(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public String getName() {
        return "TicEX Modifiers";
    }

    @Override
    protected void addModifiers() {
        buildModifier(TicEXRegistry.COSMIC_LUCK_MODIFIER)
            .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
            .addModule(EnchantmentModule.builder(Enchantments.BLOCK_FORTUNE).toolItem(ItemPredicate.or(ItemPredicate.tag(TinkerTags.Items.MELEE), ItemPredicate.tag(TinkerTags.Items.HARVEST))).level(10).constant())
            .addModule(EnchantmentModule.builder(Enchantments.MOB_LOOTING).toolItem(ItemPredicate.or(ItemPredicate.tag(TinkerTags.Items.MELEE), ItemPredicate.tag(TinkerTags.Items.HARVEST))).level(10).constant());

        buildModifier(TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER)
            .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
            .priority(125)
            .addModule(ReduceToolDamageModule.builder().flat(1.0f));

        /* buildModifier(TicEXRegistry.TRANSCENDENTAL_MODIFIER)
            .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
            .priority(999)
            .addModule(AttributeModule.Builder(ModifierModule.LOADER.)); */

        buildModifier(TicEXRegistry.DENSE_MODIFIER)
            .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
            .addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).flat(0.25f));

        buildModifier(TicEXRegistry.REBIRTH_MODIFIER)
            .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    }
    
}
