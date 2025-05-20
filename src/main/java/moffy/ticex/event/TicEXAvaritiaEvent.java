package moffy.ticex.event;

import moffy.ticex.client.ItemArrowRenderer;
import moffy.ticex.entity.ItemArrow;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXAvaritiaEvent {
    public static void onGetHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        
        if(target instanceof Player player){
            if(player.getMainHandItem().getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(player.getMainHandItem());
                if (!player.getMainHandItem().isEmpty() && TicEXRegistry.OMNIPOTEMCE_MODIFIER != null && tool.getModifierLevel(TicEXRegistry.OMNIPOTEMCE_MODIFIER.get()) > 0 && player.getMainHandItem().useOnRelease()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            for(ItemStack armorStack : player.getArmorSlots()){
                if(armorStack.getItem() instanceof IModifiable){
                    ToolStack armor = ToolStack.from(armorStack);
                    if(TicEXRegistry.TRANSCENDENTAL_MODIFIER != null && armor.getModifierLevel(TicEXRegistry.TRANSCENDENTAL_MODIFIER.get()) > 0){
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                    }
                }
            }
        } 
    }

    @OnlyIn(Dist.CLIENT)
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType<ItemArrow>)TicEXRegistry.ENDESTSHOT_PROJECTILE.get(), pContext -> new ItemArrowRenderer(pContext, 1f));
    }
}
