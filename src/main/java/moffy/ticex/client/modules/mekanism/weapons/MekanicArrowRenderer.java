package moffy.ticex.client.modules.mekanism.weapons;

import moffy.ticex.TicEX;
import moffy.ticex.entity.mekanism.MekanicProjectile;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MekanicArrowRenderer extends ArrowRenderer<MekanicProjectile> {
    ResourceLocation TEXTURE_LOC = TicEX.getResource("textures/entity/mekanic_arrow");

    public MekanicArrowRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(MekanicProjectile pEntity) {
        return TEXTURE_LOC;
    }
}
