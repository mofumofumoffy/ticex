package moffy.ticex.client;

import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class PartPredicate {
    private ModifierId modifierId;
    private MaterialVariantId materialVariantId;


    public PartPredicate(ModifierId id){
        this.modifierId = id;
        this.materialVariantId = null;
    }

    public PartPredicate(MaterialVariantId id){
        this.modifierId = null;
        this.materialVariantId = id;
    }

    public boolean isModifierId(){
        return materialVariantId == null;
    }

    public boolean isMaterialVariantId(){
        return modifierId == null;
    }

    public ModifierId getModifierId() {
        return modifierId;
    }

    public MaterialVariantId getMaterialVariantId() {
        return materialVariantId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PartPredicate){
            PartPredicate other = (PartPredicate)obj;
            return isModifierId() ? this.modifierId.equals(other.getModifierId()) : this.materialVariantId.equals(other.getMaterialVariantId());
        }
        return false;
    }
}
