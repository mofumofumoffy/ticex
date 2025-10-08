package moffy.ticex.client.render.custom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DecoratedRenderType extends RenderType {

    private final RenderType delegate;

    private final VertexFormat formatOverride;
    private final VertexFormat.Mode modeOverride;
    private final Runnable setupStateOverride;
    private final Runnable clearStateOverride;

    private DecoratedRenderType(RenderType renderType,
                               @Nullable VertexFormat formatOverride, @Nullable VertexFormat.Mode modeOverride,
                               @Nullable Runnable setupStateOverride, @Nullable Runnable clearStateOverride) {
        super(
                renderType.name,
                renderType.format,
                modeOverride != null ? modeOverride : renderType.mode,
                renderType.bufferSize,
                renderType.affectsCrumbling,
                renderType.sortOnUpload,
                setupStateOverride != null ? setupStateOverride : renderType.setupState,
                clearStateOverride != null ? clearStateOverride : renderType.clearState
        );
        this.delegate = renderType;
        this.formatOverride = formatOverride;
        this.modeOverride = modeOverride;
        this.setupStateOverride = setupStateOverride;
        this.clearStateOverride = clearStateOverride;
    }

    public static DecoratedRenderType decorate(RenderType renderType,
                                               @Nullable VertexFormat formatOverride, @Nullable VertexFormat.Mode modeOverride,
                                               @Nullable Runnable setupStateOverride, @Nullable Runnable clearStateOverride) {
        return new DecoratedRenderType(renderType, formatOverride, modeOverride, setupStateOverride, clearStateOverride);
    }

    public static DecoratedRenderType decorate(RenderType renderType,
                                               @Nullable VertexFormat formatOverride, @Nullable VertexFormat.Mode modeOverride) {
        return new DecoratedRenderType(renderType, formatOverride, modeOverride, null, null);
    }

    public static DecoratedRenderType decorate(RenderType renderType,
                                               @Nullable Runnable setupStateOverride, @Nullable Runnable clearStateOverride) {
        return new DecoratedRenderType(renderType, null, null, setupStateOverride, clearStateOverride);
    }

    @Override
    public void end(@NotNull BufferBuilder pBufferBuilder, @NotNull VertexSorting pQuadSorting) {
        delegate.end(pBufferBuilder, pQuadSorting);
    }

    @Override
    public @NotNull String toString() {
        return delegate.toString();
    }

    @Override
    public int bufferSize() {
        return delegate.bufferSize();
    }

    @Override
    public @NotNull VertexFormat format() {
        return formatOverride != null ? formatOverride : delegate.format();
    }

    @Override
    public @NotNull VertexFormat.Mode mode() {
        return modeOverride != null ? mode : delegate.mode();
    }

    @Override
    public @NotNull Optional<RenderType> outline() {
        return delegate.outline();
    }

    @Override
    public boolean isOutline() {
        return delegate.isOutline();
    }

    @Override
    public boolean affectsCrumbling() {
        return delegate.affectsCrumbling();
    }

    @Override
    public boolean canConsolidateConsecutiveGeometry() {
        return delegate.canConsolidateConsecutiveGeometry();
    }

    @Override
    public void setupRenderState() {
        if (setupStateOverride != null) {
            setupStateOverride.run();
        } else {
            delegate.setupRenderState();
        }
    }

    @Override
    public void clearRenderState() {
        if (clearStateOverride != null) {
            clearStateOverride.run();
        } else {
            delegate.clearRenderState();
        }
    }
}
