package moffy.ticex.event;

import moffy.ticex.client.render.ticex.ItemArrowRenderer;
import moffy.ticex.entity.ItemArrow;
import moffy.ticex.lib.utils.TicEXAvaritiaUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXAvaritiaEvent {
    private static ToolStack tool;

    private static void addDrop(LivingDropsEvent event, ItemStack drop) {
        ItemEntity entity = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), drop);
        entity.setDefaultPickUpDelay();
        event.getDrops().add(entity);
    }

    public static void onGetHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (target instanceof Player player) {
            if (player.getMainHandItem().getItem() instanceof IModifiable) {
                ToolStack tool = ToolStack.from(player.getMainHandItem());
                if (
                    !player.getMainHandItem().isEmpty() &&
                    TicEXRegistry.OMNIPOTENCE_MODIFIER != null &&
                    tool.getModifierLevel(TicEXRegistry.OMNIPOTENCE_MODIFIER.get()) > 0 &&
                    player.getMainHandItem().useOnRelease()
                ) {
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ItemStack armorStack : player.getArmorSlots()) {
                if (armorStack.getItem() instanceof IModifiable) {
                    ToolStack armor = ToolStack.from(armorStack);
                    if (
                        TicEXRegistry.TRANSCENDENTAL_MODIFIER != null &&
                        armor.getModifierLevel(TicEXRegistry.TRANSCENDENTAL_MODIFIER.get()) > 0
                    ) {
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                    }
                }
            }
        }
    }

    public static void onPlayerTick(PlayerTickEvent event) {
        Player player = event.player;
        Abilities abilities = player.getAbilities();
        if (!abilities.mayfly && TicEXAvaritiaUtils.hasCelestial(player)) {
            abilities.mayfly = true;
            player.onUpdateAbilities();
        }

    }

    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.isRecentlyHit() &&
                event.getEntity() instanceof AbstractSkeleton
                && event.getSource().getEntity() instanceof Player player
        ) {
            if (player.getMainHandItem().getItem() instanceof IModifiable) {
                ToolStack tool = ToolStack.from(player.getMainHandItem());
                if(TicEXRegistry.SKULLFIRE_MODIFIER != null && tool.getModifierLevel(TicEXRegistry.SKULLFIRE_MODIFIER.get()) > 0){
                    if (event.getDrops().isEmpty()) {
                        addDrop(event, new ItemStack(Items.WITHER_SKELETON_SKULL, 1));
                    } else {
                        int skulls = 0;
                        for (var drop : event.getDrops()) {
                            ItemStack stack = drop.getItem();
                            if (stack.is(Items.WITHER_SKELETON_SKULL)) {
                                skulls++;
                            }
                        }
                        if (skulls == 0) {
                            addDrop(event, new ItemStack(Items.WITHER_SKELETON_SKULL, 1));
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType<ItemArrow>) TicEXRegistry.ENDESTSHOT_PROJECTILE.get(), pContext ->
            new ItemArrowRenderer(pContext, 1f)
        );
    }
}
