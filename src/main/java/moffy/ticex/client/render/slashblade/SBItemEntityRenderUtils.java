package moffy.ticex.client.render.slashblade;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class SBItemEntityRenderUtils {

    private static final Cache<ItemStack, BladeItemEntity> BLADE_ENTITY_CACHE = CacheBuilder.newBuilder()
            .maximumSize(50)
            .expireAfterAccess(Duration.ofSeconds(30))
            .build();

    public static void render(EntityRenderDispatcher entityRenderDispatcher, ItemStack itemStack, Level level, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        try {
            BladeItemEntity bladeItemEntity = BLADE_ENTITY_CACHE.get(itemStack, () -> createBladeEntity(level, itemStack));
            var renderer = entityRenderDispatcher.getRenderer(bladeItemEntity);

            renderer.render(bladeItemEntity, 0.0f, 0.0f, poseStack, bufferSource, packedLight);
        } catch (ExecutionException ignored) {
            // Ignore
        }
    }

    private static BladeItemEntity createBladeEntity(Level level, ItemStack itemStack) {
        SBToolItemEntity entity = TicEXRegistry.SLASHBLADE_TOOL_ITEM_ENTITY.get().create(level);
        if(entity != null) {
            entity.setItem(itemStack);
        }
        return entity;
    }
}
