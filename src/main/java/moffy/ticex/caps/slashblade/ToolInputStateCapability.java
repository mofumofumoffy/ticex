package moffy.ticex.caps.slashblade;

import mods.flammpfeil.slashblade.capability.inputstate.InputState;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolInputStateCapability extends InputState{

    protected IToolStackView tool;

    public ToolInputStateCapability(IToolStackView tool){
        super();
        this.tool = tool;
    }

    
}
