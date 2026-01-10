package moffy.ticex.datagen.general;

import moffy.ticex.TicEX;
import moffy.ticex.lib.utils.TicEXDEUtils;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class TicEXDamageTypeProvider implements RegistrySetBuilder.RegistryBootstrap<DamageType> {
    public TicEXDamageTypeProvider() {
    }

    public static void register(RegistrySetBuilder builder) {
        builder.add(Registries.DAMAGE_TYPE, new TicEXDamageTypeProvider());
    }

    @Override
    public void run(BootstapContext<DamageType> context) {
        context.register(TicEXDEUtils.TOOL_CHAOTIC,
                new DamageType(modPrefix("tool_chaotic"), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(TicEXDEUtils.TOOL_DRACONIC,
                new DamageType(modPrefix("tool_draconium"), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(TicEXDEUtils.TOOL_DRACONIUM,
                new DamageType(modPrefix("tool_draconium"), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(TicEXDEUtils.TOOL_WYVERN,
                new DamageType(modPrefix("tool_wyvern"), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
    }

    public String modPrefix(String id) {
        return TicEX.MODID + "." + id;
    }
}
