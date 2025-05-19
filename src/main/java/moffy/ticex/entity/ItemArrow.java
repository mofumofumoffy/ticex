package moffy.ticex.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.tools.item.CrystalshotItem.CrystalshotEntity;

public abstract class ItemArrow extends CrystalshotEntity{

    public ItemArrow(EntityType<? extends CrystalshotEntity> type, Level level) {
        super(type, level);
    }

    public ItemArrow(Level level, LivingEntity shooter){
        super(level, shooter);
    }
    
    public abstract ItemStack getItem();
}
