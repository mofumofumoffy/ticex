package moffy.ticex.event;

import committee.nova.mods.avaritia.init.registry.ModDamageTypes;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXAvaritiaEvent {
    public static void onGetHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if(player.getMainHandItem().getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(player.getMainHandItem());
            if (!player.getMainHandItem().isEmpty() && tool.getModifierLevel(TicEXRegistry.OMNIPOTEMCE_MODIFIER.get()) > 0 && player.getMainHandItem().useOnRelease()) {
                event.setCanceled(true);
            }
        }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if(player.getMainHandItem().getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(player.getMainHandItem());
                if (!player.getMainHandItem().isEmpty() && tool.getModifierLevel(TicEXRegistry.OMNIPOTEMCE_MODIFIER.get()) > 0 && !(event.getSource() instanceof ModDamageTypes.DamageSourceRandomMessages)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
            }   
            }
        }
    }
}
