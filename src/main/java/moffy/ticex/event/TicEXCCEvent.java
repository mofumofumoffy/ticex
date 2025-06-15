package moffy.ticex.event;

import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import moffy.ticex.lib.utils.TicEXCCUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;


public class TicEXCCEvent {
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide) {
            Player player = event.getEntity();
            Entity target = event.getTarget();

            if(hasModem(player)){
                PocketServerComputer computerItem = getPocketComputer(player);
                if(computerItem != null){
                    computerItem.queueEvent("player_attack", new Object[]{TicEXCCUtils.createEntityMapWithProps(player), TicEXCCUtils.createEntityMap(target)});
                }
            }
        }
    }

    public static void onPlayerDeath(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (!event.getEntity().level().isClientSide && livingEntity instanceof Player player && hasModem(player)) {
            PocketServerComputer computerItem = getPocketComputer(player);
            if(computerItem != null){
                computerItem.queueEvent("player_death", new Object[]{TicEXCCUtils.createEntityMapWithProps(player)});
            }
        }
    }

    public static void onPlayerTick(LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (!event.getEntity().level().isClientSide && livingEntity instanceof Player player && hasModem(player)) {
            PocketServerComputer computerItem = getPocketComputer(player);
            if(computerItem != null){
                computerItem.queueEvent("player_tick", new Object[]{TicEXCCUtils.createEntityMapWithProps(player)});
            }
        }
    }

    public static void onPlayerHurt(LivingHurtEvent event){
        if (!event.getEntity().level().isClientSide) {
            Entity source = event.getSource().getEntity();
            LivingEntity target = event.getEntity();

            if(target instanceof Player player && hasModem(player)){
                PocketServerComputer computerItem = getPocketComputer(player);
                if(computerItem != null){
                    computerItem.queueEvent("player_hurt", new Object[]{TicEXCCUtils.createEntityMap(source), TicEXCCUtils.createEntityMapWithProps(player)});
                }
            }
        }
    }

    public static void onPlayerJump(LivingJumpEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (!event.getEntity().level().isClientSide && livingEntity instanceof Player player && hasModem(player)) {
            PocketServerComputer computerItem = getPocketComputer(player);
            if(computerItem != null){
                computerItem.queueEvent("player_jump", new Object[]{TicEXCCUtils.createEntityMapWithProps(player)});
            }
        }
    }

    public static boolean hasModem(Player player){
        ItemStack chestStack =  player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof IModifiable) {
            ToolStack chest = ToolStack.from(chestStack);
            return chest.getModifierLevel(TicEXRegistry.MODEM_MODIFIER.get()) > 0;
        }
        return false;
    }

    public static PocketServerComputer getPocketComputer(Player player){
        if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof IModifiable){
            ToolStack chest = ToolStack.from(player.getItemBySlot(EquipmentSlot.CHEST));
            if(chest.getModifierLevel(TicEXRegistry.MODEM_MODIFIER.get()) > 0){
                for(ItemStack stack : player.getInventory().items){
                    if(stack.getItem() instanceof PocketComputerItem){
                        return PocketComputerItem.getServerComputer(player.getServer(), stack);
                    }
                }
            }
        }
        return null;
    }
}
