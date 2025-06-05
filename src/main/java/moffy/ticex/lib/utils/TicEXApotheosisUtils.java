package moffy.ticex.lib.utils;

import java.util.UUID;
import dev.shadowsoffire.attributeslib.api.ALObjects.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class TicEXApotheosisUtils {
    private static boolean isApothicAttributesLoaded = ModList.get().isLoaded("attributeslib");
    private static UUID modifierUUID = UUID.fromString("841a954a-1deb-4c01-925f-973d9e265bf5");
    private static AttributeModifier modifier = isApothicAttributesLoaded ? new AttributeModifier(modifierUUID, "celestial", 1, AttributeModifier.Operation.ADDITION) : null;
    private static AttributeInstance getAttributeInstance(Player player){
        return player.getAttributes().getInstance(Attributes.CREATIVE_FLIGHT.get());
    }

    static public void enableCreativeFlight(Player player) {
        AttributeInstance attr = isApothicAttributesLoaded ? getAttributeInstance(player) : null;
        if (isApothicAttributesLoaded) {
            if (!attr.hasModifier(modifier)) {
                attr.addPermanentModifier(modifier);
            }
        }else if(!player.getAbilities().mayfly){
            player.getAbilities().mayfly = true;
        }
        player.onUpdateAbilities();
    }

    static public void disableCreativeFlight(Player player){
        AttributeInstance attr = isApothicAttributesLoaded ? getAttributeInstance(player) : null;
        if(isApothicAttributesLoaded){
            attr.removeModifier(modifierUUID);
        }else if(player.getAbilities().mayfly){
            player.getAbilities().mayfly = false;
        }
        player.onUpdateAbilities();
    }
}
