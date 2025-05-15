package moffy.ticex.datagen.modifier;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.SlotType;

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
        ModifierSlotModule UPGRADE = new ModifierSlotModule(SlotType.UPGRADE);
        if(TicEXRegistry.MODEM_MODIFIER != null)buildModifier(TicEXRegistry.MODEM_MODIFIER).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(UPGRADE);
    }
    
}
