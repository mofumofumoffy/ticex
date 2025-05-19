package moffy.ticex.mixin;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.math.Transformation;

import moffy.ticex.client.PartPredicate;
import moffy.ticex.client.ShaderToolQuad;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@Mixin(NormalModifierModel.class)
public class NormalModifierModelMixin {

    @Shadow( remap = false )
    private Material small;

    @Shadow( remap = false )
    private Material large;

    @Shadow( remap = false )
    private int color;

    @Shadow( remap = false )
    private int luminosity;

    @Inject(
        at = @At("invoke"),
        method = "addQuads",
        cancellable = true,
        remap = false
    )
    public void addQuadsExtension(IToolStackView tool, ModifierEntry entry, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels, CallbackInfo cb) {
        if(TicEXRegistry.TOOL_SHADERS.isToolTarget(tool)){
            Material spriteName = isLarge ? large : small;
            if (spriteName != null) {
                TextureAtlasSprite sprite = spriteGetter.apply(spriteName);
                List<BakedQuad>quads = MantleItemLayerModel.getQuadsForSprite(color, -1, sprite, transforms, luminosity, pixels);
                quadConsumer.accept(quads.stream().map(quad -> quad == null ? null : (BakedQuad)new ShaderToolQuad(quad, new PartPredicate(entry.getId()))).toList());
            }
            cb.cancel();   
        }
    }
}
