package moffy.ticex.lib;

import moffy.ticex.TicEX;
import moffy.ticex.caps.curios.GauntletItemHandler;
import moffy.ticex.entity.curios.ResonanceToolProjectile;
import moffy.ticex.network.curios.TicEXSyncEntityMovements;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class ResonanceTools {
    public static final float BASE_RADIUS = 1.2f;

    public static void shoot(Player player) {
        CuriosApi.getCuriosInventory(player).ifPresent(curioItemHandler -> {
            curioItemHandler.findFirstCurio(stack -> stack.getItem() instanceof IModifiable && ToolStack.from(stack).getModifierLevel(TicEXModifiers.INCOMPARABLE_MODIFIER.get()) > 0).ifPresent(slotResult -> {
                shootGauntletStack(player, slotResult.stack());
            });
        });
    }

    public static void shootGauntletStack(Player player, ItemStack gauntletStack){
        shootGauntletStack(player, gauntletStack, true);
    }

    public static void shootGauntletStack(Player player, ItemStack gauntletStack, boolean useCooldown) {
        gauntletStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            if (!(itemHandler instanceof GauntletItemHandler gauntletItemHandler)) {
                return;
            }

            List<Integer> availableSlots = new ArrayList<>();
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                if (!itemHandler.getStackInSlot(i).isEmpty() && gauntletItemHandler.getItemCooldown(i) == 0) {
                    availableSlots.add(i);
                }
            }

            if (availableSlots.isEmpty()) {
                return;
            }

            int shootSlot = availableSlots.get(player.getRandom().nextIntBetweenInclusive(0, availableSlots.size() - 1));

            ItemStack toolStack = itemHandler.getStackInSlot(shootSlot);

            Vec3 center = player.position();
            float time = player.tickCount;
            double baseAngle = 2 * Math.PI / availableSlots.size() * shootSlot - time * 0.07 + Math.toRadians(player.getYRot());
            ;
            double radius = getRadius(itemHandler.getSlots());

            Vec3 offset = new Vec3(
                    Math.cos(baseAngle),
                    1.25,
                    Math.sin(baseAngle)
            ).scale(radius);

            Vec3 originPos = center.add(offset);


            shootProjectile(player, toolStack, originPos);

            if(useCooldown){
                gauntletItemHandler.setItemCooldown(shootSlot, 15);
            }
        });
    }

    public static void shootProjectile(Player player, ItemStack stack, Vec3 pos) {
        Level level = player.level();
        if (!level.isClientSide) {
            ResonanceToolProjectile arrow = new ResonanceToolProjectile(player, level);
            arrow.setPos(pos);
            arrow.setItem(stack);

            Vec3 lookAngle = player.getLookAngle();

            float velocity = 3.0f;
            float inaccuracy = 0.0f;

            arrow.shoot(lookAngle.x, lookAngle.y, lookAngle.z, velocity, inaccuracy);
            level.addFreshEntity(arrow);
            player.playNotifySound(SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0f, 1.0f);

            TicEXSyncEntityMovements packet = new TicEXSyncEntityMovements(arrow);
            TicEX.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> arrow), packet);
        }
    }

    public static double getRadius(int slot){
        return BASE_RADIUS + (Math.ceil(slot / 6d) * 0.2);
    }
}
