package moffy.ticex.registry;

import moffy.ticex.TicEX;
import moffy.ticex.lib.hook.*;
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
    public static ModuleHook<EnergyModifierHook> ENERGY = ModifierHooks.register(
        TicEX.getResource("energy"),
        EnergyModifierHook.class,
        EnergyModifierHook.AllMerger::new,
        new EnergyModifierHook.DefaultClass()
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
}
