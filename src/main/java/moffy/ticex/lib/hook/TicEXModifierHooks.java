package moffy.ticex.lib.hook;

import slimeknights.tconstruct.library.module.ModuleHook;

public class TicEXModifierHooks {
    public static ModuleHook<EmbossmentModifierHook> EMBOSSMENT = null;
    public static ModuleHook<ProvidePropertyModifierHook> PROPERTY_PROVIDER = null;
    public static ModuleHook<EnergyModifierHook> ENERGY = null;
    public static ModuleHook<DamageSourceModifierHook> DAMAGE_SOURCE = null;
    public static ModuleHook<CriticalModifierHook> CRITICAL = null;
}
