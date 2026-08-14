package moffy.ticex.registry;

import moffy.ticex.TicEX;
import moffy.ticex.lib.hook.*;
import org.checkerframework.checker.units.qual.A;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

public class TicEXModifierHooks {
    public static ModuleHook<EmbossmentModifierHook> EMBOSSMENT = ModifierHooks.register(
        TicEX.getResource("embossment"),
        EmbossmentModifierHook.class,
        EmbossmentModifierHook.AllMerger::new,
        new EmbossmentModifierHook.DefaultClass()
    );
    public static ModuleHook<ProvidePropertyModifierHook> PROPERTY_PROVIDER = ModifierHooks.register(
        TicEX.getResource("provide_property"),
        ProvidePropertyModifierHook.class,
        ProvidePropertyModifierHook.AllMerger::new,
        new ProvidePropertyModifierHook.DefaultClass()
    );
    public static ModuleHook<DamageSourceModifierHook> DAMAGE_SOURCE = ModifierHooks.register(
        TicEX.getResource("modify_damage_source"),
        DamageSourceModifierHook.class,
        DamageSourceModifierHook.AllMerger::new,
        new DamageSourceModifierHook.DefaultClass()
    );
    public static ModuleHook<CriticalModifierHook> CRITICAL = ModifierHooks.register(
        TicEX.getResource("critical"),
        CriticalModifierHook.class,
        CriticalModifierHook.AllMerger::new,
        new CriticalModifierHook.DefaultClass()
    );

    public static ModuleHook<ArmorMeleeHitModifierHook> ARMOR_MELEE_HIT = ModifierHooks.register(
            TicEX.getResource("armor_melee_hit"),
            ArmorMeleeHitModifierHook.class,
            ArmorMeleeHitModifierHook.AllMerger::new,
            new ArmorMeleeHitModifierHook.DefaultClass()
    );
}
