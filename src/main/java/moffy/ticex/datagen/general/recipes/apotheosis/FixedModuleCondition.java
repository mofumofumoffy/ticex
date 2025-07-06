package moffy.ticex.datagen.general.recipes.apotheosis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.shadowsoffire.apotheosis.Apotheosis;
import moffy.ticex.TicEX;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FixedModuleCondition implements ICondition {
    static ResourceLocation id = new ResourceLocation(TicEX.MODID, "module_fixed");
    static Map<String, Supplier<Boolean>> types = new HashMap<>();
    final String name;

    public FixedModuleCondition(String name) {
        this.name = name;
    }

    public ResourceLocation getID() {
        return id;
    }

    public boolean test(ICondition.IContext context) {
        return (types.get(this.name)).get();
    }

    static {
        types.put("spawner", () -> Apotheosis.enableSpawner);
        types.put("garden", () -> Apotheosis.enableGarden);
        types.put("deadly", () -> Apotheosis.enableAdventure);
        types.put("adventure", () -> Apotheosis.enableAdventure);
        types.put("enchantment", () -> Apotheosis.enableEnch);
        types.put("potion", () -> Apotheosis.enablePotion);
        types.put("village", () -> Apotheosis.enableVillage);
        types.put("book", () -> Apotheosis.giveBook);
    }

    public static class Serializer implements IConditionSerializer<FixedModuleCondition> {
        public Serializer() {
        }

        public void write(JsonObject json, FixedModuleCondition value) {
            json.addProperty("module", value.name);
        }

        public FixedModuleCondition read(JsonObject json) {
            if (json.has("module") && FixedModuleCondition.types.containsKey(json.get("module").getAsString())) {
                return new FixedModuleCondition(json.get("module").getAsString());
            } else {
                throw new JsonParseException("Invalid module condition!");
            }
        }

        public ResourceLocation getID() {
            return FixedModuleCondition.id;
        }
    }
}
