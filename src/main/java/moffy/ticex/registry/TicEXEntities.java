package moffy.ticex.registry;

import moffy.ticex.entity.avaritia.EndestShotProjectile;
import moffy.ticex.entity.curios.ResonanceToolProjectile;
import moffy.ticex.entity.mekanism.MekanicProjectile;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

public class TicEXEntities {
    public static RegistryObject<EntityType<SBToolItemEntity>> SLASHBLADE_TOOL_ITEM_ENTITY = null;
    public static RegistryObject<EntityType<EndestShotProjectile>> ENDESTSHOT_PROJECTILE = null;
    public static RegistryObject<EntityType<ResonanceToolProjectile>> RESONANCE_TOOL_PROJECTILE = null;
    public static RegistryObject<EntityType<MekanicProjectile>> MEKANIC_PROJECTILE = null;
}
