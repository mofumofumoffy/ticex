package moffy.ticex.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.CustomTinkerRenders;
import moffy.ticex.client.providers.ExtraArmorModelProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.client.armor.MultilayerArmorModel;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(value = MultilayerArmorModel.class)
public class MultilayerArmorModelMixin {
    @Unique
    private final List<ExtraArmorModelProvider> ticex$providerCache = new ArrayList<>();

    @Inject(
            method = "setup",
            at = @At("TAIL"),
            remap = false
    )
    private void setupProvider(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> base, ArmorModelManager.ArmorModel model, CallbackInfoReturnable<Model> cir){
        if(stack.getItem() instanceof IModifiable){
            ticex$providerCache.clear();

            ToolStack tool = ToolStack.from(stack);
            for(MaterialVariant variant : tool.getMaterials().getList()){
                Supplier<ExtraArmorModelProvider> provider = CustomTinkerRenders.EXTRA_ARMOR_MODELS.getModelProvider(variant.getId());
                if(provider != null){
                    ExtraArmorModelProvider extraArmorModelProvider = provider.get();
                    extraArmorModelProvider.providerSetup(living, stack, slot, base, model);
                    ticex$providerCache.add(extraArmorModelProvider);
                }
            }

            for(ModifierEntry entry : tool.getModifierList()){
                Supplier<ExtraArmorModelProvider> provider = CustomTinkerRenders.EXTRA_ARMOR_MODELS.getModelProvider(entry.getId());
                if(provider != null){
                    ExtraArmorModelProvider extraArmorModelProvider = provider.get();
                    extraArmorModelProvider.providerSetup(living, stack, slot, base, model);
                    ticex$providerCache.add(extraArmorModelProvider);
                }
            }
        }
    }

    @Inject(
            method = "renderToBuffer",
            at = @At("TAIL")
    )
    private void renderExtraModel(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, CallbackInfo ci){
        for(ExtraArmorModelProvider extraArmorModelProvider : ticex$providerCache){
            extraArmorModelProvider.renderExtraModel(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }
}
