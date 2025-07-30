package moffy.ticex.event;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class TicEXCuriosEvent {
    public static void onLivingDeath(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if(!level.isClientSide()){
            RandomSource randomSource = level.getRandom();
            if(randomSource.nextIntBetweenInclusive(0, 1000) <= 0){
                level.addFreshEntity(new ItemEntity(level, livingEntity.getX(), livingEntity.getY() - 1, livingEntity.getZ(), new ItemStack(TicEXRegistry.EXHAUSTED_GLOVE.get())));
            }
        }
    }
}
