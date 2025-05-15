package moffy.ticex.lib.utils;

import java.util.UUID;
import dev.shadowsoffire.attributeslib.api.ALObjects.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class TicEXApotheosisUtils {
    static private AttributeInstance getAttributeInstance(Player player){
        return player.getAttributes().getInstance(Attributes.CREATIVE_FLIGHT.get());
    }

    static public void enableCreativeFlight(Player player,AttributeModifier modifier) {
        AttributeInstance attr = getAttributeInstance(player);
        player.getAbilities().mayfly = true;
        if (ModList.get().isLoaded("attributeslib")) {
            if (!attr.hasModifier(modifier)) {
                attr.addPermanentModifier(modifier);
            }
        }
        player.onUpdateAbilities();
    }

    static public void disableCreativeFlight(Player player,UUID modifierUUID){
        AttributeInstance attr = getAttributeInstance(player);
        player.getAbilities().mayfly = false;
        if(ModList.get().isLoaded("attributeslib")){
            attr.removeModifier(modifierUUID);
        }
        player.onUpdateAbilities();
    }
}
