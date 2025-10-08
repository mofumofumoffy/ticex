package moffy.ticex.client.render.ticex;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.render.shader.ShaderProvider;
import moffy.ticex.client.render.shader.TicEXRenderTasks.RenderTask;
import moffy.ticex.client.render.shader.ToolShaderMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.*;
import java.util.function.Function;

public class TicEXRenders {
    public static final Map<Item, Function<BakedModel, BakedModel>> CUSTOM_MODELS = new HashMap<>();

    public static final ToolShaderMap.Tool TOOL_SHADERS = new ToolShaderMap.Tool();
    public static final ToolShaderMap.Armor ARMOR_SHADERS = new ToolShaderMap.Armor();
    public static final ToolShaderMap.Generic GENERIC_SHADERS = new ToolShaderMap.Generic();

    public static boolean shouldRenderWithShader(ItemStack stack) {
        return !stack.isEmpty() && TicEXConfig.USE_SHADER.get() &&
                !TicEXRenders.CUSTOM_MODELS.containsKey(stack.getItem());
    }

    public static Map<MaterialVariantId, ShaderProvider.Tool> collectShadersForMaterials(ItemStack itemStack) {
        Map<MaterialVariantId, ShaderProvider.Tool> shaderProviders = new HashMap<>();

        Item item = itemStack.getItem();
        if (item instanceof IModifiable) {
            ToolStack tool = ToolStack.from(itemStack);
            if (!TicEXRenders.TOOL_SHADERS.isToolTarget(tool)) {
                return Collections.emptyMap();
            }

            for (MaterialVariant material : tool.getMaterials()) {
                ShaderProvider.Tool provider = TicEXRenders.TOOL_SHADERS.getShaderProvider(material.getId());
                if (provider != null) shaderProviders.put(material.getId(), provider);
            }
        } else if (item instanceof IToolPart toolPart) {
            MaterialVariantId material = toolPart.getMaterial(itemStack);
            ShaderProvider.Tool provider = TicEXRenders.TOOL_SHADERS.getShaderProvider(material);
            if (provider == null) {
                return Collections.emptyMap();
            }
            shaderProviders.put(material, provider);
        } else {
            return Collections.emptyMap();
        }

        return shaderProviders;
    }

    public static Map<ModifierId, ShaderProvider.Tool> collectShadersForModifiers(ItemStack itemStack) {
        Map<ModifierId, ShaderProvider.Tool> shaderProviders = new HashMap<>();

        Item item = itemStack.getItem();
        if (item instanceof IModifiable) {
            ToolStack tool = ToolStack.from(itemStack);

            for (ModifierEntry modifierEntry : tool.getModifierList()) {
                ModifierId modifierId = modifierEntry.getId();
                ShaderProvider.Tool shaderProvider = TicEXRenders.TOOL_SHADERS.getShaderProvider(modifierId);
                if (shaderProvider != null) shaderProviders.put(modifierId, shaderProvider);
            }
        }

        return shaderProviders;
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public static void renderQuadsTasks(
            ItemStack pItemStack,
            PoseStack pPoseStack,
            BakedModel pModel,
            ItemDisplayContext pDisplayContext,
            boolean pLeftHand,
            RenderTaskProcessor renderTaskProcessor
    ) {
        List<RenderTask> renderQueue = new ArrayList<>();

        pPoseStack.pushPose();

        pModel = ForgeHooksClient.handleCameraTransforms(
                pPoseStack,
                pModel,
                pDisplayContext,
                pLeftHand
        );
        pPoseStack.translate(-0.5F, -0.5F, -0.5F);


        for (BakedModel model : pModel.getRenderPasses(pItemStack, true)) {
            for (RenderType renderType : model.getRenderTypes(pItemStack, true)) {
                RandomSource randomSource = RandomSource.create();

                for (Direction direction : Direction.values()) {
                    randomSource.setSeed(42L);
                    renderQueue.addAll(renderTaskProcessor.getRenderTasks(renderType, model.getQuads(null, direction, randomSource)));
                }

                randomSource.setSeed(42L);
                renderQueue.addAll(renderTaskProcessor.getRenderTasks(renderType, model.getQuads(null, null, randomSource)));
            }
        }

        renderQueue.sort(
                Comparator.comparingInt(task -> task.getPhase().getIndex())
        );

        for (RenderTask task : renderQueue) {
            task.applyRenderTask();
        }

        pPoseStack.popPose();
    }

    @FunctionalInterface
    public interface RenderTaskProcessor {
        List<RenderTask> getRenderTasks(RenderType renderType, List<BakedQuad> quads);
    }
}
