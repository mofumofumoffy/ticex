package moffy.ticex.client.rendering;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomModel implements BakedModel {

    protected BakedModel original;

    public CustomModel(BakedModel original) {
        this.original = original;
    }

    public @NotNull ItemOverrides getOverrides() {
        return this.original.getOverrides();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return original.useAmbientOcclusion();
    }

    public boolean isGui3d() {
        return this.original.isGui3d();
    }

    public boolean usesBlockLight() {
        return false;
    }

    public boolean isCustomRenderer() {
        return true;
    }

    @SuppressWarnings("deprecation")
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.original.getParticleIcon();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState pState, Direction pDirection, @NotNull RandomSource pRandom) {
        return this.original.getQuads(pState, pDirection, pRandom);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            @NotNull RandomSource rand,
            @NotNull ModelData data,
            @Nullable RenderType renderType
    ) {
        return this.original.getQuads(state, side, rand, data, renderType);
    }
}
