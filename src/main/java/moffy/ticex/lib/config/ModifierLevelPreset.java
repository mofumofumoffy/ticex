package moffy.ticex.lib.config;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ModifierLevelPreset(ResourceLocation rl, String name, int max) {
    public static List<ModifierLevelPreset> PRESET = List.of(
            of("tconstruct", "tools/modifiers/upgrade/springy", "Springy", 3),
            of("tconstruct", "tools/modifiers/defense/magic_protection", "Magic Protection", 32767),
            of("tconstruct", "tools/modifiers/defense/dragonborn", "Dragonborn", 32767),
            of("ticex", "tools/modifiers/upgrade/overload", "Overload", 32767),
            of("tconstruct", "tools/modifiers/upgrade/hydraulic_from_shard", "Hydraulic from Shard", 5),
            of("tconstruct", "tools/modifiers/upgrade/offhanded", "Offhanded", 2),
            of("tconstruct", "tools/modifiers/upgrade/padded", "Padded", 3),
            of("tconstruct", "tools/modifiers/upgrade/hydraulic_from_block", "Hydraulic from Block", 5),
            of("ticex", "tools/modifiers/slotless/koshirae", "Koshirae", 32767),
            of("tconstruct", "tools/modifiers/upgrade/bane_of_sssss", "Bane of Sssss", 5),
            of("tconstruct", "tools/modifiers/ability/spilling", "Spilling", 32767),
            of("ticex", "tools/modifiers/upgrade/cardboard", "Cardboard", 32767),
            of("tconstruct", "tools/modifiers/upgrade/thorns", "Thorns", 3),
            of("tconstruct", "tools/modifiers/upgrade/lightspeed_from_block", "Lightspeed from Block", 5),
            of("tconstruct", "tools/modifiers/upgrade/swift_sneak", "Swift Sneak", 5),
            of("tconstruct", "tools/modifiers/upgrade/trueshot", "Trueshot", 3),
            of("tconstruct", "tools/modifiers/upgrade/speedy_from_block", "Speedy from Block", 3),
            of("tconstruct", "tools/modifiers/upgrade/sweeping_edge", "Sweeping Edge", 3),
            of("tconstruct", "tools/modifiers/upgrade/severing", "Severing", 3),
            of("tconstruct", "tools/modifiers/upgrade/necrotic", "Necrotic", 5),
            of("tconstruct", "tools/modifiers/ability/trick_quiver", "Trick Quiver", 2),
            of("tconstruct", "tools/modifiers/upgrade/swiftstrike_from_shard", "Swiftstrike from Shard", 5),
            of("tconstruct", "tools/modifiers/upgrade/haste_from_dust", "Haste from Dust", 5),
            of("tconstruct", "tools/modifiers/ability/slurping", "Slurping", 32767),
            of("tconstruct", "tools/modifiers/defense/revitalizing", "Revitalizing", 32767),
            of("tconstruct", "tools/modifiers/ability/reach", "Reach", 32767),
            of("tconstruct", "tools/modifiers/upgrade/power", "Power", 5),
            of("tconstruct", "tools/modifiers/defense/turtle_shell", "Turtle Shell", 32767),
            of("ticex", "tools/modifiers/upgrade/socket", "Socket", 5),
            of("tconstruct", "tools/modifiers/ability/wetting", "Wetting", 32767),
            of("tconstruct", "tools/modifiers/upgrade/blasting", "Blasting", 5),
            of("tconstruct", "tools/modifiers/defense/blast_protection", "Blast Protection", 32767),
            of("tconstruct", "tools/modifiers/ability/boundless", "Boundless", 2),
            of("tconstruct", "tools/modifiers/slotless/blindshot", "Blindshot", 32767),
            of("tconstruct", "tools/modifiers/ability/multishot", "Multishot", 32767),
            of("ticex", "tools/modifiers/defence/radiation_shielding", "Radiation Shielding", 32767),
            of("tconstruct", "tools/modifiers/ability/double_jump", "Double Jump", 32767),
            of("ticex", "tools/modifiers/defence/infernal", "Infernal", 32767),
            of("ticex", "tools/modifiers/upgrade/override", "Override", 32767),
            of("tconstruct", "tools/modifiers/upgrade/tank", "Tank", 32767),
            of("ticex", "tools/modifiers/slotless/sensor", "Sensor", 32767),
            of("tconstruct", "tools/modifiers/upgrade/sharpness_from_shard", "Sharpness from Shard", 5),
            of("tconstruct", "tools/modifiers/upgrade/speedy_from_dust", "Speedy from Dust", 3),
            of("tconstruct", "tools/modifiers/upgrade/experienced", "Experienced", 5),
            of("tconstruct", "tools/modifiers/upgrade/step_up", "Step up", 2),
            of("tconstruct", "tools/modifiers/upgrade/farsighted", "Farsighted", 32767),
            of("tconstruct", "tools/modifiers/defense/projectile_protection", "Projectile Protection", 32767),
            of("tconstruct", "tools/modifiers/upgrade/soulspeed", "Soulspeed", 3),
            of("ticex", "tools/modifiers/defence/hurricane", "Hurricane", 32767),
            of("tconstruct", "tools/modifiers/ability/gilded", "Gilded", 32767),
            of("tconstruct", "tools/modifiers/upgrade/impaling", "Impaling", 4),
            of("tconstruct", "tools/modifiers/upgrade/antiaquatic", "Antiaquatic", 5),
            of("tconstruct", "tools/modifiers/defense/melee_protection", "Melee Protection", 32767),
            of("tconstruct", "tools/modifiers/upgrade/depth_strider", "Depth Strider", 3),
            of("tconstruct", "tools/modifiers/ability/pockets", "Pockets", 32767),
            of("tconstruct", "tools/modifiers/ability/strength", "Strength", 32767),
            of("ticex", "tools/modifiers/slotless/hidden_proud", "Hidden Proud", 32767),
            of("tconstruct", "tools/modifiers/ability/expanded", "Expanded", 32767),
            of("tconstruct", "tools/modifiers/defense/shulking", "Shulking", 32767),
            of("ticex", "tools/modifiers/slotless/mekanic", "Mekanic", 32767),
            of("tconstruct", "tools/modifiers/upgrade/hydraulic_from_bricks", "Hydraulic from Bricks", 5),
            of("tconstruct", "tools/modifiers/upgrade/fiery", "Fiery", 5),
            of("tconstruct", "tools/modifiers/ability/spitting", "Spitting", 32767),
            of("tconstruct", "tools/modifiers/upgrade/sharpness_from_block", "Sharpness from Block", 5),
            of("tconstruct", "tools/modifiers/upgrade/haste_from_block", "Haste from Block", 5),
            of("tconstruct", "tools/modifiers/upgrade/punch", "Punch", 5),
            of("tconstruct", "tools/modifiers/ability/bursting", "Bursting", 32767),
            of("tconstruct", "tools/modifiers/upgrade/killager_from_block", "Killager from Block", 5),
            of("tconstruct", "tools/modifiers/upgrade/pierce", "Pierce", 3),
            of("tconstruct", "tools/modifiers/ability/bulk_quiver", "Bulk Quiver", 32767),
            of("ticex", "tools/modifiers/defence/gravity", "Gravity", 32767),
            of("tconstruct", "tools/modifiers/ability/reflecting", "Reflecting", 32767),
            of("tconstruct", "tools/modifiers/defense/fire_protection", "Fire Protection", 32767),
            of("tconstruct", "tools/modifiers/upgrade/lightspeed_from_dust", "Lightspeed from Dust", 5),
            of("tconstruct", "tools/modifiers/upgrade/overforced", "Overforced", 5),
            of("tconstruct", "tools/modifiers/upgrade/shield_strap", "Shield Strap", 32767),
            of("ticex", "tools/modifiers/slotless/embossment", "Embossment", 32767),
            of("tconstruct", "tools/modifiers/upgrade/knockback", "Knockback", 3),
            of("tconstruct", "tools/modifiers/upgrade/killager_from_dust", "Killager from Dust", 5),
            of("tconstruct", "tools/modifiers/upgrade/feather_falling", "Feather Falling", 4),
            of("tconstruct", "tools/modifiers/upgrade/respiration", "Respiration", 3),
            of("tconstruct", "tools/modifiers/upgrade/item_frame", "Item Frame", 32767),
            of("tconstruct", "tools/modifiers/upgrade/freezing", "Freezing", 3),
            of("ticex", "tools/modifiers/defence/abyssal", "Abyssal", 32767),
            of("tconstruct", "tools/modifiers/upgrade/reinforced", "Reinforced", 5),
            of("tconstruct", "tools/modifiers/upgrade/magnetic", "Magnetic", 5),
            of("tconstruct", "tools/modifiers/upgrade/swiftstrike_from_block", "Swiftstrike from Block", 5),
            of("tconstruct", "tools/modifiers/upgrade/cooling", "Cooling", 5),
            of("ticex", "tools/modifiers/upgrade/konpaku", "Konpaku", 32767),
            of("tconstruct", "tools/modifiers/upgrade/quick_charge", "Quick Charge", 4),
            of("tconstruct", "tools/modifiers/upgrade/ricochet", "Ricochet", 2),
            of("tconstruct", "tools/modifiers/ability/splashing", "Splashing", 32767),
            of("tconstruct", "tools/modifiers/upgrade/smelting", "Smelting", 32767),
            of("tconstruct", "tools/modifiers/upgrade/nearsighted", "Nearsighted", 32767),
            of("tconstruct", "tools/modifiers/upgrade/smite", "Smite", 5),
            of("ticex", "tools/modifiers/upgrade/overcasting", "Overcasting", 32767)
    );

    private static ModifierLevelPreset of(String ns, String path, String name, int max) {
        return new ModifierLevelPreset(new ResourceLocation(ns, path), name, max);
    }

    public String configName() {
        String noSpaces = name.replace(" ", "");
        if (noSpaces.isEmpty()) {
            return "";
        }
        return Character.toLowerCase(noSpaces.charAt(0)) + noSpaces.substring(1);
    }
}
